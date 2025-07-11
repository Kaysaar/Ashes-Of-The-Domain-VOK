package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class GuardianBuildingProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("guardian").getHullNameWithDashClass()+" vessel.", Misc.getPositiveHighlightColor(), 5f);
    }


    @Override
    public boolean checkIfProjectShouldUnlock() {
        return Global.getSector().getPlayerFaction().getMemory().is("$aotd_aqq_guardian", true)&& AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.ORBITAL_ASSEMBLY);
    }

    @Override
    public Object grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        ShipHullSpecAPI spec = Global.getSettings().getHullSpec("guardian");
        ShipVariantAPI member = Global.getSettings().createEmptyVariant(AoTDMisc.getVaraint(spec),spec);
        FleetMemberAPI membesr = Global.getSettings().createFleetMember(FleetMemberType.SHIP,member);
        member.setSource(VariantSource.REFIT);
        member.removePermaMod("automated");
        member.removeMod("automated");
        member.addSuppressedMod("automated");
        member.getHullMods().remove(HullMods.AUTOMATED);
        member.removeTag(Tags.TAG_AUTOMATED_NO_PENALTY);

        membesr.setVariant(member,true,true);
        membesr.updateStats();
        cargo.getMothballedShips().addFleetMember(membesr);
        return membesr;
    }
}
