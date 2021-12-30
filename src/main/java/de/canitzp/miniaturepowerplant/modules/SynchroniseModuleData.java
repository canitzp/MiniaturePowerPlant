package de.canitzp.miniaturepowerplant.modules;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Consumer;

public class SynchroniseModuleData {

    private CompoundTag data = new CompoundTag();

    public void use(Consumer<CompoundTag> supplier){
        supplier.accept(this.data);
    }

    public void set(CompoundTag nbt){
        this.data = nbt;
    }

    public CompoundTag get(){
        return this.data;
    }

    public boolean has(String key, int type){
        return this.data.contains(key, type);
    }

}
