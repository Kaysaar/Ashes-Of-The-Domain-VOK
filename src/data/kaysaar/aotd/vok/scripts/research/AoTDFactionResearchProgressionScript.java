package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.EveryFrameScript;

public class AoTDFactionResearchProgressionScript implements EveryFrameScript {
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
        AoTDMainResearchManager.getInstance().advance(amount);

    }
}
