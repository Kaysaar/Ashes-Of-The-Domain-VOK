package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;

public class AoTDCompoundUIInMarketScript extends CoreUITracker {
    public static boolean didIt = false;
    transient UIPanelAPI marketWidget;
    transient MarketAPI currentMarket;

//    @Override
//    public void advance(float amount) {
//        if(Global.getSector().getCampaignUI().getCurrentCoreTab()==null){
//            return;
//        }
//        if (!didIt){
//            if(CoreUITabId.CARGO.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())){
//                UIPanelAPI currentTab = ProductionUtil.getCurrentTab();
//                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(currentTab)) {
//                    if(ReflectionUtilis.hasMethodOfName("getOutpostPanelParams",componentAPI)){
//                        List<UIComponentAPI>componentAPIS = ReflectionUtilis.getChildrenCopy((UIPanelAPI) componentAPI);
//                        UIPanelAPI marketWidget = (UIPanelAPI) componentAPIS.get(1);
//                        MarketAPI market = (MarketAPI) ReflectionUtilis.findFieldByType(componentAPI,MarketAPI.class);
//                        if(market.getFaction()==null)return;
//                        float relativeX = marketWidget.getPosition().getX();
//                        if(ReflectionUtilis.getChildrenCopy(marketWidget).size()<3)return;
//                        LinkedHashMap<UIComponentAPI, Vector2f> cords = new LinkedHashMap<>();
//                        for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(marketWidget)) {
//                            float x = uiComponentAPI.getPosition().getX();
//                            float relX = x - relativeX;
//                            cords.put(uiComponentAPI,new Vector2f(relX,0));
//
//                        }
//                        for (UIComponentAPI uiComponentAPI : ReflectionUtilis.getChildrenCopy(marketWidget)) {
//                           marketWidget.removeComponent(uiComponentAPI);
//                        }
//                        for (Map.Entry<UIComponentAPI, Vector2f> entry : cords.entrySet()) {
//                            marketWidget.addComponent(entry.getKey()).inTL(entry.getValue().x,0);
//                        }
//                        Vector2f cord = cords.get(ReflectionUtilis.getChildrenCopy(marketWidget).get(2));
//                        cords.clear();
//                        marketWidget.removeComponent(ReflectionUtilis.getChildrenCopy(marketWidget).get(2));
//                        marketWidget.addComponent(new FleetMarketInfo(market).getMainPanel()).inTL(cord.x-10,0);
//                        didIt = true;
//                        break;
//                    }
//                }
//
//
//            }
//
//        }
//    }
    @Override
    public void advance(float amount) {
        if(Global.getSector().getCampaignUI().getCurrentCoreTab()==null){
            didIt = false;
            return;
        }
        if (!didIt){
            if(CoreUITabId.CARGO.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())&&Global.getSector().getCampaignUI().getCurrentInteractionDialog()!=null){
                UIPanelAPI coreUI = ProductionUtil.getCoreUI();
                UIPanelAPI leChildren = null;
                UIPanelAPI leGrandChildren = null;
                UIPanelAPI testing = null;
                if(coreUI!=null){
                    for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(coreUI)) {
                        if(ReflectionUtilis.hasMethodOfName("getDisplaySensorRange",componentAPI)){
                            leChildren = (UIPanelAPI) componentAPI;
                            break;
                        }
                    }
                }
                if(leChildren!=null) { //14
                    ArrayList<UIComponentAPI> componentAPIS = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leChildren);
                    testing = (UIPanelAPI) componentAPIS.get(10);
                    for (UIComponentAPI componentAPI : componentAPIS) {
                        if(ReflectionUtilis.hasMethodOfName("getFuelPerDay",componentAPI)){
                            leGrandChildren = (UIPanelAPI) componentAPI;
                            break;
                        }
                    }
                }

                if(leGrandChildren!=null) {
                    ArrayList<UIComponentAPI> grandChildrenComponents = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leGrandChildren);
                    if(grandChildrenComponents.size()==4){
                        UIComponentAPI grandGrandChild = grandChildrenComponents.get(2);
                        Vector2f xy = new Vector2f(grandGrandChild.getPosition().getX(), grandGrandChild.getPosition().getY());
                        UILinesRenderer renderer = new UILinesRenderer(0f);
                        renderer.setBoxColor(Color.MAGENTA);

                        CustomPanelAPI testings = Global.getSettings().createCustom(grandGrandChild.getPosition().getWidth(),grandGrandChild.getPosition().getHeight(),renderer);
                        CustomPanelAPI insider = testings.createCustomPanel(testing.getPosition().getWidth(),testing.getPosition().getHeight(),null);
                        testings.addComponent(insider).inTL(-5,0);
                        insider.getPosition().setSuspendRecompute(false);
                        CustomPanelAPI main = new AoTDCompoundShowcase(insider.getPosition().getWidth()+10,insider.getPosition().getHeight()).getMainPanel();
                        insider.addComponent(main);
                        TooltipMakerAPI tooltip = testings.createUIElement(1,1,true);
                        tooltip.addTooltipTo(new AoTDFuelTooltip(),main, TooltipMakerAPI.TooltipLocation.ABOVE);
                        leChildren.addComponent(testings).aboveLeft(testing,AoTDCompoundUIScript.getYPad());


                        didIt = true;
                        return;
                    }


                }


            }

        }
    }

}
