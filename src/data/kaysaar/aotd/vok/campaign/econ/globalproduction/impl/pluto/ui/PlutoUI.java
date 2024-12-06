package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.PlutoForgeSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.ResourceAllocationDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;

public class PlutoUI extends BaseMegastrucutreMenu {
    public PlutoUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        super(megastructure, parentPanel, menu);
    }

    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        PlutoMegastructure megastructure = (PlutoMegastructure) megastructureReferedTo;
        if(data.getCustomCommand().contains("assignTO")){
            PlutoForgeSection section = (PlutoForgeSection) data.getCustomData();
            BasePopUpDialog dialog = new ResourceAllocationDialog((BaseMegastrucutreMenu) this,"Resource Allocation",1,megastructure.getLaserSection().getAvailableOresAmount(Commodities.RARE_ORE)+section.getAssignedResources(Commodities.RARE_ORE),section.getAssignedResources(Commodities.RARE_ORE),0, section, Commodities.RARE_ORE);
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(750,300,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);
        }
        if(data.getCustomCommand().contains("assignO")){
            PlutoForgeSection section = (PlutoForgeSection) data.getCustomData();
            BasePopUpDialog dialog = new ResourceAllocationDialog((BaseMegastrucutreMenu) this,"Resource Allocation",1,megastructure.getLaserSection().getAvailableOresAmount(Commodities.ORE)+section.getAssignedResources(Commodities.ORE),section.getAssignedResources(Commodities.ORE),0, section,Commodities.ORE);
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(750,300,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);
        }
    }
}
