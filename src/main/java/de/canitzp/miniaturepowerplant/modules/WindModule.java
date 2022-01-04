package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;

public class WindModule extends DepletableItemModule {
    
    public static final WindModule WIND_MODULE_BASIC = new WindModule(1.0F, 100_000.0F);
    
    private final int
    
    public WindModule(float depletion, float maxDepletion){
        super(new Item.Properties().stacksTo(1), depletion, maxDepletion);
    }
    
    @Override
    public CarrierSlot[] validSlots(){
        return new CarrierSlot[]{CarrierSlot.CORE};
    }
    
    @Override
    public void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){
        getBlockCountInRange(world, pos);
    }
    
    public static long getBlockCountInRange(Level level, BlockPos pos){
        BlockPos.betweenClosedStream(pos.offset(-3, 1, -3), pos.offset(3, level.getHeight() - pos.getY(), 3)).forEach(pos1 -> {
            if(!(level.getBlockState(pos1).getBlock() instanceof AirBlock) && level.getBlockState(pos1).isCollisionShapeFullBlock(level, pos1)){
                System.out.println(level.getBlockState(pos1));
            }
        });
        return 0;
    }
    
    public static List<BlockPos> getBlockingBlocks(Level level, BlockPos pos){
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosedStream(pos.offset(-3, 1, -3), pos.offset(3, level.getHeight() - pos.getY(), 3)).forEach(nextPos -> {
            BlockState state = level.getBlockState(nextPos);
            if(!(state.getBlock() instanceof AirBlock) && state.isCollisionShapeFullBlock(level, nextPos)){
                positions.add(nextPos.immutable());
            }
        });
        return positions;
    }
}
