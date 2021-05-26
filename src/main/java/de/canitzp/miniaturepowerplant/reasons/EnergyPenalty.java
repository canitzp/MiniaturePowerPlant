package de.canitzp.miniaturepowerplant.reasons;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnergyPenalty {

    private final float multiplier;
    private final String reason;

    public EnergyPenalty(float multiplier, String reason) {
        this.multiplier = multiplier;
        this.reason = reason;
    }

    public EnergyPenalty(CompoundNBT nbt){
        this.multiplier = nbt.getFloat("penalty_multiplier");
        this.reason = nbt.getString("penalty_reason");
    }

    public float getMultiplier() {
        return this.multiplier;
    }

    public String getReason() {
        return this.reason;
    }

    public static CompoundNBT toNBT(float multiplier, String reason, String... arguments){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("penalty_multiplier", multiplier);
        nbt.putString("penalty_reason", reason);
        if(arguments.length > 0){
            ListNBT argumentList = new ListNBT();
            for (String argument : arguments) {
                argumentList.add(StringNBT.valueOf(argument));
            }
            nbt.put("reason_arguments", argumentList);
        }
        return nbt;
    }

}
