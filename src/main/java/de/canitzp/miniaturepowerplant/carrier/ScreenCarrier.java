package de.canitzp.miniaturepowerplant.carrier;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.canitzp.miniaturepowerplant.ICarrierModule;
import de.canitzp.miniaturepowerplant.MiniaturePowerPlant;
import de.canitzp.miniaturepowerplant.modules.SynchroniseModuleData;
import de.canitzp.miniaturepowerplant.reasons.EnergyPenalty;
import de.canitzp.miniaturepowerplant.reasons.EnergyProduction;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.EnergyStorage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ScreenCarrier extends ContainerScreen<ContainerCarrier> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(MiniaturePowerPlant.MODID, "textures/gui/container/carrier.png");
    private static final Map<ICarrierModule.CarrierSlot, Pair<Integer, Integer>> DEPLETION_SLOTS = new HashMap<ICarrierModule.CarrierSlot, Pair<Integer, Integer>>(){{
        put(ICarrierModule.CarrierSlot.SOLAR, Pair.of(8, 28));
        put(ICarrierModule.CarrierSlot.CORE, Pair.of(70, 51));
        put(ICarrierModule.CarrierSlot.GROUND, Pair.of(132, 74));
    }};

    public ScreenCarrier(ContainerCarrier container, PlayerInventory player, ITextComponent title) {
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
    protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrix);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bind(TEXTURE);

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
            RenderSystem.color4f(wheelColor[0] / 255F, wheelColor[1] / 255F, wheelColor[2] / 255F, 1.0F);
            this.blit(matrix, this.getGuiLeft() + 27, this.getGuiTop() + 85, 0, 198, energybarPixelWidth, 10);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.textureManager.bind(TEXTURE);

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
            List<ITextComponent> text = new ArrayList<>();
            switch (slotUnderMouse.index){
                case ContainerCarrier.SLOT_SOLAR: {
                    text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.empty_solar_slot"));
                    break;
                }
                case ContainerCarrier.SLOT_CORE: {
                    text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.empty_core_slot"));
                    break;
                }
                case ContainerCarrier.SLOT_GROUND: {
                    text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.empty_ground_slot"));
                    break;
                }
                case ContainerCarrier.SLOT_SOLAR_UPGRADE:
                case ContainerCarrier.SLOT_CORE_UPGRADE:
                case ContainerCarrier.SLOT_GROUND_UPGRADE: {
                    text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.empty_upgrade_slot"));
                    break;
                }
            }
            this.renderComponentTooltip(matrix, text, mouseX, mouseY);
        }

        // energy bar
        if(mouseX >= this.getGuiLeft() + 27 && mouseX <= this.getGuiLeft() + 27 + 141 && mouseY >= this.getGuiTop() + 85 && mouseY <= this.getGuiTop() + 85 + 10){
            // internal
            EnergyStorage energyStorage = this.getTile().getEnergyStorage();
            List<ITextComponent> text = new ArrayList<>();
            text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.energy.internal_stored", energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored()));
            // additional
            this.getTile().getAccuStorage().ifPresent(accu -> {
                text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.energy.additional_stored", accu.getEnergyStored(), accu.getMaxEnergyStored()));
            });
            text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.energy.produced", this.getTile().getProducedEnergy()).withStyle(TextFormatting.GRAY));
            text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.energy.wasted", this.getTile().getWastedEnergy()).withStyle(TextFormatting.GRAY));
            this.renderComponentTooltip(matrix, text, mouseX, mouseY);

        }

        this.renderTooltip(matrix, mouseX, mouseY);
    }

    private void renderSlotLevel(MatrixStack matrix, float depletion, int x, int y) {
        if(depletion > 0.0F){
            int depletionBarWidth = Math.max(1, Math.min(Math.round(depletion * 24), 36));
            this.blit(matrix, this.getGuiLeft() + x, this.getGuiTop() + y, 0, 195, depletionBarWidth, 3);
        }
    }

    private void renderSlotLevelHover(MatrixStack matrix, float depletion, ICarrierModule carrierModule, SynchroniseModuleData data, ICarrierModule.CarrierSlot slot, int mouseX, int mouseY, int x, int y){
        if(mouseX >= this.getGuiLeft() + x && mouseX <= this.getGuiLeft() + x + 36 && mouseY >= this.getGuiTop() + y && mouseY <= this.getGuiTop() + y + 3){
            List<EnergyProduction> energyProductions = this.getTile().getProductionForSlot(slot);
            List<EnergyPenalty> energyPenalties = this.getTile().getPenaltiesForSlot(slot);
            List<ITextComponent> text = new ArrayList<>();
            text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.depletion", Math.round(depletion * 100)));
            if(!energyProductions.isEmpty()){
                text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.producer").withStyle(TextFormatting.GRAY, TextFormatting.UNDERLINE));
                for (EnergyProduction producer : energyProductions) {
                    text.add(new StringTextComponent(" ").append(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.producer_self", producer.getEnergy(), producer.translateReason()).withStyle(TextFormatting.GRAY)));
                }
            }
            if(!energyPenalties.isEmpty() && !energyProductions.isEmpty()){
                text.add(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.penalty").withStyle(TextFormatting.GRAY, TextFormatting.UNDERLINE));
                for (EnergyPenalty penalty : energyPenalties) {
                    text.add(new StringTextComponent(" ").append(new TranslationTextComponent("container.miniaturepowerplant.carrier.hover.penalty_self", 100 - Math.round(penalty.getMultiplier() * 100), penalty.getReason()).withStyle(TextFormatting.GRAY)));
                }
            }

            ArrayList<ITextComponent> additionalText = new ArrayList<>();
            carrierModule.addDepletionInformation(this, data, additionalText);
            if(!energyProductions.isEmpty() && !additionalText.isEmpty()){
                text.add(new StringTextComponent(""));
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
