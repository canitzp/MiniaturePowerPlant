package de.canitzp.miniaturepowerplant.accumulator;

import de.canitzp.miniaturepowerplant.StackEnergyStorage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AccumulatorItem extends Item {

    public static final AccumulatorItem ACCUMULATOR_BASIC = new AccumulatorItem(10000, 100);
    public static final AccumulatorItem ACCUMULATOR_PLUS = new AccumulatorItem(30000, 1000);
    public static final AccumulatorItem ACCUMULATOR_ENHANCED = new AccumulatorItem(50000, 2000);

    private final int capacity, transfer;

    public AccumulatorItem(int capacity, int transfer) {
        super(new Properties().stacksTo(1));
        this.capacity = capacity;
        this.transfer = transfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        text.add(new TranslationTextComponent("item.miniaturepowerplant.accumulator.desc.capacity", this.capacity).withStyle(TextFormatting.GRAY));
        if(this.transfer > 0 && this.transfer < this.capacity){
            text.add(new TranslationTextComponent("item.miniaturepowerplant.accumulator.desc.transfer", this.transfer).withStyle(TextFormatting.GRAY));
        }

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> {
            text.add(new TranslationTextComponent("item.miniaturepowerplant.accumulator.desc.stored", iEnergyStorage.getEnergyStored(), iEnergyStorage.getMaxEnergyStored()).withStyle(TextFormatting.GRAY));
        });
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(iEnergyStorage -> iEnergyStorage.getEnergyStored() < iEnergyStorage.getMaxEnergyStored()).orElse(true);
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(iEnergyStorage -> 1.0D - (iEnergyStorage.getEnergyStored() / (iEnergyStorage.getMaxEnergyStored() * 1D))).orElse(1D);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ICapabilityProvider() {
            private final StackEnergyStorage storage = new StackEnergyStorage(AccumulatorItem.this.capacity, AccumulatorItem.this.transfer, stack);
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                if(cap == CapabilityEnergy.ENERGY){
                    return LazyOptional.of(() -> this.storage).cast();
                }
                return LazyOptional.empty();
            }
        };
    }
}
