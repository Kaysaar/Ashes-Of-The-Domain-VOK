package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AIColonyManagerListener implements EconomyTickListener {
    public static final Logger log = Global.getLogger(AIColonyManagerListener.class);
    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        AoTDMainResearchManager manager = AoTDMainResearchManager.getInstance();
//        for (AoTDFactionResearchManager factionResearchManager : manager.getFactionResearchManagers()) {
//            if (factionResearchManager.getFaction().isPlayerFaction() || !factionResearchManager.canUpgrade) continue;
//            factionResearchManager.setCanUpgrade(false);
//            manageIndustriesConstruction(factionResearchManager.getFaction());
//
//
//        }
    }

    public void manageIndustriesConstruction(FactionAPI faction){
        for(int i=0;i<4;i++){
            heavyIndManager(faction);
        }
        initalizeUpgradeOfHeavyIndustry(faction);
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).haveResearched(AoTDTechIds.CONSUMER_GOODS_PRODUCTION)){
            initalizeUpgradeOfLightIndustry(faction);
        }
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).haveResearched(AoTDTechIds.ANTIMATTER_SYNTHESIS)){
            initalizeUpgradeOfFuelProduction(faction);
        }
    }
    public void initalizeUpgradeOfHeavyIndustry(FactionAPI faction){
        HashMap<String, Integer> industriesPoll= generateValuesForIndustries(faction,AoTDIndustries.BENEFICATION,AoTDIndustries.SUBLIMATION,AoTDIndustries.ISOTOPE_SEPARATOR,AoTDIndustries.CRYSTALIZATOR);
        for (Integer value : industriesPoll.values()) {
            if(value==0){
                return;
            }
        }
        for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
            if(factionMarket.hasIndustry(Industries.ORBITALWORKS)){
                factionMarket.removeIndustry(Industries.ORBITALWORKS,null,false);
                factionMarket.addIndustry(AoTDIndustries.ORBITAL_MANUFACTORIUM);
                factionMarket.getIndustry(AoTDIndustries.ORBITAL_MANUFACTORIUM).startBuilding();
                break;
            }
            if(factionMarket.hasIndustry(Industries.HEAVYINDUSTRY)){
                factionMarket.removeIndustry(Industries.HEAVYINDUSTRY,null,false);
                factionMarket.addIndustry(Industries.ORBITALWORKS);
                factionMarket.getIndustry(Industries.ORBITALWORKS).startBuilding();
                break;
            }
        }

    }
    public void initalizeUpgradeOfLightIndustry(FactionAPI faction){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
            if(factionMarket.hasIndustry(Industries.LIGHTINDUSTRY)){
                factionMarket.removeIndustry(Industries.LIGHTINDUSTRY,null,false);
                factionMarket.addIndustry(AoTDIndustries.CONSUMER_INDUSTRY);
                factionMarket.getIndustry(AoTDIndustries.CONSUMER_INDUSTRY).startBuilding();
                break;
            }
        }

    }
    public void initalizeUpgradeOfFuelProduction(FactionAPI faction){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
            if(factionMarket.hasIndustry(Industries.FUELPROD)){
                factionMarket.removeIndustry(Industries.FUELPROD,null,false);
                factionMarket.addIndustry(AoTDIndustries.BLAST_PROCESSING);
                factionMarket.getIndustry(AoTDIndustries.BLAST_PROCESSING).startBuilding();
                break;
            }
        }

    }
    public void heavyIndManager(FactionAPI faction) {
        HashMap<String, Integer> industriesPoll = generateValuesForIndustries(faction,AoTDIndustries.BENEFICATION,AoTDIndustries.SUBLIMATION,AoTDIndustries.ISOTOPE_SEPARATOR,AoTDIndustries.CRYSTALIZATOR);
        if (industriesPoll.get(AoTDIndustries.BENEFICATION) == 0) {
            beneficationRoll(faction);
            log.info("Faction "+faction.getId()+" has rolled for construction of benefication for the first time");
        }
        else if  (industriesPoll.get(AoTDIndustries.SUBLIMATION) == 0) {
            sublimationRoll(faction);
            log.info("Faction "+faction.getId()+" has rolled for construction of sublimation for the first time");

        }  else {
            int lowestValue = -1;
            for (Integer value : industriesPoll.values()) {
                if (lowestValue == -1) {
                    lowestValue = value;
                    continue;
                }
                if (value <= lowestValue) {
                    lowestValue = value;
                }
            }
            String industry = null;
            for (Map.Entry<String, Integer> entry : industriesPoll.entrySet()) {
                if(entry.getValue()==lowestValue){
                    industry = entry.getKey();
                    break;
                }
            }
            if(industry!=null){
                log.info("Faction "+faction.getId()+" has rolled for construction of "+industry);
                if(industry.equals(AoTDIndustries.BENEFICATION)){
                    beneficationRoll(faction);
                }
                if(industry.equals(AoTDIndustries.SUBLIMATION)){
                    sublimationRoll(faction);
                }
                if(industry.equals(AoTDIndustries.CASCADE_REPROCESSOR)){
                    isotopeSeperatorRoll(faction);
                }
                if(industry.equals(AoTDIndustries.POLICRYSTALIZATOR)){
                    policrystalizatorRoll(faction);
                }


            }

        }



    }

    @NotNull
    private static HashMap<String, Integer> generateValuesForIndustries(FactionAPI faction,String ... industires) {
        HashMap<String, Integer> industriesPoll = new HashMap<>();
        for (String s : industires) {
            industriesPoll.put(s,0);
        }

        for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
            for (Map.Entry<String, Integer> entry : industriesPoll.entrySet()) {
                if (factionMarket.hasIndustry(entry.getKey())) {
                    entry.setValue(entry.getValue() + 1);
                }
            }
        }
        return industriesPoll;
    }

    private void sublimationRoll(FactionAPI faction) {
        HashMap<String, Integer> marketConditions = new HashMap<>();
        marketConditions.put(Conditions.VOLATILES_TRACE, 1);
        marketConditions.put(Conditions.VOLATILES_DIFFUSE, 2);
        marketConditions.put(Conditions.VOLATILES_ABUNDANT, 3);
        marketConditions.put(Conditions.VOLATILES_PLENTIFUL, 4);

        marketConditions.put(Conditions.ORGANICS_TRACE, 1);
        marketConditions.put(Conditions.ORGANICS_COMMON, 2);
        marketConditions.put(Conditions.ORGANICS_PLENTIFUL, 3);
        marketConditions.put(Conditions.ORGANICS_ABUNDANT, 4);

        HashMap<String, Integer> industryConditions = new HashMap<>();
        industryConditions.put(AoTDIndustries.ISOTOPE_SEPARATOR,2);
        industryConditions.put(AoTDIndustries.BENEFICATION,-1000);
        industryConditions.put(AoTDIndustries.SUBLIMATION,-1000);

        MarketAPI market = determineLocationOfIndustry(faction,Industries.MINING,marketConditions,industryConditions);
        if(market!=null){
            startBuildingIndustry(market,Industries.MINING,AoTDIndustries.SUBLIMATION);

        }
    }
    private void isotopeSeperatorRoll(FactionAPI faction) {
        HashMap<String, Integer> marketConditions = new HashMap<>();
        marketConditions.put(Conditions.NO_ATMOSPHERE, 1);

        HashMap<String, Integer> industryConditions = new HashMap<>();
        industryConditions.put(AoTDIndustries.CASCADE_REPROCESSOR,-1000);
        industryConditions.put(AoTDIndustries.POLICRYSTALIZATOR,-1000);
        industryConditions.put(AoTDIndustries.ISOTOPE_SEPARATOR,-1000);
        industryConditions.put(AoTDIndustries.CRYSTALIZATOR,-1000);
        industryConditions.put(Industries.REFINING,+12);

        MarketAPI market = determineLocationOfIndustry(faction,Industries.REFINING,marketConditions,industryConditions);
        if(market!=null){
            startBuildingIndustry(market,Industries.REFINING,AoTDIndustries.CASCADE_REPROCESSOR);

        }
    }
    private void policrystalizatorRoll(FactionAPI faction) {
        HashMap<String, Integer> marketConditions = new HashMap<>();
        marketConditions.put(Conditions.NO_ATMOSPHERE, 1);

        HashMap<String, Integer> industryConditions = new HashMap<>();
        industryConditions.put(AoTDIndustries.CASCADE_REPROCESSOR,-1000);
        industryConditions.put(AoTDIndustries.POLICRYSTALIZATOR,-1000);
        industryConditions.put(AoTDIndustries.ISOTOPE_SEPARATOR,-1000);
        industryConditions.put(AoTDIndustries.CRYSTALIZATOR,-1000);
        industryConditions.put(Industries.REFINING,+12);
        MarketAPI market = determineLocationOfIndustry(faction,Industries.REFINING,marketConditions,industryConditions);
        if(market!=null){
            startBuildingIndustry(market,Industries.REFINING,AoTDIndustries.POLICRYSTALIZATOR);

        }
    }
    private void beneficationRoll(FactionAPI faction) {
        HashMap<String, Integer> marketConditions = new HashMap<>();
        marketConditions.put(Conditions.ORE_SPARSE, 1);
        marketConditions.put(Conditions.ORE_MODERATE, 2);
        marketConditions.put(Conditions.ORE_ABUNDANT, 3);
        marketConditions.put(Conditions.ORE_RICH, 4);
        marketConditions.put(Conditions.ORE_ULTRARICH, 5);

        marketConditions.put(Conditions.RARE_ORE_SPARSE, 1);
        marketConditions.put(Conditions.RARE_ORE_MODERATE, 2);
        marketConditions.put(Conditions.RARE_ORE_ABUNDANT, 3);
        marketConditions.put(Conditions.RARE_ORE_RICH, 4);
        marketConditions.put(Conditions.RARE_ORE_ULTRARICH, 5);

        HashMap<String, Integer> industryConditions = new HashMap<>();
        industryConditions.put(AoTDIndustries.POLICRYSTALIZATOR,2);
        industryConditions.put(AoTDIndustries.BENEFICATION,-1000);
        industryConditions.put(AoTDIndustries.SUBLIMATION,-1000);
        MarketAPI market = determineLocationOfIndustry(faction,Industries.MINING,marketConditions,industryConditions);
        if(market!=null){
            startBuildingIndustry(market,Industries.MINING,AoTDIndustries.BENEFICATION);

        }
    }

    private  void startBuildingIndustry(MarketAPI market,String idToRemove,String idToAdd) {
        market.removeIndustry(idToRemove,null,false);
        market.addIndustry(idToAdd);
        market.getIndustry(idToAdd).startBuilding();
        log.info("Started construction of "+idToAdd+" on "+market.getId());
    }

    public MarketAPI determineLocationOfIndustry(FactionAPI factionAPI,String indstryToIgnore, HashMap<String, Integer> conditionWeightIndicators, HashMap<String, Integer> industryWeightIndicators) {
        MarketAPI marketToReturn = null;
        HashMap<MarketAPI, Integer> firstPoll = new HashMap<>();
        for (MarketAPI factionMarket : Misc.getFactionMarkets(factionAPI)) {
            int indAmount = 0;
            for (Industry ind : factionMarket.getIndustries()) {
                if (ind.isHidden()||ind.getId().equals(indstryToIgnore)) continue;
                indAmount++;
            }
            if (indAmount < 12) {
                firstPoll.put(factionMarket, 0);
            }
        }
        int biggest = 0;
        for (Map.Entry<MarketAPI, Integer> marketEntry : firstPoll.entrySet()) {
            for (Map.Entry<String, Integer> conditionEntry : conditionWeightIndicators.entrySet()) {
                if (marketEntry.getKey().hasCondition(conditionEntry.getKey())) {
                    marketEntry.setValue(marketEntry.getValue() + conditionEntry.getValue());
                }
            }
            for (Map.Entry<String, Integer> industryEntry : industryWeightIndicators.entrySet()) {
                if (marketEntry.getKey().hasIndustry(industryEntry.getKey())) {
                    marketEntry.setValue(marketEntry.getValue() + industryEntry.getValue());
                }
            }
            if (marketEntry.getValue() >= biggest) {
                biggest = marketEntry.getValue();
            }
        }
        log.info("Highest score for industry roll : "+biggest);
        for (Map.Entry<MarketAPI, Integer> entry : firstPoll.entrySet()) {
            if (entry.getValue() == biggest) {
                marketToReturn = entry.getKey();
                break;
            }
        }
        if(marketToReturn!=null){
            log.info("Picked up market "+marketToReturn.getId());
        }
        else{
            log.info("Picked up no markets");
        }
        return marketToReturn;


    }
}
