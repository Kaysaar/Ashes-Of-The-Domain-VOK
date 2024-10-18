package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.EveryFrameScript;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;

public class GpManagerAdvance implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        GPManager.getInstance().advanceProductions(amount);
    }
}
