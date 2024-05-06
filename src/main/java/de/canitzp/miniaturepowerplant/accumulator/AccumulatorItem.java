package de.canitzp.miniaturepowerplant.accumulator;

import de.canitzp.miniaturepowerplant.StackEnergyStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class AccumulatorItem extends Item{

    public static final AccumulatorItem ACCUMULATOR_BASIC = new AccumulatorItem(10000, 100);
    public static final AccumulatorItem ACCUMULATOR_PLUS = new AccumulatorItem(30000, 1000);
    public static final AccumulatorItem ACCUMULATOR_ENHANCED = new AccumulatorItem(50000, 2000);

    public final int capacity, transfer;

    public AccumulatorItem(int capacity, int transfer) {
        super(new Properties().stacksTo(1));
        this.capacity = capacity;
        this.transfer = transfer;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> text, TooltipFlag flag) {
        if(this.transfer > 0 && this.transfer < this.capacity){
            text.add(Component.translatable("item.miniaturepowerplant.accumulator.desc.transfer", this.transfer).withStyle(ChatFormatting.GRAY));
        }

        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage != null) {
            text.add(Component.translatable("item.miniaturepowerplant.accumulator.desc.stored", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()).withStyle(ChatFormatting.GRAY));
        }
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        return energyStorage == null || energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage != null){
            return Math.round((energyStorage.getEnergyStored() * 13.0F) / (energyStorage.getMaxEnergyStored() * 1.0F));
        }
        return super.getBarWidth(stack);
    }

}
