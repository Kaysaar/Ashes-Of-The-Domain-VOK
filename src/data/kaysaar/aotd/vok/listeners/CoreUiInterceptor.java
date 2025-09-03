package data.kaysaar.aotd.vok.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignPlanet;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.listeners.AoDIndustrialMightListener;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.BackgroundInterlooper;
import data.kaysaar.aotd.vok.scripts.coreui.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.coreui.PlanetBackgroundTracker;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if (tab.equals(CoreUITabId.CARGO)) {
            AoTDCompoundUIInMarketScript.didIt = false;
        }
        PlanetBackgroundTracker.ignoreOutpostParam= false;
        if (param instanceof String) {
            String s = (String) param;
            if (s.equals("income_report")) {
                CoreUITracker.setMemFlag("income");
            }
        }
        if (param instanceof MarketAPI) {
            CoreUITracker.setMemFlag("colonies");
        }
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
        CoreUITracker.sendSignalToOpenCore = true;
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
        ReflectionUtilis.invokeMethodWithAutoProjection("" +
                "sendToBottomWithinItself", ProductionUtil.getCoreUI(), interlooper.getMainPanel());
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", ProductionUtil.getCoreUI(), saved);
    }
}
