package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MiniaturePowerPlant.MODID, bus = EventBusSubscriber.Bus.MOD)
public class MPPDataGenerator{
    
    @SubscribeEvent
    public static void runGenerator(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        
        if(event.includeServer()){
            generator.addProvider(true, new MPPRecipeProvider(generator, event.getLookupProvider()));
            generator.addProvider(true, new MPPItemModelGenerator(generator, helper));
            generator.addProvider(true, new MPPBlockModelGenerator(generator, helper));
            generator.addProvider(true, new MPPBlockstateGenerator(generator, helper));
            generator.addProvider(true, new MPPLootTableSub(generator, event.getLookupProvider()));
            generator.addProvider(true, new MPPTagProvider(generator, event.getLookupProvider(), helper));
        }
    }
}
