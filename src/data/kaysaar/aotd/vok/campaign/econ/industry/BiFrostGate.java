package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;

public class BiFrostGate extends BaseIndustry {
    public SectorEntityToken gate;
    public float BASE_ACCESSIBILITY = 0.5f;

    @Override
    public void apply() {
        super.apply(true);
        demand("bifrost", AoTDCommodities.PURIFIED_TRANSPLUTONICS, 5, "Bi Frost Gate Stabilization");
        Pair<String, Integer> max = getMaxDeficit(AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        if (max.two > 0 && gate != null) {
            gate.getMemory().set("$supplied", false);
        }
        if (max.two == 0 && gate != null) {
            gate.getMemory().set("$supplied", true);
        }
        String desc = getNameForModifier();

        float a = BASE_ACCESSIBILITY;
        if (a > 0) {
            market.getAccessibilityMod().modifyFlat(getModId(0), a, desc);
        }
    }

    @Override
    public boolean showWhenUnavailable() {
        //TODO - implement research later
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.BIFROST_GATE,market);
    }








    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if (mode.equals(IndustryTooltipMode.NORMAL)) {
            if (gate != null) {
                if (gate.getMemory().is("$used", true)) {
                    float value = gate.getMemory().getFloat("$cooldown");
                    String days = " days";
                    if (value <= 1) {
                        days = " day";
                    }
                    tooltip.addPara("Curently the gateway in " + market.getName() + " is inactive for " + (int) value + days, Misc.getNegativeHighlightColor(), 10f);
                }
            }
            tooltip.addPara("Accessibility bonus : 50%", Color.ORANGE, 10f);
            tooltip.addPara("This planet is now connected via the Bifrost Network.", Color.ORANGE, 10f);
        }
    }

}
