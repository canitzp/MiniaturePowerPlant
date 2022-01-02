package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;

public class SolarModule extends DepletableItemModule {

    public static final SolarModule SOLAR_MODULE_BASIC = new SolarModule(1.0F, 100_000.0F);

    public SolarModule(float depletion, float maxDepletion) {
        super(new Item.Properties().stacksTo(1), depletion, maxDepletion);
    }

    @Nullable
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.SOLAR};
    }

    @Override
    public void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data) {
        if(!world.isClientSide()){
            // energy production
            // from sunlight
            int calculateEnergy = SolarModule.calculateEnergy(world, pos);
            // multiply to reduce the max created energy from 15 to 10
            calculateEnergy = Math.round(calculateEnergy * (10F/15F));
    
            ListTag listEnergyProduction = new ListTag();
            if(calculateEnergy > 0){
                listEnergyProduction.add(EnergyProduction.toNBT(calculateEnergy, "item.miniaturepowerplant.solar_module.production.brightness"));
            }
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PRODUCTION, listEnergyProduction));

            // energy penalty
            ListTag listEnergyPenalty = new ListTag();
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
    
    // copied from DaylightDetectorBlock#updateSignalStrength
    // return value is between 0 and 15
    private static int calculateEnergy(Level level, BlockPos pos) {
        int brightness = level.getBrightness(LightLayer.SKY, pos) - level.getSkyDarken();
        float sunAngle = level.getSunAngle(1.0F);
        if (brightness > 0) {
            float f1 = sunAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
            sunAngle += (f1 - sunAngle) * 0.2F;
            brightness = Math.round((float)brightness * Mth.cos(sunAngle));
        }
        return Mth.clamp(brightness, 0, 15);
    }

}
