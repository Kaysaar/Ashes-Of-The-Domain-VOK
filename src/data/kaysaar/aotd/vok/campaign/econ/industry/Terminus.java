package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SubmarketPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.Waystation;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.submarkets.LocalResourcesSubmarketPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.ArrayList;

public class Terminus extends Waystation {

    public static float UPKEEP_MULT_PER_DEFICIT = 0.3f;
    public static final float BASE_ACCESSIBILITY = 0.4f;

    public static final float IMPROVE_ACCESSIBILITY = 0.2f;

    public static final float ALPHA_CORE_ACCESSIBILITY = 0.2f;


    public void apply() {
        super.apply(true);

        int size = market.getSize();

        demand(Commodities.FUEL, size+1);
        demand(Commodities.SUPPLIES, size+1);
        demand(Commodities.CREW, size);
        demand(Commodities.VOLATILES, 2);
        demand(Commodities.RARE_METALS, 2);
        String desc = getNameForModifier();

        float a = BASE_ACCESSIBILITY;
        if (a > 0) {
            market.getAccessibilityMod().modifyFlat(getModId(0), a, desc);
        }

        if (market.isPlayerOwned()) {
            SubmarketPlugin sub = Misc.getLocalResources(market);
            if (sub instanceof LocalResourcesSubmarketPlugin) {
                LocalResourcesSubmarketPlugin lr = (LocalResourcesSubmarketPlugin) sub;
                float mult = Global.getSettings().getFloat("stockpileMultExcess");
                lr.getStockpilingBonus(Commodities.FUEL).modifyFlat(getModId(0), (size+3) * mult);
                lr.getStockpilingBonus(Commodities.SUPPLIES).modifyFlat(getModId(0), (size+3) * mult);
                lr.getStockpilingBonus(Commodities.CREW).modifyFlat(getModId(0), (size+3) * mult);
                lr.getStockpilingBonus(Commodities.VOLATILES).modifyFlat(getModId(0), 2f * mult);
                lr.getStockpilingBonus(Commodities.RARE_METALS).modifyFlat(getModId(0), 2f * mult);
            }
        }


        if (!isFunctional()) {
            supply.clear();
            unapply();
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.setHasWaystation(false);
        market.getAccessibilityMod().unmodifyFlat(getModId(0));
        market.getAccessibilityMod().unmodifyFlat(getModId(1));
        market.getAccessibilityMod().unmodifyFlat(getModId(2));



        if (market.isPlayerOwned()) {
            SubmarketPlugin sub = Misc.getLocalResources(market);
            if (sub instanceof LocalResourcesSubmarketPlugin) {
                LocalResourcesSubmarketPlugin lr = (LocalResourcesSubmarketPlugin) sub;
                // base bonuses
                lr.getStockpilingBonus(Commodities.FUEL).unmodifyFlat(getModId(0));
                lr.getStockpilingBonus(Commodities.SUPPLIES).unmodifyFlat(getModId(0));
                lr.getStockpilingBonus(Commodities.CREW).unmodifyFlat(getModId(0));
                lr.getStockpilingBonus(Commodities.VOLATILES).unmodifyFlat(getModId(0));
                lr.getStockpilingBonus(Commodities.RARE_METALS).unmodifyFlat(getModId(0));
            }
        }
    }

    protected float getUpkeepPenalty(Pair<String, Integer> deficit) {
        float loss = deficit.two * UPKEEP_MULT_PER_DEFICIT;
        if (loss < 0) loss = 0;

        return loss;
    }

    protected Pair<String, Integer> getUpkeepAffectingDeficit() {
        return getMaxDeficit(Commodities.FUEL, Commodities.SUPPLIES, Commodities.CREW);
    }

    @Override
    protected void addPostDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        if (!market.isPlayerOwned()) return;

        float opad = 10f;

//		tooltip.addPara("As long as demand is met, allows the colony to stockpile fuel, supplies, and crew, even " +
//						"if it does not produce them locally.", opad);
    }

    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            MutableStat fake = new MutableStat(0);

            String desc = getNameForModifier();
            float a = BASE_ACCESSIBILITY;
            if (a > 0) {
                fake.modifyFlat(getModId(0), a, desc);
            }
            float total = a;
            String totalStr = "+" + (int)Math.round(total * 100f) + "%";
            Color h = Misc.getHighlightColor();
            if (total < 0) {
                h = Misc.getNegativeHighlightColor();
                totalStr = "" + (int)Math.round(total * 100f) + "%";
            }
            float opad = 10f;
            float pad = 3f;
            if (total >= 0) {
                tooltip.addPara("Accessibility bonus: %s", opad, h, totalStr);
            } else {
                tooltip.addPara("Accessibility penalty: %s", opad, h, totalStr);
            }

            tooltip.addPara("As long as the demands are met, it allows the colony to stockpile fuel, supplies, and crew, even " +
                    "if it does not produce them locally. The stockpile capacity exceeds one generated by local production.", opad);
        }
    }

    @Override
    protected void applyAlphaCoreModifiers() {
        if (market.isPlayerOwned()) {
            SubmarketPlugin sub = Misc.getLocalResources(market);
            if (sub instanceof LocalResourcesSubmarketPlugin) {
                float bonus = market.getSize() * Global.getSettings().getFloat("stockpileMultExcess");
                LocalResourcesSubmarketPlugin lr = (LocalResourcesSubmarketPlugin) sub;
                lr.getStockpilingBonus(Commodities.FUEL).modifyFlat(getModId(1), bonus);
                lr.getStockpilingBonus(Commodities.SUPPLIES).modifyFlat(getModId(1), bonus);
                lr.getStockpilingBonus(Commodities.CREW).modifyFlat(getModId(1), bonus);
            }
        }
    }

    @Override
    protected void applyNoAICoreModifiers() {
        if (market.isPlayerOwned()) {
            SubmarketPlugin sub = Misc.getLocalResources(market);
            if (sub instanceof LocalResourcesSubmarketPlugin) {
                LocalResourcesSubmarketPlugin lr = (LocalResourcesSubmarketPlugin) sub;
                lr.getStockpilingBonus(Commodities.FUEL).unmodifyFlat(getModId(1));
                lr.getStockpilingBonus(Commodities.SUPPLIES).unmodifyFlat(getModId(1));
                lr.getStockpilingBonus(Commodities.CREW).unmodifyFlat(getModId(1));
            }
        }
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
        float a = ALPHA_CORE_ACCESSIBILITY;
        String aStr = "" + (int)Math.round(a * 100f) + "%";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demands by %s units. " +
                            "Greatly increases stockpile capacity.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                    aStr);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demands by %s units." +
                        "Greatly increases stockpile capacity.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                aStr);

    }



    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.INTERSTELLAR_LOGISTICS,market);
    }

    public boolean isAvailableToBuild() {
        return market.hasSpaceport()&& AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.INTERSTELLAR_LOGISTICS,market);
    }

    public String getUnavailableReason() {
        ArrayList<String> reasons = new ArrayList<>();
        if(!AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.INTERSTELLAR_LOGISTICS,market)){
            reasons.add(AoTDMainResearchManager.getInstance().getNameForResearchBd(AoTDTechIds.INTERSTELLAR_LOGISTICS));

        }
        StringBuilder bd = new StringBuilder();
        boolean insert = false;
        for (String reason : reasons) {
            if(insert){
                bd.append("\n");
            }
            bd.append(reason);

            insert = true;
        }

        return bd.toString();
    }


    @Override
    public boolean canImprove() {
        return true;
    }

}
