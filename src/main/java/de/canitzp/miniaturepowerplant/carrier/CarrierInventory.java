package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.modules.DepletableModule;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class CarrierInventory extends SimpleContainer implements WorldlyContainer {

    private static final int[] SIDED_SLOTS = new int[]{CarrierMenu.SLOT_SOLAR, CarrierMenu.SLOT_SOLAR_UPGRADE, CarrierMenu.SLOT_CORE, CarrierMenu.SLOT_CORE_UPGRADE, CarrierMenu.SLOT_GROUND, CarrierMenu.SLOT_GROUND_UPGRADE, CarrierMenu.SLOT_BATTERY};

    private final TileCarrier carrier;

    public CarrierInventory(TileCarrier carrier) {
        super(SIDED_SLOTS.length);
        this.carrier = carrier;
    }

    @Override
    public void setChanged() {
        this.carrier.onBlockUpdate();
        this.carrier.setChanged();
        super.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slotNumber, @Nonnull ItemStack stack) {
        switch (slotNumber){
            case CarrierMenu.SLOT_SOLAR -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.SOLAR);
            }
            case CarrierMenu.SLOT_SOLAR_UPGRADE -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.SOLAR_UPGRADE);
            }
            case CarrierMenu.SLOT_CORE -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.CORE);
            }
            case CarrierMenu.SLOT_CORE_UPGRADE -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.CORE_UPGRADE);
            }
            case CarrierMenu.SLOT_GROUND -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.GROUND);
            }
            case CarrierMenu.SLOT_GROUND_UPGRADE -> {
                return ICarrierModule.isSlotValid(stack, ICarrierModule.CarrierSlot.GROUND_UPGRADE);
            }
            case CarrierMenu.SLOT_BATTERY -> {
                return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
            }
            default -> {
                return super.canPlaceItem(slotNumber, stack);
            }
        }
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull Direction side) {
        return SIDED_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotNumber, @Nonnull ItemStack stack, @Nullable Direction side) {
        return this.canPlaceItem(slotNumber, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slotNumber, @Nonnull ItemStack stack, Direction side) {
        switch (slotNumber){
            case CarrierMenu.SLOT_SOLAR, CarrierMenu.SLOT_CORE, CarrierMenu.SLOT_GROUND -> {
                if(stack.getItem() instanceof DepletableModule){
                    return ((DepletableModule) stack.getItem()).isDepleted(stack);
                } else {
                    return true;
                }
            }
            default -> {
                return false;
            }
        }
    }
}
