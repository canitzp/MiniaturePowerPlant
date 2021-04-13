package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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
        if (!stack.isEmpty() && stack.hasTag() && stack.getTag().contains(NBT_KEY_DEPLETION, Constants.NBT.TAG_FLOAT)) {
            return stack.getTag().getFloat(NBT_KEY_DEPLETION);
        }
        return 0.0F;
    }

    // calc result => 1.0 = 100% depleted -> broken
    default float getDepletionPercentage(ItemStack stack){
        return DepletableModule.getCurrentDepletion(stack) / this.getMaxDepletion();
    }

    @Override
    default List<EnergyProduction> produceEnergy(World world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, SynchroniseModuleData data) {
        List<EnergyProduction> reasons = new ArrayList<>();
        data.use(nbt -> {
            if (nbt.contains(NBT_KEY_PRODUCTION, Constants.NBT.TAG_LIST)) {
                nbt.getList(NBT_KEY_PRODUCTION, Constants.NBT.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundNBT).map(inbt -> (CompoundNBT)inbt).forEach(compoundNBT -> {
                    reasons.add(new EnergyProduction(compoundNBT));
                });
            }
        });
        return reasons;
    }

    @Override
    default List<EnergyPenalty> penaltyEnergy(World world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, SynchroniseModuleData data) {
        List<EnergyPenalty> reasons = new ArrayList<>();
        data.use(nbt -> {
            if (nbt.contains(NBT_KEY_PENALTY, Constants.NBT.TAG_LIST)) {
                nbt.getList(NBT_KEY_PENALTY, Constants.NBT.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundNBT).map(inbt -> (CompoundNBT)inbt).forEach(compoundNBT -> {
                    reasons.add(new EnergyPenalty(compoundNBT));
                });
            }
        });
        return reasons;
    }

}
