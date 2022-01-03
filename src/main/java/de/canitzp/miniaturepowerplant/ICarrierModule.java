package de.canitzp.miniaturepowerplant;

import de.canitzp.miniaturepowerplant.carrier.ScreenCarrier;
import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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

    default List<EnergyProduction> produceEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data){
        return Collections.emptyList();
    }

    default List<EnergyPenalty> penaltyEnergy(Level world, BlockPos pos, TileCarrier tile, CarrierSlot mySlot, CarrierSlot otherSlot, SynchroniseModuleData data){
        return Collections.emptyList();
    }

    default float getEnergyMultiplier(TileCarrier tile, CarrierSlot slot, SynchroniseModuleData data){
        return 1.0F;
    }

    default void addDepletionInformation(ScreenCarrier screen, SynchroniseModuleData data, List<Component> text){}

    default void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data){}

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
        
        public CarrierSlot getCompanion(){
            if(this.ordinal() % 2 == 0){
                // is module
                return CarrierSlot.values()[this.ordinal() + 1];
            } else {
                // is upgrade
                return CarrierSlot.values()[this.ordinal() - 1];
            }
        }
    }
}
