package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class EcoTourism extends BaseIndustry {
    public static String HAZARD_SOURCE = "ecotourism";
    public static float BASE_BONUS = 0.35f;
    public static float ALPHA_CORE_BONUS = 0.2f;
    public static float ALCOHOL_BOUNUS = 0.25f;
    public static float FREE_PORT_BONUS = 0.1f;
    public static int STAB_BONUS = 2;
    public static String CONDITION = "mild_climate";

    public static final float ACCESSIBILITY = 0.15f;
    public float total_income;


    @Override
    public void apply() {
        super.apply(true);
        market.getHazard().modifyFlat(HAZARD_SOURCE,-0.10f,"Eco Tourism");

        demand(Commodities.LUXURY_GOODS,market.getSize()+2);
        if (!isFunctional()) {
            unapply();
        }

        if(Global.getSettings().getModManager().isModEnabled("alcoholism")){
            demand("alcoholism_tea_c",5);
            demand("alcoholism_water_c",5);
            demand("alcoholism_tears_c",5);
        }
        total_income= BASE_BONUS*market.getSize()+BASE_BONUS*ACCESSIBILITY;
        if(!market.isIllegal(Commodities.DRUGS)){
            total_income+=FREE_PORT_BONUS;
        }

        if(Global.getSettings().getModManager().isModEnabled("alcoholism")){
            total_income+=ALCOHOL_BOUNUS;
        }
        for (Pair<String, Integer> deficitluxuries : getAllDeficit()) {
            if(deficitluxuries.two>0){
                total_income-=0.02f;
            }
        }
        if(isFunctional()){
            market.getIncomeMult().modifyMult(getModId(0), total_income, "Eco Tourism");
            market.getStability().modifyFlat(getModId(1), STAB_BONUS, "Eco Tourism");
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getHazard().unmodifyFlat(HAZARD_SOURCE);
        market.getIncomeMult().unmodifyMult(getModId(0));
        market.getStability().unmodifyFlat(getModId(1));
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            addStabilityPostDemandSection(tooltip, hasDemand, mode);



        }
    }


    protected void addStabilityPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        Color h = Misc.getHighlightColor();
        float opad = 10f;
        float a = total_income;
        String aStr = "+" + (float)Math.round(a * 1f) + "";
        tooltip.addPara("Flat Bonus Colony income from Tourism: %s", opad, h, aStr);

        h = Misc.getHighlightColor();
        tooltip.addPara("Stability Bonus: %s", opad, h, "" + STAB_BONUS);


    }
    public boolean isAvailableToBuild() {
        if(market.hasCondition(CONDITION)&&market.hasSpaceport()&&!market.hasCondition("pollution")&& market.hasCondition("habitable")){
            return true;
        }

        return false;
    }
    public String getUnavailableReason() {
        return "Requires a functional spaceport, And Habitable planet that's have mild climate without any pollution ";
    }
    @Override
    protected void applyAlphaCoreModifiers() {
        market.getIncomeMult().modifyFlat(getModId(1), ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
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
            text.addPara(pre + "Reduces upkeep cost by %s. " +
                            "Increases colony income by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" +
                            str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s." +
                        "Increases colony income by %s.", 0f, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" +
                        str);

    }
    @Override
    public boolean canImprove() {
        return false;
    }
    @Override
    public boolean showWhenUnavailable() {
        return true;
    }
    public String getNameForModifier() {
        return Misc.ucFirst(getCurrentName().toLowerCase());
    }

    public float getPatherInterest() {
        return super.getPatherInterest() + 0.1F;
    }
}
