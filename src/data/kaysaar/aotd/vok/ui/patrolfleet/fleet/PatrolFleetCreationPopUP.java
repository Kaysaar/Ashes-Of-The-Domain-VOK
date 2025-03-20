package data.kaysaar.aotd.vok.ui.patrolfleet.fleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.ui.patrolfleet.CreateNewFleetTab;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetInfoTab;

public class PatrolFleetCreationPopUP extends BasePopUpDialog {
    CreateNewFleetTab tab;
    PatrolFleetDataManager main;
    public PatrolFleetCreationPopUP(String headerTitle, PatrolFleetDataManager main ){
        super(headerTitle);
        this.main = main;
        this.isDialog = true;

    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        super.createContentForDialog(tooltip, width);
        tab = new CreateNewFleetTab(width,610);
        tooltip.addCustom(tab.mainPanel,5f);
    }

    @Override
    public void applyConfirmScript() {
        super.applyConfirmScript();
        if(!tab.getData().getExpectedVesselsInFleet().isEmpty()){
            tab.getData().init(Global.getSettings().createPerson());
            main.data.refreshArmadaPanel();
        }
    }

    @Override
    public void onExit() {
        super.onExit();

        tab.buttons.clear();
        tab.currShowcase.showcases.clear();
        tab.shipOptionPanelInterface.clear(); //Avoiding mem Leaks
        main.createFleetsTab();
        UIData.recompute();
    }

}
