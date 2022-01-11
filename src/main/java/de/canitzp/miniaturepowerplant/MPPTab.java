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
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MPPTab extends CreativeModeTab{

    public static final MPPTab INSTANCE = new MPPTab();

    private static final ItemStack ICON = new ItemStack(BlockCarrier.INSTANCE_ITEM);

    private MPPTab() {
        super(MiniaturePowerPlant.MODID);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static MPPTab create(){
        return INSTANCE;
    }

    @Nonnull
    @Override
    public ItemStack makeIcon() {
        return ICON;
    }

    @Override
    public void fillItemList(NonNullList<ItemStack> list) {
        // blocks
        list.add(BlockCarrier.INSTANCE_ITEM.getDefaultInstance());
        fillEmpty(list, 8);

        // modules
        list.add(SolarModule.SOLAR_MODULE_WOOD.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_STONE.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_IRON.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_GOLD.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_LAPIS.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_REDSTONE.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_DIAMOND.getDefaultInstance());
        list.add(SolarModule.SOLAR_MODULE_NETHERITE.getDefaultInstance());
        fillEmpty(list, 1);
        
        list.add(TemperatureModule.TEMP_MODULE_BASIC.getDefaultInstance());
        list.add(WaterModule.WATER_MODULE_BASIC.getDefaultInstance());
        list.add(WindModule.WIND_MODULE_BASIC.getDefaultInstance());
        fillEmpty(list, 6);

        // upgrades
        list.add(EcoUpgrade.ECO_UPGRADE.getDefaultInstance());
        list.add(EcoUpgrade.ECO_PLUS_UPGRADE.getDefaultInstance());
        list.add(EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC.getDefaultInstance());
        fillEmpty(list, 6);

        // accumulators
        list.add(AccumulatorItem.ACCUMULATOR_BASIC.getDefaultInstance());
        list.add(AccumulatorItem.ACCUMULATOR_PLUS.getDefaultInstance());
        list.add(AccumulatorItem.ACCUMULATOR_ENHANCED.getDefaultInstance());
    }

    private static void fillEmpty(NonNullList<ItemStack> list, int amount){
        for(int i = 0; i < amount; i++){
            list.add(ItemStack.EMPTY);
        }
    }

}
