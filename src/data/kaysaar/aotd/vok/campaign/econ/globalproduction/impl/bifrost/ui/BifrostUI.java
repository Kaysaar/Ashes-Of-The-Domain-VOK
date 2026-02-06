package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.BifrostMega;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog.BifrostGateActivationDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog.BifrostStarSystemSelectorDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonPackage;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.GPUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BifrostUI extends BaseMegastructureMenu {
    public BifrostUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegastructureMenu menu) {
        super(megastructure, parentPanel, menu);
    }

    ButtonAPI addGate;
    public TooltipMakerAPI tooltipOfActions;

    @Override
    public void createTitleSection() {
        mainTitlePanel = GPUIMisc.createTitleSection(mainPanel, megastructureReferedTo, "Bifrost Gates");
        mainPanel.addComponent(mainTitlePanel).inTL(-5, lastY + 40);
        lastY += mainTitlePanel.getPosition().getHeight() + 40;
    }

    public void addUI() {
        width = parentPanel.getPosition().getWidth();
        height = parentPanel.getPosition().getHeight();
        createTitleMenu();
        createTitleSection();
        buttons.clear();
        float totalHeight = height - lastY - 10;
        toolTipPanel = mainPanel.createCustomPanel(width, totalHeight, null);
        tooltipOfActions = toolTipPanel.createUIElement(width, 40, false);
        addGate = tooltipOfActions.addAreaCheckbox("Construct Bifrost Gate", null, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, width, 40, 0f);
        addGate.getPosition().inTL(0, 0);
        setButtonEnabled();
        tooltipOfSections = toolTipPanel.createUIElement(width, totalHeight - 50, true);
        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            createSectionMenu(megaStructureSection, 0f, 0f);
        }
        lastYForSection = lastY;
        toolTipPanel.addUIElement(tooltipOfActions).inTL(0, -60);
        toolTipPanel.addUIElement(tooltipOfSections).inTL(0, 5);
        mainPanel.addComponent(toolTipPanel).inTL(-5, lastYForSection);
    }

    private void setButtonEnabled() {
        ArrayList<StarSystemAPI> systems = new ArrayList<>();
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if (!systems.contains(playerMarket.getStarSystem())) {
                systems.add(playerMarket.getStarSystem());


            }
        }
        BifrostMega mega = (BifrostMega) this.getMegastructure();
        for (BifrostSection section : mega.getSections()) {
            systems.remove(section.getStarSystemAPI());
        }
        addGate.setEnabled(!systems.isEmpty());
    }

    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        if(data.getCustomCommand().contains("deactivateGate")){
            BifrostSection section = (BifrostSection) data.getCustomData();
            String title = "Gate de-activation";
            if(section.isDisabled){
                title = "Gate activation";
            }
            BasePopUpDialog dialog = new BifrostGateActivationDialog(title,section,this);
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(800,200,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()-(panelAPI.getPosition().getHeight()/2),true);
//            AshMisc.initPopUpDialog(new BifrostGateActivationDialog(title,section,this),800,200);

        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (addGate != null && addGate.isChecked()) {
            addGate.setChecked(false);
            BifrostStarSystemSelectorDialog dialog = new BifrostStarSystemSelectorDialog("Choose system to build Bifrost Gate", this);
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(800, 420, dialog);
            UIPanelAPI panelAPI1 = ProductionUtil.getCoreUI();
            dialog.init(panelAPI, panelAPI1.getPosition().getCenterX() - (panelAPI.getPosition().getWidth() / 2), panelAPI1.getPosition().getCenterY() - (panelAPI.getPosition().getHeight() / 2), true);
        }

    }

    @Override
    public void clearUI() {
        super.clearUI();
    }

    @Override
    public void resetSection(String sectionID) {
        offset = tooltipOfSections.getExternalScroller().getYOffset();
        LinkedHashMap<String, Pair<Float,Float>> offsets = new LinkedHashMap<>();
        for (ButtonPackage buttonPackage : buttons) {
            offsets.put(buttonPackage.getSection().getSpec().getSectionID(),new Pair<>(buttonPackage.getTooltipHeightOptions(),buttonPackage.getTooltipHeightOther()));
        }
        buttons.clear();
        toolTipPanel.removeComponent(tooltipOfSections);
        tooltipOfActions.removeComponent(tooltipOfActions);
        mainPanel.removeComponent(toolTipPanel);

        panelsOfSections.clear();

        toolTipPanel = mainPanel.createCustomPanel(width,height-lastYForSection-10f,null);
        tooltipOfSections = toolTipPanel.createUIElement(width,height-lastYForSection-50f,true);
        tooltipOfActions = toolTipPanel.createUIElement(width,40f,true);
        addGate = tooltipOfActions.addAreaCheckbox("Construct Bifrost Gate", null, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, width, 40, 0f);
        addGate.getPosition().inTL(0, 0);
        setButtonEnabled();
        for (GPMegaStructureSection megaStructureSection : megastructureReferedTo.getMegaStructureSections()) {
            Pair<Float,Float> pair = offsets.get(megaStructureSection.getSpec().getSectionID());
            createSectionMenu(megaStructureSection,pair.one,pair.two);
        }
        offsets.clear();

        toolTipPanel.addUIElement(tooltipOfActions).inTL(0, -60);
        toolTipPanel.addUIElement(tooltipOfSections).inTL(0, 5);
        mainPanel.addComponent(toolTipPanel).inTL(-5,lastYForSection);
        tooltipOfSections.getExternalScroller().setYOffset(offset);
        mainMenu.reInitalizeButtonUI();
        mainMenu.resetMarketData();

    }
}
