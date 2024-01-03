package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class ResearchedUpkeepModifier extends BaseMarketConditionPlugin {
    public void apply(String id) {
        super.apply(id);
        market.getUpkeepMult().modifyMult("tech_upkeep", 0.9f, "Technological Advances");

    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getUpkeepMult().unmodifyMult("tech_upkeep");
    }

    public static void applyRessourceCond(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition("aotd_tech_upkeep")) {
                marketAPI.addCondition("aotd_tech_upkeep");

        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}
