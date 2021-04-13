package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SolarModule extends DepletableItemModule {

    public static final SolarModule SOLAR_MODULE_BASIC = new SolarModule(1.0F, 100_000.0F);

    public SolarModule(float depletion, float maxDepletion) {
        super(new Properties().stacksTo(1), depletion, maxDepletion);
    }

    @Nullable
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.SOLAR};
    }

    @Override
    public void tick(World world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data) {
        if(!world.isClientSide()){
            // energy production
            int brightness = world.getBrightness(LightType.SKY, tile.getBlockPos()) - world.getSkyDarken();
            // - 4 because the max value is 14 and we want it to be 10.
            // Math.max because the lowest value (because of -4) is -1 and we don't want to consume energy
            int energyFromBrightness = Math.max(0, brightness - 4);

            ListNBT listEnergyProduction = new ListNBT();
            listEnergyProduction.add(EnergyProduction.toNBT(energyFromBrightness, "item.miniaturepowerplant.solar_module.production.brightness"));
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PRODUCTION, listEnergyProduction));

            // energy penalty
            ListNBT listEnergyPenalty = new ListNBT();
            if(world.isRaining()){
                if(world.isThundering()){
                    listEnergyPenalty.add(EnergyPenalty.toNBT(0.85F, "item.miniaturepowerplant.solar_module.penalty.thunder"));
                } else {
                    listEnergyPenalty.add(EnergyPenalty.toNBT(0.5F, "item.miniaturepowerplant.solar_module.penalty.rain"));
                }
            }
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PENALTY, listEnergyPenalty));
        }
    }

}
