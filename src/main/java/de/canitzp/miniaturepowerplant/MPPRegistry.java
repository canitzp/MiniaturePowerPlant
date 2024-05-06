package de.canitzp.miniaturepowerplant;

import com.mojang.serialization.Codec;
import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import de.canitzp.miniaturepowerplant.upgrades.EfficiencyUpgrade;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MPPRegistry {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MiniaturePowerPlant.MODID);
    public static final Holder<CreativeModeTab> TAB = TABS.register("tab", MPPTab::create);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MiniaturePowerPlant.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MiniaturePowerPlant.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MiniaturePowerPlant.MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, MiniaturePowerPlant.MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPE = DeferredRegister.createDataComponents(MiniaturePowerPlant.MODID);

    public static void init(IEventBus bus){
        DATA_COMPONENT_TYPE.register(bus);
        BLOCKS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        MENU_TYPES.register(bus);
        ITEMS.register(bus);
        TABS.register(bus);
    }

    public static final Supplier<Block> CARRIER = BLOCKS.register("carrier", () -> BlockCarrier.INSTANCE);
    public static final Supplier<Item> CARRIER_ITEM = ITEMS.register("carrier", () -> BlockCarrier.INSTANCE_ITEM);

    public static final Supplier<Item> SOLAR_BASIC = ITEMS.register("solar_module", () -> SolarModule.SOLAR_MODULE_WOOD);
    public static final Supplier<Item> SOLAR_STONE = ITEMS.register("solar_module_stone", () -> SolarModule.SOLAR_MODULE_STONE);
    public static final Supplier<Item> SOLAR_IRON = ITEMS.register("solar_module_iron", () -> SolarModule.SOLAR_MODULE_IRON);
    public static final Supplier<Item> SOLAR_GOLD = ITEMS.register("solar_module_gold", () -> SolarModule.SOLAR_MODULE_GOLD);
    public static final Supplier<Item> SOLAR_LAPIS = ITEMS.register("solar_module_lapis", () -> SolarModule.SOLAR_MODULE_LAPIS);
    public static final Supplier<Item> SOLAR_REDSTONE = ITEMS.register("solar_module_redstone", () -> SolarModule.SOLAR_MODULE_REDSTONE);
    public static final Supplier<Item> SOLAR_DIAMOND = ITEMS.register("solar_module_diamond", () -> SolarModule.SOLAR_MODULE_DIAMOND);
    public static final Supplier<Item> SOLAR_NETHERITE = ITEMS.register("solar_module_netherite", () -> SolarModule.SOLAR_MODULE_NETHERITE);

    public static final Supplier<Item> TEMP_MODULE_BASIC = ITEMS.register("temperature_module_basic", () -> TemperatureModule.TEMP_MODULE_BASIC);
    public static final Supplier<Item> WATER_MODULE_BASIC = ITEMS.register("water_module_basic", () -> WaterModule.WATER_MODULE_BASIC);
    public static final Supplier<Item> WIND_MODULE_BASIC = ITEMS.register("wind_module_basic", () -> WindModule.WIND_MODULE_BASIC);
    public static final Supplier<Item> ECO_UPGRADE = ITEMS.register("eco_upgrade", () -> EcoUpgrade.ECO_UPGRADE);
    public static final Supplier<Item> ECO_PLUS_UPGRADE = ITEMS.register("eco_plus_upgrade", () -> EcoUpgrade.ECO_PLUS_UPGRADE);
    public static final Supplier<Item> EFFICIENCY_UPGRADE_BASIC = ITEMS.register("efficiency_upgrade", () -> EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC);
    public static final Supplier<Item> ACCUMULATOR_BASIC = ITEMS.register("accumulator_basic", () -> AccumulatorItem.ACCUMULATOR_BASIC);
    public static final Supplier<Item> ACCUMULATOR_PLUS = ITEMS.register("accumulator_plus", () -> AccumulatorItem.ACCUMULATOR_PLUS);
    public static final Supplier<Item> ACCUMULATOR_ENHANCED = ITEMS.register("accumulator_enhanced", () -> AccumulatorItem.ACCUMULATOR_ENHANCED);

    public static final Supplier<BlockEntityType<?>> CARRIER_TILE = BLOCK_ENTITY_TYPES.register("carrier", () -> TileCarrier.TYPE);

    public static final Supplier<MenuType<?>> CARRIER_MENU = MENU_TYPES.register("carrier", () -> CarrierMenu.TYPE);

    public static final Supplier<DataComponentType<Integer>> DC_ENERGY = DATA_COMPONENT_TYPE.registerComponentType("energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT).cacheEncoding());
    public static final Supplier<DataComponentType<Float>> DC_DEPLETION = DATA_COMPONENT_TYPE.registerComponentType("depletion", builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT).cacheEncoding());

}
