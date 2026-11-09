package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.industries.AoTDBaseIndustry;
import data.kaysaar.aotd.tot.scripts.economy.AoTDIndustryData;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.GnajidAICSynergySource;

public class GnajidAICCondition extends BaseMarketConditionPlugin {
    public static final String CONDITION_ID = "aotd_gnajid_aic";
    public static final float CONSTRUCTION_BONUS = 0.20f;
    public static final float SYNERGY_BONUS = 0.50f;
    public static final float DEMAND_MULT = 0.90f;
    private static final String DEMAND_MOD_ID = "aotd_gnajid_aic_demand";
    private static final String DEMAND_DESC = "Advanced Industrial Controller";
    private transient float demandRefreshDays;

    @Override
    public void apply(String id) {
        super.apply(id);
        updateDemandModifiers();
    }

    @Override
    public void unapply(String id) {
        removeDemandModifiers();
        super.unapply(id);
    }

    private void updateDemandModifiers() {
        if (market == null) return;
        AoTDIndustryData data = AoTDIndustryData.getInstance(market);
        for (Industry industry : market.getIndustries()) {
            // A pending industry has a large temporary demand-suppression stat.
            // Multiplying that stat would distort the suppression correction.
            boolean pending = data.isPending(industry.getId());
            for (MutableCommodityQuantity demand : industry.getAllDemand()) {
                if (pending) demand.getQuantity().unmodifyMult(DEMAND_MOD_ID);
                else demand.getQuantity().modifyMult(DEMAND_MOD_ID, DEMAND_MULT, DEMAND_DESC);
            }
            if (industry instanceof AoTDBaseIndustry aotd) {
                for (MutableStat demand : aotd.getExactDemand().values()) {
                    if (pending) demand.unmodifyMult(DEMAND_MOD_ID);
                    else demand.modifyMult(DEMAND_MOD_ID, DEMAND_MULT, DEMAND_DESC);
                }
            }
        }
    }

    private void removeDemandModifiers() {
        if (market == null) return;
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity demand : industry.getAllDemand()) {
                demand.getQuantity().unmodifyMult(DEMAND_MOD_ID);
            }
            if (industry instanceof AoTDBaseIndustry aotd) {
                for (MutableStat demand : aotd.getExactDemand().values()) {
                    demand.unmodifyMult(DEMAND_MOD_ID);
                }
            }
        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (Global.getSector() == null || Global.getSector().isPaused() || amount <= 0f) return;
        if (market == null || market.getFaction() == null || !market.getFaction().isPlayerFaction()) return;

        float days = Global.getSector().getClock().convertToDays(amount);
        if (days <= 0f) return;
        // 20% faster industry construction, without altering construction costs.
        float bonusDays = days * CONSTRUCTION_BONUS;
        for (Industry industry : market.getIndustries()) {
            if (industry.isBuilding() && industry instanceof BaseIndustry base) {
                base.setBuildProgress(base.getBuildProgress() + bonusDays);
            }
        }

        demandRefreshDays += days;
        if (demandRefreshDays >= 0.5f) {
            demandRefreshDays = 0f;
            updateDemandModifiers();
        }
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara("Increases industry construction speed by %s.", 5f,
                Misc.getPositiveHighlightColor(), "+20%");
        tooltip.addPara("Adds %s to industrial synergy efficiency.", 5f,
                Misc.getPositiveHighlightColor(), "+50 percentage points");
        tooltip.addPara("Reduces commodity demand from industries by %s.", 5f,
                Misc.getPositiveHighlightColor(), "10%");
        tooltip.addPara("A deployed controller remains installed independently of the Gnajid station.",
                Misc.getGrayColor(), 5f);
    }

    @Override
    public boolean showIcon() {
        return true;
    }
}
