package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

import java.awt.*;

public class IXRadiantConversionProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("radiant_ix").getHullNameWithDashClass()+" vessel.", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public void createRewardSectionForInfo(TooltipMakerAPI tooltip, float width) {
        super.createRewardSectionForInfo(tooltip, width);
    }

    @Override
    public boolean canAttemptStage(String stageID) {
        return stageID.equals("ix_radiant_conversion_stage_1")||getStage("ix_radiant_conversion_stage_1").isCompleted();
    }

    @Override
    public void printAdditionalReqForStage(TooltipMakerAPI tooltip,String stadeId) {
        LabelAPI labelAPI = tooltip.addPara("Must first complete %s",5f, Color.ORANGE,getStage("ix_radiant_conversion_stage_1").getSpec().getName());
        labelAPI.setAlignment(Alignment.MID);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return Global.getSector().getPlayerFaction().getMemory().is("$ix_aqq_radiant", true)&& AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.ORBITAL_ASSEMBLY);
    }

    @Override
    public Object grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(Global.getSettings().getHullSpec("radiant_ix")));
        fleet.getVariant().setSource(VariantSource.REFIT);
        fleet.getVariant().clear();
        return fleet;
    }
}
