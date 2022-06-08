package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MPPTab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockCarrier extends BaseEntityBlock implements LiquidBlockContainer{

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty TOP_MODULE = BooleanProperty.create("top_module");
    public static final BooleanProperty CENTER_MODULE = BooleanProperty.create("center_module");
    public static final BooleanProperty BOTTOM_MODULE = BooleanProperty.create("bottom_module");

    public static final BlockCarrier INSTANCE = new BlockCarrier();
    public static final BlockItem INSTANCE_ITEM = new BlockItem(INSTANCE, new Item.Properties().tab(MPPTab.INSTANCE));

    private BlockCarrier() {
        super(Properties.of(Material.HEAVY_METAL).noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(TOP_MODULE, false).setValue(CENTER_MODULE, false).setValue(BOTTOM_MODULE, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {

        // test for bucket like item
        ItemStack heldStack = player.getItemInHand(hand);
        if(!heldStack.isEmpty() && heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            return InteractionResult.PASS;
        }

        if(world.isClientSide()){
            return InteractionResult.SUCCESS;
        }

        MenuProvider menuProvider = this.getMenuProvider(state, world, pos);
        if(menuProvider != null){
            NetworkHooks.openGui(((ServerPlayer) player), menuProvider, packetBuffer -> packetBuffer.writeBlockPos(pos));
        }

        return InteractionResult.SUCCESS;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
        return new TileCarrier(pos, state);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }
    
    private BlockState composeState(BlockState current, LevelAccessor world, BlockPos pos){
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileCarrier){
            return current
                    .setValue(TOP_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.SOLAR))
                    .setValue(CENTER_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.CORE))
                    .setValue(BOTTOM_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.GROUND));
        }
        return current;
    }
    
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos){
        return this.composeState(state, world, pos);
    }

    public void updateFromTile(LevelAccessor world, BlockPos pos){
        BlockState currentState = world.getBlockState(pos);
        BlockState newState = this.composeState(currentState, world, pos);
        if(currentState != newState){
            world.setBlock(pos, newState, 0b11);
        }
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, TOP_MODULE, CENTER_MODULE, BOTTOM_MODULE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    private boolean canLightPass(BlockState state){
        return !(state.getValue(TOP_MODULE) || state.getValue(CENTER_MODULE) ||state.getValue(BOTTOM_MODULE));
    }
    
    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return this.canLightPass(state);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return this.canLightPass(state) ? 0.95F : 1.0F;
    }
    
    @Override
    public boolean canPlaceLiquid(BlockGetter p_54766_, BlockPos p_54767_, BlockState p_54768_, Fluid p_54769_){
        return false;
    }
    
    @Override
    public boolean placeLiquid(LevelAccessor p_54770_, BlockPos p_54771_, BlockState p_54772_, FluidState p_54773_){
        return false;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return level.isClientSide ? null : createTickerHelper(type, TileCarrier.TYPE, TileCarrier::tick);
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rnd){
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile instanceof TileCarrier){
            ((TileCarrier) tile).animationTick(state, rnd);
        }
    }
}
