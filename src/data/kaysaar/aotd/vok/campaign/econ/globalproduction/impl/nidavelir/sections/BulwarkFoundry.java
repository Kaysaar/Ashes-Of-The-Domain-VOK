package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;

public class BulwarkFoundry extends NidavelirBaseSection {
    int effectivePercent = 5;
    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {

        super.createTooltipForButtons(tooltip, buttonId);
    }
    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("nidavelir_nexus").isRestored&&super.isRestorationAllowed();
    }
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned *effectivePercent*getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));;
        tooltip.addPara("For each assigned manpower point to this section:",5f);
        createTooltipForMainSection(tooltip);
    }
    @Override
    public void createTooltipForMainSection(TooltipMakerAPI tooltip) {

        int manpowerAssigned = getEffectiveManpowerForEffects();
        int points = (int) (manpowerAssigned *effectivePercent*getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0])));;
        tooltip.addPara("- Production speed of %s is increased by %s",3f,Color.ORANGE,"cruisers and capitals",points+"%");
        tooltip.addPara("- All produced ships have built-in %s and %s",3f,Color.ORANGE,"Flux Distributor","Flux Coil Adjunct");
        if (isRestored) tooltip.addPara("Currently assigned manpower to this structure %s",10f, Color.ORANGE,""+(manpowerAssigned));

    }

    @Override
    public void unapplyEffectOfSection() {
        GPManager.getInstance().getCruiserCapitalSpeed().unmodifyMult("aotd_nidav");
    }
    @Override
    public void printMenu(TooltipMakerAPI tooltip, int manpowerToBeAssigned, boolean wantToAutomate) {
//        if (!wantToAutomate) {
//            tooltip.addPara("Currently assigned manpower to this structure %s",10f, Color.ORANGE,""+(manpowerToBeAssigned));
//        }
        tooltip.addPara("Increase speed of building cruisers and capitals by %s",3f,Color.ORANGE,(effectivePercent*manpowerToBeAssigned)+"%");
        tooltip.addPara("All produced ships have built-in %s and %s",3f,Color.ORANGE,"Flux Distributor","Flux Coil Adjunct");
    }
    @Override
    public void applyEffectOfSection() {
        effectivePercent =5;
        float percent = (float) effectivePercent /100;
        float bonus = percent*getEffectiveManpowerForEffects();
        bonus*=getPenaltyFromManager(NidavelirComplexMegastructure.commoditiesDemand.keySet().toArray(new String[0]));
        GPManager.getInstance().getCruiserCapitalSpeed().modifyMult("aotd_nidav",1f-bonus);
    }
    @Override
    public void applyAdditionalGPChanges(HashMap<String, Integer> map) {
        AoTDMisc.putCommoditiesIntoMap(map,AoTDCommodities.REFINED_METAL,40*getEffectiveManpowerForEffects());

    }


}
