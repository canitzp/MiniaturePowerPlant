package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MPPRegistry;
import de.canitzp.miniaturepowerplant.modules.DepletableModule;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyBoost;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileCarrier extends BlockEntity implements MenuProvider, Nameable{

    public static final BlockEntityType<TileCarrier> TYPE = BlockEntityType.Builder.of(TileCarrier::new, BlockCarrier.INSTANCE).build(null);

    private final CarrierInventory inventory = new CarrierInventory(this);
    public final SidedInvWrapper sw = new SidedInvWrapper(this.inventory, null);
    public final EnergyStorage energyStorage = new EnergyStorage(20000);

    private final IEnergyStorage readOnlyEnergyStorage = new IEnergyStorage(){

        @Override
        public int receiveEnergy(int i, boolean b) {
            return 0;
        }

        @Override
        public int extractEnergy(int i, boolean b) {
            return TileCarrier.this.getEnergyStorage().extractEnergy(i, b);
        }

        @Override
        public int getEnergyStored() {
            return TileCarrier.this.getEnergyStorage().getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return TileCarrier.this.getEnergyStorage().getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return TileCarrier.this.getEnergyStorage().canExtract();
        }

        @Override
        public boolean canReceive() {
            return false;
        }
    };

    private int wastedEnergy = 0, producedEnergy = 0;

    private Map<ICarrierModule.CarrierSlot, SynchroniseModuleData> syncDataMap = new HashMap<>();

    public TileCarrier(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player){
        return CarrierMenu.createServerContainer(windowId, inv, this);
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider){
        CompoundTag nbt = new CompoundTag();
        this.writeNBT(nbt, provider);
        return nbt;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider){
        this.readNBT(pkt.getTag(), lookupProvider);
    }
    
    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider lookupProvider){
        super.loadAdditional(nbt, lookupProvider);
        this.readNBT(nbt, lookupProvider);
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider){
        super.saveAdditional(nbt, provider);
        this.writeNBT(nbt, provider);
    }
    
    @Override
    public Component getDisplayName(){
        return Component.empty();
    }
    
    @Override
    public Component getName(){
        return this.getDisplayName();
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TileCarrier tile) {
        if(level != null && !level.isClientSide()){
            boolean changed = false;

            // sync pulse
            if((level.getGameTime() & 0b111) == 0b0){ // call every eight tick => 2.5 times a second
                for (Player player : level.players()) {
                    if(player instanceof ServerPlayer){
                        if(player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64 && !tile.isRemoved() && level.getBlockEntity(pos) == tile){
                            ((ServerPlayer) player).connection.send(tile.getUpdatePacket());
                        }
                    }
                }
            }

            // modules tick
            for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                if(!tile.isDepleted(slot)){
                    ICarrierModule carrierModule = tile.getCarrierModule(slot);
                    if(carrierModule != null){
                        carrierModule.tick(level, tile.getBlockPos(), tile, tile.getSyncData(slot));
                    }
                }
            }

            // modules energy production
            if(tile.energyStorage.getEnergyStored() < tile.energyStorage.getMaxEnergyStored()) {
                List<ICarrierModule.CarrierSlot> hasProducedEnergy = new ArrayList<>();
                int completeEnergyProduction = 0;
                for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                    int moduleEnergy = tile.getProductionForSlot(slot).stream().mapToInt(EnergyProduction::getEnergy).sum();
                    double modulePenalty = tile.getPenaltiesForSlot(slot).stream().mapToDouble(EnergyPenalty::getMultiplier).sum();
                    double moduleBoost = tile.getBoostsForSlot(slot).stream().mapToDouble(EnergyBoost::getMultiplier).sum();
    
                    // calculate penalties
                    if(modulePenalty >= 1.0D){
                        moduleEnergy = 0;
                    } else if (modulePenalty > 0){
                        long moduleEnergyLong = Math.round(moduleEnergy * (1.0D - modulePenalty));
                        if(moduleEnergyLong < Integer.MAX_VALUE){
                            moduleEnergy = (int) moduleEnergyLong;
                        } else {
                            moduleEnergy = Integer.MAX_VALUE;
                        }
                    }
    
                    if(moduleEnergy > 0){
                        // calculate boosts
                        if(moduleBoost > 0){
                            long moduleEnergyLong = Math.round(moduleEnergy * (1.0D + moduleBoost));
                            if(moduleEnergyLong < Integer.MAX_VALUE){
                                moduleEnergy = (int) moduleEnergyLong;
                            } else {
                                moduleEnergy = Integer.MAX_VALUE;
                            }
                        }
        
                        hasProducedEnergy.add(slot);
                        completeEnergyProduction += moduleEnergy;
                    }
                }

                if (completeEnergyProduction > 0) {
                    tile.producedEnergy = completeEnergyProduction;
                    tile.wastedEnergy = completeEnergyProduction - tile.energyStorage.receiveEnergy(completeEnergyProduction, false);
                } else {
                    tile.producedEnergy = 0;
                    tile.wastedEnergy = 0;
                }

                // modules depletion
                for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                    if (!tile.isDepleted(slot) && hasProducedEnergy.contains(slot)) {
                        ItemStack stack = tile.inventory.getItem(slot.ordinal());
                        if (!stack.isEmpty() && stack.getItem() instanceof DepletableModule) {
                            stack.set(MPPRegistry.DC_DEPLETION, tile.getDepletion(slot) + stack.getOrDefault(MPPRegistry.DC_DEPLETION, 0F));
                            changed = true;
                        }
                    }
                }
            } else {
                if(tile.producedEnergy > 0 && tile.wastedEnergy > 0){
                    tile.producedEnergy = 0;
                    tile.wastedEnergy = 0;
                }
            }

            // push energy to surrounding blocks
            if(tile.energyStorage.getEnergyStored() > 0){
                for (Direction side : Direction.values()) {
                    BlockEntity surroundingTile = tile.level.getBlockEntity(tile.getBlockPos().relative(side));
                    if(surroundingTile != null){
                        IEnergyStorage surroundingTileEnergyStorage = tile.level.getCapability(Capabilities.EnergyStorage.BLOCK, tile.getBlockPos().relative(side), side.getOpposite());
                        if(surroundingTileEnergyStorage == null){
                            continue;
                        }

                        // tile energy
                        int receiveEnergy = surroundingTileEnergyStorage.receiveEnergy(tile.getEnergyStorage().extractEnergy(surroundingTileEnergyStorage.receiveEnergy(Integer.MAX_VALUE, true), false), false);

                        if(receiveEnergy > 0){
                            changed = true;
                        }

                        // accu energy
                        IEnergyStorage accuEnergyStorage = tile.getAccuStorage();
                        if(accuEnergyStorage != null){
                            receiveEnergy = surroundingTileEnergyStorage.receiveEnergy(accuEnergyStorage.extractEnergy(surroundingTileEnergyStorage.receiveEnergy(Integer.MAX_VALUE, true), false), false);
                            if(receiveEnergy > 0){
                                changed = true;
                            }
                        }
                    }
                }
            }

            // move energy to accu/battery
            if(tile.energyStorage.getEnergyStored() > 0){
                IEnergyStorage accuEnergyStorage = tile.getAccuStorage();
                if(accuEnergyStorage != null){
                    int extracedEnergy = tile.energyStorage.extractEnergy(
                            accuEnergyStorage.receiveEnergy(
                                    tile.energyStorage.extractEnergy(tile.energyStorage.getEnergyStored(), true),
                                    false),
                            false);
                    if(extracedEnergy > 0){
                        changed = true;
                    }
                }
            }
            if(changed){
                tile.setChanged();
            }
        }
    }
    
    public void animationTick(BlockState state, RandomSource rnd){
        for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
            if(!this.isDepleted(slot)){
                ICarrierModule carrierModule = this.getCarrierModule(slot);
                if(carrierModule != null){
                    carrierModule.blockAnimationTick((ClientLevel) this.getLevel(), this.getBlockPos(), this, state, rnd, this.getSyncData(slot));
                }
            }
        }
    }

    public CarrierInventory getInventory() {
        return this.inventory;
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public IEnergyStorage getEnergyStorageReadOnly(){
        return this.readOnlyEnergyStorage;
    }

    public IEnergyStorage getAccuStorage(){
        ItemStack stack = this.inventory.getItem(CarrierMenu.SLOT_BATTERY);
        if(!stack.isEmpty()){
            return stack.getCapability(Capabilities.EnergyStorage.ITEM);
        }
        return null;
    }

    public int getProducedEnergy() {
        return this.producedEnergy;
    }

    public int getWastedEnergy() {
        return this.wastedEnergy;
    }

    public ICarrierModule getCarrierModule(ICarrierModule.CarrierSlot slot){
        ItemStack stack = this.inventory.getItem(slot.ordinal());
        if(!stack.isEmpty() && stack.getItem() instanceof ICarrierModule){
            return (ICarrierModule) stack.getItem();
        }
        return null;
    }

    public float getDepletion(ICarrierModule.CarrierSlot slot){
        float depletion = 0.0F;
        for(int id = 0; id <= 5; id++){
            ItemStack stack = this.inventory.getItem(id);
            if(stack.getItem() instanceof ICarrierModule){
                depletion += ((ICarrierModule) stack.getItem()).getDepletion(this, slot, ICarrierModule.CarrierSlot.values()[id], this.getSyncData(slot));
            }
        }
        return depletion;
    }

    public boolean isDepleted(ICarrierModule.CarrierSlot slot){
        ItemStack stack = this.inventory.getItem(slot.ordinal());
        if(!stack.isEmpty() && stack.getItem() instanceof ICarrierModule){
            return ((ICarrierModule) stack.getItem()).isDepleted(stack);
        }
        return false;
    }

    public boolean isModuleInstalled(ICarrierModule.CarrierSlot moduleSlot){
        switch (moduleSlot) {
            case SOLAR: case SOLAR_UPGRADE: {
                return !this.inventory.getItem(CarrierMenu.SLOT_SOLAR).isEmpty();
            }
            case CORE: case CORE_UPGRADE: {
                return !this.inventory.getItem(CarrierMenu.SLOT_CORE).isEmpty();
            }
            case GROUND: case GROUND_UPGRADE: {
                return !this.inventory.getItem(CarrierMenu.SLOT_GROUND).isEmpty();
            }
        }
        return false;
    }

    public ModuleGrade getGradeForSlot(ICarrierModule.CarrierSlot slot){
        ICarrierModule carrierModule = this.getCarrierModule(slot);
        if(carrierModule != null){
            return carrierModule.getGrade();
        }
        return ModuleGrade.NONE;
    }

    public SynchroniseModuleData getSyncData(ICarrierModule.CarrierSlot slot){
        if(this.isModuleInstalled(slot)){
            SynchroniseModuleData data = this.syncDataMap.get(slot);
            if(data == null){
                data = new SynchroniseModuleData();
                this.syncDataMap.put(slot, data);
            }
            return data;
        } else {
            this.syncDataMap.remove(slot);
            return null;
        }
    }

    public List<EnergyProduction> getProductionForSlot(ICarrierModule.CarrierSlot otherSlot){
        List<EnergyProduction> productions = new ArrayList<>();
        for (ICarrierModule.CarrierSlot mySlot : ICarrierModule.CarrierSlot.values()) {
            if (!this.isDepleted(mySlot)) {
                ICarrierModule carrierModule = this.getCarrierModule(mySlot);
                if (carrierModule != null) {
                    productions.addAll(carrierModule.produceEnergy(this.getLevel(), this.getBlockPos(), this, mySlot, otherSlot , this.getSyncData(otherSlot)));
                }
            }
        }
        return productions;
    }

    public List<EnergyPenalty> getPenaltiesForSlot(ICarrierModule.CarrierSlot otherSlot){
        List<EnergyPenalty> penalties = new ArrayList<>();
        for (ICarrierModule.CarrierSlot mySlot : ICarrierModule.CarrierSlot.values()) {
            if (!this.isDepleted(mySlot)) {
                ICarrierModule carrierModule = this.getCarrierModule(mySlot);
                if (carrierModule != null) {
                    penalties.addAll(carrierModule.penaltyEnergy(this.getLevel(), this.getBlockPos(), this, mySlot, otherSlot, this.getSyncData(otherSlot)));
                }
            }
        }
        return penalties;
    }
    
    public List<EnergyBoost> getBoostsForSlot(ICarrierModule.CarrierSlot otherSlot){
        List<EnergyBoost> boosts = new ArrayList<>();
        for (ICarrierModule.CarrierSlot mySlot : ICarrierModule.CarrierSlot.values()) {
            if (!this.isDepleted(mySlot)) {
                ICarrierModule carrierModule = this.getCarrierModule(mySlot);
                if (carrierModule != null) {
                    boosts.addAll(carrierModule.boostEnergy(this.getLevel(), this.getBlockPos(), this, mySlot, otherSlot, this.getSyncData(otherSlot)));
                }
            }
        }
        return boosts;
    }
    
    public int getEnergyForSlotOnly(ICarrierModule.CarrierSlot responderSlot){
        return this.getCarrierModule(responderSlot).produceEnergy(this.getLevel(), this.getBlockPos(), this, responderSlot, responderSlot, this.getSyncData(responderSlot)).stream().mapToInt(EnergyProduction::getEnergy).sum();
    }

    public void onBlockUpdate(){
        Level level = this.getLevel();
        if (level != null) {
            BlockCarrier.INSTANCE.updateFromTile(level, this.getBlockPos());
        }
    }

    private void writeNBT(CompoundTag nbt, HolderLookup.Provider provider){
        CompoundTag syncTag = new CompoundTag();
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> synchroniseModuleData.use(compoundNBT -> syncTag.put(slot.name(), compoundNBT)));
        nbt.put("sync", syncTag);

        nbt.putInt("produced_energy", this.producedEnergy);
        nbt.putInt("wasted_energy", this.wastedEnergy);
        
        nbt.put("energy_storage", this.energyStorage.serializeNBT(provider));
    
        ListTag inv = new ListTag();
        for(int i = 0; i < this.inventory.getContainerSize(); i++){
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                inv.add(itemstack.save(provider, compoundtag));
            }
        }
        nbt.put("inventory", inv);
    }

    private void readNBT(CompoundTag nbt, HolderLookup.Provider lookupProvider){
        CompoundTag syncTag = nbt.getCompound("sync");
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> {
            synchroniseModuleData.set(syncTag.getCompound(slot.name()));
        });

        this.producedEnergy = nbt.getInt("produced_energy");
        this.wastedEnergy = nbt.getInt("wasted_energy");
    
        this.energyStorage.deserializeNBT(lookupProvider, nbt.get("energy_storage"));
    
        ListTag inv = nbt.getList("inventory", Tag.TAG_COMPOUND);
        for(int i = 0; i < this.inventory.getContainerSize(); i++){
            CompoundTag compoundtag = inv.getCompound(i);
            if(!compoundtag.isEmpty()){
                int slotId = compoundtag.getInt("Slot");
                if (slotId < this.inventory.getContainerSize()) {
                    this.inventory.setItem(slotId, ItemStack.parse(lookupProvider, compoundtag).orElse(ItemStack.EMPTY));
                }
            }
        }
    }


    public void dropContents() {
        Containers.dropContents(this.level, this.getBlockPos(), this.inventory);
    }
}
