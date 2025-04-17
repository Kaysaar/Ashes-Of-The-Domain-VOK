package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.ZigguratCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

public class ASSArkoshipProject extends AoTDSpecialProject {
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("ass_arkship").getHullNameWithDashClass()+" vessel.", Misc.getPositiveHighlightColor(), 5f);
    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        GPBaseMegastructure megastructure = GPManager.getInstance().getMegastructure("aotd_nidavelir");
        return (megastructure!=null&&megastructure.getSectionById("nidavelir_nexus").isRestored&&megastructure.getSectionById("nidavelir_eternium").isRestored);
    }

    @Override
    public void grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(Global.getSettings().getHullSpec("ass_arkship")));
        fleet.getVariant().clear();

    }
}
