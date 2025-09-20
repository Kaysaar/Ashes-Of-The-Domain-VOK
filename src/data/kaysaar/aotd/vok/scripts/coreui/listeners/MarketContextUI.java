package data.kaysaar.aotd.vok.scripts.coreui.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;


public final class MarketContextUI {
    public final UIPanelAPI panelOfOtherInfo;
    public final UIPanelAPI mainColonyPanel;
    public final MarketAPI market;
    public final boolean grandColoniesLayout;

    public MarketContextUI(MarketAPI market, UIPanelAPI panelOfOtherInfo, UIPanelAPI mainColonyPanel, boolean grandColoniesLayout) {
        this.market = market;
        this.panelOfOtherInfo = panelOfOtherInfo;
        this.mainColonyPanel = mainColonyPanel;
        this.grandColoniesLayout = grandColoniesLayout;
    }
}
