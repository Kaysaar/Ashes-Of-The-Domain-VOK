package data.kaysaar.aotd.vok.achievements;

import com.fs.starfarer.api.Global;
import org.magiclib.achievements.MagicAchievement;

public class AoTDNickelAchievement extends MagicAchievement {

    @Override
    public void onSaveGameLoaded(boolean isComplete) {
        super.onSaveGameLoaded(isComplete);
        if (isComplete) return;
        Global.getSector().getListenerManager().addListener(this, true);
    }

    @Override
    public void advanceAfterInterval(float amount) {
        int amounts =2;
        /// TODO - Implement this later down the line

//        for (GPBaseMegastructure megastructuresBasedOnClass : GPManager.getInstance().getMegastructuresBasedOnClass(HypershuntMegastructure.class)) {
//            if(megastructuresBasedOnClass.isFullyRestored()){
//                amounts --;
//            }
//        }
        if(amounts <= 0){
            completeAchievement();
        }
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Global.getSector().getListenerManager().removeListener(this);
    }
}
