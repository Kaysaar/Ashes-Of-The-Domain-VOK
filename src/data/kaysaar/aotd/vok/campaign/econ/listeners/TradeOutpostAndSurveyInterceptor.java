package data.kaysaar.aotd.vok.campaign.econ.listeners;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.graphics.util.Fader;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui.ColonyDevelopmentDialog;
import data.kaysaar.aotd.vok.campaign.econ.industry.TradePostIndustry;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketUIListener;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.SurveyPanelContextUI;
import data.misc.ProductionUtil;

import java.util.List;

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
        }
        UIComponentAPI comp;
            List<UIComponentAPI> co = ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI());
            comp = co.get(co.size()-1);
            if(ctx.surveyPanel.equals(ReflectionUtilis.getPrivateVariable("delegate",comp))){
                Fader fader = (Fader) ReflectionUtilis.invokeMethodWithAutoProjection("getFader",comp);
                if(!fader.getState().equals(Fader.State.OUT)){
                    fader.forceOut();
                    ColonyDevelopmentDialog dialog = new ColonyDevelopmentDialog("Choose Colony Development Plan", (UIPanelAPI) comp,ctx.market);
                    AshMisc.initPopUpDialog(dialog,1000,600);
                }

            }


    }
}
