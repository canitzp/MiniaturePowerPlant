package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(MiniaturePowerPlant.MODID)
public class MiniaturePowerPlant {

    public static final String MODID = "miniaturepowerplant";

    public MiniaturePowerPlant(IEventBus modEventBus, ModContainer modContainer) {
        System.out.println("Loading MPP");

        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerCapabilities);

        MPPRegistry.init(modEventBus);
    }

    private void registerScreens(RegisterMenuScreensEvent event){
        event.register(CarrierMenu.TYPE, ScreenCarrier::new);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileCarrier.TYPE, (tile, direction) -> tile.sw);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TileCarrier.TYPE, (tile, direction) -> tile.getEnergyStorageReadOnly());

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, unused) -> {
            return new StackEnergyStorage(((AccumulatorItem) stack.getItem()).capacity, ((AccumulatorItem) stack.getItem()).transfer, stack);
        }, MPPRegistry.ACCUMULATOR_BASIC.get(), MPPRegistry.ACCUMULATOR_PLUS.get(), MPPRegistry.ACCUMULATOR_ENHANCED.get());
    }

}
