package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MiniaturePowerPlant.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MPPDataGenerator{
    
    @SubscribeEvent
    public static void runGenerator(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        
        if(event.includeServer()){
            generator.addProvider(true, new MPPRecipeProvider(generator));
            generator.addProvider(true, new MPPItemModelGenerator(generator, helper));
        }
    }
}
