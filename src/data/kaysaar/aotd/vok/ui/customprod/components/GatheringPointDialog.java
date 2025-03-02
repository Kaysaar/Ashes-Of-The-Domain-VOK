package data.kaysaar.aotd.vok.ui.customprod.components;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import data.kaysaar.aotd.vok.ui.customprod.components.gatheringpoint.AoTDGatehringPointPlugin;

import java.util.ArrayList;

public class GatheringPointDialog extends BasePopUpDialog {
    NidavelirMainPanelPlugin plugin;
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    MarketAPI currentGatheringPoint;
    public GatheringPointDialog(String headerTitle, NidavelirMainPanelPlugin plugin) {
        super(headerTitle);
        this.plugin = plugin;
        currentGatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        super.createContentForDialog(tooltip, width);
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            Pair<CustomPanelAPI,ButtonAPI> pair = AoTDGatehringPointPlugin.getMarketEntitySpriteButton(width-5,75,75,playerMarket);
            buttons.add(pair.two);
            tooltip.addCustom(pair.one,5f);

        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        for (ButtonAPI button : buttons) {
            MarketAPI market = (MarketAPI) button.getCustomData();
            if(button.isChecked()){
                button.setChecked(false);
                currentGatheringPoint = market;
            }
            if(market!=null&&currentGatheringPoint!=null) {
                if(market.getId().equals(currentGatheringPoint.getId())) {
                    button.highlight();
                }
                else {
                    button.unhighlight();
                }
            }
            else{
                button.unhighlight();
            }
        }
    }

    @Override
    public void applyConfirmScript() {
        Global.getSector().getPlayerFaction().getProduction().setGatheringPoint(currentGatheringPoint);
        buttons.clear();
        plugin.refreshGatheringPointBar();
    }
}
