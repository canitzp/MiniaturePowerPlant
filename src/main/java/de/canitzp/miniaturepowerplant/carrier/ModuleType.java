package de.canitzp.miniaturepowerplant.carrier;

import de.canitzp.miniaturepowerplant.ICarrierModule.CarrierSlot;
import net.minecraft.util.StringRepresentable;

import static de.canitzp.miniaturepowerplant.ICarrierModule.CarrierSlot.CORE;
import static de.canitzp.miniaturepowerplant.ICarrierModule.CarrierSlot.GROUND;
import static de.canitzp.miniaturepowerplant.carrier.ModuleGrade.*;

public enum ModuleType implements StringRepresentable {

    GENERIC(new CarrierSlot[]{}, new ModuleGrade[]{}),
    SOLAR(new CarrierSlot[]{CarrierSlot.SOLAR}, new ModuleGrade[]{WOOD, STONE, IRON, GOLD, DIAMOND, REDSTONE, NETHERITE}),
    TEMPERATURE(new CarrierSlot[]{CORE, GROUND}, new ModuleGrade[]{WOOD}),
    WIND(new CarrierSlot[]{CORE}, new ModuleGrade[]{WOOD}),
    WATER(new CarrierSlot[]{CORE, GROUND}, new ModuleGrade[]{WOOD});

    private final CarrierSlot[] allowedSlots;
    private final ModuleGrade[] allowedGrades;

    ModuleType(CarrierSlot[] allowedSlots, ModuleGrade[] allowedGrades) {
        this.allowedSlots = allowedSlots;
        this.allowedGrades = allowedGrades;
    }

    public CarrierSlot[] getAllowedSlots() {
        return allowedSlots;
    }

    public ModuleGrade[] getAllowedGrades() {
        return allowedGrades;
    }

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
}
