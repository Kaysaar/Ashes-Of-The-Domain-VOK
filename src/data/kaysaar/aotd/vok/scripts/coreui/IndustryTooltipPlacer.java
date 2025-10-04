package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.ColonyUIListener;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;

import java.util.List;


public class IndustryTooltipPlacer implements EveryFrameScript {
    @Override public boolean isDone() { return false; }
    @Override public boolean runWhilePaused() { return true; }

    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().getCurrentCoreTab() == null) return;
        CoreUITabId tab = Global.getSector().getCampaignUI().getCurrentCoreTab();
        if (!(CoreUITabId.CARGO.equals(tab) || CoreUITabId.OUTPOSTS.equals(tab))) return;

        UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
        if (currentTab == null) return;

        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
            if (!ReflectionUtilis.hasMethodOfName("getOutpostPanelParams", componentAPI)) continue;

            List<UIComponentAPI> componentAPIS = ReflectionUtilis.getChildrenCopy((UIPanelAPI) componentAPI);
            UIPanelAPI markets = (UIPanelAPI) componentAPIS.stream()
                    .filter(x -> ReflectionUtilis.hasMethodOfName("showOverview", x))
                    .findFirst().orElse(null);
            if (markets == null) break;
            UIComponentAPI panelOfIndustries = ReflectionUtilis.getChildrenCopy(markets).stream()
                    .filter(x -> ReflectionUtilis.hasMethodOfName("recreateWithEconUpdate", x))
                    .findFirst().orElse(null);
            if (panelOfIndustries == null) break;

            MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPI, MarketAPI.class);
            if (market == null) break;

            UIPanelAPI panelOfOtherInfo = (UIPanelAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) panelOfIndustries)
                    .stream().filter(x -> ReflectionUtilis.hasMethodOfName("getImmigration", x))
                    .findFirst().orElse(null);
            if (panelOfOtherInfo == null) break;

            boolean grandColoniesLayout = false;
            CustomPanelAPI gcSizeMatch = (CustomPanelAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) panelOfIndustries)
                    .stream().filter(x -> x instanceof CustomPanelAPI && x.getPosition().getWidth() == 830 && x.getPosition().getHeight() == 400)
                    .findFirst().orElse(null);
            if (gcSizeMatch != null) grandColoniesLayout = true;

            ColonyUIListener.notifyMarketOverview(new IndustryPanelContextUI(
                    market, panelOfOtherInfo, (UIPanelAPI) panelOfIndustries, grandColoniesLayout
            ));
            ColonyUIListener.notifyMarketOverview(new CargoPanelContextUI((UIPanelAPI) componentAPI,market));

            break;
        }
    }
}
