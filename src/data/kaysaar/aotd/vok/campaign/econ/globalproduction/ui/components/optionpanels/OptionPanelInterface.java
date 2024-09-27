package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.optionpanels;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;

import java.util.ArrayList;

public interface OptionPanelInterface {
    public CustomPanelAPI getOptionPanel();
    public CustomPanelAPI getDesignPanel();
    public ArrayList<ButtonAPI>getOrderButtons();
    public  void init();
    public void clear();
    public void reInit();
    public void advance(float amount);
    public void reset();
    public TextFieldAPI getTextField();
    public boolean canClose();
}
