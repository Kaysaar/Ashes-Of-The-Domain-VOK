package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.ui.basecomps.ExtendUIPanelPlugin;

import java.awt.*;
import java.util.List;

public class HeadOfResearchShowcase implements ExtendUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth()+1,mainPanel.getPosition().getHeight()-5,null);
        TooltipMakerAPI tooltipMain = contentPanel.createUIElement(contentPanel.getPosition().getWidth(), contentPanel.getPosition().getHeight(), true);
        float width = contentPanel.getPosition().getWidth();
        tooltipMain.addSectionHeading("Head of R&D Bonus", Misc.getButtonTextColor(),Misc.getDarkPlayerColor(), Alignment.MID,width, 0f);
        tooltipMain.setTextWidthOverride(width);
        ScientistPerson scientist  = AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil;
        if (scientist != null) {
            tooltipMain.addPara("Skill - "+ scientist.getActiveSkillName(), Misc.getHighlightColor(),5f);
            scientist.createActiveSkillDescription(tooltipMain);

        } else {
            tooltipMain.addPara("No data!", 5f);
        }
        tooltipMain.addSectionHeading("R&D Team Bonus",  Misc.getButtonTextColor(),Misc.getDarkPlayerColor(), Alignment.MID,width, 5f);
        boolean added = false;
        for (ScientistPerson scientistPerson : AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchCouncil()) {
            tooltipMain.addPara(scientistPerson.getScientistPerson().getNameString()+ " (%s)",3f,Color.ORANGE,scientistPerson.getPassiveSkillName());
            tooltipMain.setBulletedListMode(BaseIntelPlugin.BULLET);
            scientistPerson.createPassiveSkillDescription(tooltipMain);
            tooltipMain.setBulletedListMode(null);

            added = true;
        }
        if(!added){
            tooltipMain.addPara("No data!", 5f);
        }
        tooltipMain.addSectionHeading("Other Bonuses",  Misc.getButtonTextColor(),Misc.getDarkPlayerColor(), Alignment.MID,width, 5f);
        int effective = (int) (AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchSpeedBonus().getModifiedValue()*100);
        tooltipMain.addPara("Current research speed bonus : %s", 5f, Color.ORANGE, effective + "%");
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites() > 0|| BlackSiteProjectManager.getInstance().canEngageInBlackSite()) {
            effective = (int) (AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().getModifiedValue()*100);
            tooltipMain.addPara("Current black site project speed bonus : %s", 3f, Color.ORANGE, effective + "%");


        }
        contentPanel.addUIElement(tooltipMain).inTL(0, 0);
        mainPanel.addComponent(contentPanel).inTL(0,0);
    }

    UILinesRenderer renderer;

    public HeadOfResearchShowcase(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        createUI();

        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
