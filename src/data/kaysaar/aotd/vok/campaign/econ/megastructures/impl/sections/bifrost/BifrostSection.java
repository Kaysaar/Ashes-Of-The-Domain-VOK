package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost;

import ashlib.data.plugins.ui.models.ProgressBarComponentV2;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContractManager;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.CoronalHypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding.BifrostLocationData;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts.BaseRestorationContract;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BifrostSection extends BaseMegastructureSection {
    public SectorEntityToken gateTiedTo;

    public SectorEntityToken getGateTiedTo() {
        return gateTiedTo;
    }
    BifrostLocationData data;

    public BifrostLocationData getData() {
        return data;
    }
    public boolean restorationStoppedDueToIndustryDelete = false;
    public void stopReconDueToLostOfControlCenter(){
        AoTDTradeContractManager.getInstance().removeContract(getRestorationContractID());
        this.restorationStoppedDueToIndustryDelete = true;
    }
    public void restartReconDueToRegainingControlCenter(){
        if(this.restorationStoppedDueToIndustryDelete){
            this.restorationStoppedDueToIndustryDelete = false;
            startRestoration();
        }
    }
    public LinkedHashMap<String,Integer>getDemandForGateMaintenance(){
        LinkedHashMap<String,Integer>res = new LinkedHashMap<>();
        if(!CoronalHypershuntMegastructure.isWithinReceiverSystem(starSystemAPI)){
            res.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,3);
        }
        return res;
    }

    @Override
    public float getUpkeepOfSection() {
        return super.getUpkeepOfSection();
    }
    public float getUpkeep(boolean override) {
        if(override){
            return Math.round(getSpec().getBaseUpkeepAfterRestoration()/2f);
        }
        return getSpec().getBaseUpkeepAfterRestoration();
    }
    public float getRawBonus(){
        float totalAccess = 0f;
        if(!isRestored||isDisabled)return 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(getStarSystemAPI())) {
            if(!market.isPlayerOwned()||!market.getFaction().isPlayerFaction())continue;
            if(market.getAccessibilityMod().getFlatBonus()>=0){
                totalAccess+=market.getAccessibilityMod().getFlatBonus();

            }
            if(market.getAccessibilityMod().getFlatBonus("aotd_bifrost")!=null) {
                totalAccess-=market.getAccessibilityMod().getFlatBonus("aotd_bifrost").getValue();

            }
        }
        totalAccess/=10f;
        return totalAccess;
    }
    public int getCooldown(){
        if(CoronalHypershuntMegastructure.isWithinReceiverSystem(starSystemAPI)){
            return 0;
        }
        return (int) gateTiedTo.getMemory().getFloat("$cooldown");
    }
    public LinkedHashMap<String,Integer>getCurrentDemand(){
        if(isRestored){
            LinkedHashMap<String,Integer>demand = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : getDemandForGateMaintenance().entrySet()) {
                demand.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(),true,entry.getKey()));
            }
            return AoTDMisc.getOrderedResourceMap(demand);
        }
        else{
            return AoTDMisc.getOrderedResourceMap(getMonthlyResNeeded());

        }
    }

    @Override
    public void startRestoration() {
        isRestoring = true;
        if(AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID())!=null){
            BaseRestorationContract contract = (BaseRestorationContract) AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID());
            contract.setFrozen(false);
        }
        else{
            BaseRestorationContract contract = new BaseRestorationContract(this){
                public String getContractType() {
                    return "Bifrost Construction";
                }

                @Override
                public String getSubTypeOfContractString() {
                    return starSystemAPI.getBaseName();
                }
            };
            AoTDTradeContractManager.getInstance().addContract(contract);
        }
    }

    public String getRestorationContractID(){
        return getMegastructureTiedTo().getUniqueGenId()+"_"+this.getSpec().getId()+"_"+getStarSystemAPI().getId();
    }
    public void addStatusToGate(float totalWidth, float totalHeight, float yForLabel,TooltipMakerAPI tooltip){
        if(isRestoring){
            String percent = Math.round(getProgressOfRestoration()*100f)+"%";
            tooltip.addPara("Under Construction ("+percent+")",Misc.getTooltipTitleAndLightHighlightColor(),1f).setAlignment(Alignment.MID);

            ProgressBarComponentV2 componentV2 = new ProgressBarComponentV2(totalWidth+5,totalHeight-25,getProgressOfRestoration(),Misc.getBasePlayerColor());
            tooltip.addCustom(componentV2.getMainPanel(),2f).getPosition().inTL(0,totalHeight-componentV2.getMainPanel().getPosition().getHeight()-5);
        }
        else{
            if(isRestored){
                if(CoronalHypershuntMegastructure.isWithinReceiverSystem(starSystemAPI)){
                    tooltip.addPara("Operational! Connected to %s",yForLabel,Misc.getPositiveHighlightColor(),Color.cyan,"Coronal Hypershunt").setAlignment(Alignment.MID);
                }
                else{
                    if(getCooldown()!=0){
                        tooltip.addPara("On cooldown for %s!",yForLabel,Color.ORANGE,AoTDMisc.convertDaysToString(getCooldown())).setAlignment(Alignment.MID);

                    }
                    else{
                        tooltip.addPara("Operational!",Misc.getPositiveHighlightColor(),yForLabel).setAlignment(Alignment.MID);

                    }

                }
            }
            else{
                tooltip.addPara("Paused Construction",Misc.getTooltipTitleAndLightHighlightColor(),1f).setAlignment(Alignment.MID);
                ProgressBarComponentV2 componentV2 = new ProgressBarComponentV2(totalWidth+5,totalHeight-25,getProgressOfRestoration(),Misc.getBasePlayerColor());
                tooltip.addCustom(componentV2.getMainPanel(),2f).getPosition().inTL(0,totalHeight-componentV2.getMainPanel().getPosition().getHeight()-5);
            }
        }
    }
    @Override
    public String getName() {
        return "Bifrost Gate : "+getStarSystemAPI().getBaseName();
    }

    public void setGateTiedTo(SectorEntityToken gateTiedTo) {
        this.gateTiedTo = gateTiedTo;
    }
    public StarSystemAPI starSystemAPI;

    public StarSystemAPI getStarSystemAPI() {
        return starSystemAPI;
    }
    public boolean isDisabled;

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setStarSystemAPI(StarSystemAPI starSystemAPI) {
        this.starSystemAPI = starSystemAPI;
    }
    @Override
    public void applyEffectOfSection() {
        super.applyEffectOfSection();
    }



    @Override
    public void advance(float amount) {
        super.advance(amount);
        MarketAPI market = null;
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarkets(starSystemAPI)) {
            if(marketAPI.getFaction()!=null) {
                if(marketAPI.isPlayerOwned()||(marketAPI.getFaction().isPlayerFaction())){
                    market = marketAPI;
                    break;
                }
            }

        }
        if(market==null && isRestoring){
            isRestoring = false;
            progressOfRestoration = 0f;
            BifrostMegastructure mega = (BifrostMegastructure) getMegastructureTiedTo();
            mega.removeBifrostGate(this);
            return;
        }

    }


    public void setData(BifrostLocationData data) {
        this.data = data;
    }


    @Override
    public void applyOnRestoration() {
        if(getData()==null){
            MarketAPI market = null;
            for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarkets(starSystemAPI)) {
                if(marketAPI.isPlayerOwned()||marketAPI.getFaction().isPlayerFaction()){
                    market = marketAPI;
                    break;
                }
            }
            if(market!=null){
                gateTiedTo = BifrostMegastructure.spawnGate(market);
            }
        }
        else{
            gateTiedTo = BifrostMegastructure.spawnGate(getData());
        }


    }

    @Override
    public boolean isBuildable() {
        return true;
    }
}
