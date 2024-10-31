package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.ui.HypershuntUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

public class HypershuntMegastrcutre extends GPBaseMegastructure {
    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new HypershuntUI(this,parentPanel, menu);
    }
}
