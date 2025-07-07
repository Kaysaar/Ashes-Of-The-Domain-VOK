package data.kaysaar.aotd.vok.campaign.econ.growingdemand.models;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.IntervalUtil;


public class GrowingDemandMover implements EveryFrameScript {
    public IntervalUtil util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay()*10,Global.getSector().getClock().getSecondsPerDay()*10);
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
        util.advance(amount);
        if(util.intervalElapsed()){
            GrowingDemandManager.getInstance().advance(util.getElapsed());
        }
    }
}
