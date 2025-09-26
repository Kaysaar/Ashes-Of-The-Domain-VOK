package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.SharedUnlockData;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.scripts.campaign.econ.conditions.terrain.hyperspace.niko_MPC_hyperspaceLinked;

public class HyperspaceDrugs extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain access to new commodity : Cerulean Vapors", Misc.getPositiveHighlightColor(), 5f);
        tooltip.addPara("This drug can only be produced by Neurochemical Laboratory on hyperspace bipartisan worlds enveloped by hyperspace clouds. Planetary shields also block the effect.", Misc.getPositiveHighlightColor(), 5f);
        tooltip.addPara("Note : Demand for this drug will grow with time, as it needs time to spread through market.", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.DRUGS_AMPLIFICATION) && Misc.getPlayerMarkets(true).stream().filter(x -> x.hasCondition("niko_MPC_hyperspaceBipartisan") && x.getSize() >= 4).anyMatch(x -> {
            niko_MPC_hyperspaceLinked linked = (niko_MPC_hyperspaceLinked) x.getCondition("niko_MPC_hyperspaceBipartisan").getPlugin();
            return linked.containedByHyperclouds();
        });
    }

    @Override
    public Object grantReward() {
        Global.getSector().getMemory().set("$aotd_vapors_unlocked", true);
        SharedUnlockData.get().reportPlayerAwareOfCommodity("wwlb_cerulean_vapors", true);
        Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("wwlb_cerulean_vapors",250);
        return null;
    }
}
