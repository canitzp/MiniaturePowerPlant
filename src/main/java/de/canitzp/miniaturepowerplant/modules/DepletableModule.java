package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MPPRegistry;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyBoost;
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

    String NBT_KEY_PRODUCTION = "production";
    String NBT_KEY_PENALTY = "penalty";
    String NBT_KEY_BOOST = "boost";

    float getDepletion();

    float getMaxDepletion();

    @Override
    default boolean isDepleted(ItemStack stack) {
        return DepletableModule.getCurrentDepletion(stack) >= this.getMaxDepletion();
    }

    static float getCurrentDepletion(ItemStack stack){
        return stack.getOrDefault(MPPRegistry.DC_DEPLETION, 0F);
    }

    // calc result => 1.0 = 100% depleted -> broken
    default float getDepletionPercentage(ItemStack stack){
        return DepletableModule.getCurrentDepletion(stack) / this.getMaxDepletion();
    }

    @Override
    default List<EnergyProduction> produceEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        List<EnergyProduction> productions = new ArrayList<>();
        if(mySlot.equals(otherSlot)){
            data.use(nbt -> {
                if (nbt.contains(NBT_KEY_PRODUCTION, Tag.TAG_LIST)) {
                    nbt.getList(NBT_KEY_PRODUCTION, Tag.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundTag).map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> {
                        productions.add(new EnergyProduction(compoundNBT));
                    });
                }
            });
        }
        return productions;
    }

    @Override
    default List<EnergyPenalty> penaltyEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        List<EnergyPenalty> penalties = new ArrayList<>();
        if(mySlot.equals(otherSlot)){
            data.use(nbt -> {
                if (nbt.contains(NBT_KEY_PENALTY, Tag.TAG_LIST)) {
                    nbt.getList(NBT_KEY_PENALTY, Tag.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundTag).map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> {
                        penalties.add(new EnergyPenalty(compoundNBT));
                    });
                }
            });
        }
        return penalties;
    }
    
    @Override
    default List<EnergyBoost> boostEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data){
        List<EnergyBoost> boosts = new ArrayList<>();
        if(mySlot.equals(otherSlot)){
            data.use(nbt -> {
                if (nbt.contains(NBT_KEY_BOOST, Tag.TAG_LIST)) {
                    nbt.getList(NBT_KEY_BOOST, Tag.TAG_COMPOUND).stream().filter(inbt -> inbt instanceof CompoundTag).map(inbt -> (CompoundTag)inbt).forEach(compoundNBT -> {
                        boosts.add(new EnergyBoost(compoundNBT));
                    });
                }
            });
        }
        return boosts;
    }

}
