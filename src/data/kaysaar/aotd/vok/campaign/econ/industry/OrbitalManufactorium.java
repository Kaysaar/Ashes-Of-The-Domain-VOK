package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class OrbitalManufactorium extends HeavyIndustry {
    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        AoDUtilis.ensureIndustryHasNoItem(this);
        int shipBonus = 0;
        float qualityBonus = 0.7f;


        demand(Commodities.METALS, size-1);
        demand(Commodities.RARE_METALS, size-3);
        demand(AoTDCommodities.REFINED_METAL,size-2);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS,size-2);
        supply(Commodities.HEAVY_MACHINERY, size+1);
        supply(Commodities.SUPPLIES, size+1);
        supply(Commodities.HAND_WEAPONS, size+1);
        supply(Commodities.SHIPS, size+1);

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS, Commodities.RARE_METALS,AoTDCommodities.PURIFIED_TRANSPLUTONICS,AoTDCommodities.REFINED_METAL);
        int maxDeficit = size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);

//		if (market.getId().equals("chicomoztoc")) {
//			System.out.println("efwefwe");
//		}

        if (qualityBonus > 0) {
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(1), qualityBonus, "Orbital works");
        }

        float stability = market.getPrevStability();
        if (stability < 5) {
            float stabilityMod = (stability - 5f) / 5f;
            stabilityMod *= 0.5f;
            //market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), stabilityMod, "Low stability at production source");
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(0), stabilityMod, getNameForModifier() + " - low stability");
        }

        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }
    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STREAMLINED_PRODUCTION,market);

    }
    @Override
    public boolean showWhenUnavailable() {
        return  AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STREAMLINED_PRODUCTION,market);
    }
    @Override
    public String getUnavailableReason() {
        if(!AoDUtilis.checkForItemBeingInstalled(market,Industries.ORBITALWORKS,Items.PRISTINE_NANOFORGE)){
            return "Pristine Nanoforge must be installed on Orbital Works to update";
        }
        return null;
    }
}
