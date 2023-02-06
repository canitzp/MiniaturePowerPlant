package de.canitzp.miniaturepowerplant.reasons;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    
    @OnlyIn(Dist.CLIENT)
    public String translateReason(){
        return I18n.get(this.getReason());
    }

    public static CompoundTag toNBT(float multiplier, String reason){
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("boost_multiplier", multiplier);
        nbt.putString("boost_reason", reason);
        return nbt;
    }

}
