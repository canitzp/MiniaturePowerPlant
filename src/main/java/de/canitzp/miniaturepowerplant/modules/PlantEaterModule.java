package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.ModuleGrade;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PlantEaterModule extends DepletableItemModule{
    
    public PlantEaterModule(Properties properties, float depletion, float maxDepletion){
        super(properties, depletion, maxDepletion);
    }
    
    @Override
    public CarrierSlot[] validSlots(){
        return new CarrierSlot[]{CarrierSlot.GROUND};
    }

    @Override
    public ModuleGrade getGrade() {
        return ModuleGrade.NONE;
    }

    @Override
    public boolean tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){
        return false;
    }
}
