package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.ui;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.AutomationDialog;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.ManpowerManagementDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;

public class NidavelirUI extends BaseMegastructureMenu {
    public NidavelirUI(GPBaseMegastructure megastructure, CustomPanelAPI parentPanel, GPMegastructureMenu menu) {
        super(megastructure, parentPanel, menu);
    }
    @Override
    public void buttonHasBeenPressed(ButtonData data) {
        if(data.getCustomCommand().contains("assignManpower")){
            NidavelirComplexMegastructure mega = (NidavelirComplexMegastructure) megastructureReferedTo;
            BasePopUpDialog dialog = new ManpowerManagementDialog("Manpower Management",(NidavelirBaseSection) data.getCustomData(),this,mega.getRemainingManpowerPoints());
            AshMisc.initPopUpDialog(dialog,700,440);
        }
        if(data.getCustomCommand().contains("automateSection")){
            BasePopUpDialog dialog = new AutomationDialog("Section Automation",(NidavelirBaseSection) data.getCustomData(),this);
            AshMisc.initPopUpDialog(dialog,720,390);
        }
    }
}
