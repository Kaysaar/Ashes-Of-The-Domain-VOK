package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class PlanetaryDefenceForce extends BaseIndustry implements EconomyTickListener {

    public static float DEFENSE_BONUS_BASE = 1.5f;

    public static float IMPROVE_DEFENSE_BONUS = 0.25f;
;
    public void apply() {
        Global.getSector().getListenerManager().addListener(this, true);
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.SUPPLIES, size+2);
        demand(Commodities.HAND_WEAPONS, size );
        demand(Commodities.CREW,size);
        supply(Commodities.MARINES, size + 2);
        for (Industry industry : market.getIndustries()) {
            if(industry instanceof HeavyIndustry){
                // here entire logic of checking etc
            }
        }


        modifyStabilityWithBaseMod();

        float mult = getDeficitMult(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        String extra = "";
        if (mult != 1) {
            String com = getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS).one;
            extra = " (" + getDeficitText(com).toLowerCase() + ")";
        }
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD)
                .modifyMult(getModId(), DEFENSE_BONUS_BASE*mult,"Planetary Defence Force");



        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

    }


    @Override
    public void unapply() {
        super.unapply();

        unmodifyStabilityWithBaseMod();

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId());
    }




    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        //return mode == IndustryTooltipMode.NORMAL && isFunctional();
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        //if (mode == IndustryTooltipMode.NORMAL && isFunctional()) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);

            addGroundDefensesImpactSection(tooltip, DEFENSE_BONUS_BASE, Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
        }
    }
    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
//        int ammount=0;
//        if(isFunctional()&&market.hasIndustry(AoTDIndustries.PLANETARY_DEFENCE_FORCE)&&market.getFaction().isPlayerFaction())
//            ammount= Misc.getStorageCargo(market).getMarines();
//            if(ammount<2000){
//            market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addCommodity(Commodities.MARINES,market.getSize()*20);
//            }


    }

    @Override
    protected int getBaseStabilityMod() {
        return 1;
    }

    @Override
    protected Pair<String, Integer> getStabilityAffectingDeficit() {
        return getMaxDeficit(Commodities.SUPPLIES, Commodities.MARINES, Commodities.HAND_WEAPONS);
    }


    public static float ALPHA_CORE_BONUS = 0.5f;
    @Override
    protected void applyAlphaCoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(
                getModId(1), 1f + ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
    }


    @Override
    protected void applyNoAICoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId(1));
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
        //String str = Strings.X + (int)Math.round(a * 100f) + "%";
        String str = Strings.X + (1f + a) + "";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s units. " +
                            "Increases ground defenses strength by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                    str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep costs by %s. Reduces demand by %s units. " +
                        "Increases ground defenses strength by %s.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                str);

    }


    @Override
    public boolean canImprove() {
        return true;
    }

    protected void applyImproveModifiers() {
        if (isImproved()) {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("ground_defenses_improve",
                    1f + IMPROVE_DEFENSE_BONUS,
                    getImprovementsDescForModifiers() + " (" + getNameForModifier() + ")");
        } else {
            market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult("ground_defenses_improve");
        }
    }
    @Override
    public boolean isAvailableToBuild() {
       return false;
    }


    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        float a = IMPROVE_DEFENSE_BONUS;
        String str = Strings.X + (1f + a) + "";

        if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
            info.addPara("Ground defenses strength increased by %s.", 0f, highlight, str);
        } else {
            info.addPara("Increases ground defenses strength by %s.", 0f, highlight, str);
        }

        info.addSpacer(opad);
        super.addImproveDesc(info, mode);
    }

    @Override
    public MarketCMD.RaidDangerLevel adjustCommodityDangerLevel(String commodityId, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }

    @Override
    public MarketCMD.RaidDangerLevel adjustItemDangerLevel(String itemId, String data, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }
    public boolean showWhenUnavailable() {
        return false;
    }


}
