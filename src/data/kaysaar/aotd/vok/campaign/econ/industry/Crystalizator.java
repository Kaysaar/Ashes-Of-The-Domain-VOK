package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import static data.kaysaar.aotd.vok.plugins.AoDUtilis.checkForItemBeingInstalled;


public class Crystalizator extends BaseIndustry {
    public void apply() {
        super.apply(true);
        AoDUtilis.ensureIndustryHasNoItem(this);
        int size = market.getSize();


        demand(Commodities.HEAVY_MACHINERY, size - 2); // have to keep it low since it can be circular
        demand(Commodities.ORE, size + 2);
        demand(AoTDCommodities.POLYMERS, size - 2);
        supply(Commodities.METALS, size+4);
        supply(AoTDCommodities.REFINED_METAL, size-2);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.HEAVY_MACHINERY, Commodities.ORE, AoTDCommodities.POLYMERS);

        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;
        if (!isFunctional()) {
            supply.clear();
        }
        applyDeficitToProduction(2, deficit, Commodities.METALS,AoTDCommodities.REFINED_METAL);
    }


    @Override
    public void unapply() {
        super.unapply();
    }


    public float getPatherInterest() {
        return 2f + super.getPatherInterest();
    }

    @Override
    public boolean isAvailableToBuild() {
        return checkForItemBeingInstalled(this.market,Industries.REFINING,Items.CATALYTIC_CORE)&& AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PRODUCTION_MATRIX,market);
    }
    @Override
    public String getUnavailableReason() {
        if(!checkForItemBeingInstalled(this.market,Industries.REFINING,Items.CATALYTIC_CORE)){
            return "Catalytic Core required to be installed in Refining";
        }
        return null;
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.ALLOY_PRODUCTION_MATRIX,market);
    }
    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
