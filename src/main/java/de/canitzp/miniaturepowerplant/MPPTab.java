package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import de.canitzp.miniaturepowerplant.upgrades.EfficiencyUpgrade;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MiniaturePowerPlant.MODID)
public class MPPTab{

    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(new ResourceLocation(MiniaturePowerPlant.MODID, "tab"), builder -> {
            builder.icon(BlockCarrier.INSTANCE_ITEM::getDefaultInstance);
            builder.title(Component.translatable(MiniaturePowerPlant.MODID + ".tab"));
            builder.displayItems((parameters, output) -> {
                MPPTab.fillItemList(output);
            });
        });
    }

    public static void fillItemList(CreativeModeTab.Output output) {
        // blocks
        output.accept(BlockCarrier.INSTANCE_ITEM.getDefaultInstance());
        //fillEmpty(list, 8);

        // modules
        output.accept(SolarModule.SOLAR_MODULE_WOOD.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_STONE.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_IRON.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_GOLD.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_LAPIS.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_REDSTONE.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_DIAMOND.getDefaultInstance());
        output.accept(SolarModule.SOLAR_MODULE_NETHERITE.getDefaultInstance());
        //fillEmpty(list, 1);

        output.accept(TemperatureModule.TEMP_MODULE_BASIC.getDefaultInstance());
        output.accept(WaterModule.WATER_MODULE_BASIC.getDefaultInstance());
        output.accept(WindModule.WIND_MODULE_BASIC.getDefaultInstance());
        //fillEmpty(list, 6);

        // upgrades
        output.accept(EcoUpgrade.ECO_UPGRADE.getDefaultInstance());
        output.accept(EcoUpgrade.ECO_PLUS_UPGRADE.getDefaultInstance());
        output.accept(EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC.getDefaultInstance());
        //fillEmpty(list, 6);

        // accumulators
        output.accept(AccumulatorItem.ACCUMULATOR_BASIC.getDefaultInstance());
        output.accept(AccumulatorItem.ACCUMULATOR_PLUS.getDefaultInstance());
        output.accept(AccumulatorItem.ACCUMULATOR_ENHANCED.getDefaultInstance());
    }

    private static void fillEmpty(NonNullList<ItemStack> list, int amount){
        for(int i = 0; i < amount; i++){
            list.add(ItemStack.EMPTY);
        }
    }

}
