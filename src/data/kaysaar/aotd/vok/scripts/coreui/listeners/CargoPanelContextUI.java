package data.kaysaar.aotd.vok.scripts.coreui.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

public class CargoPanelContextUI {
    public final UIPanelAPI tradePanel;
    public final MarketAPI market;
    public CargoPanelContextUI(UIPanelAPI tradePanel,MarketAPI market) {
        this.tradePanel = tradePanel;
        this.market = market;
    }
}
