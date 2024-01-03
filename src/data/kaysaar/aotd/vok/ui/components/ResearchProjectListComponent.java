package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.models.ResearchProject;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import java.awt.*;
import java.util.ArrayList;

public class ResearchProjectListComponent extends UiPanel{
    CustomPanelAPI imagePanel;
    CustomPanelAPI descriptionPanel;
    CustomPanelAPI coverPanel;
    ScientistAPI currentScientist;
    CustomPanelAPI bonusPanel;
    CustomPanelAPI specialProjectsPanel;
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
        specialProjectsPanel = panel.createCustomPanel(300,height-217,null);
        TooltipMakerAPI modPanelTooltip = specialProjectsPanel.createUIElement(300,height-217,true);
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
        tooltip.addSectionHeading("Special Project Lists",Alignment.MID,0f);
        for (ResearchProject s : AoTDMainResearchManager.getInstance().getResearchProjects()) {
            if(!s.haveMetReqOnce)continue;
            CustomPanelAPI buttonPanel = panel.createCustomPanel(290, 100, null);
            TooltipMakerAPI vTT = buttonPanel.createUIElement(AoTDUiComp.WIDTH_OF_TECH_PANEL, AoTDUiComp.HEIGHT_OF_TECH_PANEL, false);
            buttons.add(vTT.addAreaCheckbox("", s.id, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), 290, 50, 0));
            vTT.addPara(s.spec.nameOfProject, Color.ORANGE, 10f).getPosition().inTL(10, 3);
            buttonPanel.addUIElement(vTT).inTL(0, -1);
            modPanelTooltip.addCustom(buttonPanel,10f);

        }
        specialProjectsPanel.addUIElement(modPanelTooltip).inTL(-5,0);
        tooltip.addCustom(specialProjectsPanel,1f);
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

    public CustomPanelAPI getSpecialProjectsPanel() {
        return specialProjectsPanel;
    }
}
