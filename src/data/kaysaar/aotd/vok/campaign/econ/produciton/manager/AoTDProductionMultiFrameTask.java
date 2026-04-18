package data.kaysaar.aotd.vok.campaign.econ.produciton.manager;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.campaign.econ.contract.iter.MultiFrameTask;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;

import java.util.List;

public class AoTDProductionMultiFrameTask extends MultiFrameTask {
    public List<AoTDProductionOrderSnapshot> snapshots;
    int currIndex = 0;
    public AoTDProductionMultiFrameTask(List<AoTDProductionOrderSnapshot>snapshots){
        this.snapshots = snapshots;
    }
    @Override
    public void doNextBatch() {
        if(!this.isDone()){
            AoTDProductionOrderSnapshot snapshot = snapshots.get(currIndex);
            snapshot.productionData.forEach(AoTDProductionOrderData::consumeSpecialItemsIfNeeded);
            snapshot.advance(Global.getSector().getClock().getSecondsPerDay());
            currIndex++;
        }
    }

    @Override
    public boolean isDone() {
        return this.currIndex>=snapshots.size();
    }

    @Override
    public String getLoggingIdentifier() {
        return "";
    }
}
