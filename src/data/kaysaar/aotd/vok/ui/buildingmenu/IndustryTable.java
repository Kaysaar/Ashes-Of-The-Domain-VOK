package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;

import java.util.ArrayList;
import java.util.Map;

public class IndustryTable extends UITableImpl {
    ArrayList<IndustrySpecAPI>specs;
    MarketAPI market;
    public IndustryTable(float width, float height, CustomPanelAPI panelToPlace, boolean doesHaveScroller, float xCord, float yCord,MarketAPI market) {
        super(width, height, panelToPlace, doesHaveScroller, xCord, yCord);
        specs = new ArrayList<>();
        this.market = market;
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation"));
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation_mid"));
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation_high"));
        if(dropDownButtons.isEmpty()){
            for (Map.Entry<IndustrySpecAPI, ArrayList<IndustrySpecAPI>> entry : BuildingMenuMisc.getSpecMapParentChild().entrySet()) {
                IndustryDropDownButton button  = new IndustryDropDownButton(this,width,40,0,0,entry.getKey(),entry.getValue(),market);
                dropDownButtons.add(button);
            }
            for (IndustrySpecAPI industrySpecAPI : BuildingMenuMisc.getAllSpecsWithoutDowngrade()) {
                IndustryButton button = new IndustryButton(width-20,40,industrySpecAPI,0f,market);
                button.initializeUI();
                buttonsToCheck.add(button);
            }


        }


    }

    @Override
    public void createTable() {
        for (DropDownButton dropDownButton : dropDownButtons) {
            IndustryDropDownButton button = (IndustryDropDownButton) dropDownButton;
            if(BuildingMenuMisc.isIndustryFromTreePresent(button.mainSpec,market))continue;
            boolean found = false;
            for (IndustrySpecAPI subSpec : button.subSpecs) {
                if(shouldShowIndustry(subSpec.getNewPluginInstance(market))){
                    found = true;
                    break;
                }
            }
            if(!found)continue;
            button.resetUI();
            button.createUI();
            tooltipOfImpl.addCustom(dropDownButton.getPanelOfImpl(),2f);
        }
        for (CustomButton customButton : buttonsToCheck) {
            IndustryButton button = (IndustryButton) customButton;
            Industry ind = button.spec.getNewPluginInstance(market);
            if(showIndustry(ind, button.spec)){
                tooltipOfImpl.addCustom(customButton.getPanel(),2f);

            }
        }
        super.createTable();

    }

    private boolean showIndustry(Industry ind, IndustrySpecAPI spec) {
        return shouldShowIndustry(ind) && !BuildingMenuMisc.isIndustryFromTreePresent(spec, market);
    }

    public boolean shouldShowIndustry(Industry ind){
        if(ind.isAvailableToBuild()){
            return !market.hasIndustry(ind.getSpec().getId());
        }
        else{
            return ind.showWhenUnavailable()&&!market.hasIndustry(ind.getSpec().getId());
        }
    }

}