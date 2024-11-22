package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.Fonts;
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

public class VanguardForge  extends NidavelirBaseSection {
    int effective = 5;
    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored;
    }

    @Override
    public void unapplyEffectOfSection() {
        GPManager.getInstance().getFrigateDestroyerSpeed().unmodifyMult("aotd_nidav");
    }

    @Override
    public void applyEffectOfSection() {
        float percent = (float) effective /100;
        float bonus = percent*getEffectiveManpowerForEffects();
        bonus*=getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0]));
        GPManager.getInstance().getFrigateDestroyerSpeed().modifyMult("aotd_nidav",1f-bonus);
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned *effective*getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));;
        tooltip.addPara("For each assigned manpower point to section:",5f);
        tooltip.addPara("Currently assigned manpower to this structure %s",10f, Color.ORANGE,""+(manpowerAssigned));
        tooltip.addPara("Increase speed of special projects completion by %s",3f,Color.ORANGE,points+"%");
    }
    @Override
    public void applyAdditionalGPChanges(HashMap<String, Integer> map) {
        map.put(AoTDCommodities.REFINED_METAL,40*getCurrentManpowerAssigned());
    }

    @Override
    public void printMenu(TooltipMakerAPI tooltip, int manpowerToBeAssigned, boolean wantToAutomate) {
        tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
        if (!wantToAutomate) {
            tooltip.addPara("Currently assigned manpower to this structure %s",10f, Color.ORANGE,""+(manpowerToBeAssigned));
        }
        tooltip.addPara("Increase speed of building frigates and destroyers by %s",3f,Color.ORANGE,(effective*manpowerToBeAssigned)+"%");
    }
}
