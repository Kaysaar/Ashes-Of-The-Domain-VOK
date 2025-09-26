package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.LightIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.scripts.campaign.econ.conditions.terrain.hyperspace.niko_MPC_hyperspaceLinked;

import java.util.ArrayList;
import java.util.List;

public class BioChem extends LightIndustry {
    @Override
    public List<SpecialItemData> getVisibleInstalledItems() {
        return super.getVisibleInstalledItems();
    }

    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.ORGANICS, size+2);
        demand(Commodities.HEAVY_MACHINERY, size+2);
        //supply(Commodities.SUPPLIES, size - 3);

        //if (!market.getFaction().isIllegal(Commodities.LUXURY_GOODS)) {
        if (!market.isIllegal(Commodities.DRUGS)) {
            supply(Commodities.DRUGS, size+5);
        } else {
            supply(Commodities.DRUGS, 0);
        }
        supply(Commodities.DOMESTIC_GOODS, size-2);
        if(!market.isIllegal("wwlb_cerulean_vapors")&&  Global.getSector().getMemory().is("$aotd_vapors_unlocked", true)){
            if(market.hasCondition("niko_MPC_hyperspaceBipartisan")){
                niko_MPC_hyperspaceLinked linked = (niko_MPC_hyperspaceLinked) this.market.getCondition("niko_MPC_hyperspaceBipartisan").getPlugin();
                if(linked.containedByHyperclouds()){
                    supply("wwlb_cerulean_vapors",size);
                }
            }
        }
        //if (!market.getFaction().isIllegal(Commodities.DRUGS)) {

        Pair<String, Integer> deficit = getMaxDeficit(Commodities.ORGANICS,Commodities.HEAVY_MACHINERY);
        applyDeficitToProduction(2, deficit,
                Commodities.DRUGS);

        if (!isFunctional()) {
            supply.clear();
        }
    }

    @Override
    public String getCurrentImage() {
        return spec.getImageName();
    }

    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DRUGS_AMPLIFICATION,market);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DRUGS_AMPLIFICATION,market);
    }

    @Override
    public String getUnavailableReason() {
        ArrayList<String>reasons = new ArrayList<>();
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.DRUGS_AMPLIFICATION,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.DRUGS_AMPLIFICATION));

        }
        StringBuilder bd = new StringBuilder();
        boolean insert = false;
        for (String reason : reasons) {
            if(insert){
                bd.append("\n");
            }
            bd.append(reason);

            insert = true;
        }

        return bd.toString();
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
