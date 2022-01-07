package de.canitzp.miniaturepowerplant.carrier;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyBoost;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.energy.EnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenCarrier extends AbstractContainerScreen<CarrierMenu>{

    private static final ResourceLocation TEXTURE = new ResourceLocation(MiniaturePowerPlant.MODID, "textures/gui/container/carrier.png");
    private static final Map<ICarrierModule.CarrierSlot, Pair<Integer, Integer>> DEPLETION_SLOTS = new HashMap<ICarrierModule.CarrierSlot, Pair<Integer, Integer>>(){{
        put(ICarrierModule.CarrierSlot.SOLAR, Pair.of(8, 28));
        put(ICarrierModule.CarrierSlot.CORE, Pair.of(70, 51));
        put(ICarrierModule.CarrierSlot.GROUND, Pair.of(132, 74));
    }};

    public ScreenCarrier(CarrierMenu container, Inventory player, Component title) {
        super(container, player, title);
    }

    @Override
    protected void init() {
        super.init();
        this.imageWidth = 176;
        this.imageHeight = 195;

        int titleWidth = this.minecraft.font.width(this.title);
        this.titleLabelX = (this.imageWidth - 5) - titleWidth;

        this.inventoryLabelY = 102;
    }

    @Override
    protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrix);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.blit(matrix, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.imageWidth, this.imageHeight);

        // render depletion bar
        DEPLETION_SLOTS.forEach((slot, pair) -> {
            this.renderSlotLevel(matrix, this.getDepletion(slot), pair.getLeft(), pair.getRight());
        });

        // energy bar; shows the energy of the carrier and accu combined
        AtomicInteger storedEnergy = new AtomicInteger(this.getTile().getEnergyStorage().getEnergyStored());
        AtomicInteger energyCapacity = new AtomicInteger(this.getTile().getEnergyStorage().getMaxEnergyStored());
        this.getTile().getAccuStorage().ifPresent(accuEnergyStorage -> {
            storedEnergy.addAndGet(accuEnergyStorage.getEnergyStored());
            energyCapacity.addAndGet(accuEnergyStorage.getMaxEnergyStored());
        });
        int energybarPixelWidth = Math.round((storedEnergy.get() / (energyCapacity.get() * 1.0F)) * 141.0F);
        if(energybarPixelWidth > 0){
            float[] wheelColor = getWheelColor(this.getTile().getLevel().getGameTime() % 256);
            RenderSystem.setShaderColor(wheelColor[0] / 255F, wheelColor[1] / 255F, wheelColor[2] / 255F, 1.0F);
            this.blit(matrix, this.getGuiLeft() + 27, this.getGuiTop() + 85, 0, 198, energybarPixelWidth, 10);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        // render depletion bar
        DEPLETION_SLOTS.forEach((slot, pair) -> {
            if(this.getTile().isModuleInstalled(slot)){
                ICarrierModule carrierModule = this.getTile().getCarrierModule(slot);
                SynchroniseModuleData data = this.getTile().getSyncData(slot);
                float depletion = this.getDepletion(slot);
                this.renderSlotLevelHover(matrix, depletion, carrierModule, data, slot, mouseX, mouseY, pair.getLeft(), pair.getRight());
            }
        });

        // empty slots tooltip
        Slot slotUnderMouse = this.getSlotUnderMouse();
        if(slotUnderMouse != null && slotUnderMouse.container.equals(this.menu.getTile().getInventory()) && !slotUnderMouse.hasItem()){
            List<Component> text = new ArrayList<>();
            switch (slotUnderMouse.index){
                case CarrierMenu.SLOT_SOLAR: {
                    text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.empty_solar_slot"));
                    break;
                }
                case CarrierMenu.SLOT_CORE: {
                    text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.empty_core_slot"));
                    break;
                }
                case CarrierMenu.SLOT_GROUND: {
                    text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.empty_ground_slot"));
                    break;
                }
                case CarrierMenu.SLOT_SOLAR_UPGRADE:
                case CarrierMenu.SLOT_CORE_UPGRADE:
                case CarrierMenu.SLOT_GROUND_UPGRADE: {
                    text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.empty_upgrade_slot"));
                    break;
                }
            }
            this.renderComponentTooltip(matrix, text, mouseX, mouseY);
        }

        // energy bar
        if(mouseX >= this.getGuiLeft() + 27 && mouseX <= this.getGuiLeft() + 27 + 141 && mouseY >= this.getGuiTop() + 85 && mouseY <= this.getGuiTop() + 85 + 10){
            // internal
            EnergyStorage energyStorage = this.getTile().getEnergyStorage();
            List<Component> text = new ArrayList<>();
            text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.energy.internal_stored", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
            // additional
            this.getTile().getAccuStorage().ifPresent(accu -> {
                text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.energy.additional_stored", accu.getEnergyStored(), accu.getMaxEnergyStored()));
            });
            text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.energy.produced", this.getTile().getProducedEnergy()).withStyle(ChatFormatting.GRAY));
            text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.energy.wasted", this.getTile().getWastedEnergy()).withStyle(ChatFormatting.GRAY));
            this.renderComponentTooltip(matrix, text, mouseX, mouseY);

        }

        this.renderTooltip(matrix, mouseX, mouseY);
    }

    private void renderSlotLevel(PoseStack matrix, float depletion, int x, int y) {
        if(depletion > 0.0F){
            int depletionBarWidth = Math.max(1, Math.min(Math.round(depletion * 24), 36));
            this.blit(matrix, this.getGuiLeft() + x, this.getGuiTop() + y, 0, 195, depletionBarWidth, 3);
        }
    }

    private void renderSlotLevelHover(PoseStack matrix, float depletion, ICarrierModule carrierModule, SynchroniseModuleData data, ICarrierModule.CarrierSlot slot, int mouseX, int mouseY, int x, int y){
        if(mouseX >= this.getGuiLeft() + x && mouseX <= this.getGuiLeft() + x + 36 && mouseY >= this.getGuiTop() + y && mouseY <= this.getGuiTop() + y + 3){
            List<EnergyProduction> energyProductions = this.getTile().getProductionForSlot(slot);
            List<EnergyPenalty> energyPenalties = this.getTile().getPenaltiesForSlot(slot);
            List<EnergyBoost> energyBoosts = this.getTile().getBoostsForSlot(slot);
            List<Component> text = new ArrayList<>();
            text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.depletion", Math.round(depletion * 100)));
            if(!energyProductions.isEmpty()){
                text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.producer").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE));
                for (EnergyProduction producer : energyProductions) {
                    text.add(new TextComponent(" ").append(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.producer_self", producer.getEnergy(), producer.translateReason()).withStyle(ChatFormatting.GRAY)));
                }
            }
            if(!energyPenalties.isEmpty() && !energyProductions.isEmpty()){
                text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.penalty").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE));
                for (EnergyPenalty penalty : energyPenalties) {
                    text.add(new TextComponent(" ").append(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.penalty_self", Math.round(penalty.getMultiplier() * 100), penalty.translateReason()).withStyle(ChatFormatting.GRAY)));
                }
            }
            if(!energyBoosts.isEmpty()){
                text.add(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.boost").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE));
                for(EnergyBoost boost : energyBoosts){
                    text.add(new TextComponent(" ").append(new TranslatableComponent("container.miniaturepowerplant.carrier.hover.boost_self", Math.round(boost.getMultiplier() * 100), boost.translateReason()).withStyle(ChatFormatting.GRAY)));
                }
            }

            ArrayList<Component> additionalText = new ArrayList<>();
            carrierModule.addDepletionInformation(this, data, additionalText);
            if(!energyProductions.isEmpty() && !additionalText.isEmpty()){
                text.add(new TextComponent(""));
            }
            text.addAll(additionalText);

            this.renderComponentTooltip(matrix, text, mouseX, mouseY);
        }
    }

    private TileCarrier getTile(){
        return this.menu.getTile();
    }

    private float getDepletion(ICarrierModule.CarrierSlot slot){
        return this.getTile().getDepletion(slot);
    }

    public static float[] getWheelColor(float pos) {
        if (pos < 85.0f) { return new float[] { pos * 3.0F, 255.0f - pos * 3.0f, 0.0f }; }
        if (pos < 170.0f) { return new float[] { 255.0f - (pos -= 85.0f) * 3.0f, 0.0f, pos * 3.0f }; }
        return new float[] { 0.0f, (pos -= 170.0f) * 3.0f, 255.0f - pos * 3.0f };
    }
}
