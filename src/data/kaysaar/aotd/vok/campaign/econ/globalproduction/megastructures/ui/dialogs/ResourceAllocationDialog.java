package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections.PlutoForgeSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.BaseMegastrucutreMenu;

import java.awt.*;

public class ResourceAllocationDialog extends BaseSliderDialog{
    PlutoForgeSection section;
    String resource;
    CommoditySpecAPI specAPI;
    public ResourceAllocationDialog(BaseMegastrucutreMenu menu, String headerTitle, int mult, int maxSegments, int currSegment, int minSection, PlutoForgeSection section,String resource) {
        super(menu, headerTitle, mult, maxSegments, currSegment, minSection);
        this.section = section;
        this.resource = resource;
        specAPI = Global.getSettings().getCommoditySpec(resource);
    }
    @Override
    public float getBarY() {
        return 120f;
    }

    @Override
    public float getBarX() {
        return 5f;
    }
    @Override
    public void populateTooltipTop(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_16);
        tooltip.addPara("Current upkeep %s",5f, Color.ORANGE,Misc.getDGSCredits(20000*currentSegment));
        tooltip.addPara("Current units of %s assigned %s",5f, Color.ORANGE,specAPI.getName(),""+currentSegment);
        section.createTooltipForResourceProduction(resource,currentSegment,tooltip);

    }

    @Override
    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip) {
        return tooltip.addPara("Assigned resources : "+currentSegment*mult+" / "+maxSegment*mult, Misc.getTooltipTitleAndLightHighlightColor(),5f);

    }

    @Override
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_12);
        tooltip.addPara("Note! Even when structure produces a lot of supply units, only effective amount will be sold, to sell more on market, increase accessibility!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
    }

    @Override
    public void applyConfirmScript() {
        section.updateResourceDesignated(resource,currentSegment);
        menu.resetEntireUI();
    }
}
