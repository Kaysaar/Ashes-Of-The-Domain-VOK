package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.TradeCenter;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class KaysaarUnderworld extends BaseIndustry implements MarketImmigrationModifier {

    public static float BASE_BONUS = 50f;
    public static float ALPHA_CORE_BONUS = 40f;
    public static float IMPROVE_BONUS = 25f;

    public static float STABILITY_PELANTY = 4f;


    protected transient SubmarketAPI saved = null;
    public void apply() {
        super.apply(true);
        if (isFunctional() && market.isPlayerOwned()) {
            if(!market.hasCondition(Conditions.ORGANIZED_CRIME)){
                market.addCondition(Conditions.ORGANIZED_CRIME);
            }
            SubmarketAPI open = market.getSubmarket(Submarkets.SUBMARKET_BLACK);
            if (open == null) {
                if (saved != null) {
                    market.addSubmarket(saved);
                } else {
                    market.addSubmarket(Submarkets.SUBMARKET_BLACK);
                    SubmarketAPI sub = market.getSubmarket(Submarkets.SUBMARKET_BLACK);
                    sub.setFaction(Global.getSector().getFaction(Factions.PIRATES));
                    Global.getSector().getEconomy().forceStockpileUpdate(market);
                }
            }
        } else if (market.isPlayerOwned()) {
            market.removeSubmarket(Submarkets.SUBMARKET_BLACK);
        }

        //modifyStabilityWithBaseMod();
        market.getStability().modifyFlat(getModId(), -STABILITY_PELANTY, getNameForModifier());

        market.getIncomeMult().modifyPercent(getModId(0), BASE_BONUS, getNameForModifier());
        if (!isFunctional()||!market.isFreePort()) {
            unapply();
        }
    }


    @Override
    public void unapply() {
        super.unapply();

        if (market.isPlayerOwned()) {
            SubmarketAPI open = market.getSubmarket(Submarkets.SUBMARKET_BLACK);
            saved = open;
            market.removeSubmarket(Submarkets.SUBMARKET_BLACK);

        }

        market.getStability().unmodifyFlat(getModId());
        if(market.hasCondition(Conditions.ORGANIZED_CRIME)){
            market.removeCondition(Conditions.ORGANIZED_CRIME);
        }

        market.getIncomeMult().unmodifyPercent(getModId(0));
    }


    protected void addStabilityPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        Color h = Misc.getHighlightColor();
        float opad = 10f;

        float a = BASE_BONUS;
        String aStr = "+" + (int)Math.round(a * 1f) + "%";
        tooltip.addPara("Colony income: %s", opad, h, aStr);

        h = Misc.getNegativeHighlightColor();
        tooltip.addPara("Stability penalty: %s", opad, h, "" + -(int)STABILITY_PELANTY);
    }

    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        if (market.isPlayerOwned() || currTooltipMode == IndustryTooltipMode.ADD_INDUSTRY) {
            tooltip.addPara("Adds an  \'Black Market\' that the colony's owner is able to trade with.", 10f);
        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);
        }
    }

    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.add(Factions.TRITACHYON, 10f);
    }

    public boolean isAvailableToBuild() {
        if(market.hasIndustry("kaysaarcapital_forbidden_city")){
            return false;
        }
        if(market.hasIndustry("commerce")){
            return false;
        }
        if(Global.getSettings().getModManager().isModEnabled("yunruindustries")){
            if(market.hasIndustry("yunru_bazaar")){
                return false;
            }
        }
        if(market.hasSpaceport()&&market.isFreePort()){
            return true;
        }

        return false;
    }

    public String getUnavailableReason() {
        if(market.hasIndustry("kaysaarcapital_forbidden_city")){
            return  "Requires a functional spaceport and enabled Free Port policy"+"Cannot be build due, to Forbidden City established on that planet";
        }
        if(market.hasIndustry("commerce")){
            return  "Requires a functional spaceport and enabled Free Port policy"+"Cannot be build due, to Independent trade being to well established here by Commerce";
        }
        if(Global.getSettings().getModManager().isModEnabled("yunruindustries")){
            if(market.hasIndustry("yunru_bazaar")){
                return  "Requires a functional spaceport and eneabled Free Port policy"+"Cannot be build due, to Independent trade being to well established here by Baazar";
            }
        }
        return "Requires a functional spaceport and eneabled Free Port policy";
    }


    //market.getIncomeMult().modifyMult(id, INCOME_MULT, "Industrial planning");
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
            text.addPara(pre + "Reduces upkeep cost by %s" +
                            "Increases colony income by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" +
                    str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s" +
                        "Increases colony income by %s.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" +
                str);

    }


    @Override
    public boolean canImprove() {
        return true;
    }

    protected void applyImproveModifiers() {
        if (isImproved()) {
            market.getIncomeMult().modifyPercent(getModId(2), IMPROVE_BONUS,
                    getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getIncomeMult().unmodifyPercent(getModId(2));
        }
    }


    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        float a = IMPROVE_BONUS;
        String aStr = "" + (int)Math.round(a * 1f) + "%";

        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Colony income increased by %s.", 0f, highlight, aStr);
        } else {
            info.addPara("Increases colony income by %s.", 0f, highlight, aStr);
        }

        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
    }
}
