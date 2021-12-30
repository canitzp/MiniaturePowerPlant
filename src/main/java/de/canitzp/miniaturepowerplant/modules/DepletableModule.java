package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public interface DepletableModule extends ICarrierModule {

    String NBT_KEY_DEPLETION = "depletion";
    String NBT_KEY_PRODUCTION = "production";
    String NBT_KEY_PENALTY = "penalty";

    float getDepletion();

    float getMaxDepletion();

    @Override
    default boolean isDepleted(ItemStack stack) {
        return DepletableModule.getCurrentDepletion(stack) >= this.getMaxDepletion();
    }

    static float getCurrentDepletion(ItemStack stack){
        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(NBT_KEY_DEPLETION, Tag.TAG_FLOAT)) {
            return stack.getTag().getFloat(NBT_KEY_DEPLETION);
        }
        return 0.0F;
    }

    // calc result => 1.0 = 100% depleted -> broken
    default float getDepletionPercentage(ItemStack stack){
        return DepletableModule.getCurrentDepletion(stack) / this.getMaxDepletion();
    }

    @Override
    default List<EnergyProduction> produceEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        List<EnergyProduction> reasons = new ArrayList<>();
        if(mySlot.equals(otherSlot)){
            data.use(nbt -> {
                if (nbt.contains(NBT_KEY_PRODUCTION, Tag.TAG_LIST)) {
                    nbt.getList(NBT_KEY_PRODUCTION, Tag.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundTag).map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> {
                        reasons.add(new EnergyProduction(compoundNBT));
                    });
                }
            });
        }
        return reasons;
    }

    @Override
    default List<EnergyPenalty> penaltyEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        List<EnergyPenalty> reasons = new ArrayList<>();
        if(mySlot.equals(otherSlot)){
            data.use(nbt -> {
                if (nbt.contains(NBT_KEY_PENALTY, Tag.TAG_LIST)) {
                    nbt.getList(NBT_KEY_PENALTY, Tag.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundTag).map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> {
                        reasons.add(new EnergyPenalty(compoundNBT));
                    });
                }
            });
        }
        return reasons;
    }

}
