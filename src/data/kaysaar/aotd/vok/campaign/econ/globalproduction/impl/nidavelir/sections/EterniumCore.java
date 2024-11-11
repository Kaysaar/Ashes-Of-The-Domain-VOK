package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class EterniumCore extends NidavelirBaseSection {


    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each assigned manpower point to section:",5f);
        tooltip.addPara("Increase speed of special projects completion by %s",3f,Color.ORANGE,"2%");
        tooltip.addPara("Increase GP production of %s by %s for each assigned point of manpower, as long as demand for refined metal is met",5f,Color.ORANGE,"ship hulls, weapons, advanced components and domain heavy machinery",""+10);
        tooltip.addPara("Increase GP demand of %s by %s for each assigned point of manpower",5f,Color.ORANGE,"refined metal",""+10);

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

    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
    }
}
