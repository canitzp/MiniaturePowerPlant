package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.stream.Collectors;

public class PlantEaterModule extends DepletableItemModule{
    
    public PlantEaterModule(Properties properties, float depletion, float maxDepletion){
        super(properties, depletion, maxDepletion);
    }
    
    @Override
    public CarrierSlot[] validSlots(){
        return new CarrierSlot[]{CarrierSlot.GROUND};
    }
    
    @Override
    public void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){
    
    }
}
