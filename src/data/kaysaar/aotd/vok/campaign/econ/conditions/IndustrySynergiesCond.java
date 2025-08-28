package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import data.kaysaar.aotd.vok.campaign.econ.industry.MiscHiddenIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.BaseIndustrySynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;

;

public class IndustrySynergiesCond extends BaseMarketConditionPlugin {
    IntervalUtil util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay(), Global.getSector().getClock().getSecondsPerDay());

    public void apply(String id) {
        super.apply(id);
        MiscHiddenIndustry.getInstance(market);
        applyEffects();
    }

    public void applyEffects() {
        IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarket(market).forEach(this::applySynergyEffect);
    }

    public void applySynergyEffect(BaseIndustrySynergy synergy) {
        synergy.apply(IndustrySynergiesManager.getInstance().calculateEfficiency(market), market);
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.unapply(market));
    }
    public static IndustrySynergiesCond getInstance(MarketAPI market) {
        if(!market.hasCondition("aotd_industry_synergies")){
            market.addCondition("aotd_industry_synergies");
        }
        return (IndustrySynergiesCond) market.getCondition("aotd_industry_synergies").getPlugin();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (util == null) {
            util = new IntervalUtil(Global.getSector().getClock().getSecondsPerDay(), Global.getSector().getClock().getSecondsPerDay());
        }

        IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.advance(market, amount, false));
        util.advance(amount);
        if (util.intervalElapsed()) {
            IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x -> x.endOfTheDay(market));
        }

    }


    public String getModId() {
        return condition.getId();
    }

    @Override
    public boolean showIcon() {
        return false;

    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_industry_synergies")) {
            marketAPI.addCondition("aotd_industry_synergies");
        }
    }
}
