package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;
import java.util.HashMap;

public class EterniumCore extends NidavelirBaseSection {

    int effectivePercent = 5;

    @Override
    public boolean isRestorationAllowed() {
        return megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored && super.isRestorationAllowed();
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned * effectivePercent * getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));
        ;
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
        tooltip.addPara("Speed of %s is increased by %s", 3f, Color.ORANGE, "black site projects", points + "%");
    }

    @Override
    public void applyEffectOfSection() {
        effectivePercent = 5;
        int points = (int) (getEffectiveManpowerForEffects() * effectivePercent * getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));
        AoTDMainResearchManager.getInstance().getManagerForPlayer().getBlackSiteSpecialProjBonus().modifyMult("aotd_nidav", (points/100f));
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
