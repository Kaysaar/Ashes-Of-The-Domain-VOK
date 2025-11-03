package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.campaign.CampaignPlanet;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.listeners.AoDIndustrialMightListener;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.BackgroundInterlooper;
import data.kaysaar.aotd.vok.scripts.coreui.PlanetBackgroundTracker;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;

public class CoreUiInterceptor implements CoreUITabListener, PlayerColonizationListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if (tab.equals(CoreUITabId.CARGO)) {
            AoTDCompoundUIInMarketScript.didIt = false;
        }
        PlanetBackgroundTracker.ignoreOutpostParam= false;

        IndustrySynergiesManager.getInstance().advanceImpl(0f);
        AoDIndustrialMightListener.applyResourceConditionToAllMarkets();
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
            if (componentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof BackgroundInterlooper looper) {
                    looper.reference = null;
                    ProductionUtil.getCoreUI().removeComponent(panel);
                    break;
                }
            }

            if (tab.equals(CoreUITabId.CARGO) && ProductionUtil.getCoreUI() != null) {
                if (param != null) {
                    if (ReflectionUtilis.findNestedMarketApiFieldFromOutpostParams(param) instanceof MarketAPI market) {
                        if (market.getPrimaryEntity().getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
                            initalizeBackgroundPLanet(megastructure);
                        }
                    }
                } else if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null) {
                    if (Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget() instanceof PlanetAPI planetAPI) {
                        if (planetAPI.getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
                            initalizeBackgroundPLanet(megastructure);
                        }
                    }
                }


            }
            if (tab.equals(CoreUITabId.OUTPOSTS) && ProductionUtil.getCoreUI() != null) {
                if (param instanceof MarketAPI market) {
                    if (market.getPrimaryEntity().getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
                        CoreUiInterceptor.initalizeBackgroundPLanet(megastructure);                    }

                }


            }

        }
    }
    public static void initalizeBackgroundPLanetWithPreSetOpacity(NidavelirComplexMegastructure megastructure,float oppacity) {
        UIPanelAPI saved = (UIPanelAPI) ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI());
        if(saved==null)return;
        CampaignPlanet planetSaved = (CampaignPlanet) ReflectionUtilis.invokeMethod("getPlanet", saved);
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
            if (componentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof BackgroundInterlooper) {
                    return;
                }
            }
        }
        BackgroundInterlooper interlooper = new BackgroundInterlooper(megastructure.shipyard.getType(), saved.getPosition().getWidth(), saved.getPosition().getHeight(), planetSaved, saved, megastructure.shipyard.getCurrAngle());
        interlooper.setMaxOpacity(oppacity);
        ProductionUtil.getCoreUI().addComponent(interlooper.getMainPanel()).inBL(0, 0);
        ReflectionUtilis.invokeMethodWithAutoProjection(
                "sendToBottomWithinItself", ProductionUtil.getCoreUI(), interlooper.getMainPanel());
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", ProductionUtil.getCoreUI(), saved);
    }
    public static void initalizeBackgroundPLanet(NidavelirComplexMegastructure megastructure) {
        UIPanelAPI saved = (UIPanelAPI) ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI());
        if(saved==null)return;
        CampaignPlanet planetSaved = (CampaignPlanet) ReflectionUtilis.invokeMethod("getPlanet", saved);
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
            if (componentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof BackgroundInterlooper) {
                    return;
                }
            }
        }
        BackgroundInterlooper interlooper = new BackgroundInterlooper(megastructure.shipyard.getType(), saved.getPosition().getWidth(), saved.getPosition().getHeight(), planetSaved, saved, megastructure.shipyard.getCurrAngle());
        ProductionUtil.getCoreUI().addComponent(interlooper.getMainPanel()).inBL(0, 0);
        ReflectionUtilis.invokeMethodWithAutoProjection(
                "sendToBottomWithinItself", ProductionUtil.getCoreUI(), interlooper.getMainPanel());
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", ProductionUtil.getCoreUI(), saved);
    }
    public static BackgroundInterlooper initalizeBackgroundPLanetForOtherDialog(NidavelirComplexMegastructure megastructure,UIPanelAPI saved,UIPanelAPI addTo) {
        if(saved==null)return  null;
        CampaignPlanet planetSaved = (CampaignPlanet) ReflectionUtilis.invokeMethod("getPlanet", saved);
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(addTo)) {
            if (componentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof BackgroundInterlooper) {
                    return null;
                }
            }
        }
        BackgroundInterlooper interlooper = new BackgroundInterlooper(megastructure.shipyard.getType(), saved.getPosition().getWidth(), saved.getPosition().getHeight(), planetSaved, saved, megastructure.shipyard.getCurrAngle());
        addTo.addComponent(interlooper.getMainPanel()).inBL(0, 0);
        interlooper.setDialogMode(true);
        ReflectionUtilis.invokeMethodWithAutoProjection(
                "sendToBottomWithinItself", addTo, interlooper.getMainPanel());
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself",addTo, saved);
        ReflectionUtilis.invokeMethodWithAutoProjection(
                "ensureCompRendersAbove", addTo, interlooper.getMainPanel(),ReflectionUtilis.invokeMethodWithAutoProjection("getBg",addTo));
        return interlooper;
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        MarketAPI market = planet.getMarket();
        if (market.getPrimaryEntity().getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
            Global.getSector().addTransientScript(new DelayedActionScript(0.003f) {
                @Override
                public boolean runWhilePaused() {
                    return true;
                }

                @Override
                public void doAction() {
                    initalizeBackgroundPLanet(megastructure);
                }
            });

        }
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {

    }
}
