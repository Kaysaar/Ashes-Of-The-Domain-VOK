package data.kaysaar.aotd.vok.campaign.econ.produciton.manager;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.IntervalUtil;

public class AoTDProductionMover implements EveryFrameScript {
    public boolean executeTaskFirst = false;
    public AoTDProductionMultiFrameTask task;
    public IntervalUtil util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay(),Global.getSector().getClock().getSecondsPerDay());
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
        if(executeTaskFirst){
            task.doNextBatch();
            if(task.isDone()){
                task = null;
                AoTDProductionManager.getInstance().getCurrentSnapshots().removeIf(x->x.productionData.isEmpty());
                executeTaskFirst = false;
            }
        }
        else{
            util.advance(amount);
            if(util.intervalElapsed()){
                executeTaskFirst = true;
                task = new AoTDProductionMultiFrameTask(AoTDProductionManager.getInstance().getCurrentSnapshots());
            }
        }

    }
}