package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels;

import com.fs.starfarer.api.ui.CustomPanelAPI;

public interface BasePanelInterface {
    public CustomPanelAPI getOptionPanel();
    public CustomPanelAPI getDesignPanel();
    public  void init();
    public void clear();
    public void reInit();
    public void advance(float amount);
    public void reset();
}
