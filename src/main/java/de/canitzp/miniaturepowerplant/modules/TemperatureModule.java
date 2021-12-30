package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class TemperatureModule extends DepletableItemModule {

    public static final TemperatureModule TEMP_MODULE_BASIC = new TemperatureModule(1.0F, 100_000.0F);

    public TemperatureModule(float depletion, float maxDepletion) {
        super(new Properties().stacksTo(1), depletion, maxDepletion);
    }

    @Nullable
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.CORE, CarrierSlot.GROUND};
    }

    @Override
    public void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data) {
        if(!world.isClientSide()){
            // production
            float adjustedTemperature = world.getBiome(pos).getBaseTemperature();
            ListTag listEnergyProduction = new ListTag();
            listEnergyProduction.add(EnergyProduction.toNBT(Math.round(adjustedTemperature * 10), "item.miniaturepowerplant.temperature_module.production.adjusted_temperature"));
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PRODUCTION, listEnergyProduction));

            // penalty
            ListTag listEnergyPenalty = new ListTag();
            if(world.isRainingAt(pos)){
                listEnergyPenalty.add(EnergyPenalty.toNBT(0.5F, "item.miniaturepowerplant.temperature_module.penalty.rain"));
            }
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PENALTY, listEnergyPenalty));
        }
    }
}
