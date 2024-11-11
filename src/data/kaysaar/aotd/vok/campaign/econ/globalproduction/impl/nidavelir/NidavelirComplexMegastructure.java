package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.NidavelirDestroyedShipyard;
import com.fs.starfarer.api.impl.campaign.NidavelirShipyard;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
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
import java.util.HashMap;

public class NidavelirComplexMegastructure extends GPBaseMegastructure {
    public NidavelirShipyard shipyard;
    public int manpowerPoints;
    public static ArrayList<String>commoditiesProd = new ArrayList<>();
    public static ArrayList<String>commoditiesDemand = new ArrayList<>();
    static {
        commoditiesProd.add(AoTDCommodities.DOMAIN_GRADE_MACHINERY);
        commoditiesProd.add(AoTDCommodities.ADVANCED_COMPONENTS);
        commoditiesProd.add(Commodities.SHIPS);
        commoditiesProd.add(Commodities.HAND_WEAPONS);
        commoditiesDemand.add(AoTDCommodities.REFINED_METAL);

    }

    public int getManpowerPoints() {
        return entityTiedTo.getMarket().getSize()*2;
    }

    @Override
    public void createAdditionalInfoToButton(TooltipMakerAPI tooltipMakerAPI) {

        TooltipMakerAPI tooltip =  tooltipMakerAPI.beginSubTooltip(tooltipMakerAPI.getWidthSoFar());
        tooltip.addPara("Current manpower points : %s",5f, Color.ORANGE,""+getManpowerPoints()).getPosition().inTL(10,5);
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
            sections.add((NidavelirBaseSection) megaStructureSection);
        }
        return sections;
    }
    public int getRemainingManpowerPoints(){
        int current = 0;
        for (NidavelirBaseSection section : getSections()) {
            current+=section.getCurrentManpowerAssigned();
        }
        return current;
    }

}
