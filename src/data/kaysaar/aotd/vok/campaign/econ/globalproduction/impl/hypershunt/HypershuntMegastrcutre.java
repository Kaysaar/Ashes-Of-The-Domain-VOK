package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.impl.HypershuntUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover.CommodityInfo;

import java.awt.*;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class HypershuntMegastrcutre extends GPBaseMegastructure {
    public HypershuntMegastrcutre(String id) {
        super(id);
    }

    @Override
    public CustomPanelAPI createButtonSection(float width) {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        CustomPanelAPI panel = Global.getSettings().createCustom(width,200,null);
        renderer.setPanel(panel);
        TooltipMakerAPI tooltip = panel.createUIElement(width,50,false);
        TooltipMakerAPI tooltipOfIcon = tooltip.beginSubTooltip(width);
        TooltipMakerAPI tooltipOfCosts = tooltip.beginSubTooltip(width);
        tooltipOfCosts.addPara("Monthly running cost %s",0,Color.ORANGE,Misc.getDGSCredits(1000000)).getPosition().inTL(10,0);
        tooltipOfCosts.addPara("Total usage of resources",Color.ORANGE,5f).getPosition().inTL(10,20);
        tooltipOfCosts.addCustom(createResourceCostAfterTransaction(width,20),10f);
        tooltipOfIcon.addImage(Global.getSettings().getSpriteName("megastructures","hypershunt"),50,50,5f);
        tooltipOfIcon.addTitle("Coronal Hypershunt : Proxima Star System").getPosition().inTL(60,10);
        tooltip.addCustom(tooltipOfIcon,0f);
        tooltip.addSpacer(tooltipOfIcon.getHeightSoFar());
        tooltip.addCustom(tooltipOfCosts,-5f);
        tooltip.addSpacer(tooltipOfCosts.getHeightSoFar());
        panel.getPosition().setSize(width,tooltip.getHeightSoFar()+5);

        panel.addUIElement(tooltip).inTL(0,0);
        return panel;
    }

    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, CustomPanelAPI absoluteParent) {
        return new HypershuntUI(this,parentPanel,absoluteParent);
    }
}
