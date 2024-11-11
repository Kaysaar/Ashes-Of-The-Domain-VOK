package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VanguardForge  extends NidavelirBaseSection {

    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each assigned manpower point to section:",5f);
        tooltip.addPara("Production speed of %s is increased by %s",3f,Color.ORANGE,"frigates and destroyers","5%");
        tooltip.addPara("Increase production of %s by 1 for %s points of manpower",3f,Color.ORANGE,"Ship hulls and Weapons","1");
    }
    @Override
    public void applyAdditionalGPCost(HashMap<String, Integer> map) {
        map.put(AoTDCommodities.REFINED_METAL,50);
    }
    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String, Integer>map = new HashMap<>();
        float val = 10;
        int manpower =5;
        float totalVal = val*manpower*(float)AoTDMisc.getOrDefault(penaltyMap,AoTDCommodities.REFINED_METAL,1f);
        for (String s : NidavelirComplexMegastructure.commoditiesProd) {
            AoTDMisc.putCommoditiesIntoMap(map,s, (int) totalVal);
        }

        return map;

    }
}
