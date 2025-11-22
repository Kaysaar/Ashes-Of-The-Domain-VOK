package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.sections;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.HashMap;

public class PurificationCore extends PlutoForgeSection {
    @Override
    public boolean isRestorationAllowed() {
        return  megastructureTiedTo.getSectionById("pluto_ocn").isRestored&&super.isRestorationAllowed();
    }
    @Override
    public void createTooltipForButtonsBeforeRest(TooltipMakerAPI tooltip, String buttonId) {
        if(buttonId.equals("restore")&&!isRestored&&!isRestorationAllowed()){
            tooltip.addPara("Note! The %s must be restored first, before we are able to restore this section!",5f, Misc.getNegativeHighlightColor(), Color.ORANGE,"Optic Command Nexus");
        }
    }
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+2 to refined metal supply and +20 GP units of refined metal");
        tooltip.addPara("For each unit of transplutonic ore dedicated to this section : %s ",5f,Color.ORANGE,"+1 to purified transplutonics supply and +10 GP units of purified transplutonics");
    }

    @Override
    public void createTooltipForResourceProduction(String resource, int oreUnitsAssigned, TooltipMakerAPI tooltip) {
        if(resource.equals(Commodities.ORE)){
            int amount = 2;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;
            tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+"+amount+" supply units and +"+gp+" GP units of refined metal");

        }
        if(resource.equals(Commodities.RARE_ORE)){
            int amount = 1;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;
            tooltip.addPara("For each unit of ore dedicated to this section : %s ",5f,Color.ORANGE,"+"+amount+" supply units and +"+gp+" GP units of purified transplutonics");



        }

    }

    @Override
    public int getAmountOfResources(String key) {
        if(key.equals(AoTDCommodities.REFINED_METAL)){
            int amount = 2;
            amount*=this.getAssignedResources(Commodities.ORE);
            return amount;
        }
        if(key.equals(AoTDCommodities.PURIFIED_TRANSPLUTONICS)){
            int amount = 1;
            amount*=this.getAssignedResources(Commodities.RARE_ORE);
            return amount;
        }
        return super.getAmountOfResources(key);
    }

    @Override
    public void createTooltipForResourceProductionLite(String resource, int oreUnitsAssigned, TooltipMakerAPI tooltip) {
        tooltip.addPara(this.getName(),Misc.getTooltipTitleAndLightHighlightColor(),5f);
        if(resource.equals(Commodities.ORE)){
            int amount = 2;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;

            tooltip.addPara("Currently consuming %s which results in %s",2f,Color.ORANGE,oreUnitsAssigned+"","+"+amount+" supply units and +"+gp+" GP units of refined metal");

        }
        if(resource.equals(Commodities.RARE_ORE)){
            int amount = 1;
            amount*=oreUnitsAssigned;
            int gp = amount* GPManager.scale;

            tooltip.addPara("Currently consuming %s which results in %s",2f,Color.ORANGE,oreUnitsAssigned+"","+"+amount+" supply units and +"+gp+" GP units of purified transplutonics");



        }

    }
    @Override
    public HashMap<String, Integer> getProduction(HashMap<String, Float> penaltyMap) {
        HashMap<String, Integer>map = new HashMap<>();
        int ore = getAssignedResources(Commodities.ORE);
        int r_ore = getAssignedResources(Commodities.RARE_ORE);
        int oMult =2;
        int orMult = 1;
        int amountOre = ore*oMult;
        int amountROre = r_ore*orMult;
        int gpOre =amountOre*GPManager.scale;
        int gpROre = amountROre*GPManager.scale;

        AoTDMisc.putCommoditiesIntoMap(map, AoTDCommodities.REFINED_METAL,gpOre);
        AoTDMisc.putCommoditiesIntoMap(map,AoTDCommodities.PURIFIED_TRANSPLUTONICS,gpROre);
        return map;

    }

}
