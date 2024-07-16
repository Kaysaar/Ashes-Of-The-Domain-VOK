package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchProject;
import data.kaysaar.aotd.vok.scripts.research.models.SpecialProjectStage;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.components.*;
import org.apache.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class AoTDResearchUI implements CustomUIPanelPlugin {
    public static final float WIDTH = Global.getSettings().getScreenWidth() - 5;
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 50;
    PositionAPI pos;
    float oppacity = 0.0f;
    Color bgColor = new Color(6, 35, 40, 42);
    Color uiColor = Global.getSector().getPlayerFaction().getDarkUIColor();
    public static final Logger log = Global.getLogger(AoTDResearchUI.class);
    public static final float ENTRY_HEIGHT = 40;
    public static final float ENTRY_WIDTH = WIDTH - 7f;
    public static final float CONTENT_HEIGHT = 80;
    protected InteractionDialogAPI dialog;
    protected CustomVisualDialogDelegate.DialogCallbacks callbacks;
    protected CustomPanelAPI panel;
    protected CustomPanelAPI mainPanel;
    public UIMODE mode = UIMODE.TECH_TREE;
    public String selected = null;
    public ArrayList<ButtonAPI> buttons = new ArrayList<>();

    boolean madeDecision = false;
    HorizontalTooltipMaker horizontalTooltipMaker = new HorizontalTooltipMaker();
    float scroller = 0f;
    float scrollerOfEventText = 0f;
    float scrolerOfOutcome = 0f;

    private final float spaceBetweenWidth = (Global.getSettings().getScreenWidth() - WIDTH) / 2;
    private final float spaceBetweenHeight = (Global.getSettings().getScreenHeight() - HEIGHT) / 2;

    private int stencilMask1 = 0x1;
    private int stencilMask2 = 0x2;
    protected CustomPanelAPI researchCenterPanel;
    protected TooltipMakerAPI researchCenterTooltip;
    protected CustomPanelAPI helpPanel;
    protected TooltipMakerAPI helpTooltip;
    protected CustomPanelAPI techTreePanel;
    protected TooltipMakerAPI techTreeTooltip;
    protected CustomPanelAPI buttonPanel;
    protected TooltipMakerAPI buttonPanelTooltip;
    protected CustomPanelAPI specialProjectListPanel;
    protected TooltipMakerAPI specialProjectListTooltip;

    protected CustomPanelAPI techTreeButtonPanel;
    protected TooltipMakerAPI techTreeButtonTooltip;
    protected CustomPanelAPI specialProjectTitlePanel;
    protected TooltipMakerAPI specialProjectTitleTooltip;
    protected CustomPanelAPI specialProjectProgressionBarPanel;
    protected TooltipMakerAPI specialProjectProgressionBarTooltip;
    protected CustomPanelAPI specialProjectSectionOptionsPanel;
    protected TooltipMakerAPI specialProjectSectionOptionsTooltip;
    UIUtilis openGlUtilis = new UIUtilis();
    float Xoffset = 0f;
    float prevOffset = 0f;
    float Yoffset = 0f;
    TechTreeCore techTreeCoreUI = new TechTreeCore(AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchRepoOfFaction());
    ResearchOption wantsToKnow = null;
    ResearchOption prevWantsToKnow = null;
    String currentModToShow = "aotd_vok";
    ResearchOption researchingBeforeUI = AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus();
    ResearchOption researching = AoTDMainResearchManager.getInstance().getManagerForPlayer().getCurrentFocus();
    AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction();
    ResearchCenterPanel researchCenterPanelUI;
    HelpPanel helpButtonPanelYU;
    SpecialProjectButtonPanel buttonPanelUI;
    TechTreeButtonPanel techTreeButtonPanelUI;
    ResearchProjectListComponent projectListUI;
    ProjectTitleComponent projectTitleUI;
    SpecialProjectBarComponent projectBarUI;
    StageEventComponent projectStageOptions;

    ResearchProject currentlyFocused = AoTDMainResearchManager.getInstance().getCurrentProject();
    @Override
    public void positionChanged(PositionAPI position) {
        this.pos = position;
    }

    public void createUIForSpecialProjects() {
        techTreeButtonPanel = mainPanel.createCustomPanel(300, 102, null);
        techTreeButtonTooltip = techTreeButtonPanel.createUIElement(300, 102, false);

        specialProjectListPanel = mainPanel.createCustomPanel(300, HEIGHT - 150, null);
        specialProjectListTooltip = specialProjectListPanel.createUIElement(310, HEIGHT - 150, false);

        helpPanel = mainPanel.createCustomPanel(300, 51, null);
        helpTooltip = helpPanel.createUIElement(300, 51, false);


        techTreeButtonPanelUI = new TechTreeButtonPanel();
        techTreeButtonPanelUI.init(mainPanel, techTreeButtonPanel, techTreeButtonTooltip);
        techTreeButtonPanelUI.createUI();
        techTreeButtonPanelUI.placeTooltip(0, 0);
        techTreeButtonPanelUI.placeSubPanel(0, -2);


        projectListUI = new ResearchProjectListComponent();
        projectListUI.init(mainPanel, specialProjectListPanel, specialProjectListTooltip);
        projectListUI.setHeight(HEIGHT - 150);
        projectListUI.createUI();
        projectListUI.placeTooltip(-5, 0);
        projectListUI.placeSubPanel(0, 100);

        helpButtonPanelYU.init(mainPanel, helpPanel, helpTooltip);
        helpButtonPanelYU.createUI();
        helpButtonPanelYU.placeTooltip(0, 0);
        helpButtonPanelYU.placeSubPanel(0, HEIGHT - 50);

        if(currentlyFocused!=null){

            specialProjectTitlePanel =  mainPanel.createCustomPanel(WIDTH - 300, 51, null);
            specialProjectTitleTooltip =  specialProjectTitlePanel.createUIElement(WIDTH - 300, 51, false);

            specialProjectProgressionBarPanel =  mainPanel.createCustomPanel(WIDTH - 300, HEIGHT*0.54f, null);
            specialProjectProgressionBarTooltip =  specialProjectProgressionBarPanel.createUIElement(WIDTH - 290, HEIGHT*0.54f, false);

            specialProjectSectionOptionsPanel =  mainPanel.createCustomPanel(WIDTH - 300, HEIGHT-50-(HEIGHT*0.54f), null);
            specialProjectSectionOptionsTooltip =  specialProjectSectionOptionsPanel.createUIElement(WIDTH -290, 30, false);


            projectTitleUI = new ProjectTitleComponent();
            projectTitleUI.init(mainPanel, specialProjectTitlePanel, specialProjectTitleTooltip);
            projectTitleUI.setProject(currentlyFocused);
            projectTitleUI.createUI();
            projectTitleUI.placeTooltip(-5, 0);
            projectTitleUI.placeSubPanel(specialProjectListPanel.getPosition().getX() + specialProjectListPanel.getPosition().getWidth() + 10, -2);

            projectBarUI = new SpecialProjectBarComponent();
            projectBarUI.setWidthOfBar(WIDTH - 300);
            projectBarUI.setHeightOfUI(HEIGHT*0.54f);
            projectBarUI.setProjectOfIntrest(currentlyFocused);
            projectBarUI.init(mainPanel, specialProjectProgressionBarPanel, specialProjectProgressionBarTooltip);
            projectBarUI.createUI();
            projectBarUI.placeTooltip(-5, 0);
            projectBarUI.placeSubPanel(specialProjectListPanel.getPosition().getX() + specialProjectListPanel.getPosition().getWidth() + 10, specialProjectTitlePanel.getPosition().getHeight()-1);

            projectStageOptions = new StageEventComponent();
            projectStageOptions.init(mainPanel, specialProjectSectionOptionsPanel, specialProjectSectionOptionsTooltip);
            projectStageOptions.setWidth(WIDTH - 300);
            projectStageOptions.setHeight(HEIGHT-50-(HEIGHT*0.54f));
            projectStageOptions.setCurrentProject(currentlyFocused);
            projectStageOptions.createUI();
            projectStageOptions.placeTooltip(-5, 0);
            projectStageOptions.placeSubPanel(specialProjectListPanel.getPosition().getX() + specialProjectListPanel.getPosition().getWidth() + 10, specialProjectProgressionBarPanel.getPosition().getHeight()+51);
        }





        panel.addComponent(mainPanel).inTL(2, 0);
    }

    public void createUIForTechInfo() {

        techTreeCoreUI.currentModToShow = currentModToShow;
        researchCenterPanel = mainPanel.createCustomPanel(300, HEIGHT - 150, null);
        researchCenterTooltip = researchCenterPanel.createUIElement(310, HEIGHT - 150, false);

        helpPanel = mainPanel.createCustomPanel(300, 51, null);
        helpTooltip = helpPanel.createUIElement(300, 51, false);

        techTreePanel = mainPanel.createCustomPanel(WIDTH - 300, HEIGHT - 20, new handlerOfScrollbar(horizontalTooltipMaker));
        horizontalTooltipMaker.init(techTreePanel, WIDTH - 300, HEIGHT - 20, true, techTreeCoreUI.calculateWidthAndHeight().one, techTreeCoreUI.calculateWidthAndHeight().two);
        techTreeTooltip = horizontalTooltipMaker.getMainTooltip();

        buttonPanel = mainPanel.createCustomPanel(300, 102, null);
        buttonPanelTooltip = buttonPanel.createUIElement(300, 102, false);
        researchCenterPanelUI = new ResearchCenterPanel();
        helpButtonPanelYU = new HelpPanel();
        buttonPanelUI = new SpecialProjectButtonPanel();

        researchCenterPanelUI.init(mainPanel, researchCenterPanel, researchCenterTooltip);
        researchCenterPanelUI.setHeight(HEIGHT - 150);
        researchCenterPanelUI.createUI();
        researchCenterPanelUI.placeTooltip(-5, 0);
        researchCenterPanelUI.placeSubPanel(0, 100);

        helpButtonPanelYU.init(mainPanel, helpPanel, helpTooltip);
        helpButtonPanelYU.createUI();
        helpButtonPanelYU.placeTooltip(0, 0);
        helpButtonPanelYU.placeSubPanel(0, HEIGHT - 50);

        buttonPanelUI.init(mainPanel, buttonPanel, buttonPanelTooltip);
        buttonPanelUI.createUI();
        buttonPanelUI.placeTooltip(0, 0);
        buttonPanelUI.placeSubPanel(0, -2);


        techTreeCoreUI.init(mainPanel, techTreePanel, techTreeTooltip);

        techTreeCoreUI.createUI(researchCenterPanel.getPosition().getX() + researchCenterPanel.getPosition().getWidth() + 10, -2);

        horizontalTooltipMaker.getHorizontalScrollbar().setPosition(techTreePanel.getPosition().getX()  + spaceBetweenWidth, techTreePanel.getPosition().getY() + spaceBetweenHeight - 15);
        horizontalTooltipMaker.getHorizontalScrollbar().setScrollbarDimensions(30, 10);

        if (Xoffset == 0 && prevOffset == 0) {
            Xoffset = horizontalTooltipMaker.getHorizontalScrollbar().getXOffset();
            prevOffset = horizontalTooltipMaker.getHorizontalScrollbar().getCurrOffset();

        }
        horizontalTooltipMaker.getMainTooltip().getExternalScroller().setXOffset(prevOffset);
        horizontalTooltipMaker.getMainTooltip().getExternalScroller().setYOffset(Yoffset);
        panel.addComponent(mainPanel).inTL(2, 0);
        buttons.addAll(techTreeCoreUI.getAllButtons());
        if (researching != null) {
            for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                    if (researchOptionPanel.TechToResearch.Id.equals(researching.Id)) {
                        prevOffset = researchOptionPanel.x-550 + era.panel.getPosition().getX()-5;
                        Xoffset = researchOptionPanel.getCoordinates().getX()-40;
                    }
                }
            }
        } else {
            for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                    if (researchOptionPanel.TechToResearch.isResearched) {
                        prevOffset = researchOptionPanel.x-550 + era.panel.getPosition().getX()-5;
                        Xoffset = researchOptionPanel.getCoordinates().getX();
                    }
                }
            }
        }

        if (prevOffset <=  techTreeCoreUI.calculateWidthAndHeight().one ) {
            prevOffset = 0;
            Xoffset = 0;

        }
        if (prevOffset != 0) {
            horizontalTooltipMaker.getMainTooltip().getExternalScroller().setXOffset(-prevOffset + AoTDUiComp.WIDTH_OF_TECH_PANEL);
        }
        horizontalTooltipMaker.getMainTooltip().getExternalScroller().setYOffset(Yoffset);
        horizontalTooltipMaker.getHorizontalScrollbar().setScrollbarAfterinit(WIDTH - 300, (prevOffset) / 4000);
    }


    @Override
    public void renderBelow(float alphaMult) {
        if (bgColor == null) return;

        float x = pos.getX();
        float y = pos.getY();
        float w = pos.getWidth();
        float h = pos.getHeight();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(bgColor.getRed() / 255f, bgColor.getGreen() / 255f, bgColor.getBlue() / 255f,
                bgColor.getAlpha() / 255f * alphaMult * 0.01f);
        GL11.glRectf(x, y, x + w, y + h);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        //GL11.glScalef(1/uiscale, 1/uiscale, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Color color = new Color(234, 246, 253, 255);
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
        if (techTreeCoreUI != null && techTreePanel != null && !techTreeCoreUI.Eras.isEmpty()) {
            glClear(GL_STENCIL_BUFFER_BIT);
            glColorMask(false, false, false, false); //disable colour
            glEnable(GL_STENCIL_TEST); //enable stencil
            openGlUtilis.drawmask(techTreePanel);
            glStencilFunc(GL_ALWAYS, 1, 0xff); // Do not test the current value in the stencil buffer, always accept any value on there for drawing
            glStencilMask(0xff);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE); // Make every test succeed
            openGlUtilis.drawmask(techTreePanel);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP); // Make sure you will no longer (over)write stencil values, even if any test succeeds
            glColorMask(true, true, true, true); // Make sure we draw on the backbuffer again.
            glStencilFunc(GL_EQUAL, 1, 0xFF); // Now we will only draw pixels where the corresponding stencil buffer value equals 1
            for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                for (TechTreeResearchOptionPanel panels : era.getResearchOptionPanels()) {
                    if (panels.TechToResearch.isResearched()) {
                        openGlUtilis.drawProgressionBar(panels.getProgressionBar().getPosition(), 231, color, 1);

                    } else {
                        openGlUtilis.drawProgressionBar(panels.getProgressionBar().getPosition(), 231, color, AoDUtilis.calculatePercentOfProgression(panels.TechToResearch));

                    }
                }

            }

            glDisable(GL_STENCIL_TEST);
        }
        else{
            if(specialProjectProgressionBarPanel!=null)  {
                openGlUtilis.drawProgressionBar(projectBarUI.getBar().getPosition(),alphaMult,Misc.getTooltipTitleAndLightHighlightColor(),projectBarUI.projectOfIntrest.calculateProgress());
            }

        }
    }


    @Override
    public void render(float alphaMult) {
        GL11.glPushMatrix();
        //GL11.glScalef(1/uiscale, 1/uiscale, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Color color = Misc.getDarkPlayerColor();
        Color colorResearched = color.brighter().brighter();
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
        openGlUtilis.drawMainPanelBorder(panel, WIDTH, HEIGHT);
        openGlUtilis.drawMainPanelBorderSecondLayer(panel, WIDTH, HEIGHT);
        if (mode.equals(UIMODE.TECH_TREE)) {
            if (techTreeCoreUI != null && techTreePanel != null && !techTreeCoreUI.Eras.isEmpty()) {
                glClear(GL_STENCIL_BUFFER_BIT);
                glColorMask(false, false, false, false); //disable colour
                glEnable(GL_STENCIL_TEST); //enable stencil
                openGlUtilis.drawmask(techTreePanel);
                glStencilFunc(GL_ALWAYS, 1, 0xff); // Do not test the current value in the stencil buffer, always accept any value on there for drawing
                glStencilMask(0xff);
                glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE); // Make every test succeed
                openGlUtilis.drawmask(techTreePanel);
                glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP); // Make sure you will no longer (over)write stencil values, even if any test succeeds
                glColorMask(true, true, true, true); // Make sure we draw on the backbuffer again.
                glStencilFunc(GL_EQUAL, 1, 0xFF); // Now we will only draw pixels where the corresponding stencil buffer value equals 1
                for (TechTreeEraSection era : techTreeCoreUI.Eras) {

                    for (TechTreeResearchOptionPanel panels : era.getResearchOptionPanels()) {

                        openGlUtilis.drawTopPanelBorder(panels.getProgressionBar(), Misc.getDarkPlayerColor(), alphaMult);
                    }

                }
                for (ResearchOption allResearchOption : techTreeCoreUI.allResearchOptions) {
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
                    if (techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id) == null) continue;
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);

                    openGlUtilis.drawPanelBorder(techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id),allResearchOption.isResearched,alphaMult,colorResearched);

                    int alligment = 0;
                    int amount = 0;
                    GL11.glColor4f(colorResearched.getRed() / 255f, colorResearched.getGreen() / 255f, colorResearched.getBlue() / 255f, alphaMult);

                    for (ResearchOption researchOption : techTreeCoreUI.allResearchOptions) {
                        for (String s : researchOption.ReqTechsToResearchFirst) {
                            if (s.equals(allResearchOption.Id)&&allResearchOption.isResearched()) {

                                if (techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id) == null || techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(researchOption.Id) == null)
                                    continue;
                                openGlUtilis.drawTechLine(techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id), techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(researchOption.Id), -alligment);
                            }
                        }
                    }
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);

                    for (ResearchOption researchOption : techTreeCoreUI.allResearchOptions) {
                        for (String s : researchOption.ReqTechsToResearchFirst) {
                            if (s.equals(allResearchOption.Id)&&!allResearchOption.isResearched) {

                                if (techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id) == null || techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(researchOption.Id) == null)
                                    continue;
                                openGlUtilis.drawTechLine(techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(allResearchOption.Id), techTreeCoreUI.retrieveCoordinatesOfSpecificPanel(researchOption.Id), -alligment);
                            }
                        }
                    }


                }


                glDisable(GL_STENCIL_TEST);
            }
            glDisable(GL_STENCIL_TEST);
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);

            if (helpPanel != null) openGlUtilis.drawPanelBorder(helpPanel);
            if (techTreePanel != null) openGlUtilis.drawPanelBorder(techTreePanel);
            if (helpPanel != null) openGlUtilis.drawPanelBorder(buttonPanel);
            if (researchCenterPanel != null&&researchCenterPanelUI!=null) {

                openGlUtilis.drawPanelBorder(researchCenterPanel);
                openGlUtilis.drawPanelBorder(researchCenterPanelUI.getCoverPanel());
                openGlUtilis.drawPanelBorder(researchCenterPanelUI.getImagePanel());
                openGlUtilis.drawPanelBorder(researchCenterPanelUI.getBonusPanel());
                openGlUtilis.drawPanelBorder(researchCenterPanelUI.getModPanel());
            }

            if (horizontalTooltipMaker != null && horizontalTooltipMaker.getHorizontalScrollbar() != null&&horizontalTooltipMaker.mainPanel!=null) {
                horizontalTooltipMaker.getHorizontalScrollbar().displayScrollbar(Misc.getDarkPlayerColor(), alphaMult);
            }
//            if (techTreeCoreUI != null && techTreePanel != null && !techTreeCoreUI.Eras.isEmpty()) {
//                glEnable(GL_STENCIL_TEST);
//
//                // First Stencil Mask
//                glClear(GL_STENCIL_BUFFER_BIT);
//                glColorMask(false, false, false, false); // Disable color
//                glStencilFunc(GL_ALWAYS, 1, 0xff);
//                glStencilMask(0xff);
//                glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
//                openGlUtilis.drawmask(techTreePanel);
//                glColorMask(true, true, true, true); // Re-enable color
//                glStencilFunc(GL_EQUAL, 1, 0xFF);
//
//                for (TechTreeEraSection era : techTreeCoreUI.Eras) {
//                    for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
//                        for (Map.Entry<String, CustomPanelAPI> hexagon : researchOptionPanel.getHexagons().entrySet()) {
//                            // Scissor test to limit rendering to the area inside the first stencil mask
//                            glEnable(GL_SCISSOR_TEST);
//                            glScissor((int) techTreePanel.getPosition().getX(), (int) techTreePanel.getPosition().getY(), (int) techTreePanel.getPosition().getWidth(), (int) techTreePanel.getPosition().getHeight());
//
//                            // Second Stencil Mask (Circle)
//                            glClear(GL_STENCIL_BUFFER_BIT);
//                            glColorMask(false, false, false, false); // Disable color
//                            glStencilFunc(GL_ALWAYS, 2, 0xff);
//                            glStencilMask(0xff);
//                            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
//                            openGlUtilis.drawCircle(hexagon.getValue().getPosition().getCenterX(), hexagon.getValue().getPosition().getCenterY(), (hexagon.getValue().getPosition().getWidth()) / 2.0f);
//                            glColorMask(true, true, true, true); // Re-enable color
//                            glStencilFunc(GL_EQUAL, 2, 0xFF);
//                            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
//                            Render sprite only where both stencil values are present
//                            SpriteAPI sprite = Global.getSettings().getSprite(TechTreeResearchOptionPanel.returnSpriteId(hexagon.getKey()));
//                            if (sprite != null) {
//                                sprite.setSize(55, 55);
//                                sprite.setAlphaMult(alphaMult*0.8f);
//                                sprite.renderAtCenter(hexagon.getValue().getPosition().getCenterX(), hexagon.getValue().getPosition().getCenterY());
//                            }
//                            glStencilFunc(GL_EQUAL, 1, 0xFF);
//                            glDisable(GL_SCISSOR_TEST);
//                        }
//                    }
//                }
//
//                glDisable(GL_STENCIL_TEST);
//
//                glEnable(GL_STENCIL_TEST);
//
//                // First Stencil Mask
//                glClear(GL_STENCIL_BUFFER_BIT);
//                glColorMask(false, false, false, false); // Disable color
//                glStencilFunc(GL_ALWAYS, 1, 0xff);
//                glStencilMask(0xff);
//                glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
//                openGlUtilis.drawmask(techTreePanel);
//                glColorMask(true, true, true, true); // Re-enable color
//                glStencilFunc(GL_EQUAL, 1, 0xFF);
//                for (TechTreeEraSection era : techTreeCoreUI.Eras) {
//                    for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
//                        for (Map.Entry<String, CustomPanelAPI> hexagon : researchOptionPanel.getHexagons().entrySet()) {
//                            // Scissor test to limit rendering to the area inside the first stencil mask
//                            SpriteAPI sprite = Global.getSettings().getSprite("ui_icons_tech_tree","ring");
//                            if (sprite != null) {
//                                sprite.setSize(51, 50);
//                                sprite.setAlphaMult(alphaMult*0.8f);
//                                sprite.render(hexagon.getValue().getPosition().getX(), hexagon.getValue().getPosition().getY());
//                            }
//                        }
//                    }
//                }
//                glDisable(GL_STENCIL_TEST);
//
//            }


        } else {
            if (techTreeButtonPanel != null) openGlUtilis.drawPanelBorder(techTreeButtonPanel);
            if (projectListUI != null) {

                openGlUtilis.drawPanelBorder(specialProjectListPanel);
                openGlUtilis.drawPanelBorder(projectListUI.getCoverPanel());
                openGlUtilis.drawPanelBorder(projectListUI.getImagePanel());
                openGlUtilis.drawPanelBorder(projectListUI.getBonusPanel());
                openGlUtilis.drawPanelBorder(projectListUI.getSpecialProjectsPanel());
            }
            if (helpPanel != null) openGlUtilis.drawPanelBorder(helpPanel);
            if(specialProjectTitlePanel!=null)openGlUtilis.drawPanelBorder(specialProjectTitlePanel);
            if(specialProjectProgressionBarPanel!=null){
                openGlUtilis.drawPanelBorder(specialProjectProgressionBarPanel);
                openGlUtilis.drawPanelBorder(projectBarUI.getBar());
                float delta = 0.0f;
                for (SpecialProjectStage stage : projectBarUI.projectOfIntrest.stages) {
                    delta+=stage.durationOfStage/projectBarUI.projectOfIntrest.calculateTotalDays();
                    openGlUtilis.drawPanelBorderPercentage(projectBarUI.getBar(),delta);
                }

            }
            if(specialProjectSectionOptionsPanel!=null){
                openGlUtilis.drawPanelBorder(specialProjectSectionOptionsPanel);
                if(projectStageOptions.getOptionsPanel()!=null)     openGlUtilis.drawPanelBorder(projectStageOptions.getOptionsPanel());
                if(projectStageOptions.getDescriptionPanel()!=null)     openGlUtilis.drawPanelBorder(projectStageOptions.getDescriptionPanel());
            }

        }

    }


    @Override
    public void advance(float amount) {
        glClearStencil(0);
        glStencilMask(0xff);
        if (horizontalTooltipMaker != null && horizontalTooltipMaker.getHorizontalScrollbar() != null&&horizontalTooltipMaker.mainPanel!=null) {
            horizontalTooltipMaker.getHorizontalScrollbar().moveTooltip(horizontalTooltipMaker.mainPanel.getPosition().getWidth(),horizontalTooltipMaker.horizontalScrollbar.startingX);
            horizontalTooltipMaker.getHorizontalScrollbar().handleMouseDragging(horizontalTooltipMaker.mainPanel.getPosition().getWidth());
        }
            GL11.glPushMatrix();
        int width = (int) (Display.getWidth() * Display.getPixelScaleFactor()),
                height = (int) (Display.getHeight() * Display.getPixelScaleFactor());
        GL11.glViewport(0, 0, width, height);
        GL11.glOrtho(0, width, 0, height, -1, 1);

        GL11.glPopMatrix();
        oppacity += amount;
        boolean mustReset = false;
        boolean mustHardReset = false;
        boolean mustResetPanel = false;
        if (techTreeCoreUI != null) {
            for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                    ButtonAPI button = researchOptionPanel.getCurrentButton();
                    if (button.isChecked()) {
                        button.setChecked(false);
                        String data = (String) button.getCustomData();
                        String[]splitted = data.split(":");
                        for (ResearchOption allResearchOption : techTreeCoreUI.allResearchOptions) {
                            if (allResearchOption.Id.equals(splitted[1])) {
                                wantsToKnow = allResearchOption;
                                if(splitted[0].equals("research")){
                                    manager.pickResearchFocus(wantsToKnow.Id);
                                    researching = wantsToKnow;
                                    mustReset = true;
                                    Global.getSoundPlayer().playUISound("aotd_research_started",1f,1f);
                                    researchOptionPanel.reset();
                                }
                                if(splitted[0].equals("queue")){
                                    mustReset = true;
                                    manager.getQueueManager().addToQueue(wantsToKnow.Id);
                                    Global.getSoundPlayer().playUISound("aotd_research_started",1f,1f);
                                    researchOptionPanel.reset();

                                }
                                if(splitted[0].equals("stop")){
                                    manager.setCurrentFocus(null);
                                    researching=null;
                                    if(!manager.getQueueManager().getQueuedResearchOptions().isEmpty()){
                                      researching =manager.getQueueManager().removeFromTop();
                                      manager.setCurrentFocus(researching.Id);
                                    }
                                    researchOptionPanel.reset();
                                    mustReset = true;
                                }
                            }
                            if(mustReset){
                                break;
                            }
                        }

                    }
                    if(mustReset){
                        break;
                    }
                }
                if(mustReset){
                    break;
                }
            }
        }


        if (researchCenterPanel != null&&researchCenterPanelUI!=null) {

            if(!researchCenterPanelUI.buttons.isEmpty()){
                for (ButtonAPI button : researchCenterPanelUI.buttons) {
                    if (button.isChecked()) {
                        button.setChecked(false);
                        String data = (String) button.getCustomData();
                        if(data.contains(":")){
                            String[] splitted = data.split(":");
                            if(splitted[0].equals("up")){
                                manager.getQueueManager().moveUp(splitted[1]);
                            }
                            if(splitted[0].equals("down")){
                                manager.getQueueManager().moveDown(splitted[1]);
                            }
                            if(splitted[0].equals("top")){
                                manager.getQueueManager().moveToTop(splitted[1]);
                            }
                            if(splitted[0].equals("bottom")){
                                manager.getQueueManager().moveToBottom(splitted[1]);
                            }
                            if(splitted[0].equals("remove")){
                                manager.getQueueManager().removeFromQueue(splitted[1]);
                                boolean found = false;
                                for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                                    for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                                        if(researchOptionPanel.TechToResearch.Id.equals(splitted[1])){
                                            found = true;
                                            researchOptionPanel.reset();
                                        }
                                        if(found)break;
                                    }
                                }
                            }
                            mustResetPanel = true;
                            break;
                        }
                        else{
                            currentModToShow = (String) button.getCustomData();
                            mustHardReset = true;
                            mustReset = true;
                            break;
                        }

                    }
                }
            }

        }
        if(mustResetPanel){
            resetCentralPanel();
        }
        if (buttonPanelUI != null && buttonPanelUI.getSpecialProjectButton().isChecked()) {
            buttonPanelUI.getSpecialProjectButton().setChecked(false);
            mustHardReset = true;
            mustReset = true;
            reset(false, true, UIMODE.SPECIAL_PROJECTS);
            return;
        }
        if(specialProjectListPanel!=null){
            if(!projectListUI.buttons.isEmpty()){
                for (ButtonAPI button : projectListUI.buttons) {
                    if (button.isChecked()) {
                        button.setChecked(false);
                        currentlyFocused = AoTDMainResearchManager.getInstance().getResearchProjectFromRepo((String) button.getCustomData());
                        reset(false, false,null);
                        return;
                    }
                }
            }

        }
        if (techTreeButtonPanelUI != null && techTreeButtonPanelUI.getTechTreeButton().isChecked()) {
            techTreeButtonPanelUI.getTechTreeButton().setChecked(false);
            reset(false, true, UIMODE.TECH_TREE);
            return;
        }
        if(specialProjectTitlePanel!=null&&projectTitleUI.getButtonAPI().isChecked()&&projectTitleUI.getButtonAPI().isEnabled()){
            AoTDMainResearchManager.getInstance().getResearchProjectFromRepo((String) projectTitleUI.getButtonAPI().getCustomData()).startResearchProject();
            currentlyFocused = AoTDMainResearchManager.getInstance().getCurrentProject();
            reset(false, false,null);
            return;

        }

        if(projectStageOptions!=null&&specialProjectSectionOptionsPanel!=null){
            for (ButtonAPI button : projectStageOptions.buttons) {
                if(button.isChecked()){
                    button.setChecked(false);
                    currentlyFocused.applyOptionResults((String) button.getCustomData());
                    reset(false, false,null);
                    return;
                }
            }
        }
        if (mustReset && !mustHardReset) reset(true, false, null);

        if (mustReset && mustHardReset) reset(false, false, null);

    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isKeyDownEvent() && event.getEventValue() == Keyboard.KEY_ESCAPE) {
                event.consume();
                panel.removeComponent(mainPanel);
                callbacks.dismissDialog();
                dialog.dismiss();
                if(researchCenterPanelUI!=null){
                    researchCenterPanelUI.buttons.clear();
                    researchCenterPanelUI=null;
                }
                if(techTreeCoreUI!=null){
                    if(techTreeCoreUI.Eras!=null||techTreeCoreUI.Eras.isEmpty()){
                        for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                            for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                                researchOptionPanel.setTechToResearch(null);
                            }
                            era.researchOptionPanels.clear();
                            era.sortedResearchOptions.clear();
                        }
                    }
                }
                if(projectListUI!=null){
                    projectListUI.buttons.clear();
                }
                if(projectStageOptions!=null){
                    projectStageOptions.buttons.clear();
                }
                if (researchingBeforeUI != null && researching != null && !researchingBeforeUI.Id.equals(researching.Id)) {
                    MessageIntel intel = new MessageIntel("Started Researching - " + researching.Name, Misc.getBasePlayerColor());
                    intel.setIcon(manager.getFaction().getCrest());
                    intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                    manager.payForResearch(researching.Id);
                }
                if (researchingBeforeUI == null && researching != null) {
                    MessageIntel intel = new MessageIntel("Started Researching - " + researching.Name, Misc.getBasePlayerColor());
                    intel.setIcon(manager.getFaction().getCrest());
                    intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                    manager.payForResearch(researching.Id);
                }

                return;

            }
        }



    }

    @Override
    public void buttonPressed(Object buttonId) {

    }


    public void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {
        this.callbacks = callbacks;
        this.panel = panel;
        this.dialog = dialog;
        if (researchingBeforeUI != null) {
            currentModToShow = researchingBeforeUI.modID;
        }
        reset(false, false, null);


    }

    public void setEvent() {

    }

    public void reset(boolean resetForInfo, boolean hardReset, UIMODE switchTO) {
        if (wantsToKnow == null) {
            wantsToKnow = AoTDMainResearchManager.getInstance().getSpecificFactionManager(Global.getSector().getPlayerFaction()).getCurrentFocus();

        }
        if (!hardReset) {
            if (mode.equals(UIMODE.TECH_TREE)) {
                if (mainPanel != null && !resetForInfo) {
                    Xoffset = horizontalTooltipMaker.getHorizontalScrollbar().getXOffset();
                    prevOffset = horizontalTooltipMaker.getMainTooltip().getExternalScroller().getXOffset();
                    Yoffset = horizontalTooltipMaker.getMainTooltip().getExternalScroller().getYOffset();
                    for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                        era.researchOptionPanels.clear();
                    }
                    techTreeCoreUI.Eras.clear();
                    panel.removeComponent(mainPanel);
                    buttons.clear();
                    mainPanel = this.panel.createCustomPanel(WIDTH, HEIGHT, null);
                    createUIForTechInfo();
                    dialog.setOpacity(1.0f);
                } else if (mainPanel != null && resetForInfo) {
                    prevOffset = 0;
                    Xoffset = 0;
                    Yoffset = horizontalTooltipMaker.getMainTooltip().getExternalScroller().getYOffset();
                    for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                        if (wantsToKnow != null && !manager.canResearch(wantsToKnow.Id, false)) break;
                        if (researching != null) {
                            for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                                String id = (String) researchOptionPanel.getCurrentButton().getCustomData();
                                id = id.split(":")[1];
                                if(researchOptionPanel.TechToResearch.isResearched())continue;
                                if (!id.equals(researching.Id)&&manager.getCurrentFocus()!=null) {
                                    researchOptionPanel.getCurrentButton().setText("Queue");
                                    researchOptionPanel.getCurrentButton().setCustomData("queue:"+researchOptionPanel.TechToResearch.Id);
                                    researchOptionPanel.getCurrentButton().setEnabled(!manager.getQueueManager().isInQueue(researchOptionPanel.TechToResearch.Id)&&manager.canResearch(researchOptionPanel.TechToResearch.Id,false));
                                }
                                if (!id.equals(researching.Id)&&manager.getCurrentFocus()==null&&manager.getQueueManager().getQueuedResearchOptions().isEmpty()) {
                                    researchOptionPanel.getCurrentButton().setText("Research");
                                    researchOptionPanel.getCurrentButton().setCustomData("research:"+researchOptionPanel.TechToResearch.Id);
                                    researchOptionPanel.getCurrentButton().setEnabled(!manager.getQueueManager().isInQueue(researchOptionPanel.TechToResearch.Id)&&manager.canResearch(researchOptionPanel.TechToResearch.Id,false));
                                }
                                if(id.equals(researching.Id)){
                                    researchOptionPanel.reset();
                                    researchOptionPanel.getCurrentButton().setText("Stop");
                                    researchOptionPanel.getCurrentButton().setCustomData("stop:"+researchOptionPanel.TechToResearch.Id);
                                    researchOptionPanel.getCurrentButton().setEnabled(true);
                                }




                            }

                        }

                    }
                    resetCentralPanel();
                }
            }
            else{
                if(projectListUI!=null){
                    projectListUI.buttons.clear();
                }
                if(projectStageOptions!=null){
                    projectStageOptions.buttons.clear();
                }
                panel.removeComponent(mainPanel);
                buttons.clear();
                mainPanel = this.panel.createCustomPanel(WIDTH, HEIGHT, null);
                mode = UIMODE.SPECIAL_PROJECTS;
                createUIForSpecialProjects();
                dialog.setOpacity(1.0f);

            }
        }


        if (hardReset) {
            if (switchTO.equals(UIMODE.SPECIAL_PROJECTS)) {
                Xoffset = 0;
                prevOffset = 0;
                Yoffset = 0;
                for (TechTreeEraSection era : techTreeCoreUI.Eras) {
                    era.researchOptionPanels.clear();
                }
                techTreeCoreUI.Eras.clear();
                panel.removeComponent(mainPanel);
                buttons.clear();
                mainPanel = this.panel.createCustomPanel(WIDTH, HEIGHT, null);
                mode = UIMODE.SPECIAL_PROJECTS;
                createUIForSpecialProjects();
                dialog.setOpacity(1.0f);
            } else {
                mode = UIMODE.TECH_TREE;
                panel.removeComponent(mainPanel);
                buttons.clear();
                currentlyFocused=AoTDMainResearchManager.getInstance().getCurrentProject();
                mainPanel = this.panel.createCustomPanel(WIDTH, HEIGHT, null);
                createUIForTechInfo();
            }
        }
        if (mainPanel == null) {
            mainPanel = this.panel.createCustomPanel(WIDTH, HEIGHT, null);
            createUIForTechInfo();
            dialog.setOpacity(1.0f);
        }


    }

    private void resetCentralPanel() {
        mainPanel.removeComponent(researchCenterPanelUI.getBonusPanel());
        mainPanel.removeComponent(researchCenterPanelUI.getDescriptionPanel());
        mainPanel.removeComponent(researchCenterPanelUI.getModPanel());
        mainPanel.removeComponent(researchCenterPanelUI.getCoverPanel());
        mainPanel.removeComponent(researchCenterPanelUI.getQueuePanel());
        mainPanel.removeComponent(researchCenterPanelUI.getImagePanel());
        mainPanel.removeComponent(researchCenterPanel);
        researchCenterPanelUI.buttons.clear();
        researchCenterPanel = mainPanel.createCustomPanel(300, HEIGHT - 150, null);
        researchCenterTooltip = researchCenterPanel.createUIElement(310, HEIGHT - 150, false);
        researchCenterPanelUI.init(mainPanel, researchCenterPanel, researchCenterTooltip);
        researchCenterPanelUI.setHeight(HEIGHT - 150);
        researchCenterPanelUI.createUI();
        researchCenterPanelUI.placeTooltip(-5, 0);
        researchCenterPanelUI.placeSubPanel(0, 100);
    }

    public static class  handlerOfScrollbar implements CustomUIPanelPlugin {
        HorizontalTooltipMaker tooltipMaker;
        public handlerOfScrollbar(HorizontalTooltipMaker scrollbar){
            this.tooltipMaker = scrollbar;
        }
        @Override
        public void positionChanged(PositionAPI position) {

        }

        @Override
        public void renderBelow(float alphaMult) {

        }

        @Override
        public void render(float alphaMult) {

        }

        @Override
        public void advance(float amount) {
            if (tooltipMaker.getHorizontalScrollbar() != null) {
                tooltipMaker.horizontalScrollbar.detectIfRightMouse();
                tooltipMaker.getMainTooltip().getExternalScroller().setXOffset(tooltipMaker.horizontalScrollbar.getCurrOffset());
            }
        }

        @Override
        public void processInput(List<InputEventAPI> events) {
            if (tooltipMaker.getHorizontalScrollbar() != null) {
                tooltipMaker.getHorizontalScrollbar().processInputForScrollbar(events, tooltipMaker.mainPanel.getPosition().getWidth());
            }
        }

        @Override
        public void buttonPressed(Object buttonId) {

        }
    }
}
