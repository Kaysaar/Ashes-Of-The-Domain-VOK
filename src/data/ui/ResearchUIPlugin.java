package data.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;
import data.Ids.AodResearcherSkills;
import data.plugins.AoDUtilis;
import data.scripts.research.ResearchAPI;
import data.scripts.research.ResearchOption;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.magiclib.util.MagicSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static data.plugins.AoDCoreModPlugin.aodTech;
import static org.lwjgl.opengl.GL11.*;

public class ResearchUIPlugin implements CustomUIPanelPlugin {
    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    boolean processingMarket = false;
    PositionAPI pos;
    float scrollerHelp = 0;
    float progressionScroller = 0;
    List<CustomPanelAPI> techPanels = new ArrayList<>();
    List<String> itemsAoTD = new ArrayList<>();

    HashMap<CustomPanelAPI, String> tracker = new HashMap<>();
    public static List<String> TECH_LIST = MagicSettings.getList("aod_core", "TECH_TREE_LABELS");
    protected InteractionDialogAPI dialog;
    protected CustomVisualDialogDelegate.DialogCallbacks callbacks;
    protected CustomPanelAPI panel;
    float scroller = 0;
    int availableWidth, availableHeight, pW, pH, pWX, pHY;
    protected TooltipMakerAPI mainTooltip;
    ResearchOption wantsToHaveInfoAbout = null;
    Color bgColor = new Color(6, 35, 40, 42);
    float size_section = 155;
    HashMap<ButtonAPI, TooltipMakerAPI> makerAPIHashMap = new HashMap<>();
    CustomPanelAPI techPanel;
    TooltipMakerAPI techPanelTT;
    boolean wantsInfoAboutCurrentlyResearching = false;
    CustomPanelAPI optionsPanel;
    TooltipMakerAPI optionsPanelTT;
    CustomPanelAPI techTierNames;
    TooltipMakerAPI techTierNamesTT;
    CustomPanelAPI allResearchPanel;
    CustomPanelAPI industryPanelImage;
    TooltipMakerAPI industryPanelImageTT;
    TooltipMakerAPI alreadyResearchedPanelTT;
    CustomPanelAPI helpPanel;
    CustomPanelAPI wantsToKnowResearchReqIndustry;
    TooltipMakerAPI helpPanelTT;
    CustomPanelAPI industryPanelDescrp;
    TooltipMakerAPI industryPanelDescrpTT;
    CustomPanelAPI wantsToKnowResearchReqItem;
    CustomPanelAPI currentlyResearchingInfo;
    TooltipMakerAPI currentlyResearchingInfoTT;
    CustomPanelAPI wantsToKnowResearchImage;
    TooltipMakerAPI wantsToKnowResearchImageTT;
    CustomPanelAPI wantsToKnowResearch;
    TooltipMakerAPI wantsToKnowResearchTT;
    CustomPanelAPI wantsToKnowResearchReq;
    TooltipMakerAPI wantsToKnowResearchReqTT;
    TooltipMakerAPI wantsToKnowResearchReqIndustryTT;
    TooltipMakerAPI wantsToKnowResearchReqItemTT;
    CustomPanelAPI scientistPanel;
    TooltipMakerAPI scientistPanelTT;
    TooltipMakerAPI scientistPanelAnchorTT;
    CustomPanelAPI scientistDescrpPanel;
    TooltipMakerAPI scientistDescrpPanelTT;
    CustomPanelAPI scientistSpecAbilityPanel;
    TooltipMakerAPI scientistSpecAbilityPanelTT;
    CustomPanelAPI queuePanel;
    TooltipMakerAPI queuePanelTT;
    TooltipMakerAPI queuePanelOptionsTT;
    CustomPanelAPI bonusPanel;
    TooltipMakerAPI bonusPanelTT;
    CustomPanelAPI statisticsPanel;
    TooltipMakerAPI statisticsPanelTT;
    MarketAPI copy;

    UIMode currentUIMode = UIMode.PROGRESSION_TREE;
    ProgressionTreeUiMode currentCategory = ProgressionTreeUiMode.ALL;

    ResearchOption currResearching;
    List<ButtonAPI> buttons = new ArrayList<>();
    HashMap<ButtonAPI, String> buttonMap = new HashMap<>();
    int defaultSizeOfExampleMarket = 6;



    public static ResearchUIPlugin createDefault() {
        return new ResearchUIPlugin();
    }

    public String getIndustryDesc(String industryId) {
        for (IndustrySpecAPI indSpec : Global.getSettings().getAllIndustrySpecs()) {
            if (indSpec.getId().equals(industryId)) {
                return indSpec.getDesc();
            }
        }
        return "";
    }
    public void changeDefaultSizeOfExampleMarket(boolean isUp){
        int prev = defaultSizeOfExampleMarket;
        if(isUp){
            prev++;
            if(prev>10){
                prev=10;
            }
        }
        else{
            prev--;
            if(prev<=2){
                prev=3;
            }
        }
        defaultSizeOfExampleMarket = prev;
    }
    private void adjustDem(@NotNull List<MutableCommodityQuantity> dem, List<MutableCommodityQuantity> demAdjusted) {
        if (dem.isEmpty()) return;
        for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
            boolean wasHere = false;
            if (demAdjusted.isEmpty()) {
                demAdjusted.add(mutableCommodityQuantity);
                continue;
            }
            for (MutableCommodityQuantity commodityQuantity : demAdjusted) {
                if (commodityQuantity.getCommodityId().equals(mutableCommodityQuantity.getCommodityId())) {
                    wasHere = true;
                }
            }
            if (!wasHere) {
                demAdjusted.add(mutableCommodityQuantity);
            }
        }
    }

    public void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {
        //so we can get back to the original InteractionDialogPlugin and do stuff with it or close it
        currResearching = researchAPI.getCurrentResearching();
        this.panel = panel;
        this.callbacks = callbacks;
        this.dialog = dialog;
        for (int i = 0; i < 20; i++) {
            TECH_LIST.add("Test" + i);
        }
        if (itemsAoTD.isEmpty()) {
            itemsAoTD.add("research_databank");
            itemsAoTD.add("purified_rare_ore");
            itemsAoTD.add("purified_rare_metal");
            itemsAoTD.add("refined_metal");
            itemsAoTD.add("purified_ore");
            itemsAoTD.add("compound");
            itemsAoTD.add("polymer");
            itemsAoTD.add("recitificates");
            itemsAoTD.add("biotics");
            itemsAoTD.add("electronics");
            itemsAoTD.add("water");
        }
        //these might be helpful if you are doing custom rendering
        availableWidth = 1224;
        availableHeight = 844;
        pW = (int) this.panel.getPosition().getWidth();
        pH = (int) this.panel.getPosition().getHeight();
        pWX = (int) this.panel.getPosition().getCenterX();
        pHY = (int) this.panel.getPosition().getCenterY();
        copy = Global.getFactory().createMarket("to_delete_aotd", "deletion", defaultSizeOfExampleMarket);
        copy.setFactionId(Global.getSector().getPlayerFaction().getId());
        copy.setFreePort(true);
        copy.setHidden(true);
        //when something changes in the UI and it needs to be re-drawn, call this
        reset();
    }

    public void reset() {
        //clears the ui panel
        if (mainTooltip != null) {
            panel.removeComponent(mainTooltip);
            buttons.clear();
            buttonMap.clear();
        }
        techPanels.clear();
        tracker.clear();
        //create a new TooltipMakerAPI covering the entire UI panel
        //I don't think scrolling panels work here, but I might be doing them wrong
        mainTooltip = this.panel.createUIElement(this.panel.getPosition().getWidth(), this.panel.getPosition().getHeight(), false);
        mainTooltip.setForceProcessInput(true);
        mainTooltip.setBgAlpha(0.1f);
        if (currentUIMode.equals(UIMode.PROGRESSION_TREE)) {
            showResearchTree();
        }
        showAllOptions();
        if (currentUIMode.equals(UIMode.RESEARCH_CENTER)) {
            showAllResearchOptions();
            if (wantsToHaveInfoAbout != null) {
                String id = wantsToHaveInfoAbout.industryId;
                showIndustryDescrp(id);
                if (!wantsInfoAboutCurrentlyResearching) {
                    showMoreInfoContainer(id);
                    showInfoImage(id);
                }
                showReqForResearch(id);
            } else {
                showIndustryDescrp(null);
                showMoreInfoContainer(null);
                showInfoImage(null);
                showReqForResearch(null);
            }
            if (currResearching != null) {
                showTechIncased(currResearching.industryId);
                showIndustryImage(currResearching.industryId);
            } else {
                showTechIncased(null);
                showIndustryImage(null);
            }
            showScientistImage();
            showNameAndSurrnameofScientist();
            showSpecAbilityDescrp();
            showBonuses();
            showStats();
            showQueue();
        }

        if (currentUIMode.equals(UIMode.HELP)) {
            showHelp();
        }
        panel.addUIElement(mainTooltip).inTL(0, 0);


//        showStuff1();
        //showStuff2();


    }

    @Override
    public void render(float alphaMult) {
        //this is where custom GL11 stuff is drawn


        // this will draw a box at x,y with width w and heigh h
        //remember OpenGL Bottom-Left is 0,0


        GL11.glPushMatrix();
        //GL11.glScalef(1/uiscale, 1/uiscale, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Color color = Misc.getDarkPlayerColor();
        Color colorResearched = Misc.getBrightPlayerColor();
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
        //set everything to 0
        if (currentUIMode == UIMode.PROGRESSION_TREE) {
            glClear(GL_STENCIL_BUFFER_BIT);

            //disable drawing colour, enable stencil testing
            glColorMask(false, false, false, false); //disable colour
            glEnable(GL_STENCIL_TEST); //enable stencil

            drawmask(techPanel);

            glStencilFunc(GL_ALWAYS, 1, 0xff); // Do not test the current value in the stencil buffer, always accept any value on there for drawing
            glStencilMask(0xff);
            glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE); // Make every test succeed
            drawmask(techPanel);
            glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP); // Make sure you will no longer (over)write stencil values, even if any test succeeds
            glColorMask(true, true, true, true); // Make sure we draw on the backbuffer again.
            glStencilFunc(GL_EQUAL, 1, 0xFF); // Now we will only draw pixels where the corresponding stencil buffer value equals 1

            for (Map.Entry<CustomPanelAPI, String> customPanelAPIStringEntry : tracker.entrySet()) {
                CustomPanelAPI orgin = customPanelAPIStringEntry.getKey();

                GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);

                for (CustomPanelAPI allUpgrade : getAllUpgrades(customPanelAPIStringEntry.getValue())) {
                    String entry = tracker.get(allUpgrade);
                    if (researchAPI.getResearchOption(entry).isResearched) continue;
                    drawTechLine(orgin, allUpgrade);
                }
                GL11.glColor4f(colorResearched.getRed() / 255f, colorResearched.getGreen() / 255f, colorResearched.getBlue() / 255f, alphaMult);
                for (CustomPanelAPI allUpgrade : getAllUpgrades(customPanelAPIStringEntry.getValue())) {
                    String entry = tracker.get(allUpgrade);
                    if (!researchAPI.getResearchOption(entry).isResearched) continue;
                    drawTechLine(orgin, allUpgrade);
                }
                if (researchAPI.getResearchOption(customPanelAPIStringEntry.getValue()).isResearched) {
                    GL11.glColor4f(colorResearched.getRed() / 255f, colorResearched.getGreen() / 255f, colorResearched.getBlue() / 255f, alphaMult);
                } else {
                    GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
                }
                drawPanelBorder(orgin);

            }
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, alphaMult);
            glDisable(GL_STENCIL_TEST);

            drawPanelBorder(techPanel);
            drawPanelBorder(techTierNames);

        }
        if (currentUIMode == UIMode.RESEARCH_CENTER) {
            drawPanelBorder(allResearchPanel);
            drawPanelBorder(industryPanelDescrp);
            drawPanelBorder(currentlyResearchingInfo);
            if (!wantsInfoAboutCurrentlyResearching) {
                drawPanelBorder(wantsToKnowResearch);
                drawImageInfoBorder(wantsToKnowResearchImage);
            }

            drawImageInfoBorder(industryPanelImage);
            drawPanelBorder(wantsToKnowResearchReq);
            drawPanelBorder(wantsToKnowResearchReqIndustry);
            drawPanelBorder(wantsToKnowResearchReqItem);
            drawPanelBorder(scientistPanel);
            drawPanelBorder(scientistDescrpPanel);
            drawPanelBorder(scientistSpecAbilityPanel);
            drawPanelBorder(bonusPanel);
            drawPanelBorder(statisticsPanel);
            drawPanelBorder(queuePanel);
        }
        if (currentUIMode == UIMode.HELP) {
            drawPanelBorder(helpPanel);
        }

        drawPanelBorder(optionsPanel);
    }

    public List<CustomPanelAPI> getAllUpgrades(String id) {
        List<CustomPanelAPI> toDrawing = new ArrayList<>();
        for (String allPossibleUpgrade : researchAPI.getAllPossibleUpgrades(id)) {
            for (Map.Entry<CustomPanelAPI, String> customPanelAPIStringEntry : tracker.entrySet()) {
                if (customPanelAPIStringEntry.getValue().equals(allPossibleUpgrade)) {
                    toDrawing.add(customPanelAPIStringEntry.getKey());
                }
            }
        }
        return toDrawing;
    }

    void drawPanelBorder(CustomPanelAPI p) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX() - 5;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth() + 10;
        float h = p.getPosition().getHeight();
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }


    void drawImageInfoBorder(CustomPanelAPI p) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        float x = p.getPosition().getX();
        float y = p.getPosition().getY() - 3;
        float w = p.getPosition().getWidth();
        float h = p.getPosition().getHeight() + 3;
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }

    void drawTechLine(CustomPanelAPI p1, CustomPanelAPI p2) {
        GL11.glBegin(GL_LINE_LOOP);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);

        if (p1.getPosition().getCenterY() == p2.getPosition().getCenterY()) {
            GL11.glVertex2f(p1.getPosition().getCenterX() + p1.getPosition().getWidth() / 2 + 5, p1.getPosition().getCenterY());
            GL11.glVertex2f(p2.getPosition().getCenterX() - p2.getPosition().getWidth() / 2 - 5, p2.getPosition().getCenterY());
            GL11.glEnd();
        } else {
            float distance = (p2.getPosition().getCenterX() - p2.getPosition().getWidth() / 2 - 5) - (p1.getPosition().getCenterX() + p1.getPosition().getWidth() / 2 + 5);

            GL11.glVertex2f(p1.getPosition().getCenterX() + p1.getPosition().getWidth() / 2 + 5, p1.getPosition().getCenterY());
            GL11.glVertex2f(p1.getPosition().getCenterX() + p1.getPosition().getWidth() / 2 + 5 + distance / 2, p1.getPosition().getCenterY());
            GL11.glEnd();

            GL11.glBegin(GL_LINE_LOOP);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            GL11.glVertex2f(p1.getPosition().getCenterX() + p1.getPosition().getWidth() / 2 + 5 + distance / 2, p1.getPosition().getCenterY());
            GL11.glVertex2f(p2.getPosition().getCenterX() - p2.getPosition().getWidth() / 2 - 5 - distance / 2, p2.getPosition().getCenterY());
            GL11.glEnd();

            GL11.glBegin(GL_LINE_LOOP);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            GL11.glVertex2f(p2.getPosition().getCenterX() - p2.getPosition().getWidth() / 2 - 5 - distance / 2, p2.getPosition().getCenterY());
            GL11.glVertex2f(p2.getPosition().getCenterX() - p2.getPosition().getWidth() / 2 - 5, p2.getPosition().getCenterY());
            GL11.glEnd();
        }

    }

    void drawmask(CustomPanelAPI p) {
        GL11.glBegin(GL_QUADS);
        float x = p.getPosition().getX() - 6;
        float y = p.getPosition().getY();
        float w = p.getPosition().getWidth() + 11;
        float h = p.getPosition().getHeight();
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + w, y);
        GL11.glVertex2f(x + w, y + h);
        GL11.glVertex2f(x, y + h);
        GL11.glEnd();
    }

    public void showResearchTree() {
        float size_section = (float) ((availableWidth - 50) * 0.25);

        techPanel = panel.createCustomPanel(availableWidth - 175, availableHeight - 40, null);
        techPanel.getPosition().setLocation(0, 0).inTL(0, 0);

        techPanelTT = techPanel.createUIElement(availableWidth - 175, availableHeight - 40, true);
        techPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        techTierNames = panel.createCustomPanel(availableWidth - 175, 30, null);
        techTierNames.getPosition().setLocation(0, 0).inTL(0, 0);
        techTierNamesTT = techTierNames.createUIElement(availableWidth - 175, 30, false);
        techTierNamesTT.getPosition().setLocation(0, 0).inTL(0, 0);
        techTierNamesTT.setParaInsigniaLarge();
        if (currentCategory.equals(ProgressionTreeUiMode.EXPERIMENTAL)) {
            techTierNamesTT.addPara("Experimental", Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(size_section + 120, 5);
        } else {
            techTierNamesTT.addPara("Primitive", Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(50, 5);
            techTierNamesTT.addPara("Basic", Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(size_section + 50, 5);
            techTierNamesTT.addPara("Sophisticated", Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(size_section * 2 + 10, 5);
            techTierNamesTT.addPara("Pre Collapse", Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(size_section * 3 - 20, 5);
        }


        HashMap<List<ResearchOption>, Integer> farmingGroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("farming"));
        HashMap<List<ResearchOption>, Integer> heavyIndustryGroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("heavyindustry"));
        HashMap<List<ResearchOption>, Integer> aquagroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("aquaculture"));
        HashMap<List<ResearchOption>, Integer> lightindustrygroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("lightindustry"));
        HashMap<List<ResearchOption>, Integer> refininggroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("refining"));
        HashMap<List<ResearchOption>, Integer> mininggroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("mining"));
        int so_far = 0;
        int rest = 0;
        if (AoDUtilis.canExperimental()) {
            HashMap<List<ResearchOption>, Integer> experimentalgroup = researchAPI.parentGroup(researchAPI.reesarchOptionsFromSameGroup("experimental"));
            switch (currentCategory) {
                case ALL:
                    so_far = showTierSection(techPanel, techPanelTT, farmingGroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, aquagroup, so_far - 1);
                    so_far = showTierSection(techPanel, techPanelTT, lightindustrygroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, mininggroup, so_far + 1);
                    so_far = showTierSection(techPanel, techPanelTT, refininggroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, heavyIndustryGroup, so_far + 1);
                    rest = showRest(techPanel, techPanelTT, so_far);
                    break;
                case OTHER:
                    rest = showRest(techPanel, techPanelTT, so_far);
                    break;
                case FARMING:
                    so_far = showTierSection(techPanel, techPanelTT, farmingGroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, aquagroup, so_far - 1);
                    rest = so_far;
                    break;
                case HEAVY_INDUSTRY:
                    so_far = showTierSection(techPanel, techPanelTT, heavyIndustryGroup, so_far + 1);
                    rest = so_far;
                    break;
                case MINING:
                    so_far = showTierSection(techPanel, techPanelTT, mininggroup, so_far + 1);
                    rest = so_far;
                    break;
                case LIGHT_INDUSTRY:
                    so_far = showTierSection(techPanel, techPanelTT, lightindustrygroup, so_far);
                    rest = so_far;
                    break;
                case REFINING:
                    so_far = showTierSection(techPanel, techPanelTT, refininggroup, so_far);
                    rest = so_far;
                    break;
                case EXPERIMENTAL:
                    so_far = showTierSection(techPanel, techPanelTT, experimentalgroup, so_far);
                    rest = so_far;
                    break;
            }
        } else {

            switch (currentCategory) {
                case ALL:
                    so_far = showTierSection(techPanel, techPanelTT, farmingGroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, aquagroup, so_far - 1);
                    so_far = showTierSection(techPanel, techPanelTT, lightindustrygroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, mininggroup, so_far + 1);
                    so_far = showTierSection(techPanel, techPanelTT, refininggroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, heavyIndustryGroup, so_far + 1);
                    rest = showRest(techPanel, techPanelTT, so_far);
                    break;
                case OTHER:
                    rest = showRest(techPanel, techPanelTT, so_far);
                    break;
                case FARMING:
                    so_far = showTierSection(techPanel, techPanelTT, farmingGroup, so_far);
                    so_far = showTierSection(techPanel, techPanelTT, aquagroup, so_far - 1);
                    rest = so_far;
                    break;
                case HEAVY_INDUSTRY:
                    so_far = showTierSection(techPanel, techPanelTT, heavyIndustryGroup, so_far + 1);
                    rest = so_far;
                    break;
                case MINING:
                    so_far = showTierSection(techPanel, techPanelTT, mininggroup, so_far + 1);
                    rest = so_far;
                    break;
                case LIGHT_INDUSTRY:
                    so_far = showTierSection(techPanel, techPanelTT, lightindustrygroup, so_far);
                    rest = so_far;
                    break;
                case REFINING:
                    so_far = showTierSection(techPanel, techPanelTT, refininggroup, so_far);
                    rest = so_far;
                    break;
            }
        }


        for (int i = 0; i < rest; i++) {
            techPanelTT.addSpacer(115);
        }
        techPanelTT.setParaInsigniaLarge();

        techPanel.addUIElement(techPanelTT).setLocation(0, 0).inTL(0, 0);
        techTierNames.addUIElement(techTierNamesTT).setLocation(0, 0).inTL(0, 0);
        mainTooltip.addComponent(techPanel).inTL(170, 35);
        mainTooltip.addComponent(techTierNames).inTL(170, 5);
        techPanelTT.getExternalScroller().setYOffset(progressionScroller);
    }


    public void showAllOptions() {
        optionsPanel = panel.createCustomPanel(size_section, availableHeight - 10, null);
        optionsPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        optionsPanelTT = optionsPanel.createUIElement(size_section, availableHeight - 10, true);
        optionsPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        ButtonAPI buttonHelp = optionsPanelTT.addButton("Help", null, 152, 40, 10f);
        buttonHelp.getPosition().setLocation(0, 0).inTL(2, 10);
        buttons.add(buttonHelp);
        buttonMap.put(buttonHelp, "UIMODE:" + "help");

        ButtonAPI buttonProgresion = optionsPanelTT.addButton("Progression Tree", null, 152, 40, 10f);
        buttonProgresion.getPosition().setLocation(0, 0).inTL(2, 60);
        buttons.add(buttonProgresion);
        buttonMap.put(buttonProgresion, "UIMODE:" + "progression_tree");

        ButtonAPI buttonResearchCetner = optionsPanelTT.addButton("Research Center", null, 152, 40, 10f);
        buttonResearchCetner.getPosition().setLocation(0, 0).inTL(2, 110);
        buttons.add(buttonResearchCetner);
        buttonMap.put(buttonResearchCetner, "UIMODE:" + "research_center");
        LabelAPI labelAPI;
        if (currentUIMode == UIMode.PROGRESSION_TREE) {
            labelAPI = optionsPanelTT.addSectionHeading("Currently in:\nProgression Tree", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 250);
            labelAPI.autoSizeToWidth(150);
        }

        if (currentUIMode == UIMode.RESEARCH_CENTER) {
            labelAPI = optionsPanelTT.addSectionHeading("Currently in:\nResearch Center", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 250);
            labelAPI.autoSizeToWidth(150);
        }

        if (currentUIMode == UIMode.HELP) {
            labelAPI = optionsPanelTT.addSectionHeading("Currently in:\nHelp Section", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 250);
            labelAPI.autoSizeToWidth(150);
        }
        if (currentUIMode == UIMode.ARCHIVES) {
            labelAPI = optionsPanelTT.addSectionHeading("Currently in:\nArchives", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 250);
            labelAPI.autoSizeToWidth(150);
        }

        if (currentUIMode == UIMode.PROGRESSION_TREE) {


            labelAPI = optionsPanelTT.addSectionHeading("Categories", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 285);
            labelAPI.autoSizeToWidth(150);
            ButtonAPI buttonAll = optionsPanelTT.addButton("All", null, 152, 40, 10f);
            buttonAll.getPosition().setLocation(0, 0).inTL(2, 310);
            buttons.add(buttonAll);
            buttonMap.put(buttonAll, "PROGRESSION:" + "all");

            ButtonAPI buttonHeavy = optionsPanelTT.addButton("Heavy Industry", null, 152, 40, 10f);
            buttonHeavy.getPosition().setLocation(0, 0).inTL(2, 360);
            buttons.add(buttonHeavy);
            buttonMap.put(buttonHeavy, "PROGRESSION:" + "heavy_industry");

            ButtonAPI buttonLight = optionsPanelTT.addButton("Light Industry", null, 152, 40, 10f);
            buttonLight.getPosition().setLocation(0, 0).inTL(2, 410);
            buttons.add(buttonLight);
            buttonMap.put(buttonLight, "PROGRESSION:" + "light_industry");

            ButtonAPI buttonFarming = optionsPanelTT.addButton("Food Production", null, 152, 40, 10f);
            buttonFarming.getPosition().setLocation(0, 0).inTL(2, 460);
            buttons.add(buttonFarming);
            buttonMap.put(buttonFarming, "PROGRESSION:" + "farming");

            ButtonAPI buttonMining = optionsPanelTT.addButton("Mining", null, 152, 40, 10f);
            buttonMining.getPosition().setLocation(0, 0).inTL(2, 510);
            buttons.add(buttonMining);
            buttonMap.put(buttonMining, "PROGRESSION:" + "mining");

            ButtonAPI buttonRefining = optionsPanelTT.addButton("Refining", null, 152, 40, 10f);
            buttonRefining.getPosition().setLocation(0, 0).inTL(2, 560);
            buttons.add(buttonRefining);
            buttonMap.put(buttonRefining, "PROGRESSION:" + "refining");

            ButtonAPI buttonOther = optionsPanelTT.addButton("Other", null, 152, 40, 10f);
            buttons.add(buttonOther);
            buttonMap.put(buttonOther, "PROGRESSION:" + "other");
            buttonOther.getPosition().setLocation(0, 0).inTL(2, 610);
            if (AoDUtilis.canExperimental()) {
                ButtonAPI buttonExperimental = optionsPanelTT.addButton("Experimental", null, 152, 40, 10f);
                buttons.add(buttonExperimental);
                buttonMap.put(buttonExperimental, "PROGRESSION:" + "experimental");
                buttonExperimental.getPosition().setLocation(0, 0).inTL(2, 710);
            }


            if (currResearching != null) {
                float percent = (currResearching.currentResearchDays - currResearching.researchCost) / currResearching.researchCost;
                if (AoDUtilis.getResearchAPI().getCurrentResearcher() != null && AoDUtilis.getResearchAPI().getCurrentResearcher().hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE) && (currResearching.industryId.equals("triheavy") || currResearching.industryId.equals("hegeheavy") || currResearching.industryId.equals("ii_stellacastellum"))) {
                    percent = (currResearching.currentResearchDays - currResearching.researchCost * 3) / currResearching.researchCost * 3;
                }
                float true_percent = Math.abs(percent) * 100f;
                if (currResearching.currentResearchDays == currResearching.researchCost) {
                    true_percent = 0;
                }
                String formattedString = String.format("%.02f", true_percent);
                optionsPanelTT.addPara("Researching : " + currResearching.industryName + "\nProgress:" + formattedString + "%", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 165);
            }

        }
        if (currentUIMode.equals(UIMode.RESEARCH_CENTER)) {
            labelAPI = optionsPanelTT.addSectionHeading("Vault Of Knowledge", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 285);
            labelAPI.autoSizeToWidth(150);
        }
        if (currentUIMode.equals(UIMode.HELP)) {
            labelAPI = optionsPanelTT.addSectionHeading("Options", Alignment.MID, 10f);
            labelAPI.getPosition().setLocation(0, 0).inTL(3, 285);
            labelAPI.autoSizeToWidth(150);

            ButtonAPI buttonAll = optionsPanelTT.addButton("What this mod does", null, 152, 40, 10f);
            buttonAll.getPosition().setLocation(0, 0).inTL(2, 310);
            buttons.add(buttonAll);
            buttonMap.put(buttonAll, "HELP:" + "what");

            ButtonAPI buttonHeavy = optionsPanelTT.addButton("Research mechanics ", null, 152, 40, 10f);
            buttonHeavy.getPosition().setLocation(0, 0).inTL(2, 360);
            buttons.add(buttonHeavy);
            buttonMap.put(buttonHeavy, "HELP:" + "research");

            ButtonAPI buttonLight = optionsPanelTT.addButton("Paying Research Cost", null, 152, 40, 10f);
            buttonLight.getPosition().setLocation(0, 0).inTL(2, 410);
            buttons.add(buttonLight);
            buttonMap.put(buttonLight, "HELP:" + "paying");

            ButtonAPI buttonFarming = optionsPanelTT.addButton("Commodities", null, 152, 40, 10f);
            buttonFarming.getPosition().setLocation(0, 0).inTL(2, 460);
            buttons.add(buttonFarming);
            buttonMap.put(buttonFarming, "HELP:" + "commodities");

            ButtonAPI buttonMining = optionsPanelTT.addButton("FAQ", null, 152, 40, 10f);
            buttonMining.getPosition().setLocation(0, 0).inTL(2, 510);
            buttons.add(buttonMining);
            buttonMap.put(buttonMining, "HELP:" + "faq");

            ButtonAPI buttonRefining = optionsPanelTT.addButton("Special thanks", null, 152, 40, 10f);
            buttonRefining.getPosition().setLocation(0, 0).inTL(2, 560);
            buttons.add(buttonRefining);
            buttonMap.put(buttonRefining, "HELP:" + "thanks");

        }


        optionsPanel.addUIElement(optionsPanelTT).setLocation(0, 0).inTL(0, 0);


        mainTooltip.addComponent(optionsPanel).inTL(5, 5);
    }

    void showAllResearchOptions() {
        allResearchPanel = panel.createCustomPanel(size_section, 510, null);
        allResearchPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        alreadyResearchedPanelTT = allResearchPanel.createUIElement(size_section, 510, true);
        alreadyResearchedPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);

        int index = 0;
        int spacerY = 65;
        for (ResearchOption research : researchAPI.getResearchOptionsSorted()) {
            if (Global.getSettings().getIndustrySpec(research.industryId).hasTag("experimental")) {
                if (!AoDUtilis.canExperimental()) {
                    continue;
                }
            }
            CustomPanelAPI vPanel = techPanel.createCustomPanel(size_section - 20, 120, null);
            vPanel.getPosition().setLocation(0, 0).inTL(5, index * spacerY);
            TooltipMakerAPI vTT = vPanel.createUIElement(size_section - 20, 140, false);
            vTT.getPosition().inTL(5, 0);
            vTT.setForceProcessInput(true);
            ButtonAPI boxBorder;
            boxBorder = alreadyResearchedPanelTT.addAreaCheckbox("", null, Color.ORANGE,
                    Misc.getDarkPlayerColor(), Misc.getStoryBrightColor(), size_section - 20, 56, 0);
            alreadyResearchedPanelTT.addSpacer(spacerY - 56);
            buttons.add(boxBorder);
            vTT.addPara(research.industryName, Color.ORANGE, 0).getPosition().setLocation(0, 0).inTL(10, 5);

            buttons.add(boxBorder);
            buttonMap.put(boxBorder, "archive_show:" + research.industryId);
            vPanel.addUIElement(vTT).inTL(0, 0);
            alreadyResearchedPanelTT.addComponent(vPanel).inTL(0, index * spacerY);

            index++;
        }


        //adds the subpanelTooltip to the subpanel
        allResearchPanel.addUIElement(alreadyResearchedPanelTT).inTL(0, 0);
        //add the subpanel to the main panel
        alreadyResearchedPanelTT.getExternalScroller().setYOffset(scrollerHelp);
        optionsPanel.addComponent(allResearchPanel).inTL(0, 310);
    }

    public UIMode mainUIModeDecider(String uiModeId) {
        if (uiModeId.equals("help")) {
            return UIMode.HELP;
        }
        if (uiModeId.equals("research_center")) {
            return UIMode.RESEARCH_CENTER;
        }
        if (uiModeId.equals("archives")) {
            return UIMode.ARCHIVES;
        }
        return UIMode.PROGRESSION_TREE;
    }

    public ProgressionTreeUiMode progressionTreeUIModeDecider(String uiModeId) {
        if (uiModeId.contains("all")) {
            return ProgressionTreeUiMode.ALL;
        }
        if (uiModeId.contains("heavy_industry")) {
            return ProgressionTreeUiMode.HEAVY_INDUSTRY;
        }
        if (uiModeId.contains("light_industry")) {
            return ProgressionTreeUiMode.LIGHT_INDUSTRY;
        }
        if (uiModeId.contains("farming")) {
            return ProgressionTreeUiMode.FARMING;
        }
        if (uiModeId.contains("mining")) {
            return ProgressionTreeUiMode.MINING;
        }
        if (uiModeId.contains("refining")) {
            return ProgressionTreeUiMode.REFINING;
        }
        if (uiModeId.contains("other")) {
            return ProgressionTreeUiMode.OTHER;
        }
        if (uiModeId.contains("experimental")) {
            return ProgressionTreeUiMode.EXPERIMENTAL;
        }
        return ProgressionTreeUiMode.FARMING;
    }

    public void showHelp() {
        helpPanel = panel.createCustomPanel(availableWidth - 175, availableHeight - 10, null);
        helpPanel.getPosition().setLocation(0, 0).inTL(0, 0);

        helpPanelTT = helpPanel.createUIElement(availableWidth - 175, availableHeight - 10, true);
        helpPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        LabelAPI start = helpPanelTT.addSectionHeading("What is Vaults Of Knowledge", Alignment.MID, 10f);
        start.getPosition().setLocation(0, 0).inTL(3, 0);
        LabelAPI text1 = helpPanelTT.addPara("Vaults of Knowledge is one of Ashes of The Domain modules, which mainly focuses on Research Tree, and Research mechanics, in this module you will find, that many things has been chaned in colonies, this guide is here to help you learn new mechanics", 10f);
        LabelAPI researchExplain = helpPanelTT.addSectionHeading("What is Research mechanic and how to access it ", Alignment.MID, 10f);
        researchExplain.getPosition().setLocation(0, 0).inTL(3, 70);
        text1 = helpPanelTT.addPara("\nAs you might have seen for the first time some familiar industries to you have vanished from list of buildings and structures, instead you see" +
                "things like Monoculture Plots or Smelting. Welcome you have witnessed the new progression system in colonies. Basically now you are unable to build most things at the start, you need to research them first." +
                "\n\nTo start research you need to have at least one Research Facility on one of your planets, that belongs to your faction. After successful completion of that structure you will be" +
                "able to access research interface by pressing CTRL+T or by pressing Show Interface when clicking on Research Facility\n\n\n" +
                "The interface is divided into:", 10f);
        helpPanelTT.addPara("Progression Tree: Where you see visually your progress and shows possible upgrade paths for industries\n" +
                "Help section: As name suggest section where you can find basic info about mod \nResearch Center : Here you can see ongoing research, current head of Research Institute, and information about currently researched technology" +
                "", Color.ORANGE, 10f);
        helpPanelTT.addPara("To begin research you go to Progression Tree and choose technology you want to research. If research options is grey it means that this technology can't be researched. To know why " +
                "certain technology you need to press More info, which will redirect you to Archive section with opened information about technology you want to research. Here you will see all requirements you need to met before " +
                "you can start researching this technology, If entire box is highlighted it means that this technology has been researched and player is able to build it", 10f);
        start = helpPanelTT.addSectionHeading("How to Pay for Research Cost", Alignment.MID, 10f);
        start.getPosition().setLocation(0, 0).inTL(3, 350);
        helpPanelTT.addPara("As you might have saw some technologies not only require certain industries to be researched first, but also certain items as initial cost. So how you pay for starting research?" +
                "When Research Facility construction has been completed you can put items into Research Facility storage, by placing them in that magazine API counts it as paying for research, you dont need to put in one storage it will calculate total amount from all research station storages your colonies have", 10f);
        helpPanelTT.addPara("VERY IMPORTANT! : Once research will be started items will be irreversibly consumed so plan ahead of what you want to research in first place! But this process is only once as for paying for initial research costs, so when you stop research and then again" +
                " decide that this is time to research technology that you stopped for some reason you don't need to pay again with items\n", Color.ORANGE, 10f);

        start = helpPanelTT.addSectionHeading("New Commodities", Alignment.MID, 10f);
        start.getPosition().setLocation(0, 0).inTL(3, 510);
        int spacerY = 95;
        int index = 1;
        for (String s : itemsAoTD) {
            if (index == 0) {
                helpPanelTT.addImage(Global.getSettings().getCommoditySpec(s).getIconName(), 80, 80, 10f);
                helpPanelTT.addPara("", 10f).getPosition().setLocation(0, 0).inTL(3, 520);
            } else {
                helpPanelTT.addImage(Global.getSettings().getCommoditySpec(s).getIconName(), 80, 80, 10f);
                helpPanelTT.addPara("", 10f).getPosition().setLocation(0, 0).inTL(index * spacerY, 520);
            }

            index++;
        }
        index = 0;
        spacerY = 96;
        for (String s : itemsAoTD) {
            String[] strs = Global.getSettings().getCommoditySpec(s).getName().split(" ");
            LabelAPI labelAPI;
            if (index == 0) {
                labelAPI = helpPanelTT.addPara(Global.getSettings().getCommoditySpec(s).getName(), Color.ORANGE, 10f);
                labelAPI.getPosition().setLocation(0, 0).inTL(10, 630);
            } else {
                labelAPI = helpPanelTT.addPara(Global.getSettings().getCommoditySpec(s).getName(), Color.ORANGE, 10f);
                labelAPI.getPosition().setLocation(0, 0).inTL(index * spacerY, 630);
            }
            labelAPI.autoSizeToWidth(90);

            index++;
        }
        start = helpPanelTT.addSectionHeading("Commodities Quick Info", Alignment.MID, 10f);
        start.getPosition().setLocation(0, 0).inTL(3, 680);
        helpPanelTT.addPara("Research Databank:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 710);
        helpPanelTT.addPara("Those are mainly used to pay for Research. Can be found in : Research Stations, Survey Ship , Mother-ship, Ruins", 10f).getPosition().setLocation(0, 0).inTL(140, 710);
        helpPanelTT.addPara("Purified Transplutonics:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 730);
        helpPanelTT.addPara("Produced by Cascade Reprocessor - Demand by : Orbital Skunkworks Facility", 10f).getPosition().setLocation(0, 0).inTL(155, 730);
        helpPanelTT.addPara("Purified Transplutonic Ore:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 750);
        helpPanelTT.addPara("Produced by Benefication (As long there is market condition for transplutonic ore ) - Demand by : Cascade Reprocessor", 10f).getPosition().setLocation(0, 0).inTL(175, 750);
        helpPanelTT.addPara("Refined Metal: ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 770);
        helpPanelTT.addPara("Produced by Policrystalizator - Demand by :Orbital Fleetwork Facility", 10f).getPosition().setLocation(0, 0).inTL(100, 770);
        helpPanelTT.addPara("Purified Ore:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 790);
        helpPanelTT.addPara("Produced by Benefication (As long there is market condition for normal ore )  - Demand by : Policrystalizator", 10f).getPosition().setLocation(0, 0).inTL(85, 790);
        helpPanelTT.addPara("Compound:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 810);
        helpPanelTT.addPara("Produced by Sublimation (As long as there is market condition for Volatiles) - Demand by : Cascade Reprocessor", 10f).getPosition().setLocation(0, 0).inTL(85, 810);
        helpPanelTT.addPara("Polymer:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 830);
        helpPanelTT.addPara("Produced by Sublimation (As long as there is market condition for Organics) - Demand by : Policrystalizator, Consumer Industry", 10f).getPosition().setLocation(0, 0).inTL(73, 830);
        helpPanelTT.addPara(Global.getSettings().getCommoditySpec("recitificates").getName() + ":", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 850);
        helpPanelTT.addPara("Produced by Farming, Subsidized Farming, Artisanal Farming  - Demand by : Population and Infrastructure, Sublimation", 10f).getPosition().setLocation(0, 0).inTL(85, 850);
        helpPanelTT.addPara("Biotics:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 870);
        helpPanelTT.addPara("Produced by Farming, Subsidized Farming, Artisanal Farming  Demand by : Population and Infrastructure, Beneficiation", 10f).getPosition().setLocation(0, 0).inTL(55, 870);
        helpPanelTT.addPara("Integrated Components:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 890);
        helpPanelTT.addPara("Produced by Cleanroom Manufactory Demand by : Most Heavy Industries", 10f).getPosition().setLocation(0, 0).inTL(160, 890);
        helpPanelTT.addPara("Water:", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(5, 910);
        helpPanelTT.addPara("Produced by Purification Center, Mining ( As long as planet is either Frozen or Cryovolcanic)  Demand by : Population and Infrastructure, Fracking", 10f).getPosition().setLocation(0, 0).inTL(50, 910);
        start = helpPanelTT.addSectionHeading("FAQ", Alignment.MID, 10f);
        start.getPosition().setLocation(0, 0).inTL(3, 930);
        helpPanelTT.addPara("Question :Why i can't build farming from the start", 10f).getPosition().setLocation(0, 0).inTL(3, 950);
        helpPanelTT.addPara("Answer :As i said already in second section some industries are now replaced by weaker counterparts amd to get new and vanila ones you need to research them and upgrade your industries", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1000);

        helpPanelTT.addPara("Question :When do I report bug", 10f).getPosition().setLocation(0, 0).inTL(0, 1100);
        helpPanelTT.addPara("Answer :Please look into starsector.log located in starsector-core folder. Once there check when there is message of crash , in most case NullPointerExcpetion, If function that caused this is either have Research" +
                ",  Kaysaar , Aod , AoTD then yea this is caused by Ashes and you should report it so I can fix it  ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1150);

        helpPanelTT.addPara("Question :Where do I report bug", 10f).getPosition().setLocation(0, 0).inTL(3, 1250);
        helpPanelTT.addPara("Answer :Best way is on Unofficial Discord Server to post into player_tech_support or message me on dsc Kaysaar#1181 as I am there for most of time so i can quickly help you  ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1300);

        helpPanelTT.addPara("Question :Why my items disappear from Research Facility", 10f).getPosition().setLocation(0, 0).inTL(3, 1400);
        helpPanelTT.addPara("Answer :If items disappear then look at research cost. As name suggest you need to PAY for research, so those items simply disappear ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1450);

        helpPanelTT.addPara("Question :Why advanced industries consume so much commodities that i can't supply them", 10f).getPosition().setLocation(0, 0).inTL(3, 1550);
        helpPanelTT.addPara("Answer :Well remember Collapse thing? Yea about that. More advanced industries provides much more benefits, but also more challenges to overcome, you don't need to advance further unless you don't want do some Powerflexing near Hegemony", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1600);

        helpPanelTT.addPara("Question :What is the purpose of new commodities", 10f).getPosition().setLocation(0, 0).inTL(3, 1700);
        helpPanelTT.addPara("Answer :New commodities are mainly those that The Persean Sector has no means of producing them on Industrial Scale. They are necessary to progress further as you want to establish supply chains from ground up ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1750);

        helpPanelTT.addPara("Question :Where do I find those \"Special\" Databanks ?", 10f).getPosition().setLocation(0, 0).inTL(3, 1850);
        helpPanelTT.addPara("Answer :Those special databanks usually starts with name of planet like Chicomoztoc Industrial Databank indicating that this databank is on that specific planet", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 1900);

        helpPanelTT.addPara("Question :What is the difference between Skunkworks and Fleetwork ( Pre Collapse Heavy Industry )?", 10f).getPosition().setLocation(0, 0).inTL(3, 2000);
        helpPanelTT.addPara("Answer :Main difference is that first one focuses on quality of your faction fleets and its much more than just quality percentage, Fleetwork on the other hand is all about fleet size", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2050);

        helpPanelTT.addPara("Question :Where i can hire new researchers?", 10f).getPosition().setLocation(0, 0).inTL(3, 2150);
        helpPanelTT.addPara("Answer :Go to Galatia Academy, here you can hire researchers  that will help your faction establish technological dominance", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2200);

        helpPanelTT.addPara("Question :What does those researches do ?", 10f).getPosition().setLocation(0, 0).inTL(3, 2300);
        helpPanelTT.addPara("Answer :Mainly buff your research in many ways but how ? That is something you  will need to find out ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2350);

        helpPanelTT.addPara("Question :What is Industrial Center market condition?", 10f).getPosition().setLocation(0, 0).inTL(3, 2450);
        helpPanelTT.addPara("Answer :This condition is applied to world once there is at least one synergy achieved between industries. Basically it provides bonuses like stability , production etc etc ", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2500);


        helpPanelTT.addPara("Question :I want to collab with you and introduce new industries to tech tree. How I can Do it ?", 10f).getPosition().setLocation(0, 0).inTL(3, 2600);
        helpPanelTT.addPara("Answer :Simply contact me on dsc Kaysaar#1181 i have made AoTD Research module as soft dependency so I will guide you through entire implementation", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2650);

        helpPanelTT.addPara("Question :How I can support you?", 10f).getPosition().setLocation(0, 0).inTL(3, 2750);
        helpPanelTT.addPara("Answer :On mod page and in my dsc description i got link to Ko fi where you can feed ma caffeine addiction and included but not limited to: Help me with financing commissions etc etc.", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2800);

        helpPanelTT.addPara("Question :When new update will come ", 10f).getPosition().setLocation(0, 0).inTL(3, 2900);
        helpPanelTT.addPara("Answer : The longer Ashley is on Earth the faster the rate of updates will become", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 2950);

        helpPanelTT.addPara("Question :Good morning nice day for fishin ain't it ? Huha", 10f).getPosition().setLocation(0, 0).inTL(3, 3050);
        helpPanelTT.addPara("Answer : Indeed", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 3100);
        helpPanelTT.addPara("Question :Why you named yourself Kaysaar Ashley of Domain?", 10f).getPosition().setLocation(0, 0).inTL(3, 3200);
        helpPanelTT.addPara("Answer : Because i am covered in Ashes of Domain", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 3250);

        String specThanks = "Ciruno and uhbyr1 as main brains behind how industry tech tree looks and ideas behind new industries\n" +
                "RaiDz for Cryosleeper parking script\n" +
                "SirHartley for a lot of tips regarding industries, listeners and providing code\n" +
                "Nia Tahl for allowing to Reverse Enginnier script that adds Smods to Legio fleets\n" +
                "Lukas04 for allowing to Reverse Enginnier script that influences colony UI from Grand Colonies\n" +
                "Entire Ashes of Domain Dev Team: for supporting my efforts on making this mod\n" +
                "Unofficial Starsector Discord server fellas for reporting bugs and feedback\n" +
                "CY/Milkdromeda, SirHartley, Avanitia for you gave me either lot of suggestions, tips  or  constantly reported bugs. Thank you for this !!!\n" +
                "Dwarven_Seaman for AoTD custom artworks \n" +
                "_RLSVD_ for AoTD custom artworks\n" +
                "Jagoda Jeruzalska for AoTD custom artworks";

        helpPanelTT.addSectionHeading(" Special Thanks ", Alignment.MID, 10f).getPosition().setLocation(0, 0).inTL(3, 3350);
        helpPanelTT.addPara(specThanks, Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(3, 3400);
        helpPanel.addUIElement(helpPanelTT).setLocation(0, 0).inTL(0, 0);
        helpPanelTT.getExternalScroller().setYOffset(scroller);
        mainTooltip.addComponent(helpPanel).inTL(170, 5);

    }

    private int showTierSection(CustomPanelAPI main, TooltipMakerAPI section, HashMap<List<ResearchOption>, Integer> industryTypeGroup, int highestIndexSoFar) {

        float xmover = 276;
        float spacerY = 120;
        int higestIndex = 0;

        for (List<ResearchOption> researchOptions : industryTypeGroup.keySet()) {
            int index = highestIndexSoFar;

            for (ResearchOption research : researchOptions) {

                if (Global.getSettings().getIndustrySpec(research.industryId).hasTag("casual_upgrade")) {
                    index -= 1;
                }
                if (Global.getSettings().getIndustrySpec(research.industryId).hasTag("rare_upgrade")) {
                    index += 1;
                }
                CustomPanelAPI vPanel = main.createCustomPanel(190, 70, null);
                vPanel.getPosition().setLocation(0, 0).inTL(5 + xmover * research.researchTier, 10 + index * spacerY);
                TooltipMakerAPI vTT = vPanel.createUIElement(190, 70, false);
                vTT.getPosition().inTL(5 + xmover * research.researchTier, 0);
                vTT.setForceProcessInput(true);


                LabelAPI label = vTT.addPara(research.industryName, Color.ORANGE, 0);
                label.getPosition().setLocation(0, 0).inTL(10, 8);

                ButtonAPI buttonInfo = vTT.addButton("More Info", null, 90, 18, 10f);
                buttonInfo.getPosition().setLocation(0, 0).inTL(0, 42);
                buttons.add(buttonInfo);
                buttonMap.put(buttonInfo, "research_info:" + research.industryId);
                ButtonAPI button = null;
                if (researchAPI.getCurrentResearching() != null && research.industryId.equals(researchAPI.getCurrentResearching().industryId)) {
                    button = vTT.addButton("Stop Research", null, 95, 18, 10f);

                    buttons.add(button);
                    buttonMap.put(button, "stop_research:" + research.industryId);
                } else if (researchAPI.isResearching()&&!researchAPI.isInQueue(research.industryId)) {

                    button = vTT.addButton("Queue", null, 95, 18, 10f);
                    button.setEnabled(researchAPI.canResearch(research.industryId, true));
                    buttons.add(button);
                    buttonMap.put(button, "queue_research:" + research.industryId);
                    if(researchAPI.isInQueue(research.industryId)){
                        button.setEnabled(false);
                    }
                }
                else{
                    button = vTT.addButton("Research", null, 95, 18, 10f);
                    button.setEnabled(researchAPI.canResearch(research.industryId, true));
                    buttons.add(button);
                    buttonMap.put(button, "start_research:" + research.industryId);
                }

                if (button != null) {
                    button.getPosition().setLocation(0, 0).inTL(95, 42);
                    if (research.isResearched||researchAPI.isInQueue(research.industryId)) {
                        button.setEnabled(false);
                        makerAPIHashMap.put(button, vTT);
                    }

                }


                vPanel.addUIElement(vTT).inTL(0, 0);
                section.addComponent(vPanel).inTL(10 + xmover * research.researchTier, (10 + index * spacerY));

                techPanels.add(vPanel);
                tracker.put(vPanel, research.industryId);

                index++;

                if (Global.getSettings().getIndustrySpec(research.industryId).hasTag("casual_upgrade")) {
                    index++;
                }
                if (Global.getSettings().getIndustrySpec(research.industryId).hasTag("rare_upgrade")) {
                    index -= 1;
                }

            }
            if (index > higestIndex) {
                higestIndex = index;
            }
        }


        return higestIndex;
    }

    private int showRest(CustomPanelAPI main, TooltipMakerAPI section, int highestIndexSoFar) {

        float xmover = 276;
        float spacerY = 120;
        int higestIndex = 0;

        int index = highestIndexSoFar;
        List<ResearchOption> filteredResearch = new ArrayList<>();
        for (ResearchOption research : researchAPI.getResearchOptions()) {
            IndustrySpecAPI indspec = Global.getSettings().getIndustrySpec(research.industryId);
            if (indspec.hasTag("farming") || indspec.hasTag("aquaculture") || indspec.hasTag("lightindustry") || indspec.hasTag("heavyindustry") || indspec.hasTag("mining") || indspec.hasTag("refining") || indspec.hasTag("experimental")) {
                continue;
            }
            filteredResearch.add(research);
        }
        for (int i = 0; i < 4; i++) {
            index = highestIndexSoFar;
            if (currentCategory != ProgressionTreeUiMode.OTHER) {
                if (i == 1) {
                    index -= 2;
                }
                if (i == 2) {
                    index -= 1;
                }
            }

            for (ResearchOption research : filteredResearch) {
                if (research.researchTier != i && i != 3) {
                    continue;
                }
                if (research.researchTier <= 2 && i == 3) {
                    continue;
                }
                if (research.isDisabled) continue;
                CustomPanelAPI vPanel = main.createCustomPanel(190, 70, null);
                vPanel.getPosition().setLocation(0, 0).inTL(5 + xmover * research.researchTier, 10 + index * spacerY);
                TooltipMakerAPI vTT = vPanel.createUIElement(190, 70, false);
                vTT.getPosition().inTL(5 + xmover * research.researchTier, 0);
                vTT.setForceProcessInput(true);

                LabelAPI label = vTT.addPara(research.industryName, Color.ORANGE, 0);
                label.getPosition().setLocation(0, 0).inTL(5, 8);
                ;
                ButtonAPI buttonInfo = vTT.addButton("More Info", null, 90, 18, 10f);
                buttonInfo.getPosition().setLocation(0, 0).inTL(1, 42);
                buttons.add(buttonInfo);
                buttonMap.put(buttonInfo, "research_info:" + research.industryId);
                ButtonAPI button = null;
                if (researchAPI.getCurrentResearching() != null && research.industryId.equals(researchAPI.getCurrentResearching().industryId)) {
                    button = vTT.addButton("Stop Research", null, 95, 18, 10f);

                    buttons.add(button);
                    buttonMap.put(button, "stop_research:" + research.industryId);
                } else if (researchAPI.isResearching()&&!researchAPI.isInQueue(research.industryId)) {

                    button = vTT.addButton("Queue", null, 95, 18, 10f);
                    button.setEnabled(researchAPI.canResearch(research.industryId, true));
                    buttons.add(button);
                    buttonMap.put(button, "queue_research:" + research.industryId);
                    if(researchAPI.isInQueue(research.industryId)){
                        button.setEnabled(false);
                    }
                }
                else{
                    button = vTT.addButton("Research", null, 95, 18, 10f);
                    button.setEnabled(researchAPI.canResearch(research.industryId, true));
                    buttons.add(button);
                    buttonMap.put(button, "start_research:" + research.industryId);
                }

                if (button != null) {
                    button.getPosition().setLocation(0, 0).inTL(95, 42);
                    if (research.isResearched||researchAPI.isInQueue(research.industryId)) {
                        button.setEnabled(false);
                        makerAPIHashMap.put(button, vTT);
                    }

                }

                vPanel.addUIElement(vTT).inTL(0, 0);
                section.addComponent(vPanel).inTL(10 + xmover * research.researchTier, (10 + index * spacerY));
                section.addSpacer(10);
                techPanels.add(vPanel);
                tracker.put(vPanel, research.industryId);
                index++;
                if (index > higestIndex) {
                    higestIndex = index;
                }


            }
        }


        return (int) higestIndex;
    }

    void showTechIncased(String industryID) {
        float sizeSection = availableWidth - 175;
        float height = (int) this.pH / 1.45f;
        currentlyResearchingInfo = panel.createCustomPanel(sizeSection / 2, (float) availableHeight / 6.5f, null);
        currentlyResearchingInfo.getPosition().setLocation(0, 0).inTL(0, 0);
        currentlyResearchingInfoTT = currentlyResearchingInfo.createUIElement(sizeSection / 2, (float) availableHeight / 6.5f, false);
        currentlyResearchingInfoTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (industryID != null) {
            String tier = tierDecider(currResearching.researchTier);

            float percent = (currResearching.currentResearchDays - currResearching.researchCost) / currResearching.researchCost;
            if (AoDUtilis.getResearchAPI().getCurrentResearcher() != null && AoDUtilis.getResearchAPI().getCurrentResearcher().hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE) && (currResearching.industryId.equals("triheavy") || currResearching.industryId.equals("hegeheavy") || currResearching.industryId.equals("ii_stellacastellum"))) {
                percent = (currResearching.currentResearchDays - currResearching.researchCost * 3) / currResearching.researchCost * 3;
            }
            float true_percent = Math.abs(percent) * 100f;
            if (currResearching.currentResearchDays == currResearching.researchCost) {
                true_percent = 0;
            }

            currentlyResearchingInfoTT.addPara("Curently Researching : " + Global.getSettings().getIndustrySpec(industryID).getName(), Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(185, 5);
            currentlyResearchingInfoTT.addPara("Tier : " + tier, Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(185, 30);
            String formattedString = String.format("%.02f", true_percent);
            currentlyResearchingInfoTT.addPara("Curent progress :" + formattedString + "%" + "\n" + currResearching.currentResearchDays + " days till research is completed", Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(185, 55);
            ButtonAPI buttonAPI = currentlyResearchingInfoTT.addButton("Show more info", null, 180, 25, 10f);
            buttonAPI.getPosition().setLocation(0, 0).inTL(344, 99);
            buttons.add(buttonAPI);
            buttonMap.put(buttonAPI, "archive_show:" + industryID);
            buttonAPI = currentlyResearchingInfoTT.addButton("Stop Research ", null, 180, 25, 10f);
            buttonAPI.getPosition().setLocation(0, 0).inTL(1, 99);
            buttons.add(buttonAPI);
            buttonMap.put(buttonAPI, "stop_research:" + industryID);

        }

        currentlyResearchingInfo.addUIElement(currentlyResearchingInfoTT).inTL(0, 0);
        mainTooltip.addComponent(currentlyResearchingInfo).inTL(175, 5);

    }

    void showReqForResearch(String id) {
        float sizeSection = availableWidth - 175;
        wantsToKnowResearchReq = panel.createCustomPanel(sizeSection / 2, 330, null);
        wantsToKnowResearchReq.getPosition().setLocation(0, 0).inTL(0, 0);
        wantsToKnowResearchReqTT = wantsToKnowResearchReq.createUIElement(sizeSection / 2, 330, false);
        wantsToKnowResearchReqTT.getPosition().setLocation(0, 0).inTL(0, 0);

        wantsToKnowResearchReqIndustry = panel.createCustomPanel(sizeSection / 2, 110, null);
        wantsToKnowResearchReqIndustry.getPosition().setLocation(0, 0).inTL(0, 0);
        wantsToKnowResearchReqIndustryTT = wantsToKnowResearchReqIndustry.createUIElement(sizeSection / 2, 110, true);
        wantsToKnowResearchReqIndustry.getPosition().setLocation(0, 0).inTL(0, 0);

        wantsToKnowResearchReqItem = panel.createCustomPanel(sizeSection / 2, 110, null);
        wantsToKnowResearchReqItem.getPosition().setLocation(0, 0).inTL(0, 0);
        wantsToKnowResearchReqItemTT = wantsToKnowResearchReqItem.createUIElement(sizeSection / 2, 110, true);
        wantsToKnowResearchReqItemTT.getPosition().setLocation(0, 0).inTL(0, 0);

        if (id != null) {
            List<ResearchOption> allreq = researchAPI.getRequierements(wantsToHaveInfoAbout.industryId, false);
            allreq.addAll(researchAPI.getRequierements(wantsToHaveInfoAbout.industryId, true));
            wantsToKnowResearchReqTT.addPara("Technologies required for researching: " + Global.getSettings().getIndustrySpec(id).getName(), Color.WHITE, 10f);
            int index = 0;
            int spacerY = 60;
            Color base = Misc.getStoryOptionColor();
            Color bg = Misc.getStoryBrightColor();
            boolean triple = false;
            for (ResearchOption s : allreq) {
                if (s.isDisabled) continue;

                if (s.isResearched) {
                    base = Misc.getStoryOptionColor();
                    bg = Misc.getStoryBrightColor();
                } else {
                    bg = Misc.getNegativeHighlightColor();
                    base = Misc.getNegativeHighlightColor();
                }

                CustomPanelAPI vPanel = wantsToKnowResearchReq.createCustomPanel(sizeSection / 2.2f, 50, null);
                vPanel.getPosition().setLocation(0, 0).inTL(0, index * spacerY);
                TooltipMakerAPI vTT = vPanel.createUIElement(sizeSection / 2.5f, 250, false);
                vTT.getPosition().inTL(0, 0);
                vTT.setForceProcessInput(true);
                ButtonAPI boxBorder = wantsToKnowResearchReqIndustryTT.addAreaCheckbox("", null, base,
                        Misc.getDarkPlayerColor(), bg, sizeSection / 2.2f, 52, 0);
                boxBorder.getPosition().inTL(0, index * spacerY);
                boxBorder.setEnabled(false);
                wantsToKnowResearchReqIndustryTT.addSpacer(10);
                vTT.addPara(s.industryName, Color.ORANGE, 0).getPosition().setLocation(0, 0).inTL(5, 8);
                vPanel.addUIElement(vTT).inTL(0, 0);
                wantsToKnowResearchReqIndustryTT.addComponent(vPanel).inTL(0, index * spacerY);
                index++;
            }
            wantsToKnowResearchReqTT.addPara("Items requiered to pay initally for starting research of this industry ", 10f).getPosition().setLocation(0, 0).inTL(5, 29 + (float) availableHeight / 6.5f);
            if (wantsToHaveInfoAbout.requieredItems != null) {
                index = 0;
                spacerY = 60;
                for (HashMap.Entry<String, Integer> items : wantsToHaveInfoAbout.requieredItems.entrySet()) {
                    Color color1;
                    Color color2;
                    Color color3;
                    if (researchAPI.hasMetReq(items)) {
                        color1 = Misc.getStoryOptionColor();
                        color2 = Misc.getDarkPlayerColor();
                        color3 = Misc.getStoryOptionColor();
                    } else {
                        color1 = Misc.getNegativeHighlightColor();
                        color2 = Misc.getDarkPlayerColor();
                        color3 = Misc.getNegativeHighlightColor();
                    }
                    int newCalculus = items.getValue();
                    if (researchAPI.getCurrentResearcher() != null) {
                        PersonAPI person = researchAPI.getCurrentResearcher();
                        if (person.hasTag("aotd_resourceful")) {
                            newCalculus = items.getValue() - 1;

                            boolean notCutting = items.getKey().equals("hegeheavy_databank") || items.getKey().equals("triheavy_databank") || items.getKey().equals("ii_ind_databank");
                            if (notCutting) {
                                newCalculus += 1;
                            }
                            if (items.getKey().equals("domain_artifacts") || items.getKey().equals("water")) {
                                newCalculus -= 99;
                            }
                            if (newCalculus == 0 && !notCutting) {
                                continue;
                            }

                        }
                        if (person.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)) {
                            newCalculus = items.getValue() - 1;
                            boolean cutting = items.getKey().equals("hegeheavy_databank") || items.getKey().equals("triheavy_databank") || items.getKey().equals("ii_ind_databank");
                            if (!cutting) {
                                newCalculus += 1;
                            }
                            if (newCalculus == 0 && cutting) {
                                continue;
                            }
                            triple = true;

                        }
                    }

                    if (Global.getSettings().getSpecialItemSpec(items.getKey()) != null) {
                        SpecialItemSpecAPI itemReq = Global.getSettings().getSpecialItemSpec(items.getKey());
                        CustomPanelAPI vPanel = wantsToKnowResearchReqItem.createCustomPanel(sizeSection / 2.2f, 50, null);
                        vPanel.getPosition().setLocation(0, 0).inTL(0, index * spacerY);
                        TooltipMakerAPI vTT = vPanel.createUIElement(sizeSection / 2.2f, 50, false);
                        vTT.getPosition().inTL(0, 0);
                        vTT.setForceProcessInput(true);
                        ButtonAPI boxBorder = wantsToKnowResearchReqItemTT.addAreaCheckbox("", null, color1,
                                color2, color3, sizeSection / 2.2f, 50, 0);
                        boxBorder.getPosition().inTL(0, index * spacerY);
                        boxBorder.setEnabled(false);
                        wantsToKnowResearchReqItemTT.addSpacer(spacerY - 50);
                        vTT.addPara("Item:" + itemReq.getName() + "\nQuantity: " + newCalculus, Color.ORANGE, 0).getPosition().setLocation(0, 0).inTL(5, 15);
                        vPanel.addUIElement(vTT).inTL(0, 0);
                        wantsToKnowResearchReqItemTT.addComponent(vPanel).inTL(0, index * spacerY);
                    } else {
                        CommoditySpecAPI itemReq = Global.getSettings().getCommoditySpec(items.getKey());
                        CustomPanelAPI vPanel = wantsToKnowResearchReqItem.createCustomPanel(sizeSection / 2.2f, 50, null);
                        vPanel.getPosition().setLocation(0, 0).inTL(0, index * spacerY);
                        TooltipMakerAPI vTT = vPanel.createUIElement(sizeSection / 2.2f, 50, false);
                        vTT.getPosition().inTL(0, 0);
                        vTT.setForceProcessInput(true);
                        ButtonAPI boxBorder = wantsToKnowResearchReqItemTT.addAreaCheckbox("", null, color1,
                                color2, color3, sizeSection / 2.2f, 50, 0);
                        boxBorder.getPosition().inTL(0, index * spacerY);
                        boxBorder.setEnabled(false);
                        wantsToKnowResearchReqItemTT.addSpacer(spacerY - 50);
                        vTT.addPara("Item:" + itemReq.getName() + "\nQuantity: " + newCalculus, Color.ORANGE, 0).getPosition().setLocation(0, 0).inTL(5, 15);
                        vPanel.addUIElement(vTT).inTL(0, 0);
                        wantsToKnowResearchReqItemTT.addComponent(vPanel).inTL(0, index * spacerY);
                    }
                    index++;
                }

            }
            if (!wantsToHaveInfoAbout.isResearched) {
                float cost = wantsToHaveInfoAbout.researchCost;
                float secondaryCost = wantsToHaveInfoAbout.currentResearchDays;
                if (secondaryCost != -1) {
                    cost = secondaryCost;
                }
                if (AoDUtilis.getResearchAPI().getCurrentResearcher() != null && AoDUtilis.getResearchAPI().getCurrentResearcher().hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE) && triple) {
                    cost *= 3;
                }
                wantsToKnowResearchReqTT.addPara("It would take " + cost + " days to complete research ", 10f).getPosition().setLocation(0, 0).inTL(5, 175 + (float) availableHeight / 6.5f);
                if (currResearching == null) {
                    ButtonAPI buttonAPI = wantsToKnowResearchReqTT.addButton("Start Research ", null, 120, 20, 10f);
                    if (!researchAPI.canResearch(wantsToHaveInfoAbout.industryId, true)) {
                        buttonAPI.setEnabled(false);
                    }
                    buttons.add(buttonAPI);
                    buttonMap.put(buttonAPI, "start_research:" + wantsToHaveInfoAbout.industryId);
                    buttonAPI.getPosition().setLocation(0, 0).inTL(405, 173 + (float) availableHeight / 6.5f);
                }
                else if (!researchAPI.isInQueue(wantsToHaveInfoAbout.industryId) && !wantsToHaveInfoAbout.industryId.equals(currResearching.industryId)){
                    ButtonAPI buttonAPI = wantsToKnowResearchReqTT.addButton("Queue", null, 120, 20, 10f);
                    if (!researchAPI.canResearch(wantsToHaveInfoAbout.industryId, true)) {
                        buttonAPI.setEnabled(false);
                    }
                    buttons.add(buttonAPI);
                    buttonMap.put(buttonAPI, "queue_research:" + wantsToHaveInfoAbout.industryId);
                    buttonAPI.getPosition().setLocation(0, 0).inTL(405, 173 + (float) availableHeight / 6.5f);
                }

            }
        }


        wantsToKnowResearchReq.addUIElement(wantsToKnowResearchReqTT).inTL(0, 0);
        wantsToKnowResearchReqIndustry.addUIElement(wantsToKnowResearchReqIndustryTT).inTL(0, 0);
        wantsToKnowResearchReqItem.addUIElement(wantsToKnowResearchReqItemTT).inTL(0, 0);
        mainTooltip.addComponent(wantsToKnowResearchReqItem).inTL(175, 190 + (float) availableHeight / 6.5f);
        mainTooltip.addComponent(wantsToKnowResearchReqIndustry).inTL(175, 42.5f + (float) availableHeight / 6.5f);
        mainTooltip.addComponent(wantsToKnowResearchReq).inTL(175, 5 + (float) availableHeight / 6.5f);
    }

    void showScientistImage() {
        float sizeSection = availableWidth - 175;
        scientistPanel = panel.createCustomPanel(167, 167, null);
        scientistPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        scientistPanelTT = scientistPanel.createUIElement(167, 167, false);
        scientistPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        scientistPanelAnchorTT = scientistPanel.createUIElement(167, 167, false);
        scientistPanelAnchorTT.getPosition().setLocation(0, 0).inTL(0, 0);
//        ButtonAPI boxBorder = scientistPanelTT.addAreaCheckbox("", null,Misc.getBasePlayerColor(),
//                Misc.getDarkPlayerColor(), Misc.getBasePlayerColor(), 172, 167, 0);
        if (researchAPI.getCurrentResearcher() != null) {
            scientistPanelAnchorTT.addImage(researchAPI.getCurrentResearcher().getPortraitSprite(), 157, 157, 10f);
        }
        scientistPanel.addUIElement(scientistPanelTT).inTL(-5, 0);
        scientistPanel.addUIElement(scientistPanelAnchorTT).inTL(0, -6);
        mainTooltip.addComponent(scientistPanel).inTL(185 + sizeSection / 2, 5);
    }

    void showNameAndSurrnameofScientist() {
        float sizeSection = availableWidth - 175;
        scientistDescrpPanel = panel.createCustomPanel(330, 60, null);
        scientistDescrpPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        scientistDescrpPanelTT = scientistDescrpPanel.createUIElement(330, 60, false);
        scientistDescrpPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        scientistDescrpPanelTT.addPara("Current Head of Research Center", Color.CYAN, 10f);
        if (researchAPI.getCurrentResearcher() != null) {
            PersonAPI personAPI = researchAPI.getCurrentResearcher();
            scientistDescrpPanelTT.addPara(personAPI.getName().getFirst() + " " + personAPI.getName().getLast(), Color.ORANGE, 10f);
        } else {
            scientistDescrpPanelTT.addPara("None", Color.RED, 10f);
        }
        scientistDescrpPanel.addUIElement(scientistDescrpPanelTT).inTL(0, 0);
        mainTooltip.addComponent(scientistDescrpPanel).inTL(10 + 185 + 167 + sizeSection / 2, 5);
    }

    void showSpecAbilityDescrp() {
        float sizeSection = availableWidth - 175;
        scientistSpecAbilityPanel = panel.createCustomPanel(330, 107, null);
        scientistSpecAbilityPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        scientistSpecAbilityPanelTT = scientistSpecAbilityPanel.createUIElement(330, 107, true);
        scientistSpecAbilityPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (researchAPI.getCurrentResearcher() != null) {
            PersonAPI personAPI = researchAPI.getCurrentResearcher();
            if (personAPI.getId().equals("sophia")) {
                personAPI.addTag("aotd_resourceful");
            }
            if (personAPI.hasTag("aotd_resourceful")) {
                scientistSpecAbilityPanelTT.addPara("Special Ability", Color.CYAN, 10f);
                scientistSpecAbilityPanelTT.addPara("Resourceful", Color.ORANGE, 10f);
                scientistSpecAbilityPanelTT.addPara("Description: That scientist is very cautious and wants to use as little resources to accomplish task as possible\n\nDecrease cost of item for inital research by 1 unit.\n\nThis does not include special databanks", Color.WHITE, 10f);
            }
            if (personAPI.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)) {
                scientistSpecAbilityPanelTT.addPara("Special Ability", Color.CYAN, 10f);
                scientistSpecAbilityPanelTT.addPara("Seeker of Knowledge", Color.ORANGE, 10f);
                scientistSpecAbilityPanelTT.addPara("Description: That scientist possesses knowledge that for many has been already deemed to be lost in sands of time\n\nNegate need for special type of databanks for research, but increase amount of days to research technologies that requieres special databank three times more", Color.WHITE, 10f);
            }
        }

        scientistSpecAbilityPanel.addUIElement(scientistSpecAbilityPanelTT).inTL(0, 0);
        mainTooltip.addComponent(scientistSpecAbilityPanel).inTL(10 + 185 + 167 + sizeSection / 2, 5 + 60);
    }

    void showBonuses() {
        float sizeSection = availableWidth - 175;
        bonusPanel = panel.createCustomPanel((sizeSection / 2) - 18, 230, null);
        bonusPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        bonusPanelTT = bonusPanel.createUIElement((sizeSection / 2) - 18, 230, true);
        bonusPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        bonusPanelTT.addPara("Current Bonuses\n", Color.CYAN, 10f);
        boolean bonus = false;
        if (researchAPI.getCurrentResearcher() != null) {
            if (researchAPI.getCurrentResearcher().hasTag("aotd_resourceful")) {
                bonusPanelTT.addPara("Bonus from Head of Research Center Skill - Resourceful\n", Color.ORANGE, 10f);
                bonus = true;
            }
            if (researchAPI.getCurrentResearcher().hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)) {
                bonusPanelTT.addPara("Bonus from Head of Research Center Skill - Seeker of Knowledge\n", Color.ORANGE, 10f);
                bonus = true;
            }
        }
        if (researchAPI.alreadyResearchedAmount() >= 10) {
            bonusPanelTT.addPara("Unlocked Industry Synergies (Researched 10 or more technologies)\n", Color.ORANGE, 10f);
            bonus = true;
        }
        if(researchAPI.getResearchFacilitiesQuantity()>1){
         bonus=true;
            bonusPanelTT.addPara((researchAPI.getResearchFacilitiesQuantity()-1)*10+"% bonus speed to research (Increase by building more research facilities)", Color.ORANGE, 10f);
        }
        if (!bonus) {
            bonusPanelTT.addPara("None At this moment !", Color.RED, 10f);
        }

        bonusPanel.addUIElement(bonusPanelTT).inTL(0, 0);
        mainTooltip.addComponent(bonusPanel).inTL(185 + sizeSection / 2, 5 + 60 + 107);
    }

    void showStats() {
        float sizeSection = availableWidth - 175;
        statisticsPanel = panel.createCustomPanel((sizeSection / 2) - 18, (337/2)+1, null);
        statisticsPanel.getPosition().setLocation(0, 0).inTL(0, 0);
        statisticsPanelTT = statisticsPanel.createUIElement((sizeSection / 2) - 18, (337/2)+1, true);
        statisticsPanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        int tier0 = researchAPI.alreadyResearchedAmountCertainTier(0);
        int tier1 = researchAPI.alreadyResearchedAmountCertainTier(1);
        int tier2 = researchAPI.alreadyResearchedAmountCertainTier(2);
        int tier3 = researchAPI.alreadyResearchedAmountCertainTier(3);
        int all = researchAPI.alreadyResearchedAmount();
        int left = researchAPI.getResearchOptions().size() -all  - researchAPI.getDissabledResearch();
        statisticsPanelTT.addPara("Currently researched " + tier1 + " of Basic technologies", Color.ORANGE, 10f);
        statisticsPanelTT.addPara("Currently researched " + tier2 + " of Sophisticated technologies", Color.ORANGE, 10f);
        statisticsPanelTT.addPara("Currently researched " + tier3 + " of Pre Collapse technologies", Color.ORANGE, 10f);
        statisticsPanelTT.addPara("Technologies left to research " + left, Color.ORANGE, 10f);
        if (all >= 7) {
            statisticsPanelTT.addPara("Some individuals started to notice your faction's recent advancements in many technological fields", Color.CYAN, 10f);
        }
        if (all < 7) {
            statisticsPanelTT.addPara("Your technological advancements are below normal level of the Persean Sector", Color.CYAN, 10f);
        }
        if (all >= 7 && all < 16) {
            statisticsPanelTT.addPara("Your technological advancements are on equal level of the Peresean Sector", Color.CYAN, 10f);
        }
        if (all >= 16 && left != 0) {
            statisticsPanelTT.addPara("Your technological advancements are slightly higher than of the Persean Sector", Color.CYAN, 10f);
        }
        if (left <= 0) {
            statisticsPanelTT.addPara("We have researched all technologies and advanced ourselves to the point that we can be called without any doubt \"Domain's true successor\". A new hope of the Persean Sector", Color.CYAN, 10f);
        }
        statisticsPanel.addUIElement(statisticsPanelTT).inTL(0, 0);
        mainTooltip.addComponent(statisticsPanel).inTL(185 + sizeSection / 2, 502+(337/2));
    }
    void showQueue() {
        float sizeSection = availableWidth - 175;
        queuePanel = panel.createCustomPanel((sizeSection / 2) - 18, (337/2)+100, null);
        queuePanel.getPosition().setLocation(0, 0).inTL(0, 0);
        queuePanelTT = queuePanel.createUIElement((sizeSection / 2) - 18, (337/2)+100, true);
        queuePanelTT.getPosition().setLocation(0, 0).inTL(0, 0);
        queuePanelOptionsTT = queuePanel.createUIElement((sizeSection / 2) - 18, (337/2)+50, true);
        queuePanelOptionsTT.getPosition().setLocation(0, 0).inTL(0, 0);
        queuePanelTT.addPara("Research Queue System\nCurrently Queue holds "+ researchAPI.getResearchQueue().size()+ " Research Options",Color.CYAN,10f).getPosition().setLocation(0,0).inTL(5,10);
        ButtonAPI buttonApi = queuePanelTT.addButton("Clear Queue",null,150,30,10f);
        buttonApi.getPosition().setLocation(0,0).inTL(340,10);
        buttons.add(buttonApi);
        buttonMap.put(buttonApi, "clear_queue");
        int index = 0;
        int spacerY = 65;
        for (ResearchOption research : researchAPI.getResearchQueue()) {
            CustomPanelAPI vPanel = queuePanel.createCustomPanel((sizeSection / 2) - 18, 120, null);
            vPanel.getPosition().setLocation(0, 0).inTL(5, index * spacerY);
            TooltipMakerAPI vTT = vPanel.createUIElement((sizeSection / 2) - 18, 140, false);
            vTT.getPosition().inTL(5, 0);
            vTT.setForceProcessInput(true);
            ButtonAPI boxBorder;
            boxBorder = queuePanelOptionsTT.addAreaCheckbox("", null, Color.ORANGE,
                    Misc.getDarkPlayerColor(), Misc.getStoryBrightColor(), (sizeSection / 2) - 18, 56, 0);
            queuePanelOptionsTT.addSpacer(spacerY - 56);
            boxBorder.setHighlightBrightness(0f);
            boxBorder.setHighlightBounceDown(false);
            boxBorder.setFlashBrightness(0f);
            boxBorder.setGlowBrightness(0f);
            boxBorder.setEnabled(false);
            boxBorder.setChecked(false);
            boxBorder.setClickable(false);
            boxBorder.setMouseOverSound(null);
            boxBorder.setButtonPressedSound(null);
            ButtonAPI up;
            ButtonAPI down;
            ButtonAPI prioritize;
            ButtonAPI unprioritize;
            ButtonAPI delete;
            vTT.addPara(research.industryName, Color.ORANGE, 0).getPosition().setLocation(0, 0).inTL(10, 5);
             up = vTT.addButton("Down",null,60,20,10f);
             up.getPosition().setLocation(0,0).inTL(140,25);
            down = vTT.addButton("Up",null,60,20,10f);
            down.getPosition().setLocation(0,0).inTL(210,25);
            prioritize = vTT.addButton("Move to top",null,100,20,10f);
            prioritize.getPosition().setLocation(0,0).inTL(280,25);
            unprioritize = vTT.addButton("Move to bottom ",null,110,20,10f);
            unprioritize.getPosition().setLocation(0,0).inTL(390,25);
            delete = vTT.addButton("Remove",null,110,20,10f);

            delete.getPosition().setLocation(0,0).inTL(10,25);
            vPanel.addUIElement(vTT).inTL(0, 0);
            buttons.add(up);
            buttonMap.put(up, "queue_up:"+research.industryId);
            buttons.add(down);
            buttonMap.put(down, "queue_down:"+research.industryId);
            buttons.add(prioritize);
            buttonMap.put(prioritize, "queue_prioritize:"+research.industryId);
            buttons.add(unprioritize);
            buttonMap.put(unprioritize, "queue_unprioritize:"+research.industryId);
            buttons.add(delete);
            buttonMap.put(delete, "queue_delete:"+research.industryId);
            queuePanelOptionsTT.addComponent(vPanel).inTL(0, index * spacerY);
            index++;
        }
        queuePanel.addUIElement(queuePanelTT).inTL(0, 0);
        queuePanel.addUIElement(queuePanelOptionsTT).inTL(0, 50);
        mainTooltip.addComponent(queuePanel).inTL(185 + sizeSection / 2, 402);
    }
    void showMoreInfoContainer(String industryID) {
        float sizeSection = availableWidth - 175;
        float height = (int) this.pH / 1.45f;
        wantsToKnowResearch = panel.createCustomPanel(sizeSection / 2, 93, null);
        wantsToKnowResearch.getPosition().setLocation(0, 0).inTL(0, 0);
        wantsToKnowResearchTT = wantsToKnowResearch.createUIElement(sizeSection / 2, 93, false);
        wantsToKnowResearchTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (industryID != null) {
            String IndOrStructure = "";
            if (Global.getSettings().getIndustrySpec(industryID).hasTag("structure")) {
                IndOrStructure = " - Structure";
            } else {
                IndOrStructure = " - Industry";
            }
            String tier = tierDecider(researchAPI.getResearchOption(industryID).researchTier);
            if (Global.getSettings().getIndustrySpec(industryID).hasTag("experimental")) tier = "Experimental";
            wantsToKnowResearchTT.addPara(Global.getSettings().getIndustrySpec(industryID).getName() + IndOrStructure, Color.ORANGE, 10f).getPosition().setLocation(0, 0).inTL(190, 5);
            wantsToKnowResearchTT.addPara("Tier : " + tier, Color.CYAN, 10f).getPosition().setLocation(0, 0).inTL(190, 30);
            if (researchAPI.getResearchOption(industryID).isResearched) {
                wantsToKnowResearchTT.addPara("Researched : Yes", Color.GREEN, 10f).getPosition().setLocation(0, 0).inTL(190, 55);
            } else {
                wantsToKnowResearchTT.addPara("Researched : No", Color.RED, 10f).getPosition().setLocation(0, 0).inTL(190, 55);
            }
            ButtonAPI buttonDownSize = wantsToKnowResearchTT.addButton("Down", null, 40, 15, 10f);
            buttonDownSize.getPosition().setLocation(0, 0).inTL(330, 54);
            buttons.add(buttonDownSize);
            buttonMap.put(buttonDownSize, "down_size");
            ButtonAPI buttonUpSize = wantsToKnowResearchTT.addButton("Up", null, 40, 15, 10f);
            buttonUpSize.getPosition().setLocation(0, 0).inTL(470, 54);
            ButtonAPI sizeBox = wantsToKnowResearchTT.addAreaCheckbox("",null,Misc.getBasePlayerColor(),
              Misc.getDarkPlayerColor(), Misc.getBasePlayerColor(),90,20,10f);
            wantsToKnowResearchTT.addPara("Size of Example Market ",Color.WHITE,10f).getPosition().setLocation(0,0).inTL(340,30);
            wantsToKnowResearchTT.addPara("Size : "+defaultSizeOfExampleMarket,Color.CYAN,10f).getPosition().setLocation(0,0).inTL(395,54);
            sizeBox.getPosition().setLocation(0,0).inTL(375,52);
            sizeBox.setHighlightBrightness(0f);
            sizeBox.setHighlightBounceDown(false);
            sizeBox.setFlashBrightness(0f);
            sizeBox.setGlowBrightness(0f);
            sizeBox.setEnabled(false);
            sizeBox.setChecked(false);
            sizeBox.setClickable(false);
            sizeBox.setMouseOverSound(null);
            sizeBox.setButtonPressedSound(null);

            buttons.add(buttonUpSize);
            buttonMap.put(buttonUpSize, "up_size");
        }

        wantsToKnowResearch.addUIElement(wantsToKnowResearchTT).inTL(0, 0);
        mainTooltip.addComponent(wantsToKnowResearch).inTL(175, 465);

    }

    private String tierDecider(int tier) {
        if (tier == 0) {
            return "Primitive";
        }
        if (tier == 1) {
            return "Basic";
        }
        if (tier == 2) {
            return "Sophisticated";
        }
        if (tier == 3) {
            return "Pre Collapse";
        }
        return "Pre Collapse";
    }

    void showInfoImage(String industryID) {
        float height = (int) this.pH / 1.45f;
        wantsToKnowResearchImage = panel.createCustomPanel(183, 90, null);
        wantsToKnowResearchImage.getPosition().setLocation(0, 0).inTL(0, 0);
        wantsToKnowResearchImageTT = wantsToKnowResearchImage.createUIElement(183, 90, false);
        wantsToKnowResearchImageTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (industryID != null) {
            wantsToKnowResearchImageTT.addImage(Global.getSettings().getIndustrySpec(industryID).getImageName(), 195, 90, 1);

        }
        wantsToKnowResearchImage.addUIElement(wantsToKnowResearchImageTT).inTL(-10, 0.5f);
        //add the subpanel to the main panel
        mainTooltip.addComponent(wantsToKnowResearchImage).inTL(170, 465);

    }

    void showIndustryImage(String industryID) {
        float height = (int) this.pH / 1.45f;
        industryPanelImage = panel.createCustomPanel(183, 90, null);
        industryPanelImage.getPosition().setLocation(0, 0).inTL(0, 0);
        industryPanelImageTT = industryPanelImage.createUIElement(183, 90, false);
        industryPanelImageTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (industryID != null) {
            industryPanelImageTT.addImage(Global.getSettings().getIndustrySpec(industryID).getImageName(), 195, 90, 1);

        }
        industryPanelImage.addUIElement(industryPanelImageTT).inTL(-10, 0);
        //add the subpanel to the main panel
        mainTooltip.addComponent(industryPanelImage).inTL(170, 6);

    }

    void showIndustryDescrp(String industryID) {
        boolean hadIndustryBefore = false;
        int sizeSection = availableWidth - 175;
        float opad = 10f;
        float height = (float) availableHeight / 3;

        if (wantsInfoAboutCurrentlyResearching) {
            height += 94;
        }
        industryPanelDescrp = panel.createCustomPanel((float) sizeSection / 2, height, null);
        industryPanelDescrp.getPosition().setLocation(0, 0).inTL(0, 0);
        industryPanelDescrpTT = industryPanelDescrp.createUIElement((float) sizeSection / 2, height, true);
        industryPanelDescrpTT.getPosition().setLocation(0, 0).inTL(0, 0);
        if (industryID != null) {
            MarketAPI original = researchAPI.firstMarketThatHaveResearchFacility();
            Color color = original.getFaction().getBaseUIColor();
            Color dark = original.getFaction().getDarkUIColor();
            Color gray = Misc.getGrayColor();
            String modId = researchAPI.getResearchOption(industryID).modId;
            industryPanelDescrpTT.addSectionHeading("Basic Information", color, dark, Alignment.MID, opad).getPosition().setLocation(0, 0).inTL(0, 5);
            if (Global.getSettings().getIndustrySpec(industryID).hasTag("lost_tech")) {
                industryPanelDescrpTT.addPara("\nWarning! This Industry is using or producing commodities that are no longer available on industrial scale in the Persean Sector", Misc.getNegativeHighlightColor(), 1f);
            }
            if (modId == null || modId.isEmpty() || modId.contains("vanilla")) {
                industryPanelDescrpTT.addPara("\n[Vanilla] " + getIndustryDesc(industryID), 1);

            } else {
                industryPanelDescrpTT.addPara("\n[" + Global.getSettings().getModManager().getModSpec(modId).getName() + "]" + getIndustryDesc(industryID), 1);
            }
            copy.setSize(defaultSizeOfExampleMarket);
            processingMarket = true;
            List<MarketConditionAPI> copyConditions = copy.getConditions();
            ArrayList<String> conditionIds = new ArrayList<>();
            for (MarketConditionAPI condition : copyConditions) {
                if (condition.getId().contains("ore") || condition.getId().contains("volatiles") || condition.getId().contains("organics") || condition.getId().contains("farmland")) {
                    conditionIds.add(condition.getSpec().getId());
                }

            }
            for (String condition : conditionIds) {
                copy.removeCondition(condition);
            }
            copy.addCondition(Conditions.RARE_ORE_MODERATE);
            copy.addCondition(Conditions.ORE_MODERATE);
            copy.addCondition(Conditions.VOLATILES_DIFFUSE);
            copy.addCondition(Conditions.ORGANICS_COMMON);
            copy.addCondition(Conditions.FARMLAND_ADEQUATE);

            copy.reapplyConditions();
            copy.reapplyIndustries();
            if (!copy.hasIndustry(industryID)) {
                copy.addIndustry(industryID);
            } else {
                hadIndustryBefore = true;
            }
            copy.reapplyIndustries();
            copy.getIndustry(industryID).reapply();
            Industry upgrdInd = copy.getIndustry(industryID);
            List<MutableCommodityQuantity> dem = upgrdInd.getAllDemand();
            for (MutableCommodityQuantity mutableCommodityQuantity : upgrdInd.getAllDemand()) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyMult("ui", 0);
            }
            upgrdInd.reapply();
            List<MutableCommodityQuantity> sup = upgrdInd.getAllSupply();
            List<MutableCommodityQuantity> demAdjusted = new ArrayList<>();
            List<MutableCommodityQuantity> supAdjusted = new ArrayList<>();
            if (hadIndustryBefore) {
                adjustDem(dem, demAdjusted);
                adjustDem(sup, supAdjusted);
            } else {
                demAdjusted.addAll(dem);
                supAdjusted.addAll(sup);
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : upgrdInd.getAllDemand()) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("ui");
            }

            boolean hasDemand = false;
            for (MutableCommodityQuantity curr : dem) {
                hasDemand = true;
                break;
            }

            boolean hasSupply = false;
            if (upgrdInd.getId().equals(Industries.FARMING) || upgrdInd.getId().equals(Industries.AQUACULTURE) || upgrdInd.getId().equals(Industries.MINING)) {
                hasSupply = true;
            }

            if (!supAdjusted.isEmpty()) {
                hasSupply = true;
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : upgrdInd.getAllDemand()) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().modifyMult("ui", 0);
            }
            upgrdInd.reapply();
            if (hasSupply) {
                industryPanelDescrpTT.addSectionHeading("Production", color, dark, Alignment.MID, opad);
                industryPanelDescrpTT.beginIconGroup();
                industryPanelDescrpTT.setIconSpacingMedium();
                float icons = 0;
                if (upgrdInd.getId().equals(Industries.FARMING)) {
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.FOOD), copy.getSize(), IconRenderMode.NORMAL);
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(AodCommodities.RECITIFICATES), (int)(copy.getSize()*0.5), IconRenderMode.NORMAL);
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(AodCommodities.BIOTICS),(int)(copy.getSize()*0.5), IconRenderMode.NORMAL);

                    icons = copy.getSize();
                } else if (upgrdInd.getId().equals(Industries.AQUACULTURE)) {
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.FOOD), copy.getSize(), IconRenderMode.NORMAL);
                    icons = copy.getSize();
                } else if (upgrdInd.getId().equals(Industries.MINING)) {

                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.ORE), copy.getSize(), IconRenderMode.NORMAL);
                    icons += copy.getSize();
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.RARE_ORE), copy.getSize(), IconRenderMode.NORMAL);
                    icons += copy.getSize();
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.ORGANICS), copy.getSize(), IconRenderMode.NORMAL);
                    icons += copy.getSize();
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(Commodities.VOLATILES), copy.getSize(), IconRenderMode.NORMAL);
                    icons += copy.getSize();
                    industryPanelDescrpTT.addIcons(copy.getCommodityData(AodCommodities.WATER), copy.getSize() - 2, IconRenderMode.NORMAL);
                    icons += copy.getSize() - 2;


                } else {
                    for (MutableCommodityQuantity curr : upgrdInd.getAllSupply()) {
                        //if (qty <= 0) continue
                        int minus = 0;
                        if (copy.getAdmin().getStats().hasSkill(Skills.INDUSTRIAL_PLANNING)) {
                            minus = -1;
                        }
                        industryPanelDescrpTT.addIcons(copy.getCommodityData(curr.getCommodityId()), curr.getQuantity().getModifiedInt() + minus, IconRenderMode.NORMAL);
                        if(upgrdInd.getId().equals(AoDIndustries.SUBSIDISED_FARMING)||upgrdInd.getId().equals(AoDIndustries.ARTISANAL_FARMING)){
                            if(curr.getCommodityId().equals(Commodities.FOOD)){
                                industryPanelDescrpTT.addIcons(copy.getCommodityData(AodCommodities.BIOTICS), (int)(curr.getQuantity().getModifiedInt()*0.5f) , IconRenderMode.NORMAL);
                                industryPanelDescrpTT.addIcons(copy.getCommodityData(AodCommodities.RECITIFICATES),  (int)(curr.getQuantity().getModifiedInt()*0.5f) + minus, IconRenderMode.NORMAL);
                            }
                        }
                        icons += curr.getQuantity().getModifiedInt();

                    }
                }


                industryPanelDescrpTT.addIconGroup(30, 4, opad);
            }
            for (MutableCommodityQuantity mutableCommodityQuantity : dem) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("ui");
            }
            if (hasDemand) {
                industryPanelDescrpTT.addSectionHeading("Demand & effects", color, dark, Alignment.MID, opad);
                industryPanelDescrpTT.beginIconGroup();
                industryPanelDescrpTT.setIconSpacingMedium();
                float icons = 0;
                for (MutableCommodityQuantity curr : upgrdInd.getAllDemand()) {
                    CommodityOnMarketAPI com = copy.getCommodityData(curr.getCommodityId());
                    industryPanelDescrpTT.addIcons(com, curr.getQuantity().getModifiedInt(), IconRenderMode.NORMAL);
                    icons += curr.getQuantity().getModifiedInt();

                }


                industryPanelDescrpTT.addIconGroup(30, 4, opad);


            }

            industryPanelDescrpTT.addPara("*Shown production and demand values are adjusted as if industry was on market with size "+defaultSizeOfExampleMarket+" population without any production bonuses .*\n", gray, opad);
            ResearchOption currOption = researchAPI.getResearchOption(industryID);
            if (currOption.hasDowngrade) {
                industryPanelDescrpTT.addPara("This industry is an upgrade from: " + Global.getSettings().getIndustrySpec(currOption.downgradeId).getName() + "\n", Color.ORANGE, opad);
            }
            copy.removeCondition(Conditions.RARE_ORE_MODERATE);
            copy.removeCondition(Conditions.ORE_MODERATE);
            copy.removeCondition(Conditions.VOLATILES_DIFFUSE);
            copy.removeCondition(Conditions.ORGANICS_COMMON);
            copy.removeCondition(Conditions.FARMLAND_ADEQUATE);


            for (String conditionId : conditionIds) {
                MarketConditionAPI condition = copy.getSpecificCondition(copy.addCondition(conditionId));
                if (copy.getSurveyLevel() == MarketAPI.SurveyLevel.FULL && condition.requiresSurveying()) {
                    condition.setSurveyed(true);
                }
            }

            copy.reapplyConditions();
            copy.reapplyIndustries();
            industryPanelDescrpTT.setParaSmallInsignia();

            for (MutableCommodityQuantity mutableCommodityQuantity : upgrdInd.getAllDemand()) {
                upgrdInd.getDemand(mutableCommodityQuantity.getCommodityId()).getQuantity().unmodifyMult("ui");
            }

            processingMarket = false;
        }


        industryPanelDescrp.addUIElement(industryPanelDescrpTT).inTL(0, 0);
        //add the subpanel to the main panel
        if (wantsInfoAboutCurrentlyResearching) {
            mainTooltip.addComponent(industryPanelDescrp).inTL(175, 465);
        } else {
            mainTooltip.addComponent(industryPanelDescrp).inTL(175, 558);
        }


    }


    @Override
    public void positionChanged(PositionAPI position) {
        this.pos = position;
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
                bgColor.getAlpha() / 255f * alphaMult);
        GL11.glRectf(x, y, x + w, y + h);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();
    }


    @Override
    public void advance(float amount) {

        int diff = (availableHeight - pH) / 2;
        glClearStencil(0);
        glStencilMask(0xff);

        GL11.glPushMatrix();
        int width = (int) (Display.getWidth() * Display.getPixelScaleFactor()),
                height = (int) (Display.getHeight() * Display.getPixelScaleFactor());
        GL11.glViewport(0, 0, width, height);
        GL11.glOrtho(0, width, 0, height, -1, 1);

        GL11.glPopMatrix();


        //handles button input processing
        //if pressing a button changes something in the diplay, call reset()
        boolean needsReset = false;
        boolean prog_switch = false;
        for (ButtonAPI b : buttons) {
            String buttonContent = buttonMap.get(b);
            String[] tokens = buttonContent.split(":");
            if (b.isHighlighted() && makerAPIHashMap.containsKey(b)) {
                TooltipMakerAPI.TooltipCreator infoTooltip = new TooltipMakerAPI.TooltipCreator() {
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    public float getTooltipWidth(Object tooltipParam) {
                        return 300;
                    }

                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        tooltip.addPara("Can not delete or suspend contact at this time.", 0f);
                    }

                };
                makerAPIHashMap.get(b).addTooltipToPrevious(infoTooltip, TooltipMakerAPI.TooltipLocation.LEFT, true);

            }

            if (b.isChecked()) {

                b.setChecked(false);
                if (tokens[0].contains("PROGRESSION")) {
                    currentCategory = progressionTreeUIModeDecider(tokens[1]);
                    prog_switch = true;
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("up_size")){
                    changeDefaultSizeOfExampleMarket(true);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("down_size")){
                    changeDefaultSizeOfExampleMarket(false);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("queue_up")){
                   researchAPI.moveUpOrDownInQueue(tokens[1],false);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("queue_down")){
                    researchAPI.moveUpOrDownInQueue(tokens[1],true);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("queue_prioritize")){
                 ;  researchAPI.moveToTopOfQueue(tokens[1]);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("queue_unprioritize")){
                    researchAPI.moveToBottomOfQueue(tokens[1]);
                    needsReset = true;
                    break;
                }
                if(tokens[0].equals("queue_delete")){
                     researchAPI.removeFromQueue(tokens[1]);
                    needsReset = true;
                    break;
                }

                if (tokens[0].contains("HELP")) {
                    if (tokens[1].equals("what")) {
                        scroller = 0;
                    }
                    if (tokens[1].equals("research")) {
                        scroller = 70;
                    }
                    if (tokens[1].equals("paying")) {
                        scroller = 370;
                    }
                    if (tokens[1].equals("commodities")) {
                        scroller = 510;
                    }
                    if (tokens[1].equals("faq")) {
                        scroller = 930;
                    }
                    if (tokens[1].equals("thanks")) {
                        scroller = 2900;
                    }
                    needsReset = true;
                    break;
                }

                if (tokens[0].contains("UIMODE")) {
                    currentUIMode = mainUIModeDecider(tokens[1]);
                    if (currentUIMode.equals(UIMode.HELP)) {
                        if (scroller != 0) {
                            scroller = 0;
                        }
                    }
                    needsReset = true;
                    break;
                }
                if (tokens[0].contains("research_info")) {
                    currentUIMode = UIMode.RESEARCH_CENTER;
                    wantsToHaveInfoAbout = researchAPI.getResearchOption(tokens[1]);
                    if (researchAPI.getCurrentResearching() != null && researchAPI.getCurrentResearching().industryId.equals(wantsToHaveInfoAbout.industryId)) {
                        wantsInfoAboutCurrentlyResearching = true;
                    }
                    else{
                        wantsInfoAboutCurrentlyResearching = false;
                    }
                    needsReset = true;
                    break;
                }
                if (tokens[0].contains("start_research")) {
                    if (researchAPI.isResearching()) {
                        MessageIntel intel = new MessageIntel("Halted Research - " + currResearching.industryName, Misc.getBasePlayerColor());
                        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                        researchAPI.stopResearch();
                        currResearching = null;
                    }
                    researchAPI.startResearch(tokens[1]);
                    currResearching = researchAPI.getCurrentResearching();
                    MessageIntel intel = new MessageIntel("Started Research - " + currResearching.industryName, Misc.getBasePlayerColor());
                    intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                    intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                    wantsInfoAboutCurrentlyResearching = true;
                    wantsToHaveInfoAbout = null;
                    needsReset = true;

                    break;
                }
                if (tokens[0].contains("queue_research")) {
                    researchAPI.addResearchToQueue(tokens[1]);
                    needsReset = true;
                    break;
                }
                if (tokens[0].contains("clear_queue")) {
                    researchAPI.clearEntireResearchQueue();
                    needsReset = true;
                    break;
                }
                if (tokens[0].contains("archive_show")) {
                    wantsToHaveInfoAbout = researchAPI.getResearchOption(tokens[1]);
                    if (researchAPI.getCurrentResearching() != null && researchAPI.getCurrentResearching().industryId.equals(wantsToHaveInfoAbout.industryId)) {
                        wantsInfoAboutCurrentlyResearching = true;
                    } else {
                        wantsInfoAboutCurrentlyResearching = false;
                    }
                    needsReset = true;
                    break;
                }
                if (tokens[0].contains("stop_research")) {
                    MessageIntel intel = new MessageIntel("Halted Research - " + currResearching.industryName, Misc.getBasePlayerColor());
                    intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                    intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                    researchAPI.stopResearch();
                    currResearching = null;
                    if(!researchAPI.getResearchQueue().isEmpty()){
                        researchAPI.startResearch(researchAPI.getResearchQueue().get(0).industryId);
                        currResearching = researchAPI.getCurrentResearching();
                         intel = new MessageIntel("Start Queued Research - " + currResearching.industryName, Misc.getBasePlayerColor());
                        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                        wantsInfoAboutCurrentlyResearching = true;
                        wantsToHaveInfoAbout = null;
                        researchAPI.getResearchQueue().remove(0);
                    }
                    needsReset = true;
                    break;
                }
            }

        }

        //pressing a button usually means something we are displaying has changed, so redraw the panel from scratch
        if (needsReset) {
            if (alreadyResearchedPanelTT != null) {
                scrollerHelp = alreadyResearchedPanelTT.getExternalScroller().getYOffset();
            }
            if (techPanelTT != null && !prog_switch) {
                progressionScroller = techPanelTT.getExternalScroller().getYOffset();
            }
            reset();
        }


    }

    @Override
    public void processInput(List<InputEventAPI> events) {

        //this works for keyboard events, but does not seem to capture other UI events like button presses, thus why we use advance()
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            //is ESC is pressed, close the custom UI panel and the blank IDP we used to create it
            if (event.isKeyDownEvent() && event.getEventValue() == Keyboard.KEY_ESCAPE && !processingMarket) {
                event.consume();
                callbacks.dismissDialog();
                dialog.dismiss();

                return;

            }
        }
    }


    @Override
    public void buttonPressed(Object buttonId) {

    }


}
