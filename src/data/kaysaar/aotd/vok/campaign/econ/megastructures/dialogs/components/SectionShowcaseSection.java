package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureTestDialog;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SectionShowcaseSection implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel, componentPanel;
    BaseMegastructureScript megastructureScript;
    boolean generalMegastructureMode = true;
    BaseMegastructureSection currSection = null;
    UILinesRenderer renderer;
    ButtonAPI goBack;
    ArrayList<ButtonAPI>buttonsForSection = new ArrayList<>();
    public BaseMegastructureSection getCurrSection() {
        return currSection;
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public void setCurrSection(BaseMegastructureSection currSection) {
        this.currSection = currSection;
        generalMegastructureMode = this.currSection == null;
        createUI();

    }
    BaseMegastructureDialogContent content;

    public SectionShowcaseSection(BaseMegastructureScript megastructureScript, BaseMegastructureDialogContent content) {
        this.megastructureScript = megastructureScript;
        renderer = new UILinesRenderer(0f);
        this.content = content;
        mainPanel = Global.getSettings().createCustom(410, BaseMegastructureTestDialog.height-45, this);
        renderer.setPanel(mainPanel);
        createUI();
    }

    @Override
    public void createUI() {
        if (componentPanel != null) {
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);

        if (generalMegastructureMode) {
            buttonsForSection.clear();
            TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), true);
            MegastructureSectionButton viewer = new MegastructureSectionButton(190,95,megastructureScript);
            CustomPanelAPI container = Global.getSettings().createCustom(componentPanel.getPosition().getWidth(), viewer.getComponentPanel().getPosition().getHeight(), null);
            container.addComponent(viewer.getComponentPanel()).inTL((container.getPosition().getWidth() / 2) - (viewer.getComponentPanel().getPosition().getWidth() / 2) - 5, 0);
            tooltip.addSpacer(0f).getPosition().inTL(0, 0);
            tooltip.setTitleFont(Fonts.ORBITRON_20AA);
            tooltip.addTitle("Megastructure Overview").setAlignment(Alignment.MID);
            tooltip.addCustom(container, 2f);
            tooltip.addPara(megastructureScript.getSpec().getDescription(), 3f);
            tooltip.addSectionHeading("Sections Status", Alignment.MID, 5f);
            for (BaseMegastructureSection megaStructureSection : megastructureScript.getMegaStructureSections()) {
                tooltip.addCustom(createSectionStatusRow(megaStructureSection,componentPanel.getPosition().getWidth()-10),2f);
            }
            if(megastructureScript.doesHaveCustomSectionForTooltip()){
                megastructureScript.printCustomSection(tooltip);
            }
            Industry ind = megastructureScript.getIndustryTiedToMegastructureIfPresent();
            if(ind!=null){
                if(!ind.getAllSupply().isEmpty()||!ind.getAllDemand().isEmpty()){
                    if(!ind.getAllSupply().isEmpty()){
                        tooltip.addSectionHeading("Production",Alignment.MID,5f);
                        AoTDCommodityShortPanelCombined production = new AoTDCommodityShortPanelCombined(componentPanel.getPosition().getWidth()-20, 3, ind, false, false);
                        tooltip.addCustom(production.getMainPanel(), 5f);
                    }
                    if(!ind.getAllDemand().isEmpty()){
                        tooltip.addSectionHeading("Demand",Alignment.MID,5f);
                        AoTDCommodityShortPanelCombined production = new AoTDCommodityShortPanelCombined(componentPanel.getPosition().getWidth()-20, 3, ind, true, true);
                        tooltip.addCustom(production.getMainPanel(), 5f);
                    }
                }

            }
            if(megastructureScript.isInRestorationProcess()){
                tooltip.addSectionHeading("Monthly resources required for restoration", Alignment.MID, 5f);
                LinkedHashMap<String,Integer>costs = new LinkedHashMap<>();
                for (BaseMegastructureSection megaStructureSection : megastructureScript.getMegaStructureSections()) {
                    if(megaStructureSection.isRestoring){
                        for (Map.Entry<String, Integer> entry : megaStructureSection.getMonthlyResNeeded().entrySet()) {
                            AoTDMisc.putCommoditiesIntoMap(costs, entry.getKey(), entry.getValue());
                        }
                    }

                }
                costs = AoTDMisc.getOrderedResourceMap(costs);
                ArrayList<Pair<String,Integer>>pairs = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : costs.entrySet()) {
                    Pair<String,Integer>res = new Pair<>(entry.getKey(),entry.getValue());
                    pairs.add(res);
                }
                AoTDCommodityShortPanelCombined panel = new AoTDCommodityShortPanelCombined(componentPanel.getPosition().getWidth()-20,3,pairs);
                tooltip.addCustom(panel.getMainPanel(), 5f);

            }
            if(megastructureScript.doesHaveCustomEffects()){
                tooltip.addSectionHeading("Current Effects",Alignment.MID,5f);
                megastructureScript.createCustomEffectsTooltip(tooltip);
            }

            componentPanel.addUIElement(tooltip).inTL(0, 0);

        }
        else{
            buttonsForSection.clear();
            TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight()-150, true);
            TooltipMakerAPI tooltipBT = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), 115, true);
            TooltipMakerAPI tooltipBTAlways = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), 25, false);

            MegastructureSectionButton viewer = new MegastructureSectionButton(190,95,currSection,false);
            CustomPanelAPI container = Global.getSettings().createCustom(componentPanel.getPosition().getWidth(), viewer.getComponentPanel().getPosition().getHeight(), null);
            container.addComponent(viewer.getComponentPanel()).inTL((container.getPosition().getWidth() / 2) - (viewer.getComponentPanel().getPosition().getWidth() / 2) - 5, 0);
            tooltip.addSpacer(0f).getPosition().inTL(0, 0);
            tooltip.setTitleFont(Fonts.ORBITRON_20AA);
            tooltip.addTitle("Section Overview - "+currSection.getName()).setAlignment(Alignment.MID);
            tooltip.addCustom(container, 2f);
            goBack = tooltipBTAlways.addButton("Go back to main overview",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,componentPanel.getPosition().getWidth()-15,25,0f);
            tooltip.addSectionHeading("Current Section Status", Alignment.MID, 5f);
            if(currSection.isRestored){
                tooltip.addPara("Operational, restored to full capacity!",Misc.getPositiveHighlightColor(),3f);
            }
            else{
                if(currSection.isRestoring){
                    tooltip.addPara("In process of restoration",Misc.getTooltipTitleAndLightHighlightColor(),3f);
                    String progress = ((int)(currSection.getProgressOfRestoration()*100))+"%";
                    ProgressBarComponentV2 bar = new ProgressBarComponentV2(componentPanel.getPosition().getWidth()-15,20,progress,Fonts.DEFAULT_SMALL,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),currSection.getProgressOfRestoration());
                    tooltip.addCustom(bar.getMainPanel(),5f);
                    tooltip.addSectionHeading("Monthly resources needed for restoration",Alignment.MID,5f);
                    if(currSection.getAllowedProgressOnRestoration()==1f){
                        tooltip.addPara("All resources have been delivered!",Misc.getPositiveHighlightColor(),5f).setAlignment(Alignment.MID);
                    }
                    else{
                        ArrayList<Pair<String,Integer>>pairs = new ArrayList<>();
                        LinkedHashMap<String,Integer> costs  = AoTDMisc.getOrderedResourceMap(currSection.getMonthlyResNeeded());
                        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
                            Pair<String,Integer>res = new Pair<>(entry.getKey(),entry.getValue());
                            pairs.add(res);
                        }
                        AoTDCommodityShortPanelCombined panel = new AoTDCommodityShortPanelCombined(componentPanel.getPosition().getWidth()-20,3,pairs);
                        tooltip.addCustom(panel.getMainPanel(), 5f);
                    }

                }
                else{
                    tooltip.addPara("Damaged! Restoration is required",Misc.getNegativeHighlightColor(),3f);

                }
                tooltip.addSectionHeading("Effects once section is restored",Alignment.MID, 5f);
            }
            currSection.createEffectSection(tooltip,false);
            if(currSection.doesHaveCustomSection()){
                currSection.createCustomSection(tooltip);
            }
            tooltipBT.addSectionHeading("Options", Alignment.MID, 0f).getPosition().inTL(0,0);
            buttonsForSection.addAll(currSection.createButtonsForSection(componentPanel.getPosition().getWidth()-15,25,tooltipBT));


            componentPanel.addUIElement(tooltip).inTL(0, 0);
            componentPanel.addUIElement(tooltipBT).inTL(0, componentPanel.getPosition().getHeight()-30-115);
            componentPanel.addUIElement(tooltipBTAlways).inTL(0, componentPanel.getPosition().getHeight()-25);
        }


        mainPanel.addComponent(componentPanel).inTL(0, 0);
    }

    public CustomPanelAPI createSectionStatusRow(BaseMegastructureSection section, float width) {
        float widthSection = 60;
        float height = widthSection / 2;
        CustomPanelAPI mainPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tl = mainPanel.createUIElement(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), false);
        ImageViewer viewer = new ImageViewer(widthSection, height, section.getImagePath());
        tl.addCustom(viewer.getComponentPanel(),0f);
        Color highlight = Misc.getNegativeHighlightColor();
        String type = "Damaged";
        if(section.isRestored()){
            highlight = Misc.getPositiveHighlightColor();
            type = "Operational";
        }
        if(section.isRestoring){
            highlight = Misc.getTooltipTitleAndLightHighlightColor();
            type = "Restoring";
            String progress = ((int)(section.getProgressOfRestoration()*100))+"%";
            ProgressBarComponentV2 bar = new ProgressBarComponentV2(100,height/2,progress,Fonts.DEFAULT_SMALL,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),section.getProgressOfRestoration());
            tl.addCustom(bar.getMainPanel(),0f).getPosition().inTL(width-100,height/4);
        }
        LabelAPI labelAPI = tl.addPara(section.getName() + " - "+"%s",0f,highlight,type);
        labelAPI.getPosition().setSize(labelAPI.computeTextWidth(labelAPI.getText()),labelAPI.computeTextHeight(labelAPI.getText()));
        labelAPI.getPosition().rightOfMid(viewer.getComponentPanel(),5f);

        mainPanel.addUIElement(tl).inTL(0,0);
        return mainPanel;
    }

    @Override
    public void clearUI() {

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
//        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {
        if(goBack!=null&&goBack.isChecked()){
            goBack.setChecked(false);
            goBack = null;
            setCurrSection(null);
            content.getMegastructureViewSection().setCurrentlyChosenSection(null);
            buttonsForSection.clear();
        }
        for (ButtonAPI buttonAPI : buttonsForSection) {
            if(buttonAPI.isChecked()){
                buttonAPI.setChecked(false);
                currSection.reportButtonPressedForSection(buttonAPI,content);
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
