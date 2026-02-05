package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.ui;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.AutomationDialog;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.ManpowerManagementDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.RangeIncreaseDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;

public class NidavelirUI extends BaseMegastrucutreMenu {
    public NidavelirUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        super(megastructure, parentPanel, menu);
    }
    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        if(data.getCustomCommand().contains("assignManpower")){
            NidavelirComplexMegastructure mega = (NidavelirComplexMegastructure) megastructureReferedTo;
            BasePopUpDialog dialog = new ManpowerManagementDialog("Manpower Management",(NidavelirBaseSection) data.getCustomData(),this,mega.getRemainingManpowerPoints());
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(700,440,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()-(panelAPI.getPosition().getHeight()/2),true);
        }
        if(data.getCustomCommand().contains("automateSection")){
            BasePopUpDialog dialog = new AutomationDialog("Section Automation",(NidavelirBaseSection) data.getCustomData(),this);
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(720,390,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);
        }
    }
}
