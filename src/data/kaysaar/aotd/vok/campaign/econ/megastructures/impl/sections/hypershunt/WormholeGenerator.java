package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.hypershunt;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider.RangeIncreaseDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;
import java.util.ArrayList;

public class WormholeGenerator extends BaseMegastructureSection {
    public int range = 1;
    public int purifiedTransplutonicsPerRange = 2;
    public int mult = 10;
    public int sections = 7;
    public int effectiveRange = 1;

    @Override
    public float getUpkeepOfSection() {
        return 20000 * (effectiveRange - 1);
    }

    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        tl.addSectionHeading("Inter-stellar energy grid", Alignment.MID, 5f);
        if (!isRestored) {
            tl.addPara("Once restored, allows to extend hypershunt's effective range up to %s LY", 5f, Color.ORANGE, "70");
        } else {
            tl.addPara("Wormhole Generator operational!", Misc.getPositiveHighlightColor(), 5f);
            tl.addPara("Current effective range : %s LY", 5f, Color.ORANGE, getCalculatedRange() + "");
        }


    }

    public int getPurifiedTransplutonicsPerRange() {
        if (purifiedTransplutonicsPerRange <= 0) {
            purifiedTransplutonicsPerRange = 2;
        }
        return purifiedTransplutonicsPerRange * (range - 1);
    }

    public int getEffectiveRange() {
        return Math.min(1, effectiveRange);
    }

    public int getCalculatedRange() {
        return getEffectiveRange() * mult;
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {
        if (isRestored) {
            ind.demand(AoTDCommodities.PURIFIED_TRANSPLUTONICS, getPurifiedTransplutonicsPerRange());
            int deficit = ind.getMaxDeficit(AoTDCommodities.PURIFIED_TRANSPLUTONICS).two;
            int range = getRange();
            int expectedRange = range - deficit;
            setEffectiveRange(expectedRange);

            ind.getUpkeep().modifyFlat("upkeep_def", getUpkeepOfSection(), getName());

        }


    }

    public void setEffectiveRange(int effectiveRange) {
        this.effectiveRange = effectiveRange;
        if (effectiveRange <= 0) {
            effectiveRange = 1;
        }
    }

    public int getRange() {
        return range;
    }

    @Override
    public void reportButtonPressedImpl(ButtonAPI buttonAPI, BaseMegastructureDialogContent dialogContent) {
        String data = (String) buttonAPI.getCustomData();
        if (data != null) {
            if (data.contains("adjustRange")) {
                BasePopUpDialog dialog = new RangeIncreaseDialog(this, dialogContent.getSection(), "Hypershunt Range");
                AshMisc.initPopUpDialog(dialog, 500, 300);
            }
        }

    }

    @Override
    public void addAdditionalButtonsForSection(ArrayList<ButtonAPI> bt, float width, float heightOfButtons, TooltipMakerAPI tooltip) {
        ButtonAPI button = tooltip.addButton("Adjust effective range", "adjustRange", Misc.getBasePlayerColor(), Color.cyan.darker(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored);
        tooltip.addTooltipTo(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 300;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Once restored we can adjust the effective range of the Hypershunt", 5f);
            }
        }, button, TooltipMakerAPI.TooltipLocation.LEFT, false);
    }
}

