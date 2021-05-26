package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TileCarrier extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

    public static final TileEntityType<TileCarrier> TYPE = TileEntityType.Builder.of(TileCarrier::new, BlockCarrier.INSTANCE).build(null);

    private final Inventory inventory = new Inventory(7){
        @Override
        public void setChanged() {
            TileCarrier.this.onBlockUpdate();
            super.setChanged();
        }
    };
    private final InvWrapper wrapper = new InvWrapper(this.inventory);
    private final EnergyStorage energyStorage = new EnergyStorage(20000);

    private int wastedEnergy = 0, producedEnergy = 0;

    private Map<ICarrierModule.CarrierSlot, SynchroniseModuleData> syncDataMap = new HashMap<>();

    public TileCarrier() {
        super(TYPE);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.miniaturepowerplant.carrier");
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player) {
        return ContainerCarrier.createServerContainer(windowId, player, this);
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

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.writeNBT(nbt);
        return new SUpdateTileEntityPacket(this.getBlockPos(), -1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.readNBT(pkt.getTag());
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        super.load(blockState, nbt);
        this.readNBT(nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        this.writeNBT(nbt);
        return super.save(nbt);
    }

    @Override
    public void tick() {
        World level = this.level;
        if(level != null && !level.isClientSide()){

            // sync pulse
            if((level.getGameTime() & 0b111) == 0b0){ // call every eight tick => 2.5 times a second
                for (PlayerEntity player : level.players()) {
                    if(player instanceof ServerPlayerEntity){
                        if(player.distanceToSqr(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D, this.getBlockPos().getZ() + 0.5D) <= 64 && !this.isRemoved() && this.level.getBlockEntity(this.getBlockPos()) == this){
                            ((ServerPlayerEntity) player).connection.send(this.getUpdatePacket());
                        }
                    }
                }
            }

            // modules tick
            for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                if(!this.isDepleted(slot)){
                    ICarrierModule carrierModule = this.getCarrierModule(slot);
                    if(carrierModule != null){
                        carrierModule.tick(level, this.getBlockPos(), this, this.getSyncData(slot));
                    }
                }
            }

            // modules energy production
            if(this.energyStorage.getEnergyStored() < this.energyStorage.getMaxEnergyStored()) {
                List<ICarrierModule.CarrierSlot> hasProducedEnergy = new ArrayList<>();
                List<EnergyProduction> energyProductionReasons = new ArrayList<>();
                List<EnergyPenalty> energyPenaltyReasons = new ArrayList<>();
                for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                    if (!this.isDepleted(slot)) {
                        ICarrierModule carrierModule = this.getCarrierModule(slot);
                        if (carrierModule != null) {
                            List<EnergyProduction> energyProductionReason = carrierModule.produceEnergy(this.getLevel(), this.getBlockPos(), this, slot, slot, this.getSyncData(slot));
                            if (!energyProductionReason.isEmpty()) {
                                energyProductionReasons.addAll(energyProductionReason);
                                energyPenaltyReasons.addAll(carrierModule.penaltyEnergy(this.getLevel(), this.getBlockPos(), this, slot, slot, this.getSyncData(slot)));

                                hasProducedEnergy.add(slot);
                            }
                        }
                    }
                }
                int completeEnergyProduction = energyProductionReasons.stream().mapToInt(EnergyProduction::getEnergy).sum();
                double completeEnergyPenaltyMultiplier = energyPenaltyReasons.stream().mapToDouble(EnergyPenalty::getMultiplier).sum();
                if (completeEnergyPenaltyMultiplier >= 1D) {
                    completeEnergyProduction = 0;
                } else {
                    completeEnergyProduction = Math.toIntExact(Math.round(completeEnergyProduction * (1.0D - completeEnergyPenaltyMultiplier)));
                }

                if (completeEnergyProduction > 0) {
                    this.producedEnergy = completeEnergyProduction;
                    this.wastedEnergy = completeEnergyProduction - this.energyStorage.receiveEnergy(completeEnergyProduction, false);
                } else {
                    this.producedEnergy = 0;
                    this.wastedEnergy = 0;
                }

                // modules depletion
                for (ICarrierModule.CarrierSlot slot : ICarrierModule.CarrierSlot.values()) {
                    if (!this.isDepleted(slot) && hasProducedEnergy.contains(slot)) {
                        ItemStack stack = this.inventory.getItem(slot.ordinal());
                        if (!stack.isEmpty() && stack.getItem() instanceof ICarrierModule) {
                            stack.addTagElement("depletion", FloatNBT.valueOf(this.getDepletion(slot) + stack.getOrCreateTag().getFloat("depletion")));
                        }
                    }
                }
            } else {
                this.producedEnergy = 0;
                this.wastedEnergy = 0;
            }

            // move energy to accu/battery
            if(this.energyStorage.getEnergyStored() > 0){
                this.getAccuStorage().ifPresent(energyStorage -> {
                    this.energyStorage.extractEnergy(
                            energyStorage.receiveEnergy(
                                    this.energyStorage.extractEnergy(this.energyStorage.getEnergyStored(), true),
                                    false),
                            false);
                });
            }
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public EnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public LazyOptional<IEnergyStorage> getAccuStorage(){
        ItemStack stack = this.inventory.getItem(ContainerCarrier.SLOT_BATTERY);
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
                return !this.inventory.getItem(ContainerCarrier.SLOT_SOLAR).isEmpty();
            }
            case CORE: case CORE_UPGRADE: {
                return !this.inventory.getItem(ContainerCarrier.SLOT_CORE).isEmpty();
            }
            case GROUND: case GROUND_UPGRADE: {
                return !this.inventory.getItem(ContainerCarrier.SLOT_GROUND).isEmpty();
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

    private void onBlockUpdate(){
        World level = this.getLevel();
        if (level != null) {
            BlockCarrier.INSTANCE.updateFromTile(level, this.getBlockPos());
        }
    }

    private void writeNBT(CompoundNBT nbt){
        CompoundNBT syncTag = new CompoundNBT();
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> synchroniseModuleData.use(compoundNBT -> syncTag.put(slot.name(), compoundNBT)));
        nbt.put("sync", syncTag);

        nbt.putInt("produced_energy", this.producedEnergy);
        nbt.putInt("wasted_energy", this.wastedEnergy);

        this.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(iEnergyStorage -> {
            nbt.put("EnergyCapability", CapabilityEnergy.ENERGY.writeNBT(iEnergyStorage, null));
        });

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(iItemHandler -> {
            nbt.put("IItemHandlerCapability", CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(iItemHandler, null));
        });
    }

    private void readNBT(CompoundNBT nbt){
        CompoundNBT syncTag = nbt.getCompound("sync");
        this.syncDataMap.forEach((slot, synchroniseModuleData) -> {
            synchroniseModuleData.set(syncTag.getCompound(slot.name()));
        });

        this.producedEnergy = nbt.getInt("produced_energy");
        this.wastedEnergy = nbt.getInt("wasted_energy");

        this.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(iEnergyStorage -> {
            CapabilityEnergy.ENERGY.readNBT(iEnergyStorage, null, nbt.get("EnergyCapability"));
        });

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(iItemHandler -> {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(iItemHandler, null, nbt.get("IItemHandlerCapability"));
        });
    }
}
