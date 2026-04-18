package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import kaysaar.bmo.listeners.BuildingMenuListener;

import java.util.HashSet;

public class CoronalHypershuntBMOBlocker implements BuildingMenuListener {
    @Override
    public HashSet<String> addBuildingsToBeHidden(MarketAPI marketAPI) {
        for (Industry industry : marketAPI.getIndustries()) {
            if(industry.getSpec().getId().equals("aotd_coronal_control")){
                HashSet<String>set = new HashSet<>();
                Global.getSettings().getAllIndustrySpecs().forEach(x->set.add(x.getId()));
                return set;
            }
        }

       return  new HashSet<>();
    }
}
