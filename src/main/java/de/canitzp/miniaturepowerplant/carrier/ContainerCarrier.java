package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.SlotSpecific;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.energy.CapabilityEnergy;

public class ContainerCarrier extends Container {

    public static final int SLOT_SOLAR = 0;
    public static final int SLOT_SOLAR_UPGRADE = 1;
    public static final int SLOT_CORE = 2;
    public static final int SLOT_CORE_UPGRADE = 3;
    public static final int SLOT_GROUND = 4;
    public static final int SLOT_GROUND_UPGRADE = 5;
    public static final int SLOT_BATTERY = 6;

    public static final ContainerType<ContainerCarrier> TYPE = IForgeContainerType.create(ContainerCarrier::createClientContainer);

    private final TileCarrier tile;

    private ContainerCarrier(int windowId, PlayerInventory playerInventory, TileCarrier tile) {
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
        this.addSlot(new SlotSpecific(tile.getInventory(), SLOT_BATTERY, 8, 82, stack -> stack.getCapability(CapabilityEnergy.ENERGY).isPresent()));

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

    public static ContainerCarrier createServerContainer(int windowsId, PlayerEntity player, TileCarrier tile){
        return new ContainerCarrier(windowsId, player.inventory, tile);
    }

    public static ContainerCarrier createClientContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data){
        // todo error checks
        BlockPos blockPos = data.readBlockPos();
        TileEntity tile = playerInventory.player.level.getBlockEntity(blockPos);
        return new ContainerCarrier(windowId, playerInventory, (TileCarrier) tile);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public TileCarrier getTile() {
        return this.tile;
    }
}
