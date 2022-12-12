package de.canitzp.miniaturepowerplant.carrier;

import net.minecraft.util.StringRepresentable;

public enum ModuleGrade implements StringRepresentable {

    NONE,
    WOOD,
    STONE,
    IRON,
    GOLD,
    LAPIS,
    REDSTONE,
    DIAMOND,
    NETHERITE;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }

    public boolean canLightPass(){
        return this == NONE;
    }

    public static ModuleGrade[] getValids(){
        return new ModuleGrade[]{WOOD, STONE, IRON, GOLD, LAPIS, REDSTONE, DIAMOND, NETHERITE};
    }

}
