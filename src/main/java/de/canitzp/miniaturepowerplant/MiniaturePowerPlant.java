package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(MiniaturePowerPlant.MODID)
public class MiniaturePowerPlant {

    public static final String MODID = "miniaturepowerplant";

    public MiniaturePowerPlant() {
        System.out.println("Loading MPP");

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

}
