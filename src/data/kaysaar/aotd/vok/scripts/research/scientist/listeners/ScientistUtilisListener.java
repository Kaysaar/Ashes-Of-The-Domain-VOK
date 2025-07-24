package data.kaysaar.aotd.vok.scripts.research.scientist.listeners;

import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;

public class ScientistUtilisListener implements EconomyTickListener {
    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        AoTDMainResearchManager.getInstance().getManagerForPlayer().researchCouncil.forEach(ScientistPerson::endOfMonth);
    }
}
