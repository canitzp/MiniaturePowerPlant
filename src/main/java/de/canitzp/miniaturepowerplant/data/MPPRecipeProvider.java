package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class MPPRecipeProvider extends RecipeProvider{
    
    public MPPRecipeProvider(DataGenerator generator){
        super(generator.getPackOutput());
    }

    /*protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> p_206407_) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(p_206407_).build());
    }*/

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer){
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, BlockCarrier.INSTANCE_ITEM)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('b', Items.IRON_BARS)
            .define('a', AccumulatorItem.ACCUMULATOR_BASIC)
            .pattern("bib")
            .pattern("aba")
            .pattern("bib")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
        
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, AccumulatorItem.ACCUMULATOR_BASIC)
            .define('c', Items.COPPER_INGOT)
            .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
            .define('b', Items.COPPER_BLOCK)
            .pattern(" b ")
            .pattern("crc")
            .pattern("crc")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
    
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, AccumulatorItem.ACCUMULATOR_PLUS)
            .define('c', Items.COPPER_INGOT)
            .define('a', AccumulatorItem.ACCUMULATOR_BASIC)
            .pattern("cac")
            .pattern("cac")
            .pattern("cac")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
    
        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, AccumulatorItem.ACCUMULATOR_ENHANCED)
            .define('c', Items.COPPER_INGOT)
            .define('a', AccumulatorItem.ACCUMULATOR_BASIC)
            .define('b', AccumulatorItem.ACCUMULATOR_PLUS)
            .pattern("cac")
            .pattern("cbc")
            .pattern("cac")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
        
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_WOOD)
            .define('d', Items.DAYLIGHT_DETECTOR)
            .define('l', Tags.Items.GEMS_LAPIS)
            .define('q', Tags.Items.GEMS_QUARTZ)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .define('x', ItemTags.PLANKS)
            .define('c', Items.COPPER_BLOCK)
            .pattern("ldl")
            .pattern("qrq")
            .pattern("xcx")
            .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
            .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_STONE)
                .define('d', Items.DAYLIGHT_DETECTOR)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('x', Tags.Items.STONE)
                .define('c', Items.COPPER_BLOCK)
                .pattern("ldl")
                .pattern("qrq")
                .pattern("xcx")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_IRON)
                .define('d', Items.DAYLIGHT_DETECTOR)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('x', Tags.Items.INGOTS_IRON)
                .define('c', Items.COPPER_BLOCK)
                .pattern("ldl")
                .pattern("qrq")
                .pattern("xcx")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_GOLD)
                .define('d', Items.DAYLIGHT_DETECTOR)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('x', Tags.Items.INGOTS_GOLD)
                .define('c', Items.COPPER_BLOCK)
                .pattern("ldl")
                .pattern("qrq")
                .pattern("xcx")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_LAPIS)
                .define('d', Items.DAYLIGHT_DETECTOR)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('x', Tags.Items.STORAGE_BLOCKS_LAPIS)
                .define('c', Items.COPPER_BLOCK)
                .pattern("ldl")
                .pattern("qrq")
                .pattern("xcx")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_REDSTONE)
                .define('d', Items.DAYLIGHT_DETECTOR)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('x', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('c', Items.COPPER_BLOCK)
                .pattern("ldl")
                .pattern("qrq")
                .pattern("xcx")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_DIAMOND)
                .define('s', SolarModule.SOLAR_MODULE_STONE)
                .define('d', Tags.Items.GEMS_DIAMOND)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('c', Items.COPPER_BLOCK)
                .pattern("sds")
                .pattern("qrq")
                .pattern("dcd")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        ShapedRecipeBuilder
                .shaped(RecipeCategory.MISC, SolarModule.SOLAR_MODULE_NETHERITE)
                .define('s', SolarModule.SOLAR_MODULE_IRON)
                .define('n', Tags.Items.INGOTS_NETHERITE)
                .define('q', Tags.Items.GEMS_QUARTZ)
                .define('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('c', Items.COPPER_BLOCK)
                .pattern("sqs")
                .pattern("qrq")
                .pattern("ncn")
                .unlockedBy("has_copper_block", has(Items.COPPER_BLOCK))
                .save(consumer);
        
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, TemperatureModule.TEMP_MODULE_BASIC)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('c', Items.COPPER_INGOT)
            .define('w', Items.WATER_BUCKET)
            .define('l', Items.LAVA_BUCKET)
            .define('r', Tags.Items.DUSTS_REDSTONE)
            .pattern(" i ")
            .pattern("wcl")
            .pattern("rir")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
        
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, WaterModule.WATER_MODULE_BASIC)
            .define('i', Tags.Items.INGOTS_IRON)
            .define('c', Items.COPPER_INGOT)
            .define('p', ItemTags.PLANKS)
            .define('s', Items.BUCKET)
            .pattern("isi")
            .pattern("sps")
            .pattern("csc")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
    
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, WindModule.WIND_MODULE_BASIC)
            .define('i', Tags.Items.STORAGE_BLOCKS_IRON)
            .define('c', Items.COPPER_INGOT)
            .define('d', ItemTags.WOODEN_PRESSURE_PLATES)
            .define('b', Items.IRON_BARS)
            .pattern("bdb")
            .pattern("did")
            .pattern("cdc")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
        
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EcoUpgrade.ECO_UPGRADE)
            .define('l', ItemTags.LEAVES)
            .define('c', Items.COPPER_INGOT)
            .define('p', ItemTags.PLANKS)
            .define('s', Items.STICK)
            .pattern("lcl")
            .pattern("lll")
            .pattern("sps")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
    
        ShapedRecipeBuilder
            .shaped(RecipeCategory.MISC, EcoUpgrade.ECO_PLUS_UPGRADE)
            .define('l', ItemTags.LEAVES)
            .define('c', Items.COPPER_INGOT)
            .define('p', ItemTags.PLANKS)
            .define('s', EcoUpgrade.ECO_UPGRADE)
            .pattern("lcl")
            .pattern("lll")
            .pattern("sps")
            .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
            .save(consumer);
    }
}
