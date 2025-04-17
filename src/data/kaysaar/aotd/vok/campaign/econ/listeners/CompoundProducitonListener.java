package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lazywizard.lazylib.MathUtils;

public class CompoundProducitonListener implements EconomyTickListener {
    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(AoTDMisc.isPLayerHavingIndustry(AoTDIndustries.BLAST_PROCESSING)&& Global.getSector().getMemory().is("$aotd_compound_unlocked",true)){
                int compound = MathUtils.getRandomNumberInRange(400,600);
                playerMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("compound",compound);
            }
        }
    }
}
