package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirDestroyedShipyard;
import com.fs.starfarer.api.impl.campaign.aotd_entities.NidavelirShipyard;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.sections.NidavelirBaseSection;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.ui.NidavelirUI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class NidavelirComplexMegastructure extends GPBaseMegastructure {
    public NidavelirShipyard shipyard;
    public static LinkedHashMap<String,Float> commoditiesProd = new LinkedHashMap<>();
    public static LinkedHashMap<String,Float> commoditiesDemand = new LinkedHashMap<>();
    static {
        commoditiesProd.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,20f);
        commoditiesProd.put(AoTDCommodities.ADVANCED_COMPONENTS,30f);
        commoditiesProd.put(Commodities.SHIPS,50f);
        commoditiesProd.put(Commodities.HAND_WEAPONS,50f);
        commoditiesDemand.put(AoTDCommodities.REFINED_METAL,50f);

    }

    public int getManpowerPoints() {
        return entityTiedTo.getMarket().getSize()*2;
    }

    @Override
    public void createAdditionalInfoForMega(TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading("Current effects", Alignment.MID,5f);
        for (NidavelirBaseSection section : getSections()) {
            if(section.isRestored){
                section.createTooltipForMainSection(tooltip);
            }

        }
        tooltip.addSectionHeading("Accessibility selling power",Alignment.MID,5f);
        int access = (int) Math.floor((getEntityTiedTo().getMarket().getAccessibilityMod().getFlatBonus()*10));
        tooltip.addPara("Maximum amount of %s units of supply can sold due to %s accessibility",5f,Color.ORANGE,access+"",(int)(getEntityTiedTo().getMarket().getAccessibilityMod().getFlatBonus()*100)+"%");


    }

    @Override
    public void createAdditionalInfoToButton(TooltipMakerAPI tooltipMakerAPI) {

        TooltipMakerAPI tooltip =  tooltipMakerAPI.beginSubTooltip(tooltipMakerAPI.getWidthSoFar());
        tooltip.addPara("Current manpower points : %s",5f, Color.ORANGE,""+getRemainingManpowerPoints()).getPosition().inTL(10,5);
        tooltipMakerAPI.addCustom(tooltip,5f);
        tooltipMakerAPI.setHeightSoFar(tooltipMakerAPI.getHeightSoFar()+20);
    }

    @Override
    public GPIndividualMegastructreMenu createUIPlugin(CustomPanelAPI parentPanel, GPMegasturcutreMenu menu) {
        return new NidavelirUI(this,parentPanel,menu);
    }


    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo) {
        super.trueInit(specId, entityTiedTo);
        shipyard = (NidavelirDestroyedShipyard)entityTiedTo.getStarSystem().addCustomEntity(null,"Nid","nid_shipyards_damaged",null).getCustomPlugin();
        shipyard.trueInit("aotd_nidavelir_destroyed",null, (PlanetAPI) entityTiedTo);
    }
    public ArrayList<NidavelirBaseSection> getSections(){
        ArrayList<NidavelirBaseSection>sections = new ArrayList<>();
        for (GPMegaStructureSection megaStructureSection : getMegaStructureSections()) {
            if(megaStructureSection instanceof NidavelirBaseSection){
                sections.add((NidavelirBaseSection) megaStructureSection);

            }
        }
        return sections;
    }
    public int getRemainingManpowerPoints(){
        int current = 0;
        for (NidavelirBaseSection section : getSections()) {
            current+=section.getCurrentManpowerAssigned();
        }
        return getManpowerPoints()-current;
    }

}
