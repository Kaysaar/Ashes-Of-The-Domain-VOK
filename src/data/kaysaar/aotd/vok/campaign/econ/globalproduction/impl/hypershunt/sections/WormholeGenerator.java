package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.CWFailsafeNotification;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ButtonData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.OnHoverButtonTooltip;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class WormholeGenerator extends GPMegaStructureSection {
    public int range =1;
    public int purifiedTransplutonicsPerRange = 2;
    public  int mult =10;
    public int sections =7;
    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Allow to increase effective range of Hypershunt up to %s, as long as upkeep of purified transplutonics is met.",5f,Color.ORANGE,"70 LY");
        tooltip.addSectionHeading("Effective range",Alignment.MID,5f);
        tooltip.addPara("Current effective range is %s LY which costs us %s purified transplutonics",5f,Color.ORANGE,""+getCalculatedRange(),""+getPurifiedTransplutonicsPerRange());
    }

    @Override
    public void apply() {
        float penalty = getPenaltyFromManager(AoTDCommodities.PURIFIED_TRANSPLUTONICS);
        if(penalty<1&&range!=1){
            range = 1;
            Global.getSector().getIntelManager().addIntel(new CWFailsafeNotification(this.megastructureTiedTo));
        }
    }

    @Override
    public void applyAdditionalGPChanges(HashMap<String, Integer> map) {
        if(isRestored){
            map.put(AoTDCommodities.PURIFIED_TRANSPLUTONICS,getPurifiedTransplutonicsPerRange());
        }
    }

    @Override
    public float addAdditionalUpkeep() {
        return 20000*(range-1);
    }

    public int getPurifiedTransplutonicsPerRange(){
        if(purifiedTransplutonicsPerRange<=0){
            purifiedTransplutonicsPerRange = 2 ;
        }
        return purifiedTransplutonicsPerRange*(getCalculatedRange()-10);
    }
    public int getCalculatedRange(){
        if(range<=0){
            range =1;
        }
        if(mult<=0){
            mult = 10;
        }
        if(sections<=0){
            sections =7;
        }
        return range*mult;
    }
    @Override
    public void addButtonsToList(LinkedHashMap<String, ButtonData> currentButtons) {
        ButtonData data1 = new ButtonData("Adjust effective range", this, this.isRestored, Color.CYAN, "adjustRange", new OnHoverButtonTooltip(this, "adjustRange"), "adjustRange", this.getSpec().getSectionID());
        currentButtons.put("adjustRange", data1);
    }

    @Override
    public void createTooltipForButtons(TooltipMakerAPI tooltip, String buttonId) {
        super.createTooltipForButtons(tooltip, buttonId);
        if(buttonId.equals("adjustRange")) {
            tooltip.addPara("Once restored we can adjust effective range of hypershunt",5f);
        }
    }
}
