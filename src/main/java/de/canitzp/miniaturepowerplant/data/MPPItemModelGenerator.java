package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import de.canitzp.miniaturepowerplant.upgrades.EfficiencyUpgrade;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MPPItemModelGenerator extends ItemModelProvider {
    
    public MPPItemModelGenerator(DataGenerator generator, ExistingFileHelper helper){
        super(generator, MiniaturePowerPlant.MODID, helper);
    }
    
    @Override
    protected void registerModels(){
        // modules
        singleTexture(SolarModule.SOLAR_MODULE_WOOD);
        singleTexture(SolarModule.SOLAR_MODULE_STONE);
        singleTexture(SolarModule.SOLAR_MODULE_GOLD);
        singleTexture(SolarModule.SOLAR_MODULE_IRON);
        singleTexture(SolarModule.SOLAR_MODULE_LAPIS);
        singleTexture(SolarModule.SOLAR_MODULE_REDSTONE);
        singleTexture(SolarModule.SOLAR_MODULE_DIAMOND);
        singleTexture(SolarModule.SOLAR_MODULE_NETHERITE);
        singleTexture(TemperatureModule.TEMP_MODULE_BASIC);
        singleTexture(WaterModule.WATER_MODULE_BASIC);
        singleTexture(WindModule.WIND_MODULE_BASIC);
        // upgrades
        singleTexture(EcoUpgrade.ECO_UPGRADE);
        singleTexture(EcoUpgrade.ECO_PLUS_UPGRADE);
        singleTexture(EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC);
        // accumulator
        singleTexture(AccumulatorItem.ACCUMULATOR_BASIC);
        singleTexture(AccumulatorItem.ACCUMULATOR_PLUS);
        singleTexture(AccumulatorItem.ACCUMULATOR_ENHANCED);
    }
    
    private void singleTexture(Item item){
        singleTexture(item.getRegistryName().getPath(), mcLoc("item/handheld"), "layer0", modLoc("items/" + item.getRegistryName().getPath()));
    }
}
