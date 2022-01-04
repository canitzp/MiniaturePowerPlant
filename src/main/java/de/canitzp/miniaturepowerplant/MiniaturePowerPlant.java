package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SolarModule;
import de.canitzp.miniaturepowerplant.modules.TemperatureModule;
import de.canitzp.miniaturepowerplant.modules.WaterModule;
import de.canitzp.miniaturepowerplant.modules.WindModule;
import de.canitzp.miniaturepowerplant.upgrades.EcoUpgrade;
import de.canitzp.miniaturepowerplant.upgrades.EfficiencyUpgrade;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MiniaturePowerPlant.MODID)
@Mod.EventBusSubscriber
public class MiniaturePowerPlant {

    public static final String MODID = "miniaturepowerplant";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public MiniaturePowerPlant() {
        System.out.println("Loading MPP");

        this.register();

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINER_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        // used to call the static initializer, which automatically ads the tab into the game
        MPPTab.create();

        DistExecutor.runWhenOn(Dist.CLIENT, () -> this::registerClient);
    }

    private void register(){
        BLOCKS.register("carrier", () -> BlockCarrier.INSTANCE);

        ITEMS.register("carrier", () -> BlockCarrier.INSTANCE_ITEM);
        ITEMS.register("solar_module", () -> SolarModule.SOLAR_MODULE_BASIC);
        ITEMS.register("temperature_module_basic", () -> TemperatureModule.TEMP_MODULE_BASIC);
        ITEMS.register("water_module_basic", () -> WaterModule.WATER_MODULE_BASIC);
        ITEMS.register("wind_module_basic", () -> WindModule.WIND_MODULE_BASIC);
        ITEMS.register("eco_upgrade", () -> EcoUpgrade.ECO_UPGRADE);
        ITEMS.register("eco_plus_upgrade", () -> EcoUpgrade.ECO_PLUS_UPGRADE);
        ITEMS.register("efficiency_upgrade", () -> EfficiencyUpgrade.EFFICIENCY_UPGRADE_BASIC);
        ITEMS.register("accumulator_basic", () -> AccumulatorItem.ACCUMULATOR_BASIC);
        ITEMS.register("accumulator_plus", () -> AccumulatorItem.ACCUMULATOR_PLUS);
        ITEMS.register("accumulator_enhanced", () -> AccumulatorItem.ACCUMULATOR_ENHANCED);

        TILE_TYPES.register("carrier", () -> TileCarrier.TYPE);

        CONTAINER_TYPES.register("carrier", () -> CarrierMenu.TYPE);
    }

    private void registerClient(){
        MenuScreens.register(CarrierMenu.TYPE, ScreenCarrier::new);
    }

}
