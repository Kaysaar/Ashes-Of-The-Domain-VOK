package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

public interface GPIndividualMegastructreMenu extends CustomUIPanelPlugin {
    public void clearUI();
    public void initUI();
    public CustomPanelAPI getMainPanel();
    public GPBaseMegastructure getMegastructure();

}
