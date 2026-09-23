package data.kaysaar.aotd.vok.campaign.econ.listeners;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.plugins.SurveyPlugin;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.coreui.A.R;
import data.kaysaar.aotd.tot.plugins.ProductionUtil;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui.ColonyDevelopmentDialog;
import data.kaysaar.aotd.vok.campaign.econ.industry.TradePostIndustry;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketUIListener;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.SurveyPanelContextUI;


import java.util.List;
import java.util.Map;

public class TradeOutpostAndSurveyInterceptor implements MarketUIListener {
    @Override
    public void onMarketOverviewDiscovered(IndustryPanelContextUI ctx) {

    }

    @Override
    public void onSubmarketCargoCreated(CargoPanelContextUI ctx) {
        if(TradePostIndustry.marketID.equals(SubmarketOpenedListener.subMarketIDLastlySaved)){
            UIPanelAPI trade = ctx.tradePanel;
            UIPanelAPI cargoTransfer = (UIPanelAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getTransferHandler",trade);
;            if(!ctx.market.hasIndustry("aotd_trade_outpost"))return;
            float valueOfTransaction = (float) ReflectionUtilis.invokeMethodWithAutoProjection("getTransactionValue",cargoTransfer,false);
            TradePostIndustry ind = (TradePostIndustry) ctx.market.getIndustry("aotd_trade_outpost");
            if(!ind.canPerformTransaction(valueOfTransaction)){
                ButtonAPI confirmButton = (ButtonAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getConfirmButton",trade);
                if(confirmButton.isEnabled()){
                    confirmButton.setEnabled(false);
                    confirmButton.setMouseOverSound(null);
                }
            }
            else {
                ButtonAPI confirmButton = (ButtonAPI) ReflectionUtilis.invokeMethodWithAutoProjection("getConfirmButton",trade);
                confirmButton.setMouseOverSound("ui_button_mouseover");



            }

        }
    }

    @Override
    public void onSurveyPanelCreated(SurveyPanelContextUI ctx) {
        if(ctx.market!=null){
            if(ctx.market.hasCondition("pre_collapse_facility")&&!ctx.market.getPrimaryEntity().getMemory().is("$aotd_defeated_pcf",true)){
                ButtonAPI colonize = ReflectionUtilis.findButtonWithText(ctx.surveyPanel,"Establish colony...",true,true);
                if(colonize!=null){
                    if(colonize.isEnabled()){
                        colonize.setEnabled(false);
                    }
                }
            }
            if(ctx.market.getContainingLocation()!=null&&ctx.market.getContainingLocation().hasTag(Tags.SYSTEM_ABYSSAL)&&ctx.market.getStarSystem().getId().equals("limbo")){
                ///TODO- LATER
//               Object plSurveyPanel = ReflectionUtilis.getChildrenCopy((UIPanelAPI) ReflectionUtilis.getChildrenCopy(ctx.surveyPanel).get(2)).get(0);
//               if(plSurveyPanel instanceof TooltipMakerAPI tl){
//                   if(tl.getPrev() instanceof LabelAPI label){
//                       if(label.getText().contains("abyssal hyperspace")){
//                           tl.addPara("Unless someone is willing to challenge that assumption.", Global.getSettings().getFactionSpec(Factions.DWELLER).getBaseUIColor(),5f);
//                           ButtonAPI colonize = ReflectionUtilis.findButtonWithText(ctx.surveyPanel,"Establish colony...",true,true);
//                           CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
//                           SurveyPlugin plugin = (SurveyPlugin) Global.getSettings().getNewPluginInstance("surveyPlugin");
//                           boolean met = true;
//                            if(!Global.getSettings().isDevMode()){
//                                for (Map.Entry<String, Integer> entry : plugin.getOutpostConsumed().entrySet()) {
//                                    if(playerCargo.getQuantity(CargoAPI.CargoItemType.RESOURCES,entry.getKey())<entry.getValue()){
//                                        met = false;
//                                        break;
//                                    }
//                                }
//                            }
//
//                           colonize.setEnabled(met);
//                       }
//                   }
//               }
            }
        }
        UIComponentAPI comp;
            List<UIComponentAPI> co = ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI());
            comp = co.get(co.size()-1);
            if(ctx.surveyPanel.equals(ReflectionUtilis.getPrivateVariable("delegate",comp))){
                Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getFader",comp);
                if(!fader.getState().equals(Fader.State.OUT)){
                    fader.forceOut();
                    ColonyDevelopmentDialog dialog = new ColonyDevelopmentDialog("Choose Colony Development Plan", (UIPanelAPI) comp,ctx.market);
                    AshMisc.initPopUpDialog(dialog,1000,640);
                }

            }


    }
}
