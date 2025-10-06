package data.kaysaar.aotd.vok.scripts.coreui.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;

public class SurveyPanelContextUI {
    public final UIPanelAPI surveyPanel;
    public final MarketAPI market;
    public SurveyPanelContextUI(UIPanelAPI surveyPanel,MarketAPI market) {
        this.surveyPanel = surveyPanel;
        this.market = market;
    }
}
