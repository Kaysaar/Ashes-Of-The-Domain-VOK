package data.kaysaar.aotd.vok.achivements;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
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
        for (GPBaseMegastructure megastructuresBasedOnClass : GPManager.getInstance().getMegastructuresBasedOnClass(HypershuntMegastructure.class)) {
            if(megastructuresBasedOnClass.isFullyRestored()){
                amounts --;
            }
        }
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
