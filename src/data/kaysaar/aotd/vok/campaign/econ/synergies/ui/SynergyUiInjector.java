package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketUIListener;

import java.util.*;

public class SynergyUiInjector implements MarketUIListener {

    @Override
    public void onMarketOverviewDiscovered(IndustryPanelContextUI ctx) {
        MarketAPI market = ctx.market;
        UIPanelAPI panelOfOtherInfo = ctx.panelOfOtherInfo;
        UIPanelAPI panelOfIndustries = ctx.mainColonyPanel;

        // Add the compact synergy summary next to Immigration
        ensureSynergyInfoPanel(panelOfOtherInfo, market);

        // Only add train UI if maglev is active and synergies exist
        if (!market.hasIndustry("aotd_maglev")) return;
        if (market.getIndustry("aotd_maglev").isBuilding()) return;
        if (market.getIndustry("aotd_maglev").isHidden()) return;

        ArrayList<BaseIndustrySynergy> synergiesPresent = new ArrayList<>(
                IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarketInUI(market));
        if (synergiesPresent.isEmpty()) return;

        if (ctx.grandColoniesLayout) injectForGrandColonies(panelOfIndustries, synergiesPresent, market);
        else injectForVanilla(panelOfIndustries, synergiesPresent, market);
    }

    @Override
    public void onSubmarketCargoCreated(CargoPanelContextUI ctx) {

    }

    private void ensureSynergyInfoPanel(UIPanelAPI panelOfOtherInfo, MarketAPI market) {
        for (UIComponentAPI ui : ReflectionUtilis.getChildrenCopy(panelOfOtherInfo)) {
            if (ui instanceof CustomPanelAPI panel && panel.getPlugin() instanceof SynergyInfoMarket) return;
        }
        UIPanelAPI immigrationWidget = (UIPanelAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getImmigration", panelOfOtherInfo);
        IndustrySynergiesManager.getInstance().advanceImpl(0f);
        SynergyInfoMarket marketS = new SynergyInfoMarket(market);
        panelOfOtherInfo.addComponent(marketS.getMainPanel()).rightOfTop(immigrationWidget, 20);
    }

    private void injectForGrandColonies(UIPanelAPI panelOfIndustries, ArrayList<BaseIndustrySynergy> synergiesPresent, MarketAPI market) {
        CustomPanelAPI panelAPI = (CustomPanelAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) panelOfIndustries).stream().filter(x -> x instanceof CustomPanelAPI && x.getPosition().getWidth() == 830 && x.getPosition().getHeight() == 400).findFirst().orElse(null);
        if (panelAPI != null) {
            UIPanelAPI panelInsider = (UIPanelAPI) ReflectionUtilis.getChildrenCopy(panelAPI).get(0);
            if (ReflectionUtilis.hasMethodOfName("getContentContainer", panelInsider)) {
                Object container = ReflectionUtilis.invokeMethodWithAutoProjection("getContentContainer", panelInsider);
                TooltipMakerAPI tooltip = (TooltipMakerAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) container).get(0);
                UIPanelAPI contentInside = (UIPanelAPI) ReflectionUtilis.getChildrenCopy(tooltip).get(0);
                ArrayList<CustomPanelAPI> insiders = new ArrayList<>();
                for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(contentInside)) {
                    if (uiComponentAPI instanceof CustomPanelAPI panel) {
                        if (panel.getPlugin() instanceof TrainUIRenderer) {
                            return;
                        }
                        insiders.add(panel);
                    }
                }
                insiders.size();
                HashMap<String, UIComponentAPI> widgetsToDraw = new HashMap<>();
                UIComponentAPI mainWidget = null;
                for (CustomPanelAPI insider : insiders) {
                    UIComponentAPI widget = ReflectionUtilis.getChildrenCopy(insider).get(0);
                    Industry ind = (Industry) ReflectionUtilis.findFieldOfClass(widget, Industry.class);
                    String id = ind.getId();
                    if (id.equals("aotd_maglev")) {
                        mainWidget = widget;
                    } else {
                        if (synergiesPresent.stream().anyMatch(x -> x.getIndustriesForSynergy(market).contains(id))) {
                            widgetsToDraw.put(id, widget);
                        }
                    }

                }
                CustomPanelAPI centerOfRows = Global.getSettings().createCustom(1, 1, null);
                contentInside.addComponent(centerOfRows).inTL(413, 107);
                new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, contentInside, centerOfRows, synergiesPresent,market);

            } else {
                UIPanelAPI contentInside = (UIPanelAPI) ReflectionUtilis.getChildrenCopy(panelInsider).get(0);
                ArrayList<CustomPanelAPI> insiders = new ArrayList<>();
                for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(contentInside)) {
                    if (uiComponentAPI instanceof CustomPanelAPI panel) {
                        if (panel.getPlugin() instanceof TrainUIRenderer) {
                            return;
                        }
                        insiders.add(panel);
                    }
                }
                insiders.size();
                HashMap<String, UIComponentAPI> widgetsToDraw = new HashMap<>();
                UIComponentAPI mainWidget = null;
                for (CustomPanelAPI insider : insiders) {
                    UIComponentAPI widget = ReflectionUtilis.getChildrenCopy(insider).get(0);
                    Industry ind = (Industry) ReflectionUtilis.findFieldOfClass(widget, Industry.class);
                    String id = ind.getId();
                    if (id.equals("aotd_maglev")) {
                        mainWidget = widget;
                    } else {
                        if (synergiesPresent.stream().anyMatch(x -> x.getIndustriesForSynergy(market).contains(id))) {
                            widgetsToDraw.put(id, widget);
                        }
                    }
                }
                CustomPanelAPI centerOfRows = Global.getSettings().createCustom(1, 1, null);
                contentInside.addComponent(centerOfRows).inTL(413, 107);
                new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, contentInside, centerOfRows, synergiesPresent,market);
            }


        }
    }

    private void injectForVanilla(UIPanelAPI panelOfIndustries, ArrayList<BaseIndustrySynergy> synergiesPresent, MarketAPI market) {
        UIPanelAPI panelAPI = (UIPanelAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) panelOfIndustries).stream().filter(x -> ReflectionUtilis.hasMethodOfName("getWidgets", x)).findFirst().orElse(null);
        for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(panelAPI)) {
            if (uiComponentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof TrainUIRenderer) {
                    return;
                }
            }
        }
        ArrayList<UIPanelAPI> widgets = new ArrayList<>((Collection) ReflectionUtilis.invokeMethodWithAutoProjection("getWidgets", panelAPI));
        HashMap<String, UIComponentAPI> widgetsToDraw = new HashMap<>();
        UIComponentAPI mainWidget = null;
        for (UIPanelAPI widget : widgets) {
            Industry ind = (Industry) ReflectionUtilis.findFieldOfClass(widget, Industry.class);
            String id = ind.getId();
            if (id.equals("aotd_maglev")) {
                mainWidget = widget;
            } else {
                if (synergiesPresent.stream().anyMatch(x -> x.getIndustriesForSynergy(market).contains(id))) {
                    widgetsToDraw.put(id, widget);
                }
            }
        }
        CustomPanelAPI centerOfRows = Global.getSettings().createCustom(1, 1, null);
        panelAPI.addComponent(centerOfRows).inTL(408, 107);
        new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, panelAPI, centerOfRows, synergiesPresent,market);
    }

    private static UIComponentAPI getUiComponentAPI(ArrayList<BaseIndustrySynergy> synergiesPresent, MarketAPI market, UIPanelAPI widget, UIComponentAPI mainWidget, LinkedHashMap<String, UIComponentAPI> widgetsToDraw) {
        Industry ind = (Industry) ReflectionUtilis.findFieldOfClass(widget, Industry.class);
        if (ind == null) return mainWidget;
        String id = ind.getId();
        if ("aotd_maglev".equals(id)) mainWidget = widget;
        else if (synergiesPresent.stream().anyMatch(x -> x.getIndustriesForSynergy(market).contains(id))) widgetsToDraw.put(id, widget);
        return mainWidget;
    }

    private boolean containsTrainRenderer(UIPanelAPI container) {
        for (UIComponentAPI ui : ReflectionUtilis.getChildrenCopy(container)) {
            if (ui instanceof CustomPanelAPI p && p.getPlugin() instanceof TrainUIRenderer) return true;
        }
        return false;
    }

    private UIComponentAPI collectWidgets(UIPanelAPI contentInside, MarketAPI market,
                                          ArrayList<BaseIndustrySynergy> synergiesPresent,
                                          Map<String, UIComponentAPI> widgetsToDraw) {
        UIComponentAPI mainWidget = null;
        for (UIComponentAPI ui : ReflectionUtilis.getChildrenCopy(contentInside)) {
            if (!(ui instanceof CustomPanelAPI)) continue;
            UIComponentAPI widget = ReflectionUtilis.getChildrenCopy((UIPanelAPI) ui).get(0);
            Industry ind = (Industry) ReflectionUtilis.findFieldOfClass(widget, Industry.class);
            if (ind == null) continue;
            String id = ind.getId();
            if ("aotd_maglev".equals(id)) mainWidget = widget;
            else if (synergiesPresent.stream().anyMatch(x -> x.getIndustriesForSynergy(market).contains(id))) widgetsToDraw.put(id, widget);
        }
        return mainWidget;
    }
}
