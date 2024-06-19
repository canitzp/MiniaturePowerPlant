package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.carrier.BlockCarrier;
import de.canitzp.miniaturepowerplant.carrier.ModuleGrade;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MPPBlockstateGenerator extends BlockStateProvider {

    public MPPBlockstateGenerator(DataGenerator generator, ExistingFileHelper helper) {
        super(generator.getPackOutput(), MiniaturePowerPlant.MODID, helper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.carrierMultipart();
    }

    private void carrierMultipart(){
        ModelFile.ExistingModelFile model = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MiniaturePowerPlant.MODID, "block/carrier/base"));

        MultiPartBlockStateBuilder builder = getMultipartBuilder(BlockCarrier.INSTANCE);
        BlockStateProperties.HORIZONTAL_FACING.getAllValues().forEach(facing -> {
            builder.part().modelFile(model).rotationY((((int) facing.value().toYRot()) + 180) % 360).addModel().condition(BlockStateProperties.HORIZONTAL_FACING, facing.value()).end();
            for (ModuleGrade grade : ModuleGrade.getValids()) {
                ModelFile.ExistingModelFile gradedModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MiniaturePowerPlant.MODID, "block/carrier/top_" + grade.name().toLowerCase()));
                builder.part()
                        .modelFile(gradedModel)
                        .rotationY((((int) facing.value().toYRot()) + 180) % 360)
                        .addModel()
                        .condition(BlockStateProperties.HORIZONTAL_FACING, facing.value())
                        .condition(BlockCarrier.TOP_MODULE, grade)
                        .end();
            }
            for (ModuleGrade grade : ModuleGrade.getValids()) {
                ModelFile.ExistingModelFile gradedModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MiniaturePowerPlant.MODID, "block/carrier/center_" + grade.name().toLowerCase()));
                builder.part()
                        .modelFile(gradedModel)
                        .rotationY((((int) facing.value().toYRot()) + 180) % 360)
                        .addModel()
                        .condition(BlockStateProperties.HORIZONTAL_FACING, facing.value())
                        .condition(BlockCarrier.CENTER_MODULE, grade)
                        .end();
            }
            for (ModuleGrade grade : ModuleGrade.getValids()) {
                ModelFile.ExistingModelFile gradedModel = models().getExistingFile(ResourceLocation.fromNamespaceAndPath(MiniaturePowerPlant.MODID, "block/carrier/bottom_" + grade.name().toLowerCase()));
                builder.part()
                        .modelFile(gradedModel)
                        .rotationY((((int) facing.value().toYRot()) + 180) % 360)
                        .addModel()
                        .condition(BlockStateProperties.HORIZONTAL_FACING, facing.value())
                        .condition(BlockCarrier.BOTTOM_MODULE, grade)
                        .end();
            }
        });
    }
}
