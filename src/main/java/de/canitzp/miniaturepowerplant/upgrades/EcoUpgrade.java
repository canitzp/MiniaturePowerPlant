package de.canitzp.miniaturepowerplant.upgrades;

import com.google.common.collect.Lists;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EcoUpgrade extends Item implements ICarrierModule {

    public static final EcoUpgrade ECO_UPGRADE = new EcoUpgrade(0.1F, 0.0F, .85F);
    public static final EcoUpgrade ECO_PLUS_UPGRADE = new EcoUpgrade(0.25F, 0.1F, .95F);

    private final float ownModuleDepletionReduction, otherModuleDepletionReduction, energyReductionMultiplier;

    public EcoUpgrade(float ownModuleDepletionReduction, float otherModuleDepletionReduction, float energyReductionMultiplier) {
        super(new Properties().stacksTo(1));
        this.ownModuleDepletionReduction = ownModuleDepletionReduction;
        this.otherModuleDepletionReduction = otherModuleDepletionReduction;
        this.energyReductionMultiplier = energyReductionMultiplier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        if(this.ownModuleDepletionReduction > 0.0F){
            text.add(new StringTextComponent("Decreases slots depletion by " + Math.round(this.ownModuleDepletionReduction * 100) + "%").withStyle(TextFormatting.GRAY));
        }

        if(this.otherModuleDepletionReduction > 0.0F){
            text.add(new StringTextComponent("Decreases others depletion by " + Math.round(this.otherModuleDepletionReduction * 100) + "%").withStyle(TextFormatting.GRAY));
        }

        if(this.energyReductionMultiplier <= 1.0F) {
            text.add(new StringTextComponent("Decreases energy production by " + Math.round((1.0F - this.energyReductionMultiplier) * 100) + "%").withStyle(TextFormatting.GRAY));
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
    public List<EnergyPenalty> penaltyEnergy(World world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        return Lists.newArrayList(new EnergyPenalty(this.energyReductionMultiplier, "Eco upgrade penalty"));
    }

}
