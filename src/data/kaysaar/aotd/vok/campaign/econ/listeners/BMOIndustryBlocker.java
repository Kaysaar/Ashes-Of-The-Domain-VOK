package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentCondition;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import kaysaar.bmo.listeners.BuildingMenuListener;

import java.util.HashSet;

public class BMOIndustryBlocker implements BuildingMenuListener {
    @Override
    public HashSet<String> addBuildingsToBeHidden(MarketAPI marketAPI) {
        ColonyDevelopmentCondition development = ColonyDevelopmentManager.getColonyDevelopmentConditionIfPresent(marketAPI);
        if(development!=null){
            if(development.getIdOfDevelopment().equals("archeosite")){
                HashSet<String>set = new HashSet<>();
                Global.getSettings().getAllIndustrySpecs().forEach(x->{
                    if(!x.hasTag(Industries.TAG_STATION)){
                        set.add(x.getId());
                    }
                });
                set.remove(AoTDIndustries.RESEARCH_CENTER);
                set.remove(Industries.TECHMINING);
                set.remove(Industries.GROUNDDEFENSES);
                set.remove(Industries.SPACEPORT);
                return set;
            }
        }
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
