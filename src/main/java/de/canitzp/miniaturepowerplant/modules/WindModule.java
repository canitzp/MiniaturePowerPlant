package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyBoost;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WindModule extends DepletableItemModule {
    
    public static final WindModule WIND_MODULE_BASIC = new WindModule(1.0F, 100_000.0F, 10, 3);
    
    private final int energyProduction;
    private final int windRange;
    
    public WindModule(float depletion, float maxDepletion, int energyProduction, int windRange){
        super(new Item.Properties().stacksTo(1), depletion, maxDepletion);
        this.energyProduction = energyProduction;
        this.windRange = windRange;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag){
        super.appendHoverText(stack, world, text, flag);
        
        text.add(new TranslatableComponent("item.miniaturepowerplant.wind_module.desc.production", this.energyProduction).withStyle(ChatFormatting.GRAY));
        text.add(new TranslatableComponent("item.miniaturepowerplant.wind_module.desc.range", this.windRange).withStyle(ChatFormatting.GRAY));
    }
    
    @Override
    public CarrierSlot[] validSlots(){
        return new CarrierSlot[]{CarrierSlot.CORE};
    }
    
    @Override
    public void tick(Level level, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){
        ListTag production = new ListTag();
        ListTag penalties = new ListTag();
        ListTag boosts = new ListTag();
        
        production.add(EnergyProduction.toNBT(this.energyProduction, "item.miniaturepowerplant.wind_module.production.wind"));
    
        int blockedAmount = this.getBlockingBlocks(level, pos).size();
        if(blockedAmount > 0){
            penalties.add(EnergyPenalty.toNBT(blockedAmount / 10F, "item.miniaturepowerplant.wind_module.penalty.blocking_blocks"));
        }
    
        // height modifier; 100% at y=128 (layer 192); higher is more; lower is less
        float heightFactor = pos.getY() / ((level.getHeight() + level.getMinBuildHeight()) / 2.0F); // Overworld: height=384 & min_build_height=-64
        if(heightFactor > 1.0F){
            boosts.add(EnergyBoost.toNBT(heightFactor - 1.0F, "item.miniaturepowerplant.wind_module.boost.height"));
        } else if(heightFactor < 1.0F){
            penalties.add(EnergyPenalty.toNBT(1.0F - heightFactor, "item.miniaturepowerplant.wind_module.penalty.height"));
        }
    
        data.use(compoundNBT -> {
            compoundNBT.put(NBT_KEY_PRODUCTION, production);
            compoundNBT.put(NBT_KEY_PENALTY, penalties);
            compoundNBT.put(NBT_KEY_BOOST, boosts);
        });
    }
    
    /*@Override
    public void blockAnimationTick(ClientLevel level, BlockPos pos, TileCarrier tile, BlockState state, Random random, SynchroniseModuleData data){
        this.getBlockingBlocks(level, pos).forEach(blockedPos -> {
            level.addParticle(ParticleTypes.FLAME, blockedPos.getX() + 0.5, blockedPos.getY() + 0.75F, blockedPos.getZ() + 0.5F, 0, 0.05, 0);
        });
    }*/
    
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
