package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.ui.UIComponentAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;

public class CoreCorrectStateEnforcer implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if(GPManager.isEnabled){

        }
        try {

            ArrayList<UIComponentAPI> componentAPIS  = (ArrayList<UIComponentAPI>) ReflectionUtilis.invokeMethod("getChildrenCopy",ReflectionUtilis.invokeMethod("getButtons", ProductionUtil.getCoreUI()));
            for (UIComponentAPI componentAPI : componentAPIS) {
                if(ReflectionUtilis.hasMethodOfName("unhighlight",componentAPI)){
                    ReflectionUtilis.invokeMethod("unhighlight",componentAPI);
                }
            }

        }
        catch (Exception e) {

        }
    }
}
