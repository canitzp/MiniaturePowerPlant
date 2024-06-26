package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.MPPRegistry;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
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
        // only return depletion when it is for me, not for any other module
        return mySlot.equals(othersSlot) ? this.getDepletion() : 0.0F;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> text, TooltipFlag flag) {
        if(!stack.isEmpty()){
            float depletionPercentage = this.getDepletionPercentage(stack);
            if(depletionPercentage >= 1.0F){
                text.add(Component.translatable("item.miniaturepowerplant.depletable.desc.depleted").withStyle(ChatFormatting.GRAY));
            } else if (depletionPercentage > 0.0F){
                text.add(Component.translatable("item.miniaturepowerplant.depletable.desc.depletion", String.format("%.2f", depletionPercentage * 100.0F)).withStyle(ChatFormatting.GRAY));
            } else {
                text.add(Component.translatable("item.miniaturepowerplant.depletable.desc.new").withStyle(ChatFormatting.GRAY));
            }
        }
    }
    
    @Override
    public boolean isBarVisible(ItemStack stack){
        return this.getDepletionPercentage(stack) > 0.0F;
    }
    
    @Override
    public int getBarWidth(ItemStack stack){
        float depletionPercentage = this.getDepletionPercentage(stack);
        return Math.round(13.0F - (depletionPercentage * 13.0F));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int pSlotId, boolean pIsSelected) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if(customData != null){
            CompoundTag nbt = customData.copyTag();
            if(nbt.contains("depletion", Tag.TAG_FLOAT)){
                stack.set(MPPRegistry.DC_DEPLETION, nbt.getFloat("depletion"));
                nbt.remove("depletion");
                stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(nbt)).build());
            }
        }
        super.inventoryTick(stack, level, entity, pSlotId, pIsSelected);
    }
}
