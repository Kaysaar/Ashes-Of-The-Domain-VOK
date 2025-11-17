package data.kaysaar.aotd.vok.achievements;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import org.magiclib.achievements.MagicAchievement;

import java.util.ArrayList;

public class AoTDBaseMegastructureAchievement extends MagicAchievement {
    ArrayList<Class<?>>classes = new ArrayList<>();

    @Override
    public void onSaveGameLoaded(boolean isComplete) {
        super.onSaveGameLoaded(isComplete);
        if (isComplete) return;
        Global.getSector().getListenerManager().addListener(this, true);
    }

    @Override
    public void advanceAfterInterval(float amount) {
        if(!classes.isEmpty()){
            boolean allCompleted  = true;
            for (Class<?> aClass : classes) {
                boolean classCompleted = false;
                for (GPBaseMegastructure megastructuresBasedOnClass : GPManager.getInstance().getMegastructuresBasedOnClass(aClass)) {
                    if(megastructuresBasedOnClass.isFullyRestored()){
                      classCompleted = true;
                      break;
                    }
                }
                if(!classCompleted){
                    allCompleted = false;
                    break;
                }
            }
            if(allCompleted){
                completeAchievement();
            }

        }
        else{
            for (GPBaseMegastructure megastructuresBasedOnClass : GPManager.getInstance().getMegastructures()) {
                if(megastructuresBasedOnClass.isFullyRestored()){
                    completeAchievement();
                }
            }
        }


    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Global.getSector().getListenerManager().removeListener(this);
    }
}
