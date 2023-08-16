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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MPPTab{

    public static final Component TITLE = Component.translatable("tab." + MiniaturePowerPlant.MODID);

    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .icon(BlockCarrier.INSTANCE_ITEM::getDefaultInstance)
                .title(TITLE)
                .displayItems(MPPTab::fillItemList)
                .build();
    }

    public static void fillItemList(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
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
