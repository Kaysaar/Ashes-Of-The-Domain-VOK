package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseSliderDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.SectionShowcaseSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto.OpticCommandNexus;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LaserStengthDialog extends BaseSliderDialog {
    public OpticCommandNexus section;
    public LaserStengthDialog(SectionShowcaseSection menu, String headerTitle, int mult, int maxSegments, int currSegment, int minSection, OpticCommandNexus section) {
        super(menu, headerTitle, mult, maxSegments, currSegment, minSection);
        this.section = section;
    }
    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_16);
        tooltip.addPara("Laser Strength : %s",0f, Color.ORANGE,(currentSegment*mult)+"%");
        tooltip.addPara("Additional Upkeep : %s", 5f, Color.ORANGE, Misc.getDGSCredits(OpticCommandNexus.upkeepMulter * currentSegment));
        LinkedHashMap<String,Integer>supplies = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> s : section.getProductionMap().entrySet()) {
            supplies.put(s.getKey(),  AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(s.getValue()*currentSegment,false,s.getKey()));
        }
        tooltip.addSectionHeading("Production", Alignment.MID,8f);
        tooltip.addCustom(new AoTDCommodityShortPanelCombined(tooltip.getWidthSoFar()-10,5,supplies).getMainPanel(),5f);
    }

    @Override
    public float getBarY() {
        return 135f;
    }

    @Override
    public float getBarX() {
        return 5f;
    }

    @Override
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_12);
        if(currentSegment==0&&section.isFiringLaser()){
            tooltip.addPara("Setting it to 0% will result in the shutdown of the laser!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        }
        else if(currentSegment!=0&&!section.isFiringLaser()){
            tooltip.addPara("Laser firing sequence will commence once the strength of the laser is confirmed",Misc.getPositiveHighlightColor(),5f);
        }
        else{
            tooltip.addPara("Adjusting laser range can help maximize profits",Misc.getTooltipTitleAndLightHighlightColor(),5f);
            tooltip.addPara("Note: Decreasing strength of laser might also require assigning ores again!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        }

    }

    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip){
        int percent = currentSegment*mult;
        return tooltip.addPara("Laser strength : %s",5f,Color.RED,percent+"%");
    }
    @Override
    public void applyConfirmScript() {
        section.setCurrentMagnitude(currentSegment);

        menu.createUI();
    }

}
