package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.accumulator.AccumulatorItem;
import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(MiniaturePowerPlant.MODID)
public class MiniaturePowerPlant {

    public static final String MODID = "miniaturepowerplant";

    public MiniaturePowerPlant() {
        System.out.println("Loading MPP");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerCapabilities);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MPPRegistry.init(bus);

        if(FMLEnvironment.dist.isClient()){
            this.registerClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void registerClient(){
        MenuScreens.register(CarrierMenu.TYPE, ScreenCarrier::new);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, TileCarrier.TYPE, (tile, direction) -> tile.sw);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, TileCarrier.TYPE, (tile, direction) -> tile.getEnergyStorageReadOnly());

        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, unused) -> {
            return new StackEnergyStorage(((AccumulatorItem) stack.getItem()).capacity, ((AccumulatorItem) stack.getItem()).transfer, stack);
        }, MPPRegistry.ACCUMULATOR_BASIC.get(), MPPRegistry.ACCUMULATOR_PLUS.get(), MPPRegistry.ACCUMULATOR_ENHANCED.get());
    }

}
