package data.kaysaar.aotd.vok.ui.buildingmenu.industrytags;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import data.kaysaar.aotd.vok.ui.buildingmenu.IndustryDropDownButton;

import java.util.ArrayList;

public class AvailableIndustryTagSpec extends IndustryTagSpec{
    public AvailableIndustryTagSpec(String tag, String tagName, ArrayList<String> specsManuallyPlaced, IndustryTagType type) {
        super(tag, tagName, specsManuallyPlaced, type);
    }

    @Override
    public ArrayList<String> getSpecIdsForMatchup(MarketAPI market, ArrayList<IndustryDropDownButton> buttonsExisting) {
        ArrayList<String>toReturn = new ArrayList<>();
        for (IndustryDropDownButton industryDropDownButton : buttonsExisting) {
            for (IndustrySpecAPI o : industryDropDownButton.getSpecs()) {
                if(specs.contains(o.getId())){
                    Industry ind = o.getNewPluginInstance(market);
                    if(ind.isAvailableToBuild()){
                        toReturn.add(ind.getId());
                    }
                }
            }
        }
        return  toReturn;
    }
}
