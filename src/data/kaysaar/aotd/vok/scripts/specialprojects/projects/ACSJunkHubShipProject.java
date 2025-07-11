package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class ACSJunkHubShipProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("acs_junkhubship_module").getHullNameWithDashClass()+" vessel.", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return Global.getSector().getPlayerFaction().getMemory().is("$acs_aqq_junkhubship", true)&& AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.ORBITAL_ASSEMBLY);
    }

    @Override
    public Object grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(Global.getSettings().getHullSpec("acs_junkhubship_module")));
        fleet.getVariant().setSource(VariantSource.REFIT);
        fleet.getVariant().clear();
        return fleet;


    }

}
