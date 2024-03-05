package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.Refining;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorPlugin;
import data.kaysaar.aotd.vok.campaign.econ.items.ModularConstructorRepo;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import static data.kaysaar.aotd.vok.plugins.AoDUtilis.checkForItemBeingInstalled;

public class Sublimation extends BaseIndustry {
    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        if(this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
        demand(Commodities.HEAVY_MACHINERY, size);
        demand(AoTDCommodities.BIOTICS,  size);
        if(AoDUtilis.getOrganicsAmount(market)>=-1){
            supply(AoTDCommodities.POLYMERS,AoDUtilis.getOrganicsAmount(market)+(market.getSize()-2));
            supply(Commodities.ORGANICS,AoDUtilis.getOrganicsAmount(market)+market.getSize());
        }
        if(AoDUtilis.getVolatilesAmount(market)>=-1){
            supply(AoTDCommodities.COMPOUNDS,AoDUtilis.getVolatilesAmount(market)+(market.getSize()-2));
            supply(Commodities.VOLATILES,AoDUtilis.getVolatilesAmount(market)+market.getSize()-2);
        }
        Pair<String, Integer> deficit = getMaxDeficit(AoTDCommodities.BIOTICS, Commodities.HEAVY_MACHINERY,Commodities.ORGANICS);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        applyDeficitToProduction(2, deficit,
                AoTDCommodities.POLYMERS, AoTDCommodities.COMPOUNDS);
        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();

    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }

    @Override
    public boolean isAvailableToBuild() {
        return (AoDUtilis.getOrganicsAmount(market)>=-0 || AoDUtilis.getVolatilesAmount(market) >= 0)&& AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market);

    }

    @Override
    public String getUnavailableReason() {
        String reasoning= null;
        if( AoDUtilis.getOrganicsAmount(market)<=-1){
            reasoning = "There are no organics on that planet large enough to support that industry ";
        }
        if(AoDUtilis.getVolatilesAmount(market)<=-1){
            if(reasoning!=null){
                reasoning = "\nThere are neither organics nor volatiles on the planet large enough to support that industry ";
            }
            else{
                reasoning = "There are no volatiles on the planet large enough to support that industry ";
            }

        }

        return reasoning;

    }

    @Override
    public boolean showWhenUnavailable() {
        return (AoDUtilis.getOrganicsAmount(market)<=-1 || AoDUtilis.getVolatilesAmount(market) <= -1)&& AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DEEP_MINING_METHODS,market);
    }

}
