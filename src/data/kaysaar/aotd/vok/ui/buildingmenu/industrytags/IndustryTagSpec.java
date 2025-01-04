package data.kaysaar.aotd.vok.ui.buildingmenu.industrytags;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import data.kaysaar.aotd.vok.ui.buildingmenu.IndustryDropDownButton;

import java.util.ArrayList;

public class IndustryTagSpec {
    public String tag;
    public String tagName;
    public ArrayList<String>specs;
    public IndustryTagType type;
    public IndustryTagSpec(String tag, String tagName, ArrayList<String> specsManuallyPlaced,IndustryTagType type) {
        this.tag = tag;
        this.tagName = tagName;
        this.specs = specsManuallyPlaced;
        this.type = type;
    }
    public ArrayList<String> getSpecIdsForMatchup( MarketAPI market,ArrayList<IndustryDropDownButton> buttonsExisting){
        ArrayList<String>toReturn = new ArrayList<>();
        for (IndustryDropDownButton industryDropDownButton : buttonsExisting) {
            for (IndustrySpecAPI o : industryDropDownButton.getSpecs()) {
                if(specs.contains(o.getId())){
                    toReturn.add(o.getId());
                }
            }
        }
        return  toReturn;
    }
}
