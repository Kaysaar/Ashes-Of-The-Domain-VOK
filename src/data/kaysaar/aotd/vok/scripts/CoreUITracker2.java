package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.List;

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
                        UIPanelAPI markets = (UIPanelAPI) componentAPIS.stream().filter(x->ReflectionUtilis.hasMethodOfName("showOverview",x)).findFirst().orElse(null);
                        UIPanelAPI deep = (UIPanelAPI) ReflectionUtilis.getChildrenCopy(markets).get(0);
                        MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPI,MarketAPI.class);
                        UIPanelAPI d = (UIPanelAPI) ReflectionUtilis.getChildrenCopy(deep).get(1);
                        deep.removeComponent(ReflectionUtilis.getChildrenCopy(deep).get(1));
                        deep.removeComponent(ReflectionUtilis.getChildrenCopy(deep).get(1));

                        didIt = true;
                        break;
                    }
                }


            }

        }
    }

}
