package de.canitzp.miniaturepowerplant.upgrades;

import com.google.common.collect.Lists;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MPPTab;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EcoUpgrade extends Item implements ICarrierModule {

    public static final EcoUpgrade ECO_UPGRADE = new EcoUpgrade(0.1F, 0.0F, .05F);
    public static final EcoUpgrade ECO_PLUS_UPGRADE = new EcoUpgrade(0.2F, 0.1F, .075F);

    private final float ownModuleDepletionReduction, otherModuleDepletionReduction, energyReductionMultiplier;

    public EcoUpgrade(float ownModuleDepletionReduction, float otherModuleDepletionReduction, float energyReductionMultiplier) {
        super(new Properties().stacksTo(1).tab(MPPTab.INSTANCE));
        this.ownModuleDepletionReduction = ownModuleDepletionReduction;
        this.otherModuleDepletionReduction = otherModuleDepletionReduction;
        this.energyReductionMultiplier = energyReductionMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        if(this.ownModuleDepletionReduction > 0.0F){
            text.add(Component.literal("Decreases slots depletion by " + Math.round(this.ownModuleDepletionReduction * 100) + "%").withStyle(ChatFormatting.GRAY));
        }

        if(this.otherModuleDepletionReduction > 0.0F){
            text.add(Component.literal("Decreases others depletion by " + Math.round(this.otherModuleDepletionReduction * 100) + "%").withStyle(ChatFormatting.GRAY));
        }

        if(this.energyReductionMultiplier <= 1.0F) {
            text.add(Component.literal("Decreases energy production by " + Math.round(this.energyReductionMultiplier * 100) + "%").withStyle(ChatFormatting.GRAY));
        }
    }

    @Nullable
    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.SOLAR_UPGRADE, CarrierSlot.CORE_UPGRADE, CarrierSlot.GROUND_UPGRADE};
    }

    @Override
    public float getDepletion(TileCarrier tile, CarrierSlot othersSlot, CarrierSlot mySlot, SynchroniseModuleData data) {
        return -(mySlot.isUpgrade(othersSlot) ? this.ownModuleDepletionReduction : this.otherModuleDepletionReduction);
    }

    @Override
    public List<EnergyPenalty> penaltyEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        return Lists.newArrayList(new EnergyPenalty(this.energyReductionMultiplier, "item.miniaturepowerplant.eco_upgrade.penalty"));
    }

}
