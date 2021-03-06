package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.SlotSpecific;
import net.minecraft.world.Container;

public class SlotCarrier extends SlotSpecific {

    public SlotCarrier(Container inventory, int slotId, int x, int y, ICarrierModule.CarrierSlot slot) {
        super(inventory, slotId, x, y, stack -> ICarrierModule.isSlotValid(stack, slot));
    }

}
