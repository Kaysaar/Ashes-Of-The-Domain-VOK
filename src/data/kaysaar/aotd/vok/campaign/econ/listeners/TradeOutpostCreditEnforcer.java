package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.industry.TradePostIndustry;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.CargoPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.IndustryPanelContextUI;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketUIListener;

public class TradeOutpostCreditEnforcer implements MarketUIListener {
    @Override
    public void onMarketOverviewDiscovered(IndustryPanelContextUI ctx) {

    }
    String mouseOverSound = null;

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
}
