package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import java.awt.*;
import java.util.List;

public class HeadOfResearchShowcase implements CustomUIPanelPlugin {
    CustomPanelAPI mainPanel;

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    UILinesRenderer renderer;

    public HeadOfResearchShowcase(float width, float height, ScientistAPI scientist) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        TooltipMakerAPI tooltipMain = mainPanel.createUIElement(width, height - 5, true);
        tooltipMain.addSectionHeading("Head of R&D Bonus", Misc.getButtonTextColor(),Misc.getDarkPlayerColor(), Alignment.MID,width, 0f);
        if (scientist != null) {
            scientist.createSkillDescription(tooltipMain);

        } else {
            tooltipMain.addPara("No data!", 5f);
        }
        tooltipMain.addSectionHeading("Other Bonuses",  Misc.getButtonTextColor(),Misc.getDarkPlayerColor(), Alignment.MID,width, 5f);
        int effective = (int) (AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchSpeedBonus().getModifiedValue()*100);
        tooltipMain.addPara("Current research speed bonus : %s", 5f, Color.ORANGE, effective + "%");
        if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites() > 0|| BlackSiteProjectManager.getInstance().canEngageInBlackSite()) {
             effective = (int) (AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().getModifiedValue()*100);
            tooltipMain.addPara("Current black site project speed bonus : %s", 3f, Color.ORANGE, effective + "%");


        }
        mainPanel.addUIElement(tooltipMain).inTL(0, 0);
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
