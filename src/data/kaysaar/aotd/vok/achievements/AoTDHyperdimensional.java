package data.kaysaar.aotd.vok.achievements;

import com.fs.starfarer.api.Global;
import org.magiclib.achievements.MagicAchievement;

public class AoTDHyperdimensional extends MagicAchievement {
    @Override
    public void onSaveGameLoaded(boolean isComplete) {
        super.onSaveGameLoaded(isComplete);
        if (isComplete) return;
        Global.getSector().getListenerManager().addListener(this, true);
    }



    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Global.getSector().getListenerManager().removeListener(this);
    }
}
