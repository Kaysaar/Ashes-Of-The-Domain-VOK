package data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignState;
import com.fs.state.AppDriver;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class ProductionUtil {
    public static UIPanelAPI getCoreUI() {
        CampaignUIAPI campaignUI;
        campaignUI = Global.getSector().getCampaignUI();
        Object dialog = campaignUI.getCurrentInteractionDialog();
        if(AppDriver.getInstance().getCurrentState() instanceof CampaignState){
            dialog = ReflectionUtilis.invokeMethod("getEncounterDialog", AppDriver.getInstance().getCurrentState());
        }

        CoreUIAPI core;
        if (dialog == null) {
            core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCore",campaignUI);
        }
        else {
            core = (CoreUIAPI) ReflectionUtilis.invokeMethod( "getCoreUI",dialog);
        }
        return core == null ? null : (UIPanelAPI) core;
    }
    public static UIPanelAPI getCurrentTab() {
        UIPanelAPI coreUltimate = getCoreUI();
        if(getCoreUI()==null) {
            return null;
        }
        UIPanelAPI core = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab",coreUltimate);
        return core == null ? null : (UIPanelAPI) core;
    }


}
