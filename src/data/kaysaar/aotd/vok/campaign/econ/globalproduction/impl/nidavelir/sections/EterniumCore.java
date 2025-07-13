package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;

import java.awt.*;
import java.util.HashMap;

public class EterniumCore extends NidavelirBaseSection {

    int effectivePercent = 5;

    @Override
    public boolean isRestorationAllowed() {
        return megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored&&super.isRestorationAllowed();
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned *effectivePercent*getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));;
        tooltip.addPara("For each assigned manpower point to section:", 5f);
        createTooltipForMainSection(tooltip);
    }
    @Override
    public void unapplyEffectOfSection() {
        GPManager.getInstance().getSpecialProjSpeed().unmodifyMult("aotd_nidav");
    }
    @Override
    public void createTooltipForMainSection(TooltipMakerAPI tooltip) {

        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned * effectivePercent * getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));
        tooltip.addPara("Production speed of %s is increased by %s", 3f, Color.ORANGE, "black site projects", points + "%");
    }
        @Override
    public void applyEffectOfSection() {
            effectivePercent =5;
        float percent = (float) effectivePercent /100;
        float bonus = percent*getEffectiveManpowerForEffects();
        bonus*=getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0]));
        GPManager.getInstance().getSpecialProjSpeed().modifyMult("aotd_nidav",1f-bonus);
    }

    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
    }

    @Override
    public void printMenu(TooltipMakerAPI tooltip, int manpowerToBeAssigned, boolean wantToAutomate) {
        int effective = effectivePercent * manpowerToBeAssigned;

        if (!wantToAutomate) {
            tooltip.addPara("Currently assigned manpower to this structure %s", 10f, Color.ORANGE, "" + (manpowerToBeAssigned));
        }
        tooltip.addPara("Increase speed of black site projects completion by %s", 3f, Color.ORANGE, effective + "%");


    }

    @Override
    public HashMap<String, Integer> getGPUpkeep() {
        return super.getGPUpkeep();
    }


}
