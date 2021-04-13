package de.canitzp.miniaturepowerplant.reasons;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnergyProduction {

    private final int energy;
    private final String reason;

    public EnergyProduction(int energy, String reason) {
        this.energy = energy;
        this.reason = reason;
    }

    public EnergyProduction(CompoundNBT nbt){
        this.energy = nbt.getInt("energy_production");
        this.reason = nbt.getString("production_reason");
    }

    public int getEnergy() {
        return this.energy;
    }

    public String getReason() {
        return reason;
    }

    @OnlyIn(Dist.CLIENT)
    public String translateReason(){
        return I18n.get(this.getReason());
    }

    public static CompoundNBT toNBT(int energy, String reason){
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("energy_production", energy);
        nbt.putString("production_reason", reason);
        return nbt;
    }
}
