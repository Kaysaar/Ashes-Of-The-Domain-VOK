package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;


public class ResearchFacility extends BaseIndustry implements EconomyTickListener {
    public static String subMarketId = "researchfacil";

    protected transient SubmarketAPI saved = null;

    @Override
    public void apply() {
        super.apply(true);
        SubmarketAPI open = market.getSubmarket(subMarketId);
            if (open == null) {
                if (saved != null) {
                    market.addSubmarket(saved);
                } else {
                    market.addSubmarket(subMarketId);
                    SubmarketAPI sub = market.getSubmarket(subMarketId);
                    sub.setFaction(Global.getSector().getFaction(subMarketId));
                    Global.getSector().getEconomy().forceStockpileUpdate(market);
                }
            }
        Global.getSector().getListenerManager().addListener(this);

    }

    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        super.notifyBeingRemoved(mode, forUpgrade);
        CargoAPI cargoAPI = saved.getCargo();
        market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addAll(cargoAPI);
    }

    @Override
    public void unapply() {
        super.unapply();
        if (market.isPlayerOwned()) {
            SubmarketAPI open = market.getSubmarket(subMarketId);
            saved = open;
            market.removeSubmarket(subMarketId);

        }
        Global.getSector().getListenerManager().removeListener(this);
    }


    public boolean isAvailableToBuild() {
        if (market.getFaction().isPlayerFaction()) {
            return true;
        }
        return  super.isAvailableToBuild();

    }

    public String getImproveMenuText() {
        return "Change Visual";
    }

    @Override
    public boolean canImprove() {
        return false;
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public String getUnavailableReason() {
        return "The " + market.getFaction().getDisplayName() + " does not support research,as most funds are allocated to military due to Sector instability in recent years";
    }

    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        super.addRightAfterDescriptionSection(tooltip, mode);
        if (IndustryTooltipMode.NORMAL.equals(mode)) {
            if(market.getAdmin().isPlayer()&&!market.getFaction().isPlayerFaction()){
                tooltip.addPara("This research facility will be under control of "+market.getFaction().getDisplayName()+" therefore it will only contribute to them!",Misc.getNegativeHighlightColor(),10f);
            }

        }

        if (IndustryTooltipMode.ADD_INDUSTRY.equals(mode)) {
            tooltip.addPara("Building that structure will enable faction to research new technologies", Misc.getHighlightColor(), 10f);
        }

        if(this.market.hasCondition("pre_collapse_facility")){
            tooltip.addPara("With building this facility here, our scientist will be able to analyze those ruins", Misc.getPositiveHighlightColor(),10f);
        }
    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        if(this.market.hasCondition("pre_collapse_facility")){
            SubmarketAPI open = market.getSubmarket(subMarketId);
            open.getCargo().addCommodity("research_databank",1);
        }
    }
}
