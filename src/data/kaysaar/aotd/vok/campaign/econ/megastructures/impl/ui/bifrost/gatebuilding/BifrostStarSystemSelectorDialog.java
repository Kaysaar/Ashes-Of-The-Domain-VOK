package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.BifrostMainUI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts.BaseRestorationContract;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.basecomps.StarSystemSelector;
import data.kaysaar.aotd.vok.ui.basecomps.StarSystemSelectorOtherInfoData;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class BifrostStarSystemSelectorDialog extends BasePopUpDialog {
    BifrostStarSystemSelector selector;
    BifrostLocationData data;
    BifrostMainUI mainUI;
    public StarSystemSelector getSelector() {
        return selector;
    }

    public BifrostStarSystemSelectorDialog(String headerTitle,BifrostMainUI mainUI) {
        super(headerTitle);
        this.mainUI = mainUI;

    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        ArrayList<StarSystemAPI> systems = new ArrayList<>();
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(!systems.contains(playerMarket.getStarSystem())){
                if(playerMarket.getStarSystem().getEntitiesWithTag("bifrost").isEmpty()){
                    systems.add(playerMarket.getStarSystem());
                }

            }
        }
        BifrostMegastructureManager manager = BifrostMegastructureManager.getInstance();
        BifrostMegastructure mega = manager.getMegastructure();
        for (BifrostSection section : mega.getSections()) {
            systems.remove(section.getStarSystemAPI());
        }
        selector = new BifrostStarSystemSelector(systems, Global.getSettings().createCustom(640, 220, null), new StarSystemSelectorOtherInfoData() {
            @Override
            public String getNameForLabel() {
                return "Available bonus from Bifrost Gate";
            }

            @Override
            public void populateLabel(TooltipMakerAPI tooltip,StarSystemAPI system,float width,float height) {
                float totalAccess = 0f;
                for (MarketAPI market : Global.getSector().getEconomy().getMarkets(system)) {
                    if(!market.isPlayerOwned()||!market.getFaction().isPlayerFaction())continue;
                    if(market.getAccessibilityMod().getFlatBonus()>=0){
                        totalAccess+=market.getAccessibilityMod().getFlatBonus();

                    }
                    if(market.getAccessibilityMod().getFlatBonus("aotd_bifrost")!=null) {
                        totalAccess-=market.getAccessibilityMod().getFlatBonus("aotd_bifrost").getValue();

                    }
                }
                totalAccess/=10f;
                LabelAPI label = tooltip.addPara("%s",0f,Color.ORANGE,(int)(totalAccess*100f)+"% Accessibility");
                label.getPosition().inTL(width/2-(label.computeTextWidth(label.getText())/2),height/2-(label.computeTextHeight(label.getText())/2));

            }

        },this);
        selector.init();
        ArrayList<Pair<String,Integer>>pairs = new ArrayList<>();
       ;
        LinkedHashMap<String,Integer> costs  = AoTDMisc.getOrderedResourceMap( BifrostMegastructureManager.getInstance().getMegastructure().getSectionSpec().getScript().getMonthlyResNeeded());
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            Pair<String,Integer>res = new Pair<>(entry.getKey(),entry.getValue());
            pairs.add(res);
        }
        int cost = BaseRestorationContract.getCreditsWorthOfResources(costs);
        tooltip.addCustom(selector.getMainPanel(),5f).getPosition().inTL((width-selector.getMainPanel().getPosition().getWidth())/2,-tooltip.getPrev().getPosition().getY()-selector.getMainPanel().getPosition().getHeight());
        tooltip.setParaInsigniaLarge();
        tooltip.addPara("Building a Bifrost Gate will take %s, requires %s credits monthly, and it will require the following resources for the duration of construction",5f,Color.ORANGE, AoTDMisc.convertDaysToString(90),Misc.getDGSCredits(cost));
        tooltip.getPrev().getPosition().inTL(5,-tooltip.getPrev().getPosition().getY()-tooltip.getPrev().getPosition().getHeight()-5f);
        AoTDCommodityShortPanelCombined panel = new AoTDCommodityShortPanelCombined(width,5,pairs);
        tooltip.addCustom(panel.getMainPanel(),5f);



    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(selector!=null){
            selector.advance(amount);
        }
    }

    @Override
    public void applyConfirmScript() {
        if(selector.getCurrentlyChosenStarSystem()!=null&&data==null){

            BifrostMegastructureManager manager = BifrostMegastructureManager.getInstance();
            BifrostMegastructure mega = manager.getMegastructure();
            mega.addNewBifrostGate(selector.getCurrentlyChosenStarSystem());

            mainUI.createUI();
        } else if (selector.getCurrentlyChosenStarSystem()!=null&&data!=null) {

            BifrostMegastructureManager manager = BifrostMegastructureManager.getInstance();
            BifrostMegastructure mega = manager.getMegastructure();
            mega.addNewBifrostGate(selector.getCurrentlyChosenStarSystem(),data);
            mainUI.createUI();
        }
    }
}
