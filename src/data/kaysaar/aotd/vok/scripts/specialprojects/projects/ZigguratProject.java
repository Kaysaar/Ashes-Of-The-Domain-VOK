package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class ZigguratProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Install High Frequency Attractor hullmod", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {

        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (member.getHullSpec().getBaseHullId().equals("ziggurat")) {
                return AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.ORBITAL_ASSEMBLY);
            }
        }
        return false;
    }

    @Override
    public void grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if (gatheringPoint == null) {
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(Global.getSettings().getHullSpec("ZigguratProject")));
        fleet.getVariant().clear();
        fleet.getVariant().addPermaMod(HullMods.HIGH_FREQUENCY_ATTRACTOR);


    }

}
