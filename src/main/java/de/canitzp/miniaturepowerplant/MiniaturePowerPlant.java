package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.carrier.CarrierMenu;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MiniaturePowerPlant.MODID)
@Mod.EventBusSubscriber
public class MiniaturePowerPlant {

    public static final String MODID = "miniaturepowerplant";

    public MiniaturePowerPlant() {
        System.out.println("Loading MPP");

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        MPPRegistry.init(bus);

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            this.registerClient();
            return null;
        });
    }

    private void registerClient(){
        MenuScreens.register(CarrierMenu.TYPE, ScreenCarrier::new);
    }

}
