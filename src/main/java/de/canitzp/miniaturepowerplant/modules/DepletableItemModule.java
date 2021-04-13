package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class DepletableItemModule extends Item implements DepletableModule {

    private final float depletion;
    private final float maxDepletion;

    public DepletableItemModule(Properties properties, float depletion, float maxDepletion) {
        super(properties);
        this.depletion = depletion;
        this.maxDepletion = maxDepletion;
    }

    @Override
    public float getDepletion() {
        return this.depletion;
    }

    @Override
    public float getMaxDepletion() {
        return this.maxDepletion;
    }

    @Override
    public float getDepletion(TileCarrier tile, CarrierSlot othersSlot, CarrierSlot mySlot, SynchroniseModuleData data) {
        return Arrays.asList(this.validSlots()).contains(othersSlot) ? this.getDepletion() : 0.0F;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        if(!stack.isEmpty()){
            float depletionPercentage = this.getDepletionPercentage(stack);
            if(depletionPercentage >= 1.0F){
                text.add(new TranslationTextComponent("item.miniaturepowerplant.depletable.desc.depleted").withStyle(TextFormatting.GRAY));
            } else if (depletionPercentage > 0.0F){
                text.add(new TranslationTextComponent("item.miniaturepowerplant.depletable.desc.depletion", depletionPercentage * 100.0F).withStyle(TextFormatting.GRAY));
            }
        }
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return this.getDepletionPercentage(stack) > 0.0F;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return this.getDepletionPercentage(stack);
    }
}
