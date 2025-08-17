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
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.BackgroundInterlooper;
import data.kaysaar.aotd.vok.scripts.coreui.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;

public class CoreUiInterceptor implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        if (tab.equals(CoreUITabId.CARGO)) {
            AoTDCompoundUIInMarketScript.didIt = false;
        }
        if (param instanceof String) {
            String s = (String) param;
            if (s.equals("income_report")) {
                CoreUITracker.setMemFlag("income");
            }
        }
        if (param instanceof MarketAPI) {
            CoreUITracker.setMemFlag("colonies");
        }
        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
            if (componentAPI instanceof CustomPanelAPI panel) {
                if (panel.getPlugin() instanceof BackgroundInterlooper looper) {
                    looper.reference = null;
                    ProductionUtil.getCoreUI().removeComponent(panel);
                    break;
                }
            }

            if (tab.equals(CoreUITabId.CARGO) && ProductionUtil.getCoreUI() != null) {
                if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() != null) {
                    if (Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget() instanceof PlanetAPI planetAPI) {
                        if (planetAPI.getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
                            initalizeBackgroundPLanet(megastructure);
                        }
                    }
                } else if (param != null) {
                    if (ReflectionUtilis.findNestedMarketApiFieldFromOutpostParams(param) instanceof MarketAPI market) {
                        if (market.getPrimaryEntity().getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure) {
                            initalizeBackgroundPLanet(megastructure);
                        }
                    }

                }


            }

        }
        CoreUITracker.sendSignalToOpenCore = true;
    }

    private  void initalizeBackgroundPLanet(NidavelirComplexMegastructure megastructure) {
        UIPanelAPI saved = (UIPanelAPI) ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI());
        CampaignPlanet planetSaved = (CampaignPlanet) ReflectionUtilis.invokeMethod("getPlanet", saved);
        BackgroundInterlooper interlooper = new BackgroundInterlooper(megastructure.shipyard.getType(), saved.getPosition().getWidth(), saved.getPosition().getHeight(), planetSaved, saved,megastructure.shipyard.getCurrAngle());
        ProductionUtil.getCoreUI().addComponent(interlooper.getMainPanel()).inBL(0, 0);
        ReflectionUtilis.invokeMethodWithAutoProjection("" +
                "sendToBottomWithinItself", ProductionUtil.getCoreUI(), interlooper.getMainPanel());
        ReflectionUtilis.invokeMethodWithAutoProjection("sendToBottomWithinItself", ProductionUtil.getCoreUI(), saved);
    }
}
