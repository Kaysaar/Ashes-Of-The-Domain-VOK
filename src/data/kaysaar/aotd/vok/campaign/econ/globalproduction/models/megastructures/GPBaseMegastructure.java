package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class GPBaseMegastructure {
    public String  specId;
    public float currentTotalOperationCost;
    MutableStat operationCostMult;
    public SectorEntityToken entityTiedTo;
    ArrayList<GPMegaStructureSection>megaStructureSections;
    public String getIcon(){ //Icon that should be displayed in megastructures TAB
        return Global.getSettings().getSpriteName("megastructures","dummy");
    }
    public LinkedHashMap<String,Integer>getCurrentGPCost(){
        return null;
    }
    public String getName(){ //Name that will be displayed
        return null;
    }
    //Note : Width will always be 350, height can be customized
    public CustomPanelAPI createButtonSection(float width){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panel = Global.getSettings().createCustom(width,200,null);
        renderer.setPanel(panel);
        return null;
    }

    public GPMegaStructureSpec getSpec() {
        return GPManager.getInstance().getMegaSpecFromList(specId);
    }

    public GPBaseMegastructure(String id){
        this.specId = id;
        megaStructureSections = new ArrayList<>();
        for (String sectionId : getSpec().getSectionIds()) {
            GPMegaStructureSection section = GPManager.getInstance().getMegaSectionSpecFromList(sectionId).getScript();
            section.init(this,false);
            megaStructureSections.add(section);
        }
    }

    public void advance(float amount){

    }
    public void apply(float amount){
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            megaStructureSection.apply();
        }
    }
    public void unapply(float amount){
        for (GPMegaStructureSection megaStructureSection : megaStructureSections) {
            megaStructureSection.unapply();
        }
    }
    //This is UI plugin you wanna create to handle all things in Megastrucutres tab (for each instance of megastrucutre) because it is interface extending
    //CustomUIPanelPlugin you should have there all components handled
    //clearUI is always called when switching to different megastructure while initUI initalizes UI
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel,CustomPanelAPI absoluteParent){
        return null;
    }
    public CustomPanelAPI createResourceCostAfterTransaction(float width, float height) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float sections = totalSize / (commodities.size());
        float positions = totalSize / ((commodities.size()) * 4);
        float iconsize = 20;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getExpectedCosts(GPManager.getInstance().getProductionOrders()).entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false,GPManager.getInstance().getProductionOrders()), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text;
            Color col = Misc.getPositiveHighlightColor();
            if (entry.getValue() > GPManager.getInstance().getTotalResources().get(entry.getKey()))
                col = Misc.getNegativeHighlightColor();
            tooltip.addPara("%s", 0f, col, col, text).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        customPanel.addUIElement(tooltip).inTL(-10, 0);
        return customPanel;
    }
}
