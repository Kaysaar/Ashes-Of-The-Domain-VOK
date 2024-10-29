package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.util.ArrayList;

public class ButtonPackage {
    ArrayList<ButtonAPI>buttonsPlaced;
    CustomPanelAPI panelOfButtons;
    GPMegaStructureSection section;
    public ArrayList<ButtonAPI> getButtonsPlaced() {
        return buttonsPlaced;
    }

    public CustomPanelAPI getPanelOfButtons() {
        return panelOfButtons;
    }

    public GPMegaStructureSection getSection() {
        return section;
    }

    public void setSection(GPMegaStructureSection section) {
        this.section = section;
    }

    public void setButtonsPlaced(ArrayList<ButtonAPI> buttonsPlaced) {
        this.buttonsPlaced = buttonsPlaced;
    }

    public void setPanelOfButtons(CustomPanelAPI panelOfButtons) {
        this.panelOfButtons = panelOfButtons;
    }
}
