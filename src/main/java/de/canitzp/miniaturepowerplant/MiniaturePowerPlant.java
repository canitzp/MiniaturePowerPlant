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
