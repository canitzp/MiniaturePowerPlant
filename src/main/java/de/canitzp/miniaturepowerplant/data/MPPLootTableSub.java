package de.canitzp.miniaturepowerplant.data;

import de.canitzp.miniaturepowerplant.MPPRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MPPLootTableSub extends LootTableProvider {

    public MPPLootTableSub(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(generator.getPackOutput(), Set.of(), List.of(), lookupProvider);
    }

    @Override
    public List<SubProviderEntry> getTables() {
        return List.of(new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK));
    }

    private static class BlockLoot extends BlockLootSubProvider {

        protected BlockLoot(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            List<Block> list = (List<Block>) MPPRegistry.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList();
            return list;
        }

        @Override
        protected void generate() {
            this.dropSelf(MPPRegistry.CARRIER.get());
        }

    }
}
