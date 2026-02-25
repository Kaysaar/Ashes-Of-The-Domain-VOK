package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.ui;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections.WormholeGenerator;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.RangeIncreaseDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;

public class HypershuntUI extends BaseMegastructureMenu {
    public HypershuntUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegastructureMenu menu) {
        super(megastructure, parentPanel, menu);
    }

    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        if(data.getCustomCommand().contains("adjustRange")){
            BasePopUpDialog dialog = new RangeIncreaseDialog((WormholeGenerator) data.getCustomData(),this,"Hypershunt Range");
            AshMisc.initPopUpDialog(dialog,500,300);
        }
    }
}
