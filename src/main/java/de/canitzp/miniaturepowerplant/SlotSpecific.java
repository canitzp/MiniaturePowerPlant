package de.canitzp.miniaturepowerplant;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class SlotSpecific extends Slot {

    private final Predicate<ItemStack> isItemStackValid;

    public SlotSpecific(IInventory inventory, int slotId, int x, int y, Predicate<ItemStack> isItemStackValid) {
        super(inventory, slotId, x, y);

        this.isItemStackValid = isItemStackValid;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return this.isItemStackValid.test(stack);
    }
}
