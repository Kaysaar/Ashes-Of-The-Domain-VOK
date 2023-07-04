package data.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class NexerlinColonyStartNerf implements EveryFrameScript {

    //Redundant left so after few versions where this script isremoved it shall also be removed
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    boolean initalized = false;
    List<String> industriesToNerf = new ArrayList<String>();
    protected static final float DAYS_TO_WAIT = 0.1f;
    protected boolean isDone = false;
    protected float timer = 0;

    public void initList() {
        industriesToNerf.add(Industries.FARMING);
        industriesToNerf.add(Industries.MINING);
        industriesToNerf.add(Industries.HEAVYINDUSTRY);
        industriesToNerf.add(Industries.REFINING);
        industriesToNerf.add(Industries.LIGHTINDUSTRY);
        industriesToNerf.add(Industries.AQUACULTURE);
    }

    public boolean iterateUpdatesThroughMarket(MarketAPI marketAPI) {
        initList();
        for (Industry industry : marketAPI.getIndustries()) {
            for (String s : industriesToNerf) {
                if (industry.getId().equals(s)) {
                    if (industry.canDowngrade() && industry.getSpec().getDowngrade() != null) {
                        marketAPI.addIndustry(industry.getSpec().getDowngrade());
                        marketAPI.removeIndustry(industry.getId(), null, false);
                        marketAPI.reapplyIndustries();
                        return false;
                    }
                }
            }

        }
        return true;
    }

    @Override

    public void advance(float amount) {

        if (initalized) return;
        CampaignUIAPI ui = Global.getSector().getCampaignUI();
        if (Global.getSector().isInNewGameAdvance() || ui.isShowingDialog() || Global.getCurrentState() == GameState.TITLE) {
            return;
        }
        timer += Global.getSector().getClock().convertToDays(amount);
        if (timer < DAYS_TO_WAIT) return;
        initalized = true;
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
            if (playerMarket == null) continue;
            while (!iterateUpdatesThroughMarket(playerMarket)) ;
        }


    }

}
