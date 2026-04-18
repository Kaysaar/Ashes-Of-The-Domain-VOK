package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class BifrostControlCenter extends BaseIndustry {
    boolean hasBeenAdded = false;
    public static int costUpkeepForEachGate = 20000;
    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        BifrostMegastructureManager.getInstance().getMegastructure().getSections().forEach(BifrostSection::stopReconDueToLostOfControlCenter);
    }

    @Override
    protected void applyIncomeAndUpkeep(float sizeOverride) {
        super.applyIncomeAndUpkeep(sizeOverride);
        getUpkeep().unmodifyMult("ind_hazard");
    }

    @Override
    public void apply() {
        super.apply(true);
        if(!hasBeenAdded){
            hasBeenAdded = true;
            BifrostMegastructureManager.getInstance().getMegastructure().getSections().forEach(BifrostSection::restartReconDueToRegainingControlCenter);
        }
        if(market.getFaction()==null||!market.getFaction().isPlayerFaction()){
            hasBeenAdded = false;
        }
        float total =  BifrostMegastructureManager.getInstance().getMegastructure().getTotalAccessibility();
        int size = BifrostMegastructureManager.getInstance().getMegastructure().getActiveSections().size();
        if(size>=2){
            LinkedHashMap<String,Integer>totalDemand = BifrostMegastructureManager.getInstance().getMegastructure().getTotalDemandFromActive();
            for (Map.Entry<String, Integer> entry : totalDemand.entrySet()) {
                if(entry.getValue()==0)continue;
                demand(entry.getKey(), entry.getValue());
            }
            Pair<String,Integer> deficit = getMaxDeficit(totalDemand.keySet().toArray(new String[0]));
            if(deficit.two>0){
                int expected = totalDemand.get(deficit.one);
                float penalty = 1f-((float) deficit.two /expected);
                total = total*penalty;
            }
            for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFactionId())) {
                factionMarket.getAccessibilityMod().modifyFlat("aotd_bifrost",total,"Bifrost Network");
            }
        }
        if(size>0){
            getUpkeep().modifyFlat("aotd_bf_amount",costUpkeepForEachGate*size,"Gate Network Upkeep");
        }

    }

    @Override
    public void unapply() {
        super.unapply();
        for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFactionId())) {
            factionMarket.getAccessibilityMod().unmodifyFlat("aotd_bifrost");
        }
    }

    @Override
    public boolean canImprove() {
        return false;
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public void finishBuildingOrUpgrading() {
        super.finishBuildingOrUpgrading();
        
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Bifrost Network Maintenance", Alignment.MID,10f);
        tooltip.addPara("This structure is necessary for proper work of Bifrost Gates. Without this structure none of gates will work!",3f);
        tooltip.addPara("Each additional gate within network will add %s credits of upkeep to this structure!",5f, Color.ORANGE,Misc.getDGSCredits(costUpkeepForEachGate));

        int size = BifrostMegastructureManager.getInstance().getMegastructure().getActiveSections().size();
        if(size>=2){
            int total = Math.round(BifrostMegastructureManager.getInstance().getMegastructure().getTotalAccessibility()*100f);

            tooltip.addPara("Expected bonus accessibility from gate network : %s",5f,Color.ORANGE,total+"%");
        }
        else{
            tooltip.addPara("To receive bonus from gate network at least 2 gates must be active!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFactionId())) {
            if(factionMarket==market)continue;
            if(factionMarket.hasIndustry(this.getSpec().getId())){
                return false;
            }
        }
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.BIFROST_GATE,market);
    }

    @Override
    public String getUnavailableReason() {
        return super.getUnavailableReason();
    }

    @Override
    public boolean showWhenUnavailable() {
        for (MarketAPI factionMarket : Misc.getFactionMarkets(market.getFactionId())) {
            if(factionMarket==market)continue;
            if(factionMarket.hasIndustry(this.getSpec().getId())){
                return false;
            }
        }

        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.BIFROST_GATE,market);
    }

    @Override
    public boolean canShutDown() {
        return false;
    }
}
