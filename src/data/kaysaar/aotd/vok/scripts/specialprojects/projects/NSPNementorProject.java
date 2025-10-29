package data.kaysaar.aotd.vok.scripts.specialprojects.projects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;

import java.awt.*;

public class NSPNementorProject extends AoTDSpecialProject {
    @Override
    public boolean checkIfProjectShouldUnlock() {
        return BlackSiteProjectManager.getInstance().getProject("aotd_guardian").checkIfProjectWasCompleted()&& AoTDMisc.isPLayerHavingIndustry(AoTDIndustries.ORBITAL_SKUNKWORK)&& Global.getSector().getPlayerFaction().getMemory().is("$aotd_aqq_nemetor", true);
    }

    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain %s vessel",5f, Color.ORANGE,Global.getSettings().getHullSpec("nsp_nemetor").getHullNameWithDashClass());
    }

    @Override
    public Object grantReward() {
        MarketAPI gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        CargoAPI cargo = gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
        ShipHullSpecAPI spec = Global.getSettings().getHullSpec("nsp_nemetor");
        ShipVariantAPI member = Global.getSettings().createEmptyVariant(AoTDMisc.getVaraint(spec),spec);
        FleetMemberAPI membesr = Global.getSettings().createFleetMember(FleetMemberType.SHIP,member);
        member.setSource(VariantSource.REFIT);
        member.addTag(Tags.TAG_AUTOMATED_NO_PENALTY);

        membesr.setVariant(member,true,true);
        membesr.updateStats();
        cargo.getMothballedShips().addFleetMember(membesr);
        return membesr;
    }

}
