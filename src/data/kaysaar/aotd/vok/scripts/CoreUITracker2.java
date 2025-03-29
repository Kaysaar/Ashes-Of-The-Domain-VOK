package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.ui.marketinfo.IndustryPickerDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.buildingmenu.MarketDialog;
import data.kaysaar.aotd.vok.ui.patrolfleet.FleetMarketInfo;
import org.lwjgl.util.vector.Vector2f;

import java.util.*;

public class CoreUITracker2 extends CoreUITracker{
    public static boolean didIt = false;
    transient UIPanelAPI marketWidget;
    transient MarketAPI currentMarket;

    @Override
    public void advance(float amount) {
        if(Global.getSector().getCampaignUI().getCurrentCoreTab()==null){
            return;
        }
        if (!didIt){
            if(CoreUITabId.CARGO.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())){
                UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
                    if(ReflectionUtilis.hasMethodOfName("getOutpostPanelParams",componentAPI)){
                        List<UIComponentAPI>componentAPIS = ReflectionUtilis.getChildrenCopy((UIPanelAPI) componentAPI);
                        UIPanelAPI marketWidget = (UIPanelAPI) componentAPIS.get(1);
                        MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPI,MarketAPI.class);
                        if(market.getFaction()==null)return;
                        float relativeX = marketWidget.getPosition().getX();
                        if(ReflectionUtilis.getChildrenCopy(marketWidget).size()<3)return;
                        LinkedHashMap<UIComponentAPI, Vector2f> cords = new LinkedHashMap<>();
                        for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(marketWidget)) {
                            float x = uiComponentAPI.getPosition().getX();
                            float relX = x - relativeX;
                            cords.put(uiComponentAPI,new Vector2f(relX,0));

                        }
                        for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(marketWidget)) {
                           marketWidget.removeComponent(uiComponentAPI);
                        }
                        for (Map.Entry<UIComponentAPI, Vector2f> entry : cords.entrySet()) {
                            marketWidget.addComponent(entry.getKey()).inTL(entry.getValue().x,0);
                        }
                        Vector2f cord = cords.get(ReflectionUtilis.getChildrenCopy(marketWidget).get(2));
                        cords.clear();
                        marketWidget.removeComponent(ReflectionUtilis.getChildrenCopy(marketWidget).get(2));
                        marketWidget.addComponent(new FleetMarketInfo(market).getMainPanel()).inTL(cord.x-10,0);
                        didIt = true;
                        break;
                    }
                }


            }

        }
    }

}
