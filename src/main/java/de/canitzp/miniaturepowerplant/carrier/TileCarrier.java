package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileCarrier extends BlockEntity implements MenuProvider, Nameable{

    public static final BlockEntityType<TileCarrier> TYPE = BlockEntityType.Builder.of(TileCarrier::new, BlockCarrier.INSTANCE).build(null);

    private final SimpleContainer inventory = new SimpleContainer(7){
        @Override
        public void setChanged() {
            TileCarrier.this.onBlockUpdate();
            TileCarrier.this.setChanged();
            super.setChanged();
        }
    };
    private final InvWrapper wrapper = new InvWrapper(this.inventory);
    private final EnergyStorage energyStorage = new EnergyStorage(20000);

    private int wastedEnergy = 0, producedEnergy = 0;

    private Map<ICarrierModule.CarrierSlot, SynchroniseModuleData> syncDataMap = new HashMap<>();

    public TileCarrier(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player){
        return CarrierMenu.createServerContainer(windowId, inv, this);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.wrapper));
        }
        if(cap == CapabilityEnergy.ENERGY){
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> this.energyStorage));
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public CompoundTag getUpdateTag(){
        CompoundTag nbt = new CompoundTag();
        this.writeNBT(nbt);
        return nbt;
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
        this.readNBT(pkt.getTag());
    }
    
    @Override
    public void load(CompoundTag nbt){
        super.load(nbt);
        this.readNBT(nbt);
    }
    
    @Override
    protected void saveAdditional(CompoundTag nbt){
        super.saveAdditional(nbt);
        this.writeNBT(nbt);
    }
    
    @Override
    public Component getDisplayName(){
        return new TextComponent("");
    }
    
    @Override
    public Component getName(){
        return this.getDisplayName();
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, TileCarrier tile) {
        if(level != null && !level.isClientSide()){

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
                List<EnergyProduction> energyProductionReasons = new ArrayList<>();
                List<EnergyPenalty> energyPenaltyReasons = new ArrayList<>();
                for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                    if (!tile.isDepleted(slot)) {
                        ICarrierModule carrierModule = tile.getCarrierModule(slot);
                        if (carrierModule != null) {
                            
                            energyPenaltyReasons.addAll(carrierModule.penaltyEnergy(level, pos, tile, slot, slot, tile.getSyncData(slot)));
                            
                            List<EnergyProduction> energyProductionReason = carrierModule.produceEnergy(level, pos, tile, slot, slot, tile.getSyncData(slot));
                            if (!energyProductionReason.isEmpty()) {
                                energyProductionReasons.addAll(energyProductionReason);
                                hasProducedEnergy.add(slot);
                            }
                        }
                    }
                }
                int completeEnergyProduction = energyProductionReasons.stream().mapToInt(EnergyProduction::getEnergy).sum();
                double completeEnergyPenaltyMultiplier = energyPenaltyReasons.stream().mapToDouble(energyPenalty -> 1.0D - energyPenalty.getMultiplier()).sum();
                if (completeEnergyPenaltyMultiplier >= 1D) {
                    completeEnergyProduction = 0;
                } else if(!energyPenaltyReasons.isEmpty()){
                    completeEnergyProduction = Math.toIntExact(Math.round(completeEnergyProduction * (1.0D - completeEnergyPenaltyMultiplier)));
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
                        if (!stack.isEmpty() && stack.getItem() instanceof ICarrierModule) {
                            stack.addTagElement("depletion", FloatTag.valueOf(tile.getDepletion(slot) + stack.getOrCreateTag().getFloat("depletion")));
                        }
                    }
                }
            } else {
                tile.producedEnergy = 0;
                tile.wastedEnergy = 0;
            }

            // move energy to accu/battery
            if(tile.energyStorage.getEnergyStored() > 0){
                tile.getAccuStorage().ifPresent(energyStorage -> {
                    tile.energyStorage.extractEnergy(
                            energyStorage.receiveEnergy(
                                tile.energyStorage.extractEnergy(tile.energyStorage.getEnergyStored(), true),
                                    false),
                            false);
                });
            }
        }
    }

    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public LazyOptional<IEnergyStorage> getAccuStorage(){
        ItemStack stack = this.inventory.getItem(CarrierMenu.SLOT_BATTERY);
        if(!stack.isEmpty()){
            return stack.getCapability(CapabilityEnergy.ENERGY, null);
        }
        return LazyOptional.empty();
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
    
    public int getEnergyForSlotOnly(ICarrierModule.CarrierSlot responderSlot){
        return this.getCarrierModule(responderSlot).produceEnergy(this.getLevel(), this.getBlockPos(), this, responderSlot, responderSlot, this.getSyncData(responderSlot)).stream().mapToInt(EnergyProduction::getEnergy).sum();
    }

    private void onBlockUpdate(){
        Level level = this.getLevel();
        if (level != null) {
            BlockCarrier.INSTANCE.updateFromTile(level, this.getBlockPos());
        }
    }

    private void writeNBT(CompoundTag nbt){
        CompoundTag syncTag = new CompoundTag();
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> synchroniseModuleData.use(compoundNBT -> syncTag.put(slot.name(), compoundNBT)));
        nbt.put("sync", syncTag);

        nbt.putInt("produced_energy", this.producedEnergy);
        nbt.putInt("wasted_energy", this.wastedEnergy);
        
        nbt.put("energy_storage", this.energyStorage.serializeNBT());
    
        ListTag inv = new ListTag();
        for(int i = 0; i < this.inventory.getContainerSize(); i++){
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putInt("Slot", i);
                itemstack.save(compoundtag);
                inv.add(compoundtag);
            }
        }
        nbt.put("inventory", inv);
    }

    private void readNBT(CompoundTag nbt){
        CompoundTag syncTag = nbt.getCompound("sync");
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> {
            synchroniseModuleData.set(syncTag.getCompound(slot.name()));
        });

        this.producedEnergy = nbt.getInt("produced_energy");
        this.wastedEnergy = nbt.getInt("wasted_energy");
    
        this.energyStorage.deserializeNBT(nbt.get("energy_storage"));
    
        ListTag inv = nbt.getList("inventory", Tag.TAG_COMPOUND);
        for(int i = 0; i < this.inventory.getContainerSize(); i++){
            CompoundTag compoundtag = inv.getCompound(i);
            if(!compoundtag.isEmpty()){
                int slotId = compoundtag.getInt("Slot");
                if (slotId < this.inventory.getContainerSize()) {
                    this.inventory.setItem(slotId, ItemStack.of(compoundtag));
                }
            }
        }
    }
    
    
}
