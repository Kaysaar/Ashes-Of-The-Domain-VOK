package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.util.IntervalUtil;

public class AoTDFactionResearchProgressionScript implements EveryFrameScript {
    IntervalUtil util = new IntervalUtil(0.5f, 0.5f);

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
        if (util == null) util = new IntervalUtil(0.5f, 0.5f);
        util.advance(amount);
        if (util.intervalElapsed()) {
            AoTDMainResearchManager.getInstance().advance(util.getElapsed());
        }


    }
}
