package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.sections;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSection;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;

public class AegisSystem extends GPMegaStructureSection {
    public static ArrayList<String>omegaWeapons = new ArrayList<>();
    static {
        omegaWeapons.add("minipulser");
        omegaWeapons.add("shockrepeater");
        omegaWeapons.add("riftlance");
        omegaWeapons.add("realitydisruptor");
        omegaWeapons.add("riftbeam");
        omegaWeapons.add("vpdriver");
        omegaWeapons.add("cryoflux");
        omegaWeapons.add("cryoblaster");
        omegaWeapons.add("disintegrator");
        omegaWeapons.add("riftcascade");
        omegaWeapons.add("amsrm");
        omegaWeapons.add("resonatormrm");
        omegaWeapons.add("rifttorpedo");

    }

    @Override
    public void applyReductionOfUpkeep(MutableStat statToChange) {
        if(isRestored){
            float total = 1f;
            float percentage = 0.2f;
            percentage *=getPenaltyFromManager(this.getGPUpkeep().keySet().toArray(new String[0]));
            float value = total - percentage;
            statToChange.modifyMult("aegis_system",value);
        }
    }

    @Override
    public void createTooltipForBenefits(TooltipMakerAPI tooltip) {
        super.createTooltipForBenefits(tooltip);
        tooltip.addPara("Reduces upkeep of all sections by %s as long as demand for resources is met, if not then upkeep reduction will be reduced.",5f,Color.ORANGE,"10%");
        if(!isRestored){
            tooltip.addPara("Our scavenge team also reported weird readings coming from currently inaccessible sections of %s, " +
                    "similar to those of ships we met when discovered Hypershunt for first time.",5f,Color.ORANGE,this.getName());
            tooltip.addPara("Also primary  scans showcase that in this room there might be few Hypershunt taps, perhaps stored to be used once infrastructure was properly built.", Misc.getPositiveHighlightColor(),5f);
        }
        else{
            tooltip.addPara("Gained two %s weapon blueprints",5f,new Color(196, 32, 250),"[ULTRA-REDACTED]");
        }
    }

    @Override
    public void aboutToReconstructSection() {
        int firstRoll = MathUtils.getRandomNumberInRange(0,omegaWeapons.size()-1);
        int secondRoll = 0;
        while(true){
             secondRoll = MathUtils.getRandomNumberInRange(0,omegaWeapons.size()-1);
             if(firstRoll!=secondRoll){
                 break;
             }
        }
        String first = omegaWeapons.get(firstRoll);
        String second = omegaWeapons.get(secondRoll);
        Global.getSector().getPlayerFaction().addKnownWeapon(first,false);
        Global.getSector().getPlayerFaction().addKnownWeapon(second,false);
        Global.getSector().getPlayerFaction().getProduction().getGatheringPoint().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(Items.CORONAL_PORTAL,null),3);
    }
}
