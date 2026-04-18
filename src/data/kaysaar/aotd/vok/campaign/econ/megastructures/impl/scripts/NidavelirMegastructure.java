package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyardVisual;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.nidavelir.BaseNidavelirSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;

public class NidavelirMegastructure extends BaseMegastructureScript {
    NidavelirShipyardVisual visual;
    public int getManpowerGenerated(){
        return tiedMarket.getSize()*2;
    }
    public int getAvailableManpower(){
        int curr = getManpowerGenerated();
        for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
            if(megaStructureSection instanceof BaseNidavelirSection section){
                curr-=section.getAssignedManpower();
            }
        }
        return curr;

    }


    @Override
    public boolean doesHaveCustomSectionForTooltip() {
        return true;
    }
    @Override
    public void printCustomSection(TooltipMakerAPI tooltip) {
        if(getSectionById("nidavelir_nexus").isRestored()){
            tooltip.addSectionHeading("Dedicated Manpower", Alignment.MID,5f);
            tooltip.addPara("Due to current market size (%s) we are able to assign up to %s points of manpower across all sections. Each assigned point will boost production of said section, as well as section's bonuses",3f, Color.ORANGE,tiedMarket.getSize()+"",getManpowerGenerated()+"");
        }
        else{
            tooltip.addSectionHeading("Damaged Ring Section", Alignment.MID, 5f);
            tooltip.addPara(
                    "The severe degradation of Nidavelir’s ring system has rendered most sections inoperable. Restoration efforts must begin with the %s section before any further repairs can proceed.",
                    3f,
                    Color.ORANGE,
                    getSectionById("nidavelir_nexus").getName()
            );        }


    }
    @Override
    public void createCustomEffectsTooltip(TooltipMakerAPI tooltip) {
        for (BaseMegastructureSection megaStructureSection : megaStructureSections) {
            if(!megaStructureSection.isRestored())continue;
            megaStructureSection.createEffectSection(tooltip,true);
        }
    }



    @Override
    public boolean doesHaveCustomEffects() {
        return true;
    }


    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo, MarketAPI tiedMarket) {
        super.trueInit(specId, entityTiedTo,tiedMarket);
        visual = (NidavelirShipyardVisual) entityTiedTo.getStarSystem().addCustomEntity(null, "Nid", "nid_shipyards_damaged", null).getCustomPlugin();
        visual.getEntity().setLocation(0,30000);
        visual.trueInit("aotd_nidavelir_destroyed", null, (PlanetAPI) entityTiedTo);
    }

    @Override
    public Industry getIndustryTiedToMegastructureIfPresent() {
        if(tiedMarket==null)return null;
        return tiedMarket.getIndustries().stream().filter(x->x.getSpec().getId().equals("nidavelir_complex")).findFirst().orElse(null);
    }

    public void setVisual(NidavelirShipyardVisual visual) {
        this.visual = visual;
    }

    public NidavelirShipyardVisual getVisual() {
        return visual;
    }
}
