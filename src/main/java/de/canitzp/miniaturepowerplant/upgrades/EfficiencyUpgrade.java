package de.canitzp.miniaturepowerplant.upgrades;

import com.google.common.collect.Lists;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MPPTab;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyBoost;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class EfficiencyUpgrade extends Item implements ICarrierModule {

    public static final EfficiencyUpgrade EFFICIENCY_UPGRADE_BASIC = new EfficiencyUpgrade(0.25F, 0.0F, 0.1F);
    
    private final float ownModuleDepletionIncrease, otherModulesDepletionIncrease, energyMultiplier;

    public EfficiencyUpgrade(float ownModuleDepletionIncrease, float otherModulesDepletionIncrease, float energyMultiplier) {
        super(new Properties().stacksTo(1).tab(MPPTab.INSTANCE));
        this.ownModuleDepletionIncrease = ownModuleDepletionIncrease;
        this.otherModulesDepletionIncrease = otherModulesDepletionIncrease;
        this.energyMultiplier = energyMultiplier;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag){
        if(this.energyMultiplier > 0.0F){
            text.add(Component.literal("Increases slots energy generation by " + Math.round(this.energyMultiplier * 100) + "%").withStyle(ChatFormatting.GRAY));
        }
        if(this.ownModuleDepletionIncrease > 0.0F){
            text.add(Component.literal("Increases slots depletion by " + Math.round(this.ownModuleDepletionIncrease * 100) + "%").withStyle(ChatFormatting.GRAY));
        }
        if(this.otherModulesDepletionIncrease > 0.0F){
            text.add(Component.literal("Increases others depletion by " + Math.round(this.ownModuleDepletionIncrease * 100) + "%").withStyle(ChatFormatting.GRAY));
        }
    }
    
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.SOLAR_UPGRADE, CarrierSlot.CORE_UPGRADE, CarrierSlot.GROUND_UPGRADE};
    }

    @Override
    public float getDepletion(TileCarrier tile, CarrierSlot othersSlot, CarrierSlot mySlot, SynchroniseModuleData data) {
        return mySlot.isUpgrade(othersSlot) ? this.ownModuleDepletionIncrease : this.otherModulesDepletionIncrease;
    }
    
    @Override
    public List<EnergyBoost> boostEnergy(Level level, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data){
        if(otherSlot == mySlot.getCompanion()){
            return Lists.newArrayList(new EnergyBoost(this.energyMultiplier, "Efficiency Upgrade"));
        }
        return Collections.emptyList();
    }
}
