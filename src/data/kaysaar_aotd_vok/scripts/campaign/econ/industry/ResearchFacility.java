package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

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
import data.Ids.AodResearcherSkills;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;
import lunalib.lunaSettings.LunaSettings;

public class ResearchFacility extends BaseIndustry implements EconomyTickListener {
    public static final float IMMIGRATION_BONUS = 10f;
    public static String subMarketId = "researchfacil";

    protected transient SubmarketAPI saved = null;

    @Override
    public void apply() {
        super.apply(true);
        SubmarketAPI open = market.getSubmarket(subMarketId);
        if(market.getFaction().isPlayerFaction()){
            if (open == null) {
                if (saved != null) {
                    market.addSubmarket(saved);
                } else {
                    market.addSubmarket(subMarketId);
                    SubmarketAPI sub = market.getSubmarket(subMarketId);
                    sub.setFaction(Global.getSector().getFaction(subMarketId));
                    Global.getSector().getEconomy().forceStockpileUpdate(market);
                }
            } else if (market.isPlayerOwned()) {
                market.removeSubmarket(subMarketId);
            }
        }
        if(AoDUtilis.getResearchAPI().getCurrentResearching()!=null){
            int div =1 ;

            if(AoDUtilis.getResearchAPI().getCurrentResearcher()!=null&&AoDUtilis.getResearchAPI().getCurrentResearcher().hasTag(AodResearcherSkills.RESOURCEFUL)){
                div++;
            }
            if(Global.getSettings().getIndustrySpec(AoDUtilis.getResearchAPI().getCurrentResearching().industryId).hasTag("experimental")){
                this.getUpkeep().modifyFlat("research",180000/div,"Ongoing Research");
            }
            else{
                this.getUpkeep().modifyFlat("research",30000/div*AoDUtilis.getResearchAPI().getCurrentResearching().researchTier,"Ongoing Research");
            }

        }
        else{
            this.getUpkeep().unmodifyFlat("research");
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
        return false;

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
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if (IndustryTooltipMode.NORMAL.equals(mode)) {
            if ( !this.getMarket().getFaction().isPlayerFaction()) {
                tooltip.addPara("The " + market.getFaction().getDisplayName() + " does not support research,as most funds are allocated to military due to Sector instability in recent years", Misc.getHighlightColor(), 10f);
            }
        }

        if (IndustryTooltipMode.ADD_INDUSTRY.equals(mode)) {
            tooltip.addPara("Building that structure will enable your faction to research new technologies", Misc.getHighlightColor(), 10f);
        }
        if(AoDUtilis.getResearchAPI().getCurrentResearching()!=null){
            tooltip.addSectionHeading("Currently Researching", Alignment.MID,10f);
            tooltip.addPara("Researching: "+AoDUtilis.getResearchAPI().getCurrentResearching().industryName, Misc.getHighlightColor(), 10f);
        }
        boolean enabled = Boolean.TRUE.equals(LunaSettings.getBoolean("aod_core", "aoTDVOK_CHEAT_DATABANKS"));
        if(this.market.hasCondition("pre_collapse_facility")&&enabled){
            tooltip.addSectionHeading("Pre Collapse Facilities", Alignment.MID,10f);
            tooltip.addPara("With building this facility here, our scientist will be able to analyze those ruins", Misc.getPositiveHighlightColor(),10f);
        }
    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            boolean enabled = Boolean.TRUE.equals(LunaSettings.getBoolean("aod_core", "aoTDVOK_CHEAT_DATABANKS"));
            if(enabled){
                if(this.market.hasCondition("pre_collapse_facility")){
                    SubmarketAPI open = market.getSubmarket(subMarketId);
                    open.getCargo().addCommodity("research_databank",1);
                }
            }
        }
    }
}