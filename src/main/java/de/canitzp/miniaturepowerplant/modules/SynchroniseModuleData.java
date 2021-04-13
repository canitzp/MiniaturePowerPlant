package de.canitzp.miniaturepowerplant.modules;

import net.minecraft.nbt.CompoundNBT;

import java.util.function.Consumer;

public class SynchroniseModuleData {

    private CompoundNBT data = new CompoundNBT();

    public void use(Consumer<CompoundNBT> supplier){
        supplier.accept(this.data);
    }

    public void set(CompoundNBT nbt){
        this.data = nbt;
    }

    public CompoundNBT get(){
        return this.data;
    }

    public boolean has(String key, int type){
        return this.data.contains(key, type);
    }

}
