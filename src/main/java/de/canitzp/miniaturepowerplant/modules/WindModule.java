package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class WindModule extends DepletableItemModule {
    
    public static final WindModule WIND_MODULE_BASIC = new WindModule(1.0F, 100_000.0F, 3);
    
    private final int windRange;
    
    public WindModule(float depletion, float maxDepletion, int windRange){
        super(new Item.Properties().stacksTo(1), depletion, maxDepletion);
        this.windRange = windRange;
    }
    
    @Override
    public CarrierSlot[] validSlots(){
        return new CarrierSlot[]{CarrierSlot.CORE};
    }
    
    @Override
    public void tick(Level level, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){
    
    }
    
    public List<BlockPos> getBlockingBlocks(Level level, BlockPos pos){
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosedStream(pos.offset(-this.windRange, 1, -this.windRange), pos.offset(this.windRange, level.getHeight() - pos.getY(), this.windRange)).forEach(nextPos -> {
            BlockState state = level.getBlockState(nextPos);
            if(!(state.getBlock() instanceof AirBlock) && state.isCollisionShapeFullBlock(level, nextPos)){
                positions.add(nextPos.immutable());
            }
        });
        return positions;
    }
}
