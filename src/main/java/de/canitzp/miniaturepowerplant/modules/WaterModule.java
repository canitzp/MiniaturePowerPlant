package de.canitzp.miniaturepowerplant.modules;

import de.canitzp.miniaturepowerplant.carrier.TileCarrier;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public class WaterModule extends DepletableItemModule {

    public static final WaterModule WATER_MODULE_BASIC = new WaterModule(1.0F, 100_000.0F);

    public WaterModule(float depletion, float maxDepletion) {
        super(new Properties().stacksTo(1), depletion, maxDepletion);
    }

    @Override
    public CarrierSlot[] validSlots() {
        return new CarrierSlot[]{CarrierSlot.CORE, CarrierSlot.GROUND};
    }

    @Override
    public void tick(Level world, BlockPos pos, TileCarrier tile, SynchroniseModuleData data) {
        if(!world.isClientSide()){
            ListTag listEnergyProduction = new ListTag();
            for (Direction direction : Direction.values()) {
                int waterLevel = getWaterLevel(world, pos, direction);
                int energyForWaterLevel = getEnergyForWaterLevel(waterLevel);
                if(energyForWaterLevel > 0){
                    listEnergyProduction.add(EnergyProduction.toNBT(energyForWaterLevel, "Water level @ " + direction.getSerializedName()));
                }
            }
            data.use(compoundNBT -> compoundNBT.put(NBT_KEY_PRODUCTION, listEnergyProduction));
        }
    }

    private int getWaterLevel(Level world, BlockPos pos, Direction direction){
        FluidState fluidState = world.getFluidState(pos.relative(direction));
        if(fluidState.isEmpty()){
            return 0;
        }
        return fluidState.getAmount();
    }

    private int getEnergyForWaterLevel(int waterLevel){
        switch (waterLevel){
            case 1: case 7: return 2;
            case 2: case 6: return 4;
            case 3: case 5: return 6;
            case 4: return 8;
            case 8: return 1;
            default: return 0;
        }
    }
}
