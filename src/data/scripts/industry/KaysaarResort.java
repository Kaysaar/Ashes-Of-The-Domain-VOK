package data.scripts.industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import java.awt.*;

public class KaysaarResort extends BaseIndustry implements MarketImmigrationModifier {
    public static float ALPHA_CORE_BONUS = 1.5f;
    public static float ALCOHOL_BOUNUS = 1.30f;
    public static float BETA_CORE_BONUS = 1.4f;
    public static float GAMMA_CORE_BONUS = 1.3f;
    public static int STAB_BONUS = 2;
    public static String CONDITION_MILD = "mild_climate";
    public static String CONDITION_HABITABLE="habitable";

    public float total_income;


    @Override
    public void apply() {
        super.apply(true);
        String desc = getNameForModifier();
        demand(Commodities.LUXURY_GOODS,market.getSize());
        if (!isFunctional()) {
            unapply();
        }

        if(Global.getSettings().getModManager().isModEnabled("alcoholism")){
            demand("alcoholism_stout_c",4);
            demand("alcoholism_sunshine_c",4);
            demand("alcoholism_tea_c",4);
            demand("alcoholism_water_c",4);
            demand("alcoholism_king_c",4);
            demand("alcoholism_tears_c",4);
            demand("alcoholism_absynth_c",4);
        }
        market.getStability().modifyFlat(getModId(1), STAB_BONUS, "Tourism");

    }
    @Override
    public void applyAICoreToIncomeAndUpkeep() {
        float accessMult = (float) Math.round(this.market.getAccessibilityMod().computeEffective(0.0F) * 100.0F) /150.0F;
        String access = "Accessibility";
        String alkoholism = "Alcohol Supply";
        if(Global.getSettings().getModManager().isModEnabled("alcoholism")){
            Pair<String, Integer> deficitAlcohol = getMaxDeficit(
                    "alcoholism_stout_c","alcoholism_sunshine_c",
            "alcoholism_tea_c","alcoholism_water_c","alcoholism_king_c","alcoholism_tears_c",
                   "alcoholism_absynth_c" );
            int maxDeficit = market.getSize() - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
            if (deficitAlcohol.two > maxDeficit) deficitAlcohol.two = maxDeficit;
            if(deficitAlcohol.two<=0){
                this.getIncome().modifyMult("tourism_alco", ALCOHOL_BOUNUS, alkoholism);
            }
            else{
                this.getIncome().unmodifyFlat("tourism_alco");
            }


        }

        this.getIncome().modifyMult("tourism_access", accessMult, access);

        if (this.aiCoreId != null) {
            String name = "AI Core assigned";
            if (this.aiCoreId.equals("alpha_core")) {
                name = "Alpha Core assigned";
                this.getIncome().modifyMult("ind_core", ALPHA_CORE_BONUS, name);
            } else if (this.aiCoreId.equals("beta_core")) {
                name = "Beta Core assigned";
                this.getIncome().modifyMult("ind_core", BETA_CORE_BONUS, name);
            } else if (this.aiCoreId.equals("gamma_core")) {
                name = "Gamma Core assigned";
                this.getIncome().modifyMult("ind_core", GAMMA_CORE_BONUS, name);
            }
        } else {
            this.getUpkeep().unmodifyMult("ind_core");
        }
    }

        @Override
    public void unapply(){
        super.unapply();
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

        h = Misc.getHighlightColor();
        tooltip.addPara("Stability Bonus: %s", opad, h, "" + STAB_BONUS);

    }
    public boolean isAvailableToBuild() {
        if(market.hasCondition(CONDITION_MILD)&&market.hasCondition(CONDITION_HABITABLE)&&market.hasSpaceport()&&!market.hasCondition("pollution")&& market.hasCondition("habitable")){
            return true;
        }
        return false;
    }
    public String getUnavailableReason() {
        return "Requires a functional spaceport, And Habitable planet that's have mild climate without any pollution ";
    }
    @Override
    protected void applyAlphaCoreModifiers() {

    }

    @Override
    protected void applyNoAICoreModifiers() {
        market.getIncomeMult().unmodifyPercent(getModId(1));
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }

    public void addAlphaCoreDescription(TooltipMakerAPI tooltip, Industry.AICoreDescriptionMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();
        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(this.aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48.0F);
            text.addPara(pre + "Increases income by %s.", 0.0F, highlight, new String[]{"50%"});
            tooltip.addImageWithText(opad);
        } else {
            tooltip.addPara(pre + "Increases income by %s.", opad, highlight, new String[]{"50%"});
        }

    }

    public void addBetaCoreDescription(TooltipMakerAPI tooltip, Industry.AICoreDescriptionMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();
        String pre = "Beta-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Beta-level AI core. ";
        }

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(this.aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48.0F);
            text.addPara(pre + "Increases income by %s.", opad, highlight, new String[]{"40%"});
            tooltip.addImageWithText(opad);
        } else {
            tooltip.addPara(pre + "Increases income by %s.", opad, highlight, new String[]{"40%"});
        }

    }

    public void addGammaCoreDescription(TooltipMakerAPI tooltip, Industry.AICoreDescriptionMode mode) {
        float opad = 10.0F;
        Color highlight = Misc.getHighlightColor();
        String pre = "Gamma-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Gamma-level AI core. ";
        }

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(this.aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48.0F);
            text.addPara(pre + "Increases income by %s.", opad, highlight, new String[]{"30%"});
            tooltip.addImageWithText(opad);
        } else {
            tooltip.addPara(pre + "Increases income by %s.", opad, highlight, new String[]{"30%"});
        }

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


    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {

    }

    public float getPatherInterest() {
        return super.getPatherInterest() + 2.0F;
    }
    public boolean canBeDisrupted() {
        return true;
    }
}
