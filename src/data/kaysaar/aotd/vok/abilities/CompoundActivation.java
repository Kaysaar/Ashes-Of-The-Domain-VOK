package data.kaysaar.aotd.vok.abilities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.impl.campaign.abilities.BaseAbilityPlugin;
import com.fs.starfarer.api.impl.campaign.abilities.BaseToggleAbility;
import com.fs.starfarer.api.impl.campaign.abilities.ToggleAbilityWithCost;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript;
import data.kaysaar.aotd.vok.scripts.misc.AotdFuelConsumptionV2;

import java.awt.*;
import java.util.List;

import static data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript.getCalculatedCompound;

public class CompoundActivation extends BaseToggleAbility {

    @Override
    protected void activateImpl() {
        AoTDFuelConsumptionScript.turnOnCompound();
    }

    @Override
    protected void applyEffect(float amount, float level) {

    }

    @Override
    protected void deactivateImpl() {
        AoTDFuelConsumptionScript.turnOffCompound();
    }

    @Override
    protected void cleanupImpl() {

    }
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        Color gray = Misc.getGrayColor();
        Color highlight = Misc.getHighlightColor();

        String status = " (off)";
        if (turnedOn) {
            status = " (on)";
        }

        if (!Global.CODEX_TOOLTIP_MODE) {
            LabelAPI title = tooltip.addTitle("Compound Infusion " + status);
            title.highlightLast(status);
            title.setHighlightColor(gray);
        } else {
            tooltip.addSpacer(-10f);
        }

        float pad = 10f;


        tooltip.addPara(
                "By mixing Compound with antimatter fuel, the fleet can achieve significantly higher fuel efficiency, " +
                        "greatly reducing antimatter consumption during long-distance travel.",
                Misc.getPositiveHighlightColor(),
                pad
        );

        tooltip.addPara(
                "Effective fuel yield is increased by %s times.",
                pad,
                Color.ORANGE,
                "" + (int) Math.round(10)
        );

        if (!Global.CODEX_TOOLTIP_MODE) {
            CampaignFleetAPI playerFleet = getFleet();
            if (playerFleet != null) {

                int compound = Math.round( AoTDFuelConsumptionScript.getCompound(playerFleet.getCargo()));
                int fuel = (int) playerFleet.getCargo().getFuel();
                int effectiveFuel = Math.round(getCalculatedCompound(fuel, compound));

                tooltip.addPara(
                        "The fleet is currently carrying %s units of Compound and %s units of fuel, " +
                                "increasing the fleet’s effective fuel capacity to %s.",
                        pad,
                        Color.ORANGE,
                        "" + compound,
                        "" + fuel,
                        "" + effectiveFuel
                );

                if (compound <= 0) {
                    tooltip.addPara("No Compound available.", Misc.getNegativeHighlightColor(), pad);
                }
            }
        }

        tooltip.addPara(
                "Using Compound in abyssal hyperspace reduces the burn-level penalty by %s.",
                pad,
                Color.ORANGE,
                "50%"
        );

        addIncompatibleToTooltip(tooltip, expanded);



    }

    @Override
    public List<AbilityPlugin> getInterruptedList() {
        return super.getInterruptedList();
    }
}
