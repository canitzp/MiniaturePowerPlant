package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class BlockCarrier extends ContainerBlock implements IWaterLoggable {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty TOP_MODULE = BooleanProperty.create("top_module");
    public static final BooleanProperty CENTER_MODULE = BooleanProperty.create("center_module");
    public static final BooleanProperty BOTTOM_MODULE = BooleanProperty.create("bottom_module");

    public static final BlockCarrier INSTANCE = new BlockCarrier();
    public static final BlockItem INSTANCE_ITEM = new BlockItem(INSTANCE, new Item.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS));

    private BlockCarrier() {
        super(Properties.of(Material.HEAVY_METAL).noOcclusion());

        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false).setValue(TOP_MODULE, false).setValue(CENTER_MODULE, false).setValue(BOTTOM_MODULE, false));
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {

        // test for bucket like item
        ItemStack heldStack = player.getItemInHand(hand);
        if(!heldStack.isEmpty() && heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent()){
            return ActionResultType.PASS;
        }

        if(world.isClientSide()){
            return ActionResultType.SUCCESS;
        }

        INamedContainerProvider menuProvider = this.getMenuProvider(state, world, pos);
        if(menuProvider != null){
            NetworkHooks.openGui(((ServerPlayerEntity) player), menuProvider, packetBuffer -> packetBuffer.writeBlockPos(pos));
        }

        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader blockReader) {
        return new TileCarrier();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public BlockRenderType getRenderShape(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    private BlockState composeState(BlockState current, IWorld world, BlockPos pos){
        TileEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileCarrier){
            return current
                    .setValue(TOP_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.SOLAR))
                    .setValue(CENTER_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.CORE))
                    .setValue(BOTTOM_MODULE, ((TileCarrier) tile).isModuleInstalled(ICarrierModule.CarrierSlot.GROUND));
        }
        return current;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return this.composeState(state, world, pos);
    }

    public void updateFromTile(IWorld world, BlockPos pos){
        BlockState currentState = world.getBlockState(pos);
        BlockState newState = this.composeState(currentState, world, pos);
        if(currentState != newState){
            world.setBlock(pos, newState, 0b11);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, TOP_MODULE, CENTER_MODULE, BOTTOM_MODULE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.defaultFluidState() : super.getFluidState(state);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        boolean flag = fluidstate.getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag);
    }

    private boolean canLightPass(BlockState state){
        return !(state.getValue(TOP_MODULE) || state.getValue(CENTER_MODULE) ||state.getValue(BOTTOM_MODULE));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return this.canLightPass(state);
    }

    @Override
    public float getShadeBrightness(BlockState state, IBlockReader world, BlockPos pos) {
        return this.canLightPass(state) ? 0.95F : 1.0F;
    }

}
