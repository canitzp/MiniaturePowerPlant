package de.canitzp.miniaturepowerplant;

import com.google.common.collect.Lists;
import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface ICarrierModule {

    CarrierSlot[] validSlots();

    default boolean isDepleted(ItemStack stack){
        return false;
    }

    // return percentage of depletion added by this module
    float getDepletion(TileCarrier tile, CarrierSlot othersSlot, CarrierSlot mySlot, SynchroniseModuleData data);

    default List<EnergyProduction> produceEnergy(World world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, SynchroniseModuleData data){
        return Collections.emptyList();
    }

    default List<EnergyPenalty> penaltyEnergy(World world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, SynchroniseModuleData data){
        return Collections.emptyList();
    }

    default float getEnergyMultiplier(TileCarrier tile, CarrierSlot slot, SynchroniseModuleData data){
        return 1.0F;
    }

    default void addDepletionInformation(ScreenCarrier screen, SynchroniseModuleData data, List<ITextComponent> text){}

    default void tick(World world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){}

    static boolean isSlotValid(ItemStack stack, CarrierSlot slot){
        if(stack.isEmpty()){
            return false;
        }

        Item item = stack.getItem();
        if(item instanceof ICarrierModule){
            CarrierSlot[] carrierSlots = ((ICarrierModule) item).validSlots();
            return carrierSlots != null && Arrays.asList(carrierSlots).contains(slot);
        }

        return false;
    }

    enum CarrierSlot {
        SOLAR, SOLAR_UPGRADE, CORE, CORE_UPGRADE, GROUND, GROUND_UPGRADE;

        public boolean isUpgrade(CarrierSlot upgradeableSlot){
            return upgradeableSlot.ordinal() + 1 == this.ordinal();
        }
    }
}
