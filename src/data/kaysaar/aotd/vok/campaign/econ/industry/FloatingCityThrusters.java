package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.campaign.listeners.ColonyDecivListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl.FloatingCity;
import data.kaysaar.aotd.vok.campaign.econ.conditions.FloatingCityCondition;

public class FloatingCityThrusters extends BaseIndustry  implements MarketImmigrationModifier {
    public static float dailyMaxDecreaseLevel = 10;
    @Override
    public void apply() {
        int total = 0;
        for (Industry s : market.getIndustries()) {
            total+=getFuelCostPerStructure(s,true);
        }
        demand(Commodities.FUEL, total);

    }

    public static int getFuelCostPerStructure(Industry industry,boolean ignoreBuildingStructures) {
        if(industry instanceof FloatingCityThrusters)return 0;
        if(industry.isBuilding()&&!industry.isUpgrading()&&ignoreBuildingStructures)return 0;
        if(industry.isHidden())return 0;
        if(industry.getSpec().hasTag(Industries.TAG_HEAVYINDUSTRY)){
            return 5;
        }
        if(industry.isIndustry()){
            return 4;
        }
        return 3;
    }
    public static int getFuelCostPerStructureOnlyBuilding(Industry industry) {
        if(industry instanceof FloatingCityThrusters)return 0;
        if(industry.isBuilding()&&!industry.isUpgrading())return 0;
        if(industry.isHidden())return 0;
        if(industry.getSpec().hasTag(Industries.TAG_HEAVYINDUSTRY)){
            return 5;
        }
        if(industry.isIndustry()){
            return 4;
        }
        return 3;
    }
    public static int getFuelCostPerStructureExcludingBuildingStructures(MarketAPI market){
        int total = 0;
        for (Industry industry : market.getIndustries()) {
            if(!industry.isBuilding()||industry.isUpgrading())continue;
            total+=getFuelCostPerStructure(industry,false);
        }
        return total;
    }

    @Override
    public boolean isHidden() {
        return true;
    }


    @Override
    public void advance(float amount) {
        int total = 0;
        for (Industry s : market.getIndustries()) {
            total+=getFuelCostPerStructure(s,true);
        }
        float days = Global.getSector().getClock().convertToDays(amount);
        float decreaseLevel = dailyMaxDecreaseLevel;
        int deficitFuel = getMaxDeficit(Commodities.FUEL).two;
        float level = FloatingCityCondition.getCurrentLevel(market);
        FloatingCityCondition.FloatingCityStage stage =FloatingCityCondition.getCurrentStage(market);
        if(deficitFuel==0){
            // add level till its max
            if(level>=FloatingCityCondition.maxLevel){
                level = FloatingCityCondition.maxLevel;

            }
            FloatingCityCondition.setCurrentLevel(market, level);
        }
        else{
            float perc = (float) deficitFuel /total;
            float decreaseBy = decreaseLevel*days*perc;
            level-=decreaseBy;
            FloatingCityCondition.setCurrentLevel(market, level);
            if(level<=0){
                DecivTracker.decivilize(market,true,true);
                market.getMemoryWithoutUpdate().unset(FloatingCityCondition.keyForLevel);
                return;
            }

        }
        if(!market.hasCondition(Conditions.NO_ATMOSPHERE)){
            market.addCondition(Conditions.NO_ATMOSPHERE);
        }
        market.suppressCondition(Conditions.NO_ATMOSPHERE);
        if(stage== FloatingCityCondition.FloatingCityStage.BAD||stage== FloatingCityCondition.FloatingCityStage.OH_FUCK){
            market.unsuppressCondition(Conditions.TOXIC_ATMOSPHERE);
            market.unsuppressCondition(Conditions.HIGH_GRAVITY);
        }
        else{
            market.suppressCondition(Conditions.TOXIC_ATMOSPHERE);
            market.suppressCondition(Conditions.HIGH_GRAVITY);

        }


    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        FloatingCityCondition.FloatingCityStage stage = FloatingCityCondition.getCurrentStage(market);
        incoming.getWeight().unmodifyFlat("aotd_bad_situation");
        if(stage== FloatingCityCondition.FloatingCityStage.BAD){
            incoming.getWeight().modifyFlat("aotd_bad_situation",-50,"Current Altitude Level");
        }
        if(stage== FloatingCityCondition.FloatingCityStage.OH_FUCK){
            incoming.getWeight().modifyFlat("aotd_bad_situation",-100,"Current Altitude Level");
        }

    }
}
