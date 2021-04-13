package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class MPPTab extends ItemGroup {

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
        list.add(SolarModule.SOLAR_MODULE_BASIC.getDefaultInstance());
        list.add(TemperatureModule.TEMP_MODULE_BASIC.getDefaultInstance());
        fillEmpty(list, 7);

        // upgrades
        list.add(EcoUpgrade.ECO_UPGRADE.getDefaultInstance());
        list.add(EcoUpgrade.ECO_PLUS_UPGRADE.getDefaultInstance());
        fillEmpty(list, 7);

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
