package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.impl.campaign.aotd_entities.HypershuntReciverEntityPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpticCommandNexus extends GPMegaStructureSection {
    public  int currentMagnitude = 1 ;
    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
    }

    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        super.addButtonsToList(currentButtons);
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allows extraction of ores and transplutonic ores by using laser",5f);
        if(HypershuntMegastrcutre.isWithinReciverSystem(this.getMegastructureTiedTo().getEntityTiedTo())){
            tooltip.addPara("Due to connection with Hypershunt, power of laser is doubled, together with extraction yield", Misc.getPositiveHighlightColor(),5f);
        }
        if(isRestored){
            int amount = 30;
            tooltip.addPara("Currently extracting %s of %s",5f,Color.ORANGE,""+amount,"ore");
            tooltip.addPara("Currently extracting %s of %s",5f,Color.ORANGE,""+amount,"transplutonic ore");
        }
    }

    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String,Integer> map = new HashMap<>();
//        for (Map.Entry<String, Float> s : NidavelirComplexMegastructure.commoditiesProd.entrySet()) {
//            float totalVal = s.getValue() AoTDMisc.getOrDefault(penaltyMap, AoTDCommodities.REFINED_METAL,1f);
//            AoTDMisc.putCommoditiesIntoMap(map,s.getKey(), (int) totalVal);
//
//        }

        return map;

    }
}
