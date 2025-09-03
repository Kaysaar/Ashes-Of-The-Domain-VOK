package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.List;

public class PlanetBackgroundTracker implements EveryFrameScript {

    public static boolean ignoreOutpostParam = false;

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
        if (ProductionUtil.getCoreUI()!=null&&(ProductionUtil.getCurrentTab()== null || ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI()) == null)|| (CoreUITabId.OUTPOSTS.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())&&!ignoreOutpostParam)) {

            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
                if(componentAPI instanceof CustomPanelAPI panel){
                    if(panel.getPlugin() instanceof BackgroundInterlooper){
                        if(CoreUITabId.OUTPOSTS.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())) {
                            UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
                            for (UIComponentAPI componentAPIs : ReflectionUtilis.getChildrenCopy(currentTab)) {
                                if (ReflectionUtilis.hasMethodOfName("getOutpostPanelParams", componentAPIs)) {
                                    List<UIComponentAPI> componentAPIS = ReflectionUtilis.getChildrenCopy((UIPanelAPI) componentAPIs);
                                    MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPIS, MarketAPI.class);
                                    if (market != null) {
                                        if (!(market.getPrimaryEntity().getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure )) {
                                           ProductionUtil.getCoreUI().removeComponent(componentAPI);
                                        }

                                    }

                                }
                            }
                        }
                        else {
                            ProductionUtil.getCoreUI().removeComponent(componentAPI);
                        }
                        break;
                    }
                }
            }
            return;

        }

    }
}
