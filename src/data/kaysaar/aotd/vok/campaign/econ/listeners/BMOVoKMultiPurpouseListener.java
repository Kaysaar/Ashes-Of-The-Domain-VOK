package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl.BoreCity;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentCondition;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import kaysaar.bmo.listeners.BuildingMenuListener;

import java.util.HashSet;

public class BMOVoKMultiPurpouseListener implements BuildingMenuListener {
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

    @Override
    public void modifyIndustryConstructionPrice(Industry indInstance, MarketAPI market, boolean isForUpgrade, MutableStat price) {
        if(ColonyDevelopmentManager.getColonyDevelopmentConditionIfPresent(market)!=null){
            if(ColonyDevelopmentManager.getColonyDevelopmentConditionIfPresent(market).getIdOfDevelopment().equals("borecity")){
                price.modifyMult("bore_city", BoreCity.COST_MULT,"Bore City");
            }
        }
    }
}
