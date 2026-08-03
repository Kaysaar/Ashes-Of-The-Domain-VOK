package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commodityDetailedInfo.AoTDDetailedCommodityPanelContent;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseSliderDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.SectionShowcaseSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto.OpticCommandNexus;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto.PlutoForgeSection;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceAllocationDialog extends BaseSliderDialog {
    PlutoForgeSection section;
    String resource;
    CommoditySpecAPI specAPI;
    public ResourceAllocationDialog(SectionShowcaseSection menu, String headerTitle, int mult, int maxSegments, int currSegment, int minSection, PlutoForgeSection section, String resource) {
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
        tooltip.addPara("Current upkeep %s",0f, Color.ORANGE,Misc.getDGSCredits(PlutoForgeSection.resUpkeepMap.getOrDefault(resource,500) *currentSegment));
        tooltip.addSectionHeading("Projected production", Alignment.MID,10f);
        LinkedHashMap<String,Integer>supply = new LinkedHashMap<>();
        for (Map.Entry<String, String> s : section.getProdMapForOre().entrySet()) {
            if(s.getValue().equals(resource)){
                supply.put(s.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(section.getProductionMap().get(s.getKey())*currentSegment,false,s.getKey()));
            }
        }
        tooltip.addCustom(new AoTDCommodityShortPanelCombined(tooltip.getWidthSoFar(),3,supply).getMainPanel(),5f);

    }

    @Override
    public LabelAPI createLabelForBar(TooltipMakerAPI tooltip) {
        return tooltip.addPara("Assigned resources : "+currentSegment*mult+" / "+maxSegment*mult, Misc.getTooltipTitleAndLightHighlightColor(),5f);

    }

    @Override
    public void populateTooltipBelow(TooltipMakerAPI tooltip, int effectiveSegment) {
        tooltip.setParaFont(Fonts.ORBITRON_12);
    }

    @Override
    public void applyConfirmScript() {
        section.updateResourceDesignated(resource,currentSegment);
        section.getMegastructureTiedTo().getIndustryTiedToMegastructureIfPresent().apply();
        menu.createUI();
    }
}
