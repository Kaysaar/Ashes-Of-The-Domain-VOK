package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;

import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import java.awt.*;
import java.util.ArrayList;


public class ResearchCenterPanel extends UiPanel {

    CustomPanelAPI imagePanel;
    CustomPanelAPI descriptionPanel;
    CustomPanelAPI coverPanel;
    ScientistAPI currentScientist;
    CustomPanelAPI bonusPanel;
    CustomPanelAPI modPanel ;
    CustomPanelAPI queuePanel;

    float height ;
    public ArrayList<ButtonAPI> buttons = new ArrayList<>();
    public void setHeight(float height) {
        this.height = height;
    }
    public void resetQueue(){
        tooltip.removeComponent(queuePanel);
        queuePanel = panel.createCustomPanel(300,150,null);
        TooltipMakerAPI tooltipQueue = queuePanel.createUIElement(300,150,true);
        for (ResearchOption queuedResearchOption : AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().getQueuedResearchOptions()) {
            tooltipQueue.addPara(queuedResearchOption.Id,10f);
        }
        tooltip.addCustom(queuePanel,1f);
    }
    @Override
    public void createUI() {
        tooltip.addSectionHeading("Research Center", Alignment.MID, 10f);
        coverPanel = panel.createCustomPanel(300,128,null);
        imagePanel = coverPanel.createCustomPanel(115,128,null);
        bonusPanel = panel.createCustomPanel(300,100,null);
        modPanel = panel.createCustomPanel(300,130,null);
        queuePanel = panel.createCustomPanel(300,height-217-140,null);
        TooltipMakerAPI tooltipQueue = queuePanel.createUIElement(300,height-217-200,true);
        TooltipMakerAPI modPanelTooltip = modPanel.createUIElement(300,100,true);
        currentScientist = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().currentHeadOfCouncil;
        TooltipMakerAPI imageAnchor = imagePanel.createUIElement(128,128,false);
        if(currentScientist!=null){
            imageAnchor.addImage(currentScientist.getScientistPerson().getPortraitSprite(),128,118,10f);
        }

        imagePanel.addUIElement(imageAnchor).inTL(-10,0);
        descriptionPanel = coverPanel.createCustomPanel(175,128,null);
        TooltipMakerAPI tooltipMakerAPI = descriptionPanel.createUIElement(180,123,true);
        tooltipMakerAPI.addSectionHeading("Head of Research Council",Alignment.MID,10f);
        if(currentScientist!=null){
            tooltipMakerAPI.addPara(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().currentHeadOfCouncil.getScientistPerson().getName().getFullName(), Misc.getTooltipTitleAndLightHighlightColor(),8f);
            currentScientist.createSkillDescription(tooltipMakerAPI);
        }
        descriptionPanel.addUIElement(tooltipMakerAPI).inTL(-5,-5);
        coverPanel.addComponent(imagePanel).inTL(0,0);
        coverPanel.addComponent(descriptionPanel).inTL(125,5);
        TooltipMakerAPI tooltipOfBonuses= bonusPanel.createUIElement(295,50,false);
        tooltipOfBonuses.addSectionHeading("Current Bonuses:",Alignment.MID,10f);
        float amountOfFacilities = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getAmountOfResearchFacilities();
        float bonus = AoTDMainResearchManager.BONUS_PER_RESEARACH_FAC*100f;
        if(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getAmountOfResearchFacilities()>=1){
           tooltipOfBonuses.addPara("Research Speed:",Misc.getTooltipTitleAndLightHighlightColor(),10f);
            String faciltiies = "facilities";
            if(amountOfFacilities ==1){
                faciltiies = "facility";
            }
            tooltipOfBonuses.addPara("Currently controls %s of Research "+faciltiies+" : %s bonus research speed",10f, Color.ORANGE,""+(int)amountOfFacilities,""+((amountOfFacilities-1)*bonus)+"%");
        }




        bonusPanel.addUIElement(tooltipOfBonuses).inTL(-1,0);
        tooltip.addCustom(coverPanel,1f);
        tooltip.addCustom(bonusPanel,0f);
        tooltip.addSectionHeading("Mod List",Alignment.MID,0f);
        for (String s : AoTDMainResearchManager.getInstance().getModIDsRepo()) {
           buttons.add(modPanelTooltip.addButton(Global.getSettings().getModManager().getModSpec(s).getName(),s,290,50,10f));
        }
        modPanel.addUIElement(modPanelTooltip).inTL(-5,0);
        tooltip.addCustom(modPanel,1f);
        tooltip.addSectionHeading("Queue",Alignment.MID,0f);
        tooltipQueue.addSpacer(10f);
        float boxWidth = 60;
        float boxHeight = 20;
        for (ResearchOption queuedResearchOption : AoTDMainResearchManager.getInstance().getManagerForPlayer().getQueueManager().getQueuedResearchOptions()) {
            CustomPanelAPI tooltipQueuesPanels = queuePanel.createCustomPanel(300,120,null);
            TooltipMakerAPI tooltipQueues = tooltipQueuesPanels.createUIElement(300,120,false);
            ButtonAPI areaBox = tooltipQueues.addAreaCheckbox("", null, Misc.getBrightPlayerColor(), Misc.getTooltipTitleAndLightHighlightColor(), Misc.getBrightPlayerColor(),284, 85, 0);
            areaBox.setClickable(false);
            areaBox.unhighlight();
            ButtonAPI buttonsa = tooltipQueues.addButton("Up","up:"+queuedResearchOption.Id,boxWidth,boxHeight,0f);
            buttonsa.getPosition().inTL(10,50);
            buttons.add(buttonsa);

            buttonsa = tooltipQueues.addButton("Down","down:"+queuedResearchOption.Id,boxWidth,boxHeight,0f);
            buttonsa.getPosition().inTL(boxWidth+20,50);
            buttons.add(buttonsa);

            buttonsa = tooltipQueues.addButton("Top","top:"+queuedResearchOption.Id,boxWidth,boxHeight,0f);
            buttonsa.getPosition().inTL(boxWidth*2+30,50);
            buttons.add(buttonsa);

            buttonsa = tooltipQueues.addButton("Bottom","bottom:"+queuedResearchOption.Id,boxWidth,boxHeight,0f);
            buttonsa.getPosition().inTL(boxWidth*3+40,50);
            buttons.add(buttonsa);

            buttonsa = tooltipQueues.addButton("Remove","remove:"+queuedResearchOption.Id,boxWidth,boxHeight,0f);
            buttonsa.getPosition().inTL(boxWidth*3+40,20);
            buttons.add(buttonsa);

            LabelAPI labelAPI = tooltipQueues.addPara(queuedResearchOption.Name,10f);
            labelAPI.getPosition().inTL(10,5);
            tooltipQueuesPanels.addUIElement(tooltipQueues).inTL(-5,0);
            tooltipQueue.addCustom(tooltipQueuesPanels,3f);

        }
        queuePanel.addUIElement(tooltipQueue).inTL(-1,0);
        tooltip.addCustom(queuePanel,1f);
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
    public CustomPanelAPI getQueuePanel() {
        return queuePanel;
    }
    public CustomPanelAPI getModPanel() {
        return modPanel;
    }


}
