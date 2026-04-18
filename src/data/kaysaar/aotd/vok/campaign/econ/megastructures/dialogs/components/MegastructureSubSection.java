package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import ashlib.data.plugins.ui.models.resizable.ButtonComponent;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MegastructureSubSection implements ExtendedUIPanelPlugin {


    public float sppedOfSpin = 35;
    ButtonComponent component;
    CustomPanelAPI parentPanel;
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    transient SpriteAPI spriteToRender2 = Global.getSettings().getSprite("ui", "hud_circle");

    ArrayList<SpecialProjectStageWindow.SpecialProjectPointOfInterest> panelsConnect = new ArrayList<>();
    CustomPanelAPI panelInfoOfStage;
    CustomPanelAPI tooltipPanel;
    public BaseMegastructureScript megastructureScript;
    public BaseMegastructureSection section;
    public MegastructureViewSection sectionMain;
    // Rendering modes
    public enum RenderingMode {
        STRAIGHT, STEPPED
    }

    public enum OriginMode {
        HORIZONTAL, VERTICAL
    }
    boolean currentlyChosen = false;

    public void setCurrentlyChosen(boolean currentlyChosen) {
        this.currentlyChosen = currentlyChosen;
    }

    private SpecialProjectStageWindow.RenderingMode renderingMode = SpecialProjectStageWindow.RenderingMode.STEPPED;  // Default line mode
    private SpecialProjectStageWindow.OriginMode originMode = SpecialProjectStageWindow.OriginMode.HORIZONTAL;  // Default origin mode

    public void setRenderingMode(SpecialProjectStageWindow.RenderingMode mode) {
        this.renderingMode = mode;
    }

    public void setOriginMode(SpecialProjectStageWindow.OriginMode mode) {
        this.originMode = mode;
    }

    public MegastructureSubSection(BaseMegastructureScript megastructureScript, BaseMegastructureSection section, CustomPanelAPI parentPanel,
                                   SpecialProjectStageWindow.RenderingMode mode, SpecialProjectStageWindow.OriginMode origin,
                                   Vector2f posToPlace, ArrayList<Vector2f> posToConnect,MegastructureViewSection viewSection) {
        this.parentPanel = parentPanel;
        this.sectionMain = viewSection;
        this.megastructureScript = megastructureScript;
        this.section = section;
        setRenderingMode(mode);
        setOriginMode(origin);
        panelInfoOfStage = Global.getSettings().createCustom(190, 200, this);
        TooltipMakerAPI tooltip = initalizeTooltip(section, parentPanel);
        panelInfoOfStage.addComponent(tooltipPanel).inTL(0, 0);
        panelInfoOfStage.getPosition().setSize(190, tooltip.getHeightSoFar() );

        ;

        parentPanel.addComponent(panelInfoOfStage).inTL(posToPlace.x, posToPlace.y);
        for (Vector2f vector2f : posToConnect) {
            CustomPanelAPI childPanel = Global.getSettings().createCustom(1, 1, null);
            parentPanel.addComponent(childPanel).inTL(vector2f.x, vector2f.y);
            panelsConnect.add(new SpecialProjectStageWindow.SpecialProjectPointOfInterest(childPanel, Misc.random.nextBoolean()));

        }


    }

    @NotNull
    private TooltipMakerAPI initalizeTooltip(BaseMegastructureSection stage, CustomPanelAPI parentPanel) {
        tooltipPanel = Global.getSettings().createCustom(190, 95, null);

        TooltipMakerAPI tooltip = createTooltip(stage, parentPanel, tooltipPanel);
        tooltipPanel.addUIElement(tooltip).inTL(0, 0);
        return tooltip;
    }

    @NotNull
    private TooltipMakerAPI createTooltip(BaseMegastructureSection stage, CustomPanelAPI parentPanel, CustomPanelAPI panelInfoOfStage) {
        TooltipMakerAPI tooltip = panelInfoOfStage.createUIElement(panelInfoOfStage.getPosition().getWidth(), parentPanel.getPosition().getHeight(), false);
        tooltip.setParaFont(Fonts.ORBITRON_12);
         component = new MegastructureSectionButton(190,95,stage,true){
             @Override
             public void performActionOnClick(boolean isRightClick) {
                 sectionMain.setCurrentlyChosenSection(this.section);
             }
         };
        tooltip.addCustom(component.getComponentPanel(),2f).getPosition().inTL(0,0);
        tooltip.addPara(stage.getName(),-20f).getPosition().aboveLeft(component.getComponentPanel(),1);
        tooltip.setHeightSoFar(95);

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
        spriteToRender2.setColor(new Color(191, 253, 245));
        if(section.isRestored()){
            spriteToRender2.setColor(Misc.getPositiveHighlightColor());
        }
        else{
            if(section.isRestoring){
                spriteToRender2.setColor(new Color(179, 80, 39));
            }
        }
        if(currentlyChosen){
            spriteToRender2.setColor(Color.ORANGE);
        }


        for (SpecialProjectStageWindow.SpecialProjectPointOfInterest childPanel : panelsConnect) {
            float cx = childPanel.panel.getPosition().getCenterX();
            float cy = childPanel.panel.getPosition().getCenterY();
            drawCircleStencil(cx, cy, 20, 32); // 32 segments
        }

        // Step 2: Allow drawing only where stencil is NOT equal to 1 (i.e., outside the circle)
        GL11.glColorMask(true, true, true, true);
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

        // --- Render the connection lines ---
        for (SpecialProjectStageWindow.SpecialProjectPointOfInterest cs : panelsConnect) {
            CustomPanelAPI childPanel = cs.panel;
            float startX, startY, endX, endY;

            if (originMode == SpecialProjectStageWindow.OriginMode.HORIZONTAL) {
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

            if (renderingMode == SpecialProjectStageWindow.RenderingMode.STRAIGHT) {
                if (originMode == SpecialProjectStageWindow.OriginMode.HORIZONTAL) {
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

            } else if (renderingMode == SpecialProjectStageWindow.RenderingMode.STEPPED) {
                float midX, midY;

                if (originMode == SpecialProjectStageWindow.OriginMode.HORIZONTAL) {
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
        for (SpecialProjectStageWindow.SpecialProjectPointOfInterest childPanel : panelsConnect) {
            spriteToRender2.setAngle(childPanel.getAngleForRender());
            float cx = childPanel.panel.getPosition().getCenterX();
            float cy = childPanel.panel.getPosition().getCenterY();
            spriteToRender2.renderAtCenter(cx, cy);
        }
    }


    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        float speed = sppedOfSpin * amount;
        for (SpecialProjectStageWindow.SpecialProjectPointOfInterest interest : panelsConnect) {
            interest.setAngle(interest.getAngle() + speed);
        }
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return null;
    }

    public void createUI() {
        panelInfoOfStage.removeComponent(tooltipPanel);
        initalizeTooltip(section, parentPanel);
        panelInfoOfStage.addComponent(tooltipPanel).inTL(0, 0);
//        manager.getShowcaseProj().createUI();
    }

    @Override
    public void clearUI() {

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

}
