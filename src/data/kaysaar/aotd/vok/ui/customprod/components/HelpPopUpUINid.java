package data.kaysaar.aotd.vok.ui.customprod.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.awt.*;
import java.util.ArrayList;

public class HelpPopUpUINid extends PopUpUI {
    CustomPanelAPI mainPanel;
    boolean isNidUI = false;
    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }
    public HelpPopUpUINid(boolean isNidUI){
        this.isNidUI = isNidUI;
    }

    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(panelAPI.getPosition().getWidth()+5,panelAPI.getPosition().getHeight(),true);
        tooltip.addSectionHeading("Global Production (GP)",Alignment.MID,0f);
        ArrayList<String>names = new ArrayList<>();
        for (String commodity : GPManager.commodities) {
            names.add(Global.getSettings().getCommoditySpec(commodity).getName());
        }
        String highlight = Misc.getAndJoined(names);
        tooltip.addPara("Global Production represents the industrial might of colonies under your control.",5f);
        tooltip.addPara("Each unit of %s produced on your colonies will grant you %s units of Global Production in the same category.",5f,Color.ORANGE,highlight,""+GPManager.scale);
        tooltip.addPara("The more industries, megastructures and production you control, the more points you receive.",5f);
        tooltip.addPara("Each %s and %s has needs in one or more of these categories.",5f,Color.ORANGE,"production order","megastructure");
        tooltip.addSectionHeading("Production Penalties",Alignment.MID,5f);
        tooltip.addPara("If the added needs exceeds your global production, the order will get an order delivery time penalty scaling with the missing global production points and megastructures their effect reduced or nullified!",Misc.getNegativeHighlightColor(),5f);
        if(isNidUI){
            tooltip.addSectionHeading("Useful key binds", Alignment.MID,5f);
            tooltip.addPara("Shift + LMB : %s",10f, Color.ORANGE,"Increase quantity of items in order being produced at once");
            tooltip.addPara("Shift + RMB : %s",5, Color.ORANGE,"Decrease quantity of items in order being produced at once");
            tooltip.addPara("Ctrl + LMB : %s",10f, Color.ORANGE,"Increase amount of orders by 10");
            tooltip.addPara("Ctrl + RMB : %s",5, Color.ORANGE,"Decrease amount of orders by 10");

            tooltip.addPara("Shift + Ctrl + LMB : %s",10f, Color.ORANGE,"Increase quantity of items in order being produced at once by 10");
            tooltip.addPara("Shift + Ctrl + RMB : %s",5, Color.ORANGE,"Decrease quantity of items in order being produced at once by 10");
        }
        tooltip.addSectionHeading("Tips",Alignment.MID,5f);
        tooltip.addPara("Focus on building more colonies and industries that are producing listed commodities",5f);
        tooltip.addPara("Some megastructures are capable of producing more than enough resources, to satisfy all needs, but also they have their own resource upkeep, so be careful with that!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        tooltip.addPara("Keep track of your resources, and dont exceed your capabilities, as it is more efficient to produce less without penalty, than trying to produce more with penalty!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
       tooltip.addSpacer(5f);
        mainPanel.getPosition().setSize(panelAPI.getPosition().getWidth(),tooltip.getHeightSoFar());
        mainPanel.addUIElement(tooltip).inTL(0,0);
        return tooltip.getHeightSoFar();


    }
}
