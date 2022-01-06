package de.canitzp.miniaturepowerplant.reasons;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class EnergyPenalty {

    private final float multiplier;
    private final String reason;

    public EnergyPenalty(float multiplier, String reason) {
        this.multiplier = multiplier;
        this.reason = reason;
    }

    public EnergyPenalty(CompoundTag nbt){
        this.multiplier = nbt.getFloat("penalty_multiplier");
        this.reason = nbt.getString("penalty_reason");
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public String getReason() {
        return this.reason;
    }

    public static CompoundTag toNBT(float multiplier, String reason){
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("penalty_multiplier", multiplier);
        nbt.putString("penalty_reason", reason);
        return nbt;
    }

}
