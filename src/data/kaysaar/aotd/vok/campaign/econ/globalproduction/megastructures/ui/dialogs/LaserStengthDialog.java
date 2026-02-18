package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.OpticCommandNexus;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastructureMenu;

import java.awt.*;

public class LaserStengthDialog extends BaseSliderDialog{
    public OpticCommandNexus section;
    public LaserStengthDialog(BaseMegastructureMenu menu, String headerTitle, int mult, int maxSegments, int currSegment, int minSection, OpticCommandNexus section) {
        super(menu, headerTitle, mult, maxSegments, currSegment, minSection);
        this.section = section;
    }
    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_16);
        tooltip.addPara("Laser Strength : %s",5f, Color.ORANGE,(currentSegment*mult)+"%");
        tooltip.addPara("Additional Upkeep : %s", 5f, Color.ORANGE, Misc.getDGSCredits(OpticCommandNexus.upkeepMulter * currentSegment));
        tooltip.addPara("Supply units of %s and %s : %s",5f, Color.ORANGE,"ore","transplutonic ore",(currentSegment*5)+"");
    }

    @Override
    public float getBarY() {
        return 100f;
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

        menu.resetEntireUI();
    }

}
