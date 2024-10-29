package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.SMSpecialItem;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StellaManufactorium extends HeavyIndustry {
    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STELLA_MANUFACTORIUM,market) &&super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STELLA_MANUFACTORIUM,market);
    }

    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.METALS, size+2);
        demand(Commodities.RARE_METALS, size);
        demand(AoTDCommodities.REFINED_METAL, size+2);
        demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS, size + 1);
        supply(Commodities.HEAVY_MACHINERY, size+2);
        supply(Commodities.SUPPLIES, size+4);
        supply("domain_heavy_machinery", size);
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS,Commodities.RARE_ORE);
        Pair<String, Integer> deficit2 = getMaxDeficit(AoTDCommodities.REFINED_METAL,AoTDCommodities.PURIFIED_TRANSPLUTONICS);

        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);
        applyDeficitToProduction(3, deficit2,"domain_heavy_machinery"
        );

//		if (market.getId().equals("chicomoztoc")) {
//			System.out.println("efwefwe");
//		}



        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

    }











    @Override
    public boolean canInstallAICores() {
        return true;
    }



}
