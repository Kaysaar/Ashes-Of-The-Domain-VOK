package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlayerMarketTransaction;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.CargoScreenListener;

public class SubmarketOpenedListener implements CargoScreenListener {
    public static String subMarketIDLastlySaved = null;
    @Override
    public void reportCargoScreenOpened() {
        subMarketIDLastlySaved = null;
    }

    @Override
    public void reportPlayerLeftCargoPods(SectorEntityToken entity) {

    }

    @Override
    public void reportPlayerNonMarketTransaction(PlayerMarketTransaction transaction, InteractionDialogAPI dialog) {

    }

    @Override
    public void reportSubmarketOpened(SubmarketAPI submarket) {
        subMarketIDLastlySaved = submarket.getSpecId();
    }
}
