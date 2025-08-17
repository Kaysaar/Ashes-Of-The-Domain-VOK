package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class PlanetBackgroundTracker implements EveryFrameScript {



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
        if (ProductionUtil.getCoreUI()!=null&&(ProductionUtil.getCurrentTab()== null || ReflectionUtilis.invokeMethod("getPlanetBackground", ProductionUtil.getCoreUI()) == null)|| CoreUITabId.OUTPOSTS.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())) {
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI())) {
                if(componentAPI instanceof CustomPanelAPI panel){
                    if(panel.getPlugin() instanceof BackgroundInterlooper){
                        ProductionUtil.getCoreUI().removeComponent(componentAPI);
                        break;
                    }
                }
            }
            return;

        }

    }
}
