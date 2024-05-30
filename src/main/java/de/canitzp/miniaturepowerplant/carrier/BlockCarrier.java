package de.canitzp.miniaturepowerplant.carrier;

import com.mojang.serialization.MapCodec;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockCarrier extends BaseEntityBlock implements LiquidBlockContainer{

    public static final MapCodec<BlockCarrier> CODEC = simpleCodec(BlockCarrier::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ModuleGrade> TOP_MODULE = EnumProperty.create("top_module", ModuleGrade.class);
    public static final EnumProperty<ModuleGrade> CENTER_MODULE = EnumProperty.create("center_module", ModuleGrade.class);
    public static final EnumProperty<ModuleGrade> BOTTOM_MODULE = EnumProperty.create("bottom_module", ModuleGrade.class);

    public static final BlockCarrier INSTANCE = new BlockCarrier();
    public static final BlockItem INSTANCE_ITEM = new BlockItem(INSTANCE, new Item.Properties());

    private BlockCarrier() {
        this(Properties.of().mapColor(MapColor.METAL).noOcclusion().strength(5F, 1000F).sound(SoundType.STONE));
    }

    private BlockCarrier(Properties properties) {
        super(properties);

        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(WATERLOGGED, false)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(TOP_MODULE, ModuleGrade.NONE)
                .setValue(CENTER_MODULE, ModuleGrade.NONE)
                .setValue(BOTTOM_MODULE, ModuleGrade.NONE));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult trace) {
        if(world.isClientSide()){
            return InteractionResult.SUCCESS;
        }

        MenuProvider menuProvider = this.getMenuProvider(state, world, pos);
        if(menuProvider != null){
            player.openMenu(menuProvider, packetBuffer -> packetBuffer.writeBlockPos(pos));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        // test for bucket like item
        ItemStack heldStack = player.getItemInHand(hand);
        if(!heldStack.isEmpty() && heldStack.getCapability(Capabilities.FluidHandler.ITEM) != null){
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state){
        return new TileCarrier(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state){
        return RenderShape.MODEL;
    }
    
    private BlockState composeState(BlockState current, LevelAccessor world, BlockPos pos){
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileCarrier){
            return current
                    .setValue(TOP_MODULE, ((TileCarrier) tile).getGradeForSlot(ICarrierModule.CarrierSlot.SOLAR))
                    .setValue(CENTER_MODULE, ((TileCarrier) tile).getGradeForSlot(ICarrierModule.CarrierSlot.CORE))
                    .setValue(BOTTOM_MODULE, ((TileCarrier) tile).getGradeForSlot(ICarrierModule.CarrierSlot.GROUND));
        }
        return current;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState facingState, @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos facingPos){
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
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder){
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, TOP_MODULE, CENTER_MODULE, BOTTOM_MODULE, BlockStateProperties.HORIZONTAL_FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag).setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    private boolean canLightPass(BlockState state){
        return !(state.getValue(TOP_MODULE).canLightPass() || state.getValue(CENTER_MODULE).canLightPass() ||state.getValue(BOTTOM_MODULE).canLightPass());
    }
    
    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return this.canLightPass(state);
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return this.canLightPass(state) ? 0.95F : 1.0F;
    }
    
    @Override
    public boolean canPlaceLiquid(@Nullable Player player,  @NotNull BlockGetter block, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluid){
        return false;
    }
    
    @Override
    public boolean placeLiquid(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluidState){
        return false;
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type){
        return level.isClientSide ? null : createTickerHelper(type, TileCarrier.TYPE, TileCarrier::tick);
    }
    
    @Override
    public void animateTick(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull RandomSource rnd){
        BlockEntity tile = level.getBlockEntity(pos);
        if(tile instanceof TileCarrier){
            ((TileCarrier) tile).animationTick(state, rnd);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pMovedByPiston) {
        if(!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof TileCarrier) {
                ((TileCarrier) be).dropContents();
            }
        }
        super.onRemove(state, level, pos, newState, pMovedByPiston);
    }
}
