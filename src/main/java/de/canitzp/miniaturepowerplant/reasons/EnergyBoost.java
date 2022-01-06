package de.canitzp.miniaturepowerplant.reasons;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

public class EnergyBoost{

    private final float multiplier;
    private final String reason;

    public EnergyBoost(float multiplier, String reason) {
        this.multiplier = multiplier;
        this.reason = reason;
    }

    public EnergyBoost(CompoundTag nbt){
        this.multiplier = nbt.getFloat("boost_multiplier");
        this.reason = nbt.getString("boost_reason");
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public String getReason() {
        return this.reason;
    }

    public static CompoundTag toNBT(float multiplier, String reason){
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("boost_multiplier", multiplier);
        nbt.putString("boost_reason", reason);
        return nbt;
    }

}
