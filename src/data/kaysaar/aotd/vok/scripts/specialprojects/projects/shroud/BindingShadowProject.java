package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

public class BindingShadowProject extends ShroudBasedProject{
    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;

    }
    @Override
    public void createRewardSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara("Gain Shadowlance, the superweapon based on the designs of Abyssal Glare.", Misc.getPositiveHighlightColor(),5f);

    }

    @Override
    public boolean checkIfProjectShouldUnlock() {   
        return super.checkIfProjectShouldUnlock()&& BlackSiteProjectManager.getInstance().getProject("aotd_tenebrium_weapons").checkIfProjectWasCompleted();
    }

    @Override
    public Object grantReward() {
        MarketAPI  gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
        if(gatheringPoint==null){
            gatheringPoint = Misc.getPlayerMarkets(true).get(0);
        }
        gatheringPoint.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addWeapons("aotd_shadowlance",1);
        return Global.getSettings().getWeaponSpec("aotd_shadowlance");

    }
}
