package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;

public class FusionForge extends PlutoForgeSection  {


    @Override
    public void createTooltipForButtonsBeforeRest(TooltipMakerAPI tooltip, String buttonId) {
        if(buttonId.equals("restore")&&!isRestored&&!isRestorationAllowed()){
            tooltip.addPara("Note! First %s must be restored, before we are able to restore this section!",5f, Misc.getNegativeHighlightColor(), Color.ORANGE,"Optic Command Nexus");
        }
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+4 to metal supply");
        tooltip.addPara("For each unit of transplutonic ore dedicated to this section : %s ",5f,Color.ORANGE,"+2 to transplutonic supply");
    }
    @Override
    public void createTooltipForResourceProduction(String resource, int oreUnitsAssigned, TooltipMakerAPI tooltip) {
        if(resource.equals(Commodities.ORE)){
            int amount = 4;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;
            tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+"+amount+" supply units of metal");

        }
        if(resource.equals(Commodities.RARE_ORE)){
            int amount = 2;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;
            tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+"+amount+" supply units of transplutonics");



        }

    }
    @Override
    public int getAmountOfResources(String key) {
        if(key.equals(Commodities.METALS)){
            int amount = 4;
            amount*=this.getAssignedResources(Commodities.ORE);
            return amount;
        }
        if(key.equals(Commodities.RARE_METALS)){
            int amount = 2;
            amount*=this.getAssignedResources(Commodities.RARE_ORE);
            return amount;
        }
        return super.getAmountOfResources(key);
    }
    @Override
    public void createTooltipForResourceProductionLite(String resource, int oreUnitsAssigned, TooltipMakerAPI tooltip) {
        tooltip.addPara(this.getName(),Misc.getTooltipTitleAndLightHighlightColor(),5f);
        if(resource.equals(Commodities.ORE)){
            int amount = 4;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;

            tooltip.addPara("Currently consuming %s which results in %s",2f,Color.ORANGE,oreUnitsAssigned+"","+"+amount+" supply units of metal");

        }
        if(resource.equals(Commodities.RARE_ORE)){
            int amount = 2;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;
            tooltip.addPara("Currently consuming %s which results in %s",2f,Color.ORANGE,oreUnitsAssigned+"","+"+amount+" supply units of transplutonics");



        }
    }
}
