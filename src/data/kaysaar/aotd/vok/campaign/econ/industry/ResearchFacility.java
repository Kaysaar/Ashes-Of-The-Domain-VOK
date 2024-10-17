package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.awt.*;


public class ResearchFacility extends BaseIndustry implements EconomyTickListener {
    public static String subMarketId = "researchfacil";
    public static Float COST_PER_TIER = 10000F;
    protected transient SubmarketAPI saved = null;
    public static int amountDatabanksMonthly = 1;

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
        if(AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus()!=null){
           int tier = AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus().getSpec().getTier().ordinal();
           this.getUpkeep().modifyFlat("aotd_research",COST_PER_TIER*tier,"Research Cost");
        }
            this.getUpkeep().modifyFlat("aotd_research_2",10000,"Maintenance Cost");

        Global.getSector().getListenerManager().addListener(this);

    }

    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        if(saved!=null){
            CargoAPI cargoAPI = saved.getCargo();
            market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addAll(cargoAPI);

        }
        Global.getSector().getListenerManager().removeListener(this);
        super.notifyBeingRemoved(mode, forUpgrade);
    }

    @Override
    public void unapply() {
        super.unapply();
        if (market.isPlayerOwned()) {
            SubmarketAPI open = market.getSubmarket(subMarketId);
            saved = open;
            market.removeSubmarket(subMarketId);

        }
        this.getUpkeep().unmodifyFlat("aotd_research");
        this.getUpkeep().unmodifyFlat("aotd_research_2");
        Global.getSector().getListenerManager().removeListener(this);
    }


    public boolean isAvailableToBuild() {
        if (market.getFaction().isPlayerFaction()) {
            return true;
        }
        if(Global.getSettings().getModManager().isModEnabled("aotd_qol")){
            return market.getFaction().isPlayerFaction()|| QoLMisc.isCommissionedBy(market.getFactionId());
        }
        return super.isAvailableToBuild();

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
        return "The " + market.getFaction().getDisplayName() + " does not support research, as most of the funds are allocated to military spendings due to system's instability in recent years.";
    }

    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        super.addRightAfterDescriptionSection(tooltip, mode);


        if (IndustryTooltipMode.ADD_INDUSTRY.equals(mode)) {
            tooltip.addPara("Building that structure will enable your faction to research new technologies.", Misc.getHighlightColor(), 10f);
        }
        tooltip.addSectionHeading("Research costs", Alignment.MID, 10f);
        tooltip.addPara("Upkeep costs of the research facility are dependent on what is being currently researched.", 10f);

        if (this.market.hasCondition("pre_collapse_facility")) {
            tooltip.addPara("By building this facility here, our scientists will be able to analyze local pre-collapse ruins.", Misc.getPositiveHighlightColor(), 10f);
        }
        if (IndustryTooltipMode.NORMAL.equals(mode)) {
            tooltip.addSectionHeading("Currently ongoing research",Alignment.MID,10f);

            if (AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus() != null) {
                tooltip.addPara("Researching : %s",10, Color.ORANGE,AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus().getSpec().getName());
            }
            else{
                tooltip.addPara("%s",10, Color.ORANGE,"Nothing is being researched.");
            }
        }
    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        if (this.market.hasCondition("pre_collapse_facility")) {

            SubmarketAPI open = market.getSubmarket(subMarketId);
            if (open != null) {
                open.getCargo().addCommodity("research_databank", amountDatabanksMonthly);
            } else {
                market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity("research_databank", amountDatabanksMonthly);
            }
        }
    }
}
