package de.canitzp.miniaturepowerplant.accumulator;

import de.canitzp.miniaturepowerplant.MPPTab;
import de.canitzp.miniaturepowerplant.StackEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AccumulatorItem extends Item{

    public static final AccumulatorItem ACCUMULATOR_BASIC = new AccumulatorItem(10000, 100);
    public static final AccumulatorItem ACCUMULATOR_PLUS = new AccumulatorItem(30000, 1000);
    public static final AccumulatorItem ACCUMULATOR_ENHANCED = new AccumulatorItem(50000, 2000);

    private final int capacity, transfer;

    public AccumulatorItem(int capacity, int transfer) {
        super(new Properties().stacksTo(1).tab(MPPTab.INSTANCE));
        this.capacity = capacity;
        this.transfer = transfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        if(this.transfer > 0 && this.transfer < this.capacity){
            text.add(Component.translatable("item.miniaturepowerplant.accumulator.desc.transfer", this.transfer).withStyle(ChatFormatting.GRAY));
        }

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(iEnergyStorage -> {
            text.add(Component.translatable("item.miniaturepowerplant.accumulator.desc.stored", iEnergyStorage.getEnergyStored(), iEnergyStorage.getMaxEnergyStored()).withStyle(ChatFormatting.GRAY));
        });
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY).map(iEnergyStorage -> iEnergyStorage.getEnergyStored() < iEnergyStorage.getMaxEnergyStored()).orElse(true);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(CapabilityEnergy.ENERGY).resolve().orElse(null);
        if(energyStorage != null){
            return Math.round((energyStorage.getEnergyStored() * 13.0F) / (energyStorage.getMaxEnergyStored() * 1.0F));
        }
        return super.getBarWidth(stack);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
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
