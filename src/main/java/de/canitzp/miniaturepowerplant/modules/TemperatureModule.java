package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TemperatureModule extends DepletableItemModule {

    public static final TemperatureModule TEMP_MODULE_BASIC = new TemperatureModule(1.0F, 100_000.0F);

    public TemperatureModule(float depletion, float maxDepletion) {
        super(new Properties().stacksTo(1), depletion, maxDepletion);
    }

    @Nullable
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.CORE};
    }

    @Override
    public void tick(World world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data) {
        if(!world.isClientSide()){
            // production
            float adjustedTemperature = world.getBiome(pos).getTemperature(pos);
            ListNBT listEnergyProduction = new ListNBT();
            listEnergyProduction.add(EnergyProduction.toNBT(Math.round(adjustedTemperature * 10), "item.miniaturepowerplant.temperature_module.production.adjusted_temperature"));
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PRODUCTION, listEnergyProduction));

            // penalty
            ListNBT listEnergyPenalty = new ListNBT();
            if(world.isRainingAt(pos)){
                listEnergyPenalty.add(EnergyPenalty.toNBT(0.5F, "item.miniaturepowerplant.temperature_module.penalty.rain"));
            }
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PENALTY, listEnergyPenalty));
        }
    }
}
