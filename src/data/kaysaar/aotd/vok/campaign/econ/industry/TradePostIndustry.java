package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.TradeCenter;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Random;

public class TradePostIndustry extends TradeCenter {
    public float amountOfCreditsYouCanSpent = 100000;
    public static float BASE_BONUS = 25f;
    public static float ALPHA_CORE_BONUS = 25f;
    public static float IMPROVE_BONUS = 25f;

    public static float STABILITY_PENALTY = 1f;
    public static String marketID = "aotd_trade_outpost";

    public float getAmountOfCreditsYouCanSpent() {
        return amountOfCreditsYouCanSpent;
    }
    public boolean canPerformTransaction(float amount){
        return amountOfCreditsYouCanSpent >= amount;
    }
    public void subtractAmount(float amount){
        amountOfCreditsYouCanSpent -= amount;
    }

    protected transient SubmarketAPI saved = null;

    @Override
    public void apply() {
        super.apply(true);

        if (isFunctional() && market.isPlayerOwned()) {
            SubmarketAPI sub = market.getSubmarket(marketID);
            if (sub == null) {
                if (saved != null) {
                    market.addSubmarket(saved);
                } else {
                    market.addSubmarket(marketID);
                    sub = market.getSubmarket(marketID);
                    sub.setFaction(Global.getSector().getFaction(Factions.INDEPENDENT));
                    Global.getSector().getEconomy().forceStockpileUpdate(market);
                }
            }
        } else if (market.isPlayerOwned()) {
            market.removeSubmarket(marketID);
        }

        market.getStability().modifyFlat(getModId(), -STABILITY_PENALTY, getNameForModifier());

        if (!isFunctional()) {
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();

        if (market.isPlayerOwned()) {
            SubmarketAPI sub = market.getSubmarket(marketID);
            saved = sub;
            market.removeSubmarket(marketID);
        }

        market.getStability().unmodifyFlat(getModId());
    }

    @Override
    public CargoAPI generateCargoForGatheringPoint(Random random) {
        this.amountOfCreditsYouCanSpent = 100000;
        return super.generateCargoForGatheringPoint(random);

    }

    protected void addStabilityPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        Color h = Misc.getHighlightColor();
        float opad = 10f;

        h = Misc.getNegativeHighlightColor();
        tooltip.addPara("Stability penalty: %s", opad, h, "" + -(int) STABILITY_PENALTY);
    }

    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        if (market.isPlayerOwned() || currTooltipMode == IndustryTooltipMode.ADD_INDUSTRY) {
            tooltip.addPara("Adds an independent 'Trade Post Market' that the colony's owner is able to trade with. "
                    + "Market that is only opened for first seven days of each month!", 10f);
        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);
        }
    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.add(Factions.INDEPENDENT, 2f);
    }

    @Override
    public boolean isAvailableToBuild() {
        return market.hasSpaceport() && !market.getIndustries().isEmpty();
    }

    @Override
    public String getUnavailableReason() {
        return "Requires a functional spaceport and at least one industry present on market!";
    }

    @Override
    public String getCurrentImage() {
        return spec.getImageName();
    }

    @Override
    protected void applyAlphaCoreModifiers() {
        market.getIncomeMult().modifyPercent(getModId(1), ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
    }

    @Override
    protected void applyNoAICoreModifiers() {
        market.getIncomeMult().unmodifyPercent(getModId(1));
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }

    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        float a = ALPHA_CORE_BONUS;
        String str = "" + (int) Math.round(a) + "%";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                            "Increases colony income by %s.", 0f, highlight,
                    "" + (int) ((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                    str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                        "Increases colony income by %s.", opad, highlight,
                "" + (int) ((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                str);
    }

    @Override
    public boolean canImprove() {
        return false;
    }
}
