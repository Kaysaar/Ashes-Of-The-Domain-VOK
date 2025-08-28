package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

import static data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc.getIndustriesListed;

public class EmergencyMeasures extends BaseIndustrySynergy {
    @Override
    public boolean canShowSynergyInUI(MarketAPI market) {
        return IndustrySynergiesMisc.didMarketMetTechCriteria(market, AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY);
    }

    @Override
    public void populateListForSynergies(HashSet<String> industries,MarketAPI market) {
        industries.add(Industries.POPULATION);
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION));
        industries.addAll(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY));
    }

    @Override
    public boolean doesSynergyMetReq(MarketAPI market) {

        return canShowSynergyInUI(market)&&
                market.isUseStockpilesForShortages()&&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION).toArray(new String[0]))&&
                IndustrySynergiesMisc.isAtLeastOneIndustryFunctionalFromList(market,IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY).toArray(new String[0]));

    }

    @Override
    public void printReqImpl(TooltipMakerAPI tooltip, MarketAPI market, Color base, Color highLight) {
        ArrayList<String> ids = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION);
        ArrayList<String> fuelIds = IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.HEAVYINDUSTRY);
        tooltip.addPara("%s and %s are required to be functional", 3f, base, highLight,
                getIndustriesListed(ids,market),
                getIndustriesListed(fuelIds,market));
        if(market.isUseStockpilesForShortages()){
            tooltip.addPara("\"Use Stockpiles During Shortage\" must be active", Misc.getPositiveHighlightColor(),3f);
        }
        else{
            tooltip.addPara("\"Use Stockpiles During Shortage\" must be active",Misc.getNegativeHighlightColor(),3f);
        }

    }

    @Override
    public void printEffectsImpl(TooltipMakerAPI tooltip, Color base, Color highLight, float efficiency, MarketAPI market) {
        tooltip.addPara("Every day for each disrupted industry consumes %s %s and %s %s from stockpiles",3f,base,highLight,"5","Supplies","2","Heavy Machinery");
        tooltip.addPara("As long as there are enough %s and %s in stockpile : Reduce disrupted time for all structures on market by %s",3f,base,highLight,"Supplies","Heavy Machinery","66%");

    }

    @Override
    public String getSynergyName() {
        return "Emergency Measures";
    }

    @Override
    public int getAmountOfWagonsForUI(String industry) {
        return 10;
    }

    @Override
    public void endOfTheDay(MarketAPI market) {
        if(market.isUseStockpilesForShortages()){
            CommodityOnMarketAPI dataSupplies =market.getCommodityData(Commodities.SUPPLIES);
            CommodityOnMarketAPI dataHeavyMachinery = market.getCommodityData(Commodities.HEAVY_MACHINERY);
            int amountOfDisrupted = market.getIndustries().stream().filter(Industry::isDisrupted).toList().size();
            if(amountOfDisrupted>0){
                float amountSNeeded = 5*amountOfDisrupted;
                float amountHeavyNeeded = 2*amountOfDisrupted;
                if(dataSupplies.getStockpile()>=amountSNeeded&&dataHeavyMachinery.getStockpile()>=amountHeavyNeeded){
                    dataSupplies.removeFromStockpile(amountSNeeded);
                    dataHeavyMachinery.removeFromStockpile(amountHeavyNeeded);
                    market.getIndustries().stream().filter(Industry::isDisrupted).forEach(x->{
                        float leftDays = x.getDisruptedDays();
                        float efficiency = 1.66f;
                        x.setDisrupted(leftDays-efficiency);
                    });
                }
            }
        }

    }
}
