package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MPPRegistry;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import de.canitzp.miniaturepowerplant.upgrades.EfficiencyUpgrade;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class MPPItemModelGenerator extends ItemModelProvider {
    
    public MPPItemModelGenerator(DataGenerator generator, ExistingFileHelper helper){
        super(generator.getPackOutput(), MiniaturePowerPlant.MODID, helper);
    }
    
    @Override
    protected void registerModels(){
        // modules
        this.singleTexture(SolarModule.SOLAR_MODULE_WOOD);
        this.singleTexture(SolarModule.SOLAR_MODULE_STONE);
        this.singleTexture(SolarModule.SOLAR_MODULE_GOLD);
        this.singleTexture(SolarModule.SOLAR_MODULE_IRON);
        this.singleTexture(SolarModule.SOLAR_MODULE_LAPIS);
        this.singleTexture(SolarModule.SOLAR_MODULE_REDSTONE);
        this.singleTexture(SolarModule.SOLAR_MODULE_DIAMOND);
        this.singleTexture(SolarModule.SOLAR_MODULE_NETHERITE);
        this.singleTexture(TemperatureModule.TEMP_MODULE_BASIC);
        this.singleTexture(WaterModule.WATER_MODULE_BASIC);
        this.singleTexture(WindModule.WIND_MODULE_BASIC);
        // upgrades
        this.singleTexture(EcoUpgrade.ECO_UPGRADE);
        this.singleTexture(EcoUpgrade.ECO_PLUS_UPGRADE);
        this.singleTexture(EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC);
        // accumulator
        this.singleTexture(AccumulatorItem.ACCUMULATOR_BASIC);
        this.singleTexture(AccumulatorItem.ACCUMULATOR_PLUS);
        this.singleTexture(AccumulatorItem.ACCUMULATOR_ENHANCED);

        this.withExistingParent(BuiltInRegistries.BLOCK.getKey(MPPRegistry.CARRIER.get()).getPath(), new ResourceLocation(MiniaturePowerPlant.MODID, "block/carrier/base"));
    }
    
    private void singleTexture(Item item){
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        singleTexture(key.getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + key.getPath()));
    }
}
