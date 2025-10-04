package data.kaysaar.aotd.vok.submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionDoctrineAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.industry.TradePostIndustry;

import java.awt.*;

public class TradePostMarket extends OpenMarketPlugin {
    @Override
    public boolean isEnabled(CoreUIAPI ui) {
        int currDay = Global.getSector().getClock().getDay();
        if(!market.hasIndustry("aotd_trade_outpost"))return false;
        return super.isEnabled(ui)&&currDay<=7;
    }
    @Override
    public float getTariff() {
        return 0;
    }

    public void updateCargoPrePlayerInteraction() {
        float seconds = Global.getSector().getClock().convertToSeconds(sinceLastCargoUpdate);
        addAndRemoveStockpiledResources(seconds, false, true, true);
        sinceLastCargoUpdate = 0f;
        pruneShips(0f);
        if (okToUpdateShipsAndWeapons()) {
            sinceSWUpdate = 0f;

            boolean military = Misc.isMilitary(market);
            boolean hiddenBase = market.getMemoryWithoutUpdate().getBoolean(MemFlags.HIDDEN_BASE_MEM_FLAG);

            float extraShips = 0f;
            //int extraShipSize = 0;
            if (military && hiddenBase && !market.hasSubmarket(Submarkets.GENERIC_MILITARY)) {
                extraShips = 150f;
                //extraShipSize = 1;
            }

            pruneWeapons(0f);

            int weapons = 5 + Math.max(0, market.getSize() - 1) + (Misc.isMilitary(market) ? 5 : 0);
            int fighters = 1 + Math.max(0, (market.getSize() - 3) / 2) + (Misc.isMilitary(market) ? 2 : 0);

            addWeapons(weapons, weapons + 2, 0, market.getFactionId());
            addFighters(fighters, fighters + 2, 0, market.getFactionId());


            getCargo().getMothballedShips().clear();

            float freighters = 10f;
            CommodityOnMarketAPI com = market.getCommodityData(Commodities.SHIPS);
            freighters += com.getMaxSupply() * 2f;

        }

        getCargo().sort();
    }
    @Override
    public void addShips(String factionId, float combat, float freighter, float tanker, float transport, float liner, float utility, Float qualityOverride, float qualityMod, FactionAPI.ShipPickMode modeOverride, FactionDoctrineAPI doctrineOverride, int maxShipSize) {
        super.addShips(factionId, combat, freighter, tanker, transport, liner, utility, qualityOverride, qualityMod, modeOverride, doctrineOverride, maxShipSize);
    }

    public boolean showInFleetScreen() {
        return false;
    }
    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        int currDay = Global.getSector().getClock().getDay();
        tooltip.addSectionHeading("Temporary Market", Alignment.MID,5f);
        tooltip.addPara("Due to limited operations this market is only opened for first seven days, at the beginning of the month",3f);
        if(currDay>7){
            tooltip.addPara("Market closed! Come next month.", Misc.getNegativeHighlightColor(),10f);
        }
        else{
            tooltip.addPara("Market opened!", Misc.getPositiveHighlightColor(),10f);
        }
        TradePostIndustry industry = (TradePostIndustry) market.getIndustry("aotd_trade_outpost");
        tooltip.addSectionHeading("Protective measures",Alignment.MID,10f);
        tooltip.addPara("Due to imposed limitations you can only sell cargo of maximum worth of %s each month!",5f, Color.ORANGE,Misc.getDGSCredits(100000));
        tooltip.addPara("Local demand is limited — you may only receive up to %s from selling commodities this month. Further sales will not be accepted.",
                10f, Color.ORANGE, Misc.getDGSCredits(industry.getAmountOfCreditsYouCanSpent()));
    }
}
