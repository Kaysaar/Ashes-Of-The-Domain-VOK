package data.kaysaar.aotd.vok.campaign.econ.synergies.models;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class BaseIndustrySynergy implements Cloneable{

    public void apply(float efficiencyPercent,MarketAPI market){

    }
    public String getIdForEffects(){
        return this.getClass().getSimpleName();
    }
    public boolean doesSynergyMetTotalReq(MarketAPI market){
        return doesSynergyMetReq(market)&& IndustrySynergiesMisc.isIndustryFunctionalAndExisting(market, AoTDIndustries.MAGLEV_CENTRAL_HUB);
    }
    public void unapply(MarketAPI market){
    }
    public void advance(MarketAPI market,float amount){

    }
    public boolean runsInEveryFrameScript(){
        return false;
    }
    public boolean doesSynergyMetReq(MarketAPI market){
        return false;
    }
    public boolean canShowSynergyInUI(MarketAPI market){
        return false;
    }
    public HashSet<String> getIndustriesForSynergy(){
        LinkedHashSet<String> industries = new LinkedHashSet<>();
        populateListForSynergies(industries);
        return industries;
    }
    public void populateListForSynergies(HashSet<String> industries){

    }
    public String getSynergyName(){
        return "";
    }
    public void printEffects(TooltipMakerAPI tooltip,MarketAPI market,float efficiency){
        if(market==null){
            printEffectsImpl(tooltip,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE,efficiency,market);
        }
        else{
            printEffectsImpl(tooltip,Misc.getPositiveHighlightColor(),Color.ORANGE,efficiency,market);
        }
    }
    public void printEffectsImpl(TooltipMakerAPI tooltip,Color base,Color highLight,float efficiency,MarketAPI market){

    }
    public void printReq(TooltipMakerAPI tooltip,MarketAPI market){
        if(!getIndustriesForSynergy().isEmpty()){
            if(market==null){
                printReqImpl(tooltip,market,Misc.getTooltipTitleAndLightHighlightColor(),Color.ORANGE);

            }
            else{
                Color base = Misc.getPositiveHighlightColor();
                if(!this.doesSynergyMetReq(market)){
                    base = Misc.getNegativeHighlightColor();
                }
                printReqImpl(tooltip,market,base,Color.ORANGE);

            }
        }

        printAdditionalReq(tooltip,market);
    }
    public void printReqImpl(TooltipMakerAPI tooltip,MarketAPI market,Color base,Color highLight){

    }
    public void printAdditionalReq(TooltipMakerAPI tooltip,MarketAPI market){

    }
    public int getAmountOfWagonsForUI(String industry){
        return 4;
    }
    public Color getColorForWagons(String industry){
        return Color.ORANGE;
    }
    public void advanceInEveryFrameScript(float amount){
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites()>0){
            for (MarketAPI playerFactionMarket : AoTDMisc.getPlayerFactionMarkets()) {

            }
        }
    }
    @Override
    public BaseIndustrySynergy clone() {
        try {
            return (BaseIndustrySynergy) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
