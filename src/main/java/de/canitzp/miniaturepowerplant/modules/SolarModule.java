package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import javax.annotation.Nullable;
import java.util.List;

public class SolarModule extends DepletableItemModule {

    public static final SolarModule SOLAR_MODULE_WOOD = new SolarModule(1.0F, 100_000.0F, 1.0F);
    public static final SolarModule SOLAR_MODULE_STONE = new SolarModule(1.0F, 125_000.0F, 1.25F);
    public static final SolarModule SOLAR_MODULE_IRON = new SolarModule(1.0F, 175_000.0F, 1.75F);
    public static final SolarModule SOLAR_MODULE_GOLD = new SolarModule(1.0F, 100_000.0F, 3.0F);
    public static final SolarModule SOLAR_MODULE_LAPIS = new SolarModule(0.8F, 175_000.0F, 2.0F);
    public static final SolarModule SOLAR_MODULE_REDSTONE = new SolarModule(0.9F, 175_000.0F, 2.25F);
    public static final SolarModule SOLAR_MODULE_DIAMOND = new SolarModule(1.0F, 250_000.0F, 2F);
    public static final SolarModule SOLAR_MODULE_NETHERITE = new SolarModule(1.0F, 350_000.0F, 2F);

    private final float energyMultiplier;
    
    public SolarModule(float depletion, float maxDepletion, float energyMultiplier) {
        super(new Item.Properties().stacksTo(1), depletion, maxDepletion);
        this.energyMultiplier = energyMultiplier;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
        super.appendHoverText(stack, level, text, flag);
        
        text.add(Component.translatable("item.miniaturepowerplant.solar_module.desc.energy_multiplier", String.format("%.2f", this.energyMultiplier * 100.0F)).withStyle(ChatFormatting.GRAY));
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
                // multiply by solar factor
                calculateEnergy *= this.energyMultiplier;
                
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
