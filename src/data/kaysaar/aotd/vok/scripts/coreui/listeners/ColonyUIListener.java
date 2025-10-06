package data.kaysaar.aotd.vok.scripts.coreui.listeners;

import com.fs.starfarer.api.Global;

import java.util.ArrayList;
import java.util.List;

public final class ColonyUIListener {
    private static final List<MarketUIListener> MARKET_UI_LISTENERS = new ArrayList<>();

    private ColonyUIListener() {}

    public static void addMarketListener(MarketUIListener l) {
        if (l != null && !MARKET_UI_LISTENERS.contains(l)) MARKET_UI_LISTENERS.add(l);
    }

    public static void removeMarketListener(MarketUIListener l) {
        MARKET_UI_LISTENERS.remove(l);
    }

    public static void notifyMarketOverview(IndustryPanelContextUI ctx) {
        for (MarketUIListener l : MARKET_UI_LISTENERS) {
            try {
                l.onMarketOverviewDiscovered(ctx);
            } catch (Throwable ignored) {

            }
        }
    }
    public static void notifySurveyPanelOverview(SurveyPanelContextUI ctx) {
        for (MarketUIListener l : MARKET_UI_LISTENERS) {
            try {
                l.onSurveyPanelCreated(ctx);
            } catch (Throwable ignored) {

            }
        }
    }
    public static void notifyMarketOverview(CargoPanelContextUI ctx) {
        for (MarketUIListener l : MARKET_UI_LISTENERS) {
            try {
                l.onSubmarketCargoCreated(ctx);
            } catch (Throwable ignored) {

            }
        }
    }
    /** Re-register listeners on game load. */
    public static void refresh() {
        MARKET_UI_LISTENERS.clear();
        Global.getSettings().getModManager().getEnabledModPlugins().stream().filter(x->x instanceof MarketContextListenerInjector).forEach(x->{
            ((MarketContextListenerInjector) x).reloadListenerContext();
        });
    }
}
