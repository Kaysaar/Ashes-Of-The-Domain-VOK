package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface ColonyDevelopmentAPI {
    String getName();
    void generateDetailingTooltip(MarketAPI market, TooltipMakerAPI tooltip);
    void generateTooltipForMarketCond(MarketAPI market, TooltipMakerAPI tooltip, boolean expanded);
    boolean canBeAppliedOnMarket(MarketAPI market);
    void apply(MarketAPI market);
    void unapply(MarketAPI market);
}
