package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.carrier.ModuleGrade;
import de.canitzp.miniaturepowerplant.carrier.ModuleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.ArrayUtils;

public class MPPBlockstateGenerator extends BlockStateProvider {

    public MPPBlockstateGenerator(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, MiniaturePowerPlant.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.carrierMultipart();
    }

    private void carrierMultipart(){
        ModelFile.ExistingModelFile model = models().getExistingFile(new ResourceLocation(MiniaturePowerPlant.MODID, "block/carrier/base"));

        MultiPartBlockStateBuilder builder = getMultipartBuilder(BlockCarrier.INSTANCE);
        BlockStateProperties.HORIZONTAL_FACING.getAllValues().forEach(facing -> {
            builder.part().modelFile(model).rotationY((((int) facing.value().toYRot()) + 180) % 360).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, facing.value()).end();

            for (ModuleType type : ModuleType.values()) {
                if(ArrayUtils.contains(type.getAllowedSlots(), ICarrierModule.CarrierSlot.SOLAR)){
                    for (ModuleGrade grade : type.getAllowedGrades()) {
                        ModelFile.ExistingModelFile gradedModel = models().getExistingFile(new ResourceLocation(MiniaturePowerPlant.MODID, "block/carrier/top_" + type.name().toLowerCase() + "_" + grade.name().toLowerCase()));
                        builder.part().modelFile(gradedModel).rotationY((((int) facing.value().toYRot()) + 180) % 360)
                                .addModel().condition(BlockCarrier.TOP_MODULE, grade).condition(BlockStateProperties.HORIZONTAL_FACING, facing.value()).end();
                    }
                }
            }

            for (ModuleType type : ModuleType.values()) {
                if(ArrayUtils.contains(type.getAllowedSlots(), ICarrierModule.CarrierSlot.CORE)){
                    for (ModuleGrade grade : type.getAllowedGrades()) {
                        ModelFile.ExistingModelFile gradedModel = models().getExistingFile(new ResourceLocation(MiniaturePowerPlant.MODID, "block/carrier/center_" + type.name().toLowerCase() + "_" + grade.name().toLowerCase()));
                        builder.part().modelFile(gradedModel).rotationY((((int) facing.value().toYRot()) + 180) % 360)
                                .addModel().condition(BlockCarrier.CENTER_MODULE, grade).condition(BlockStateProperties.HORIZONTAL_FACING, facing.value()).end();
                    }
                }
            }

            for (ModuleType type : ModuleType.values()) {
                if(ArrayUtils.contains(type.getAllowedSlots(), ICarrierModule.CarrierSlot.GROUND)){
                    for (ModuleGrade grade : type.getAllowedGrades()) {
                        ModelFile.ExistingModelFile gradedModel = models().getExistingFile(new ResourceLocation(MiniaturePowerPlant.MODID, "block/carrier/bottom_" + type.name().toLowerCase() + "_" + grade.name().toLowerCase()));
                        builder.part().modelFile(gradedModel).rotationY((((int) facing.value().toYRot()) + 180) % 360)
                                .addModel().condition(BlockCarrier.BOTTOM_MODULE, grade).condition(BlockStateProperties.HORIZONTAL_FACING, facing.value()).end();
                    }
                }
            }
        });
    }
}
