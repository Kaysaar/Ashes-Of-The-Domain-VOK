package data.kaysaar.aotd.vok.achivements;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.magiclib.achievements.MagicAchievement;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class AoTDBaseBuildingAchivement extends MagicAchievement {
    public LinkedHashMap<String,Boolean>mapOfProgress = new LinkedHashMap<>();
    @Override
    public void onSaveGameLoaded(boolean isComplete) {
        super.onSaveGameLoaded(isComplete);
        if (isComplete) return;
        Global.getSector().getListenerManager().addListener(this, true);
    }

    @Override
    public void advanceAfterInterval(float amount) {
        for (String s : mapOfProgress.keySet()) {
            for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
                if(playerMarket.hasIndustry(s)){
                    mapOfProgress.put(s,true);
                }
            }
        }
        for (Boolean value : mapOfProgress.values()) {
            if(!value){
                return;
            }
        }
        completeAchievement();

    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Global.getSector().getListenerManager().removeListener(this);
    }

}
