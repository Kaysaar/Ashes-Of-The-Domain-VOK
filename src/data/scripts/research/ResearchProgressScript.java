package data.scripts.research;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import data.plugins.AoDUtilis;

import static data.plugins.AoDCoreModPlugin.aodTech;

public class ResearchProgressScript implements EveryFrameScript {

    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    public boolean firstTick = true;
    public int lastDayChecked = 0;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }


    public void advance(float amount) {
        researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
        if (newDay()) {
            if (researchAPI.isResearching()) {
                ResearchOption currResearch = researchAPI.getCurrentResearching();
                currResearch.currentResearchDays -= AoDUtilis.researchBonusCurrent();
                if (currResearch.currentResearchDays <= 0) {
                    researchAPI.finishResearch();
                }
            }
        }

    }

    private boolean newDay() { //New day check, stolen from VIC mod
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstTick) {
            lastDayChecked = clock.getDay();
            firstTick = false;
            return false;
        } else if (clock.getDay() != lastDayChecked) {
            lastDayChecked = clock.getDay();
            return true;
        }
        return false;
    }
}
