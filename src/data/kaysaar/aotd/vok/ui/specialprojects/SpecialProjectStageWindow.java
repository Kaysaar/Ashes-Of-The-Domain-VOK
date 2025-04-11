package data.kaysaar.aotd.vok.ui.specialprojects;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.info.WeaponInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.AoTDSpecialProject;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.AoTDSpecialProjectStageSpec;
import data.kaysaar.aotd.vok.scripts.specialprojects.OtherCostData;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class SpecialProjectStageWindow implements CustomUIPanelPlugin {
    public class SpecialProjectPointOfInterest {
        CustomPanelAPI panel;
        boolean rightDirectionSpin;
        public float angle;

        public SpecialProjectPointOfInterest(CustomPanelAPI panel, boolean rightDirectionSpin) {
            this.panel = panel;
            this.rightDirectionSpin = rightDirectionSpin;
            this.angle = 0;
        }

        public void setAngle(float angle) {
            if (rightDirectionSpin) {
                this.angle = -angle % 360;
            } else {
                this.angle = angle % 360;
            }
        }

        public float getAngle() {
            return Math.abs(angle);
        }

        public float getAngleForRender() {
            return angle;
        }
    }

    public AoTDSpecialProject project;

    public float sppedOfSpin = 35;
    ButtonAPI buttonOfStage;
    CustomPanelAPI parentPanel;
    UILinesRenderer renderer;
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    transient SpriteAPI spriteToRender2 = Global.getSettings().getSprite("ui", "hud_circle");

    ArrayList<SpecialProjectPointOfInterest> panelsConnect = new ArrayList<>();
    CustomPanelAPI panelInfoOfStage;


    // Rendering modes
    public enum RenderingMode {
        STRAIGHT, STEPPED
    }

    public enum OriginMode {
        HORIZONTAL, VERTICAL
    }

    private RenderingMode renderingMode = RenderingMode.STEPPED;  // Default line mode
    private OriginMode originMode = OriginMode.HORIZONTAL;  // Default origin mode

    public void setRenderingMode(RenderingMode mode) {
        this.renderingMode = mode;
    }

    public void setOriginMode(OriginMode mode) {
        this.originMode = mode;
    }

    public SpecialProjectStageWindow(AoTDSpecialProject project, String stageId, CustomPanelAPI parentPanel, RenderingMode mode, OriginMode origin, Vector2f posToPlace, ArrayList<Vector2f> posToConnect) {
        this.parentPanel = parentPanel;
        panelInfoOfStage = Global.getSettings().createCustom(350, 300, this);
        this.project = project;
        TooltipMakerAPI tooltip = createTooltip(stageId, parentPanel);
        setRenderingMode(mode);
        setOriginMode(origin);
        panelInfoOfStage.addUIElement(tooltip).inTL(0, 0);
        panelInfoOfStage.getPosition().setSize(350, tooltip.getHeightSoFar() + 5)
        ;
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(panelInfoOfStage);
        parentPanel.addComponent(panelInfoOfStage).inTL(posToPlace.x, posToPlace.y);
        for (Vector2f vector2f : posToConnect) {
            CustomPanelAPI childPanel = Global.getSettings().createCustom(1, 1, null);
            parentPanel.addComponent(childPanel).inTL(vector2f.x, vector2f.y);
            panelsConnect.add(new SpecialProjectPointOfInterest(childPanel, Misc.random.nextBoolean()));

        }


    }

    @NotNull
    private TooltipMakerAPI createTooltip(String stageSpecId, CustomPanelAPI parentPanel) {
        TooltipMakerAPI tooltip = panelInfoOfStage.createUIElement(panelInfoOfStage.getPosition().getWidth(), parentPanel.getPosition().getHeight(), false);
        AoTDSpecialProjectStageSpec spec = SpecialProjectSpecManager.getStageSpec(stageSpecId);
        tooltip.addTitle(spec.getName());

        tooltip.addSectionHeading("GP Resource cost", Alignment.MID, 5f);
        CustomPanelAPI custom = MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed(panelInfoOfStage.getPosition().getWidth() + 40, 20, 20, spec.getGpCost(), new HashMap<>());
        tooltip.addCustom(custom, 5f);
        tooltip.addSectionHeading("Stage starting cost", Alignment.MID, 5f);
        tooltip.addPara("Credits : " + Misc.getDGSCredits(spec.getCreditCosts()), Color.ORANGE, 5f);

        for (OtherCostData s : spec.getOtherCosts()) {
            tooltip.addCustom(getItemLabel(s), 5f);
        }
        tooltip.addSectionHeading("Progress", Alignment.MID, 5f);
        ProgressBarComponent component = new ProgressBarComponent(panelInfoOfStage.getPosition().getWidth() - 15, 21, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 5f);
        LabelAPI label = tooltip.addPara("Current progress of stage : 0%", Misc.getTooltipTitleAndLightHighlightColor(), 3f);
        buttonOfStage = tooltip.addButton("Start stage", null, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.ALL, panelInfoOfStage.getPosition().getWidth() - 10, 20, 5f);
        buttonOfStage.setEnabled(false);
        label.getPosition().setXAlignOffset(panelInfoOfStage.getPosition().getWidth() / 2 - (label.computeTextWidth(label.getText()) / 2));
        buttonOfStage.getPosition().inTL(5, -buttonOfStage.getPosition().getY() - 20);
        return tooltip;
    }

    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
        spriteToRender.setColor(new Color(176, 176, 176));
        spriteToRender.setAlphaMult(alphaMult);

        // --- Begin stencil setup to exclude circle area ---
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilMask(0xFF);

        // Step 1: Write 1s where the circles will be drawn
        GL11.glColorMask(false, false, false, false);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        spriteToRender2.setSize(50, 50);
        spriteToRender2.setAlphaMult(alphaMult); // Fully opaque for mask write
//        spriteToRender2.setColor(new Color(255, 166, 0));
        spriteToRender2.setColor(Misc.getDarkHighlightColor());
        for (SpecialProjectPointOfInterest childPanel : panelsConnect) {
            float cx = childPanel.panel.getPosition().getCenterX();
            float cy = childPanel.panel.getPosition().getCenterY();
            drawCircleStencil(cx, cy, 20, 32); // 32 segments
        }

        // Step 2: Allow drawing only where stencil is NOT equal to 1 (i.e., outside the circle)
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // --- Render the connection lines ---
        for (SpecialProjectPointOfInterest cs : panelsConnect) {
            CustomPanelAPI childPanel = cs.panel;
            float startX, startY, endX, endY;

            if (originMode == OriginMode.HORIZONTAL) {
                startY = panelInfoOfStage.getPosition().getCenterY();
                startX = (childPanel.getPosition().getCenterX() > panelInfoOfStage.getPosition().getCenterX())
                        ? panelInfoOfStage.getPosition().getX() + panelInfoOfStage.getPosition().getWidth()
                        : panelInfoOfStage.getPosition().getX();
            } else {
                startX = panelInfoOfStage.getPosition().getCenterX();
                startY = (childPanel.getPosition().getCenterY() > panelInfoOfStage.getPosition().getCenterY())
                        ? panelInfoOfStage.getPosition().getY() + panelInfoOfStage.getPosition().getHeight()
                        : panelInfoOfStage.getPosition().getY();
            }

            endX = childPanel.getPosition().getCenterX();
            endY = childPanel.getPosition().getCenterY();

            float totalDistanceX = Math.abs(endX - startX);
            float totalDistanceY = Math.abs(endY - startY);
            float halfDistanceX = totalDistanceX / 2;
            float halfDistanceY = totalDistanceY / 2;

            if (renderingMode == RenderingMode.STRAIGHT) {
                if (originMode == OriginMode.HORIZONTAL) {
                    spriteToRender.setSize(totalDistanceX, 1);
                    spriteToRender.render(Math.min(startX, endX), startY);
                    spriteToRender.setSize(1, Math.abs(endY - startY));
                    spriteToRender.render(endX, Math.min(startY, endY));
                } else {
                    spriteToRender.setSize(1, totalDistanceY);
                    spriteToRender.render(startX, Math.min(startY, endY));
                    spriteToRender.setSize(Math.abs(endX - startX), 1);
                    spriteToRender.render(Math.min(startX, endX), endY);
                }

            } else if (renderingMode == RenderingMode.STEPPED) {
                float midX, midY;

                if (originMode == OriginMode.HORIZONTAL) {
                    midX = startX + ((endX > startX) ? halfDistanceX : -halfDistanceX);
                    spriteToRender.setSize(halfDistanceX, 1);
                    spriteToRender.render(Math.min(startX, midX), startY);
                    spriteToRender.setSize(1, Math.abs(endY - startY));
                    spriteToRender.render(midX, Math.min(startY, endY));
                    spriteToRender.setSize(halfDistanceX, 1);
                    spriteToRender.render(Math.min(midX, endX), endY);
                } else {
                    midY = startY + ((endY > startY) ? halfDistanceY : -halfDistanceY);
                    spriteToRender.setSize(1, Math.abs(midY - startY));
                    spriteToRender.render(startX, Math.min(startY, midY));
                    spriteToRender.setSize(Math.abs(endX - startX), 1);
                    spriteToRender.render(Math.min(startX, endX), midY);
                    spriteToRender.setSize(1, Math.abs(endY - midY));
                    spriteToRender.render(endX, Math.min(midY, endY));
                }
            }
        }

        // Step 3: Disable stencil and draw circles normally
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        spriteToRender2.setAlphaMult(alphaMult);
        for (SpecialProjectPointOfInterest childPanel : panelsConnect) {
            spriteToRender2.setAngle(childPanel.getAngleForRender());
            float cx = childPanel.panel.getPosition().getCenterX();
            float cy = childPanel.panel.getPosition().getCenterY();
            spriteToRender2.renderAtCenter(cx, cy);
        }
    }


    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);

    }

    @Override
    public void advance(float amount) {
        float speed = sppedOfSpin * amount;
        for (SpecialProjectPointOfInterest interest : panelsConnect) {
            interest.setAngle(interest.getAngle() + speed);
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
    }

    private void drawCircleStencil(float centerX, float centerY, float radius, int segments) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(centerX, centerY);
        for (int i = 0; i <= segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            float x = (float) (centerX + Math.cos(angle) * radius);
            float y = (float) (centerY + Math.sin(angle) * radius);
            GL11.glVertex2f(x, y);
        }
        GL11.glEnd();
    }

    @Override
    public void buttonPressed(Object buttonId) {
    }

    private CustomPanelAPI getItemLabel(OtherCostData data) {
        if (data.getAmount() == 0) return null;
        CustomPanelAPI panel = Global.getSettings().createCustom(panelInfoOfStage.getPosition().getWidth(), 40, null);
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(40, 40, false);
        TooltipMakerAPI labelTooltip = panel.createUIElement(panel.getPosition().getWidth() - 40, 40, false);
        LabelAPI labelAPI1 = null;
        if (data.getCostType().equals(OtherCostData.CostType.COMMODITY)) {
            tooltipMakerAPI.addImage(Global.getSettings().getCommoditySpec(data.getId()).getIconName(), 40, 40, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getCommoditySpec(data.getId()).getName() + " : " + data.getAmount(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 5, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(data.getId(), Submarkets.SUBMARKET_STORAGE,data.costType));
        }
        if (data.getCostType().equals(OtherCostData.CostType.ITEM)) {
            tooltipMakerAPI.addImage(Global.getSettings().getSpecialItemSpec(data.getId()).getIconName(), 40, 40, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getSpecialItemSpec(data.getId()).getName() + " : " + data.getAmount(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 5, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(data.getId(), Submarkets.SUBMARKET_STORAGE,data.costType));
        }
        if (data.getCostType().equals(OtherCostData.CostType.SHIP)) {
            tooltipMakerAPI.addCustom(ShipInfoGenerator.getShipImage(Global.getSettings().getHullSpec(data.getId()), 40, null).one, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getHullSpec(data.getId()).getHullName() + " : " + data.getAmount(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 5, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(data.getId(), Submarkets.SUBMARKET_STORAGE,data.costType));
        }
        if (data.getCostType().equals(OtherCostData.CostType.WEAPON)) {
            tooltipMakerAPI.addCustom(WeaponInfoGenerator.getImageOfWeapon(Global.getSettings().getWeaponSpec(data.getId()), 40).one, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getWeaponSpec(data.getId()).getWeaponName() + " : " + data.getAmount(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 5, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(data.getId(), Submarkets.SUBMARKET_STORAGE,data.costType));
        }
        if (data.getCostType().equals(OtherCostData.CostType.FIGHTER)) {
            tooltipMakerAPI.addCustom(FighterInfoGenerator.createFormationPanel(Global.getSettings().getFighterWingSpec(data.getId()), FormationType.BOX, 40, Global.getSettings().getFighterWingSpec(data.getId()).getNumFighters()).one, 10f);
            labelAPI1 = labelTooltip.addPara(Global.getSettings().getFighterWingSpec(data.getId()).getWingName() + " : " + data.getAmount(), 10f);
            labelTooltip.addPara("You have %s located in Local Storages", 5, Color.ORANGE, "" + (int) AoTDMisc.retrieveAmountOfItems(data.getId(), Submarkets.SUBMARKET_STORAGE,data.costType));
        }

        if (GPManager.getInstance().haveMetReqForItem(data.getId(), data.getAmount(),data.costType)) {
            labelAPI1.setColor(Misc.getPositiveHighlightColor());

        } else {
            labelAPI1.setColor(Misc.getNegativeHighlightColor());
        }
        labelAPI1.autoSizeToWidth(panelInfoOfStage.getPosition().getWidth() - 40);
        panel.addUIElement(tooltipMakerAPI).inTL(-8, -10);
        panel.addUIElement(labelTooltip).inTL(40, -8);
        return panel;
    }
}



