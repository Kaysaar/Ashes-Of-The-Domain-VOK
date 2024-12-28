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
    float currYPos = 0;
    public IndustryTable(float width, float height, CustomPanelAPI panelToPlace, boolean doesHaveScroller, float xCord, float yCord,MarketAPI market) {
        super(width, height, panelToPlace, doesHaveScroller, xCord, yCord);
        specs = new ArrayList<>();
        this.market = market;
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation"));
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation_mid"));
        specs.add(Global.getSettings().getIndustrySpec("orbitalstation_high"));
        if(dropDownButtons.isEmpty()){

            ArrayList<IndustrySpecAPI>specs = BuildingMenuMisc.getAllSpecsWithoutDowngrade();
            BuildingMenuMisc.sortIndustrySpecsByName(specs);
            for (IndustrySpecAPI industrySpecAPI :specs ) {

                IndustryDropDownButton button  = new IndustryDropDownButton(this,width,40,0,0,industrySpecAPI,BuildingMenuMisc.getSpecsOfParent(industrySpecAPI.getData()),market);
                dropDownButtons.add(button);

            }


        }


    }

    @Override
    public void createTable() {
        for (DropDownButton dropDownButton : dropDownButtons) {
            IndustryDropDownButton button = (IndustryDropDownButton) dropDownButton;
            if(BuildingMenuMisc.isIndustryFromTreePresent(button.mainSpec,market))continue;
            boolean found = false;
            if(dropDownButton.droppableMode){
                for (IndustrySpecAPI subSpec : button.subSpecs) {
                    if(shouldShowIndustry(subSpec.getNewPluginInstance(market))){
                        found = true;
                        break;
                    }
                }
                if(!found)continue;
            }
            else{
                Industry ind = button.mainSpec.getNewPluginInstance(market);
                if(!showIndustry(ind, button.mainSpec))continue;
            }

            button.resetUI();
            button.createUI();
            tooltipOfImpl.addCustom(dropDownButton.getPanelOfImpl(),2f);
        }

        panelToWorkWith.addUIElement(tooltipOfImpl).inTL(0,0);
        if(tooltipOfImpl.getExternalScroller()!=null){
            if(currYPos+panelToWorkWith.getPosition().getHeight()>= tooltipOfImpl.getHeightSoFar()){
                currYPos = tooltipOfImpl.getHeightSoFar()-panelToWorkWith.getPosition().getHeight();
            }
            tooltipOfImpl.getExternalScroller().setYOffset(currYPos);
        }
        mainPanel.addComponent(panelToWorkWith).inTL(0,0);

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

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(tooltipOfImpl!=null&&tooltipOfImpl.getExternalScroller()!=null){
            currYPos = tooltipOfImpl.getExternalScroller().getYOffset();
        }
    }
}