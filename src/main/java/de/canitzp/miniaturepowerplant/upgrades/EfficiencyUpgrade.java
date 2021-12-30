package de.canitzp.miniaturepowerplant.upgrades;

import com.google.common.collect.Lists;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.List;

public class EfficiencyUpgrade extends Item implements ICarrierModule {

    private final float ownModuleDepletionIncrease, otherModulesDepletionIncrease, energyIncreaseMultiplier;

    public EfficiencyUpgrade(float ownModuleDepletionIncrease, float otherModulesDepletionIncrease, float energyIncreaseMultiplier) {
        super(new Properties().stacksTo(1));
        this.ownModuleDepletionIncrease = ownModuleDepletionIncrease;
        this.otherModulesDepletionIncrease = otherModulesDepletionIncrease;
        this.energyIncreaseMultiplier = energyIncreaseMultiplier;
    }

    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.SOLAR_UPGRADE, CarrierSlot.CORE_UPGRADE, CarrierSlot.GROUND_UPGRADE};
    }

    @Override
    public float getDepletion(TileCarrier tile, CarrierSlot othersSlot, CarrierSlot mySlot, SynchroniseModuleData data) {
        return mySlot.isUpgrade(othersSlot) ? this.ownModuleDepletionIncrease : this.otherModulesDepletionIncrease;
    }

    @Override
    public List<EnergyProduction> produceEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data) {
        //tile.getProductionForSlot(otherSlot)
        //return Lists.newArrayList(new EnergyProduction(this.energyIncreaseMultiplier, "Efficiency energy increase"));
        // todo
        return Collections.emptyList();
    }
}
