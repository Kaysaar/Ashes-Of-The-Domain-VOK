package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.util.A;
import data.kaysaar.aotd.vok.models.ResearchProject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class StageEventComponent extends UiPanel{
    public ResearchProject getCurrentProject() {
        return currentProject;
    }
    CustomPanelAPI optionsPanel;

    public CustomPanelAPI getOptionsPanel() {
        return optionsPanel;
    }

    public CustomPanelAPI getDescriptionPanel() {
        return descriptionPanel;
    }

    CustomPanelAPI descriptionPanel;
    public  ArrayList<ButtonAPI>buttons = new ArrayList<>();
    public void setCurrentProject(ResearchProject currentProject) {
        this.currentProject = currentProject;
    }
    public float width;
    public float height;
    public static  float ENTRY_HEIGHT = 40;
    public static float ENTRY_WIDTH;

    public void setWidth(float width) {
        this.width = width;
        ENTRY_WIDTH = (width*0.6f)-7f;
    }

    public void setHeight(float height) {
        this.height = height;

    }
    ResearchProject currentProject;
    @Override
    public void createUI() {
        tooltip.addSectionHeading("Project Events", Alignment.MID,10f);
        if(!currentProject.haveReachedCriticalMoment&&!currentProject.haveDoneIt){
            tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
            tooltip.addPara("Currently there are issues, that needs our attention!", Misc.getPositiveHighlightColor(),10f);
        }
        if(currentProject.haveDoneIt){
            tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
            tooltip.addPara("Project Completed! There won't be any more events regarding this project!", Misc.getPositiveHighlightColor(),10f);
        }
        float opad = 10f;
        if(currentProject.haveReachedCriticalMoment){
            Color baseColor = Misc.getButtonTextColor();
            Color bgColour = Misc.getDarkPlayerColor();
            Color brightColor = Misc.getBrightPlayerColor();
            optionsPanel = panel.createCustomPanel(width*0.6f,height-20,null);
            TooltipMakerAPI tooltipOptions =  optionsPanel.createUIElement(width*0.6f,height-20,true);
            tooltipOptions.addSpacer(15);
            for (final Map.Entry<String, String> optionEntry : currentProject.getCertainStage(currentProject.indexOfCurrentStage).optionsNameMap.entrySet()) {
                CustomPanelAPI optionButtonPanel = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT,null);
                TooltipMakerAPI anchor = optionButtonPanel.createUIElement(ENTRY_WIDTH, ENTRY_HEIGHT, false);
                ButtonAPI areaCheckbox = anchor.addAreaCheckbox("", optionEntry.getKey(), baseColor, bgColour, brightColor, //new Color(255,255,255,0)
                        ENTRY_WIDTH,
                        ENTRY_HEIGHT,
                        0f,
                        true);
                optionButtonPanel.addUIElement(anchor).inTL(-opad, 0f);
                areaCheckbox.setEnabled(currentProject.haveMetReqForOption(optionEntry.getKey()));
                anchor.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                    @Override
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    @Override
                    public float getTooltipWidth(Object tooltipParam) {
                        return 500;
                    }

                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        currentProject.generateTooltipForOption(optionEntry.getKey(),tooltip);
                    }
                }, TooltipMakerAPI.TooltipLocation.RIGHT);
                anchor = optionButtonPanel.createUIElement(ENTRY_WIDTH, ENTRY_HEIGHT, true);
                anchor.setParaFont(Fonts.ORBITRON_12);
                anchor.addPara(optionEntry.getValue(), Color.ORANGE, 10f);
                optionButtonPanel.addUIElement(anchor).inTL(2, 2);
                tooltipOptions.addCustom(optionButtonPanel, 3f);
                tooltipOptions.addSpacer(15);
                buttons.add(areaCheckbox);
            }
            descriptionPanel = panel.createCustomPanel(width*0.4f,height-18,null);
            TooltipMakerAPI descriptionTooltip =  descriptionPanel.createUIElement(width*0.4f,height-18,true);
            currentProject.generateDescriptionForCriticalMoment(descriptionTooltip);
            descriptionPanel.addUIElement(descriptionTooltip).inTL(-5,0);
            optionsPanel.addUIElement(tooltipOptions).inTL(0,0);
            panel.addComponent(optionsPanel).inTL(0,18);
            panel.addComponent(descriptionPanel).inTL(width*0.6f+10,18);

        }
    }

}
