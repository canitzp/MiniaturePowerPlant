package de.canitzp.miniaturepowerplant;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.EnergyStorage;

public class StackEnergyStorage extends EnergyStorage {

    private final ItemStack stack;

    public StackEnergyStorage(int capacity, ItemStack stack) {
        super(capacity);
        this.stack = stack;
    }

    public StackEnergyStorage(int capacity, int maxTransfer, ItemStack stack) {
        super(capacity, maxTransfer);
        this.stack = stack;
    }

    public StackEnergyStorage(int capacity, int maxReceive, int maxExtract, ItemStack stack) {
        super(capacity, maxReceive, maxExtract);
        this.stack = stack;
    }

    public StackEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy, ItemStack stack) {
        super(capacity, maxReceive, maxExtract, energy);
        this.stack = stack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }
        int energy = this.getEnergyStored();
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) {
            this.setStackEnergy(energy + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }
        int energy = this.getEnergyStored();
        int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            this.setStackEnergy(energy - energyExtracted);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return this.stack.getOrCreateTag().getInt("Energy");
    }

    private void setStackEnergy(int energyValue){
        this.stack.getOrCreateTag().putInt("Energy", energyValue);
    }

}
