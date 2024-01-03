package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import java.util.ArrayList;


public class ResearchCenterPanel extends UiPanel {

    CustomPanelAPI imagePanel;
    CustomPanelAPI descriptionPanel;
    CustomPanelAPI coverPanel;
    ScientistAPI currentScientist;
    CustomPanelAPI bonusPanel;
    CustomPanelAPI modPanel ;
    float height ;
    public ArrayList<ButtonAPI> buttons = new ArrayList<>();
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public void createUI() {
        tooltip.addSectionHeading("Research Center", Alignment.MID, 10f);
        coverPanel = panel.createCustomPanel(300,128,null);
        imagePanel = coverPanel.createCustomPanel(115,128,null);
        bonusPanel = panel.createCustomPanel(300,50,null);
        modPanel = panel.createCustomPanel(300,height-217,null);
        TooltipMakerAPI modPanelTooltip = modPanel.createUIElement(300,height-217,true);
        currentScientist = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().currentHeadOfCouncil;
        TooltipMakerAPI imageAnchor = imagePanel.createUIElement(128,128,false);
        if(currentScientist!=null){
            imageAnchor.addImage(currentScientist.getScientistPerson().getPortraitSprite(),128,118,10f);
        }

        imagePanel.addUIElement(imageAnchor).inTL(-10,0);
        descriptionPanel = coverPanel.createCustomPanel(175,128,null);
        TooltipMakerAPI tooltipMakerAPI = descriptionPanel.createUIElement(185,123,true);
        tooltipMakerAPI.addSectionHeading("Head of Research Council",Alignment.MID,10f);
        if(currentScientist!=null){
            tooltipMakerAPI.addPara(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().currentHeadOfCouncil.getScientistPerson().getName().getFullName(), Misc.getTooltipTitleAndLightHighlightColor(),8f);
            currentScientist.createSkillDescription(tooltipMakerAPI);
        }
        descriptionPanel.addUIElement(tooltipMakerAPI).inTL(-5,-5);
        coverPanel.addComponent(imagePanel).inTL(0,0);
        coverPanel.addComponent(descriptionPanel).inTL(125,5);
        TooltipMakerAPI tooltipMakerAPI1= bonusPanel.createUIElement(300,50,false);
        tooltipMakerAPI1.addPara("Currently controls "+ AoTDMainResearchManager.getInstance().getManagerForPlayer().howManyFacilitiesFactionControlls()+" research facilities.",Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltip.addCustom(coverPanel,1f);
        tooltip.addCustom(bonusPanel,0f);
        tooltip.addSectionHeading("Mod List",Alignment.MID,0f);
        for (String s : AoTDMainResearchManager.getInstance().getModIDsRepo()) {
           buttons.add(modPanelTooltip.addButton(Global.getSettings().getModManager().getModSpec(s).getName(),s,290,50,10f));
        }
        modPanel.addUIElement(modPanelTooltip).inTL(0,0);
        tooltip.addCustom(modPanel,1f);
    }

    public CustomPanelAPI getDescriptionPanel() {
        return descriptionPanel;
    }

    public CustomPanelAPI getImagePanel() {
        return imagePanel;
    }

    public CustomPanelAPI getCoverPanel() {
        return coverPanel;
    }

    public CustomPanelAPI getBonusPanel() {
        return bonusPanel;
    }

    public CustomPanelAPI getModPanel() {
        return modPanel;
    }

}
