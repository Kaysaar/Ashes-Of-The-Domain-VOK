package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.synergies.ui.SynergyInfoMarket;
import data.kaysaar.aotd.vok.campaign.econ.synergies.ui.TrainUIRenderer;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class IndustryTooltipInserter implements EveryFrameScript {
    public static boolean didIt = false;
    transient UIPanelAPI marketWidget;
    transient MarketAPI currentMarket;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if (Global.getSector().getCampaignUI().getCurrentCoreTab() == null) {
            didIt = false;
            return;
        }

        if (CoreUITabId.CARGO.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())) {
            UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
                if (ReflectionUtilis.hasMethodOfName("getOutpostPanelParams", componentAPI)) {
                    List<UIComponentAPI> componentAPIS = ReflectionUtilis.getChildrenCopy((UIPanelAPI) componentAPI);
                    UIPanelAPI marketWidget = (UIPanelAPI) componentAPIS.get(1);
                    UIPanelAPI markets = (UIPanelAPI) componentAPIS.stream().filter(x -> ReflectionUtilis.hasMethodOfName("showOverview", x)).findFirst().orElse(null);
                    if (markets != null) {
                        UIComponentAPI panelOfIndustries = ReflectionUtilis.getChildrenCopy(markets).stream().filter(x -> ReflectionUtilis.hasMethodOfName("recreateWithEconUpdate", x)).findFirst().orElse(null);
                        if (panelOfIndustries != null) {
                            MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPI, MarketAPI.class);
                            UIPanelAPI panelOfOtherInfo = (UIPanelAPI) ReflectionUtilis.getChildrenCopy((UIPanelAPI) panelOfIndustries).stream().filter(x -> ReflectionUtilis.hasMethodOfName("getImmigration", x)).findFirst().orElse(null);
                            if (panelOfOtherInfo != null) {
                                boolean found = false;
                                for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(panelOfOtherInfo)) {
                                    if (uiComponentAPI instanceof CustomPanelAPI panel) {
                                        if (panel.getPlugin() instanceof SynergyInfoMarket) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }
                                if (!found) {
                                    UIPanelAPI another = (UIPanelAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getImmigration", panelOfOtherInfo);
                                    IndustrySynergiesManager.getInstance().advanceImpl(0f);
                                    SynergyInfoMarket marketS = new SynergyInfoMarket(market);
                                    panelOfOtherInfo.addComponent(marketS.getMainPanel()).rightOfTop(another, 20);
                                }
                            }
                            if (!market.hasIndustry("aotd_maglev")) return;
                            if (market.getIndustry("aotd_maglev").isBuilding()) return;
                            if (market.getIndustry("aotd_maglev").isHidden()) return;
                            ArrayList<BaseIndustrySynergy> synergiesPresent = new ArrayList<>(IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarketInUI(market));
                            if (synergiesPresent.isEmpty()) return;
                            if (Global.getSettings().getModManager().isModEnabled("GrandColonies")) {
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
                                        new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, contentInside, centerOfRows, synergiesPresent);

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
                                        new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, contentInside, centerOfRows, synergiesPresent);
                                    }


                                }

                            } else {
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
                                new TrainUIRenderer((UIPanelAPI) mainWidget, widgetsToDraw, panelAPI, centerOfRows, synergiesPresent);
                            }

                        }
                    }


                    break;
                }
            }

        }
    }
}
