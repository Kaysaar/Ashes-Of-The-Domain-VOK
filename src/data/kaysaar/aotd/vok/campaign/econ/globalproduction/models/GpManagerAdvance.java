package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.EveryFrameScript;

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
        GPManager.getInstance().advance(amount);
    }
}
