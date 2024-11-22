package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.BifrostMega;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.sections.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.StarSystemSelector;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.StarSystemSelectorOtherInfoData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;

public class BifrostStarSystemSelector extends BasePopUpDialog{
    StarSystemSelector selector;
    BaseMegastrucutreMenu menu ;
    public BifrostStarSystemSelector(String headerTitle,BaseMegastrucutreMenu menu) {
        super(headerTitle);
        this.menu = menu;
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
        BifrostMega mega = (BifrostMega) menu.getMegastructure();
        for (BifrostSection section : mega.getSections()) {
            systems.remove(section.getStarSystemAPI());
        }
        selector = new StarSystemSelector(systems, Global.getSettings().createCustom(590, 220, null), new StarSystemSelectorOtherInfoData() {
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
        });
        selector.init();
        tooltip.addCustom(selector.getMainPanel(),5f).getPosition().inTL((width-selector.getMainPanel().getPosition().getWidth())/2,-tooltip.getPrev().getPosition().getY()-selector.getMainPanel().getPosition().getHeight());
        tooltip.setParaInsigniaLarge();
        tooltip.addPara("Building Bifrost Gate will take %s and require monthly income of %s, and it will require following resources for duration of construction",5f,Color.ORANGE, AoTDMisc.convertDaysToString(90),Misc.getDGSCredits(100000));
        tooltip.getPrev().getPosition().inTL(5,-tooltip.getPrev().getPosition().getY()-tooltip.getPrev().getPosition().getHeight()-5f);
        tooltip.addCustom(MegastructureUIMisc.createResourcePanel(width,40,40, BifrostMega.bifrostGateCost,Color.ORANGE),5f);


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
        if(selector.getCurrentlyChosenStarSystem()!=null){
            BifrostMega mega = (BifrostMega) menu.getMegastructure();
            mega.addNewBifrostGate(selector.getCurrentlyChosenStarSystem());
            menu.resetEntireUI();

        }
    }
}
