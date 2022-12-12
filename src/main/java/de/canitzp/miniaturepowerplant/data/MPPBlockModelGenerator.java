package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.carrier.ModuleGrade;
import de.canitzp.miniaturepowerplant.carrier.ModuleType;
import de.canitzp.miniaturepowerplant.modules.DepletableModule;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class MPPBlockModelGenerator extends BlockModelProvider {

    public MPPBlockModelGenerator(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MiniaturePowerPlant.MODID, helper);
    }

    @Override
    protected void registerModels() {
        for (ModuleType type : ModuleType.values()) {
            for (ModuleGrade grade : ModuleGrade.getValids()) {
                for (ICarrierModule.CarrierSlot slot : type.getAllowedSlots()) {
                    String name = slot.getLevelName() + "_" + grade.name().toLowerCase();
                    this.withExistingParent("block/carrier/" + name, modLoc("block/carrier/" + slot.getLevelName() + "_module")).texture("0", modLoc("blocks/modules/" + name));
                }
            }
        }
    }
}
