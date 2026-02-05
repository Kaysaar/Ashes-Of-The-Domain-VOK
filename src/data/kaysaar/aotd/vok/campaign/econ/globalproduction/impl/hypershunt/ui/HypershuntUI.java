package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.ui;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.RangeIncreaseDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;

public class HypershuntUI extends BaseMegastrucutreMenu {
    public HypershuntUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        super(megastructure, parentPanel, menu);
    }

    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        if(data.getCustomCommand().contains("adjustRange")){
            BasePopUpDialog dialog = new RangeIncreaseDialog((WormholeGenerator) data.getCustomData(),this,"Hypershunt Range");
            CustomPanelAPI panelAPI = Global.getSettings().createCustom(500,300,dialog);
            UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
            dialog.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()-(panelAPI.getPosition().getHeight()/2),true);
        }
    }
}
