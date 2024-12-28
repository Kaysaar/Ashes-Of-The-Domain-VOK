package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.ui.marketinfo.IndustryPickerDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.ResourceAllocationDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.List;

public class CoreUITracker2 extends CoreUITracker{
    boolean didIt = false;
    @Override
    public void advance(float amount) {
        if(Global.getSector().getCampaignUI().getCurrentCoreTab()==null){
            didIt = false;
            return;
        }
        if (!didIt){
            List<UIComponentAPI> panels = ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI());
            if(panels.get(panels.size()-1) instanceof IndustryPickerDialog){
                IndustryPickerDialog dialog = (IndustryPickerDialog) panels.get(panels.size()-1);

                dialog.dismiss(1);
                didIt = true;
                BasePopUpDialog dialog2 = new MarketDialog(null, (MarketAPI) ReflectionUtilis.get("market",ReflectionUtilis.invokeMethod("getDelegate",dialog)),ReflectionUtilis.invokeMethod("getOverview",ReflectionUtilis.invokeMethod("getDelegate",dialog)));
                CustomPanelAPI panelAPI = Global.getSettings().createCustom(1100,700,dialog2);
                UIPanelAPI panelAPI1  = ProductionUtil.getCoreUI();
                dialog2.init(panelAPI,panelAPI1.getPosition().getCenterX()-(panelAPI.getPosition().getWidth()/2),panelAPI1.getPosition().getCenterY()+(panelAPI.getPosition().getHeight()/2),true);

            }

        }
    }

}
