package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.SlotSpecific;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class CarrierMenu extends AbstractContainerMenu{

    public static final int SLOT_SOLAR = 0;
    public static final int SLOT_SOLAR_UPGRADE = 1;
    public static final int SLOT_CORE = 2;
    public static final int SLOT_CORE_UPGRADE = 3;
    public static final int SLOT_GROUND = 4;
    public static final int SLOT_GROUND_UPGRADE = 5;
    public static final int SLOT_BATTERY = 6;

    public static final MenuType<CarrierMenu> TYPE = IMenuTypeExtension.create(CarrierMenu::createClientContainer);

    private final TileCarrier tile;

    private CarrierMenu(int windowId, Inventory playerInventory, TileCarrier tile) {
        super(TYPE, windowId);

        this.tile = tile;

        // solar slots
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_SOLAR, 8, 8, ICarrierModule.CarrierSlot.SOLAR));
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_SOLAR_UPGRADE, 28, 8, ICarrierModule.CarrierSlot.SOLAR_UPGRADE));

        // core slots
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_CORE, 70, 31, ICarrierModule.CarrierSlot.CORE));
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_CORE_UPGRADE, 90, 31, ICarrierModule.CarrierSlot.CORE_UPGRADE));

        // ground slots
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_GROUND, 132, 54, ICarrierModule.CarrierSlot.GROUND));
        this.addSlot(new SlotCarrier(tile.getInventory(), SLOT_GROUND_UPGRADE, 152, 54, ICarrierModule.CarrierSlot.GROUND_UPGRADE));

        // battery slot
        this.addSlot(new SlotSpecific(tile.getInventory(), SLOT_BATTERY, 8, 82, stack -> stack.getCapability(Capabilities.EnergyStorage.ITEM) != null));

        // player inventory
        for(int row = 0; row < 3; ++row) {
            for(int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 113 + row * 18));
            }
        }

        for(int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 171));
        }
    }

    public static CarrierMenu createServerContainer(int windowsId, Inventory inv, TileCarrier tile){
        return new CarrierMenu(windowsId, inv, tile);
    }

    public static CarrierMenu createClientContainer(int windowId, Inventory playerInventory, FriendlyByteBuf data){
        // todo error checks
        BlockPos blockPos = data.readBlockPos();
        BlockEntity tile = playerInventory.player.level().getBlockEntity(blockPos);
        return new CarrierMenu(windowId, playerInventory, (TileCarrier) tile);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int slotId){
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotId);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slotId < 7) { // check if stack comes from this menu
                if (!this.moveItemStackTo(itemstack1, 7, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 7, false)) {
                return ItemStack.EMPTY;
            }
        
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
    
        return itemstack;
    }
    
    public TileCarrier getTile() {
        return this.tile;
    }
}
