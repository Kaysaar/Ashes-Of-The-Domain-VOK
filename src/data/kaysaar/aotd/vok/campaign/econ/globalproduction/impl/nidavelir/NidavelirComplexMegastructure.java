package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.NidavelirDestroyedShipyard;
import com.fs.starfarer.api.impl.campaign.NidavelirShipyard;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPIndividualMegastructreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.GPMegasturcutreMenu;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;

import java.awt.*;

public class NidavelirComplexMegastructure extends GPBaseMegastructure {
    public NidavelirShipyard shipyard;
    public int manpowerPoints;

    public int getManpowerPoints() {
        if(entityTiedTo.getMarket().getFaction()==null){
            manpowerPoints=0;
        }
        else{
            manpowerPoints = entityTiedTo.getMarket().getSize()*2;
        }
        return manpowerPoints;
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
        return super.createUIPlugin(parentPanel, menu);
    }




    @Override
    public void trueInit(String specId, SectorEntityToken entityTiedTo) {
        super.trueInit(specId, entityTiedTo);
        shipyard = (NidavelirDestroyedShipyard)entityTiedTo.getStarSystem().addCustomEntity(null,"Nid","nid_shipyards_damaged",null).getCustomPlugin();
        shipyard.trueInit("aotd_nidavelir_destroyed",null, (PlanetAPI) entityTiedTo);
    }


}
