package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MPPRegistry;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class MPPTagProvider extends BlockTagsProvider {

    public MPPTagProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), lookupProvider, MiniaturePowerPlant.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MPPRegistry.CARRIER.get());
    }
}
