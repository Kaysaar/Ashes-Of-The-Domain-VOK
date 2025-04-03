package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class AoTDFuelTooltip implements TooltipMakerAPI.TooltipCreator {
    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return 400;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addTitle("Fuel Capacity");
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        playerFleet.getFleetData().getMaxBurnLevel();
        tooltip.addPara("The total fuel capacity of your fleet is %s. The fleet is carrying %s fuel",10f, Color.ORANGE,""+(int)playerFleet.getCargo().getFuel(),""+(int)playerFleet.getCargo().getMaxFuel());
        if(expanded){

        }
        else{
            tooltip.addPara("Traveling in hyperspace requires %s fuel per equivalent normal-space light year. At the maximum burn level of %s, this translates to %s fuel and %s light years per day",5f,Color.ORANGE,
                    Misc.getRoundedValueMaxOneAfterDecimal(playerFleet.getLogistics().getBaseFuelCostPerLightYear()),
                    Misc.getRoundedValueMaxOneAfterDecimal(playerFleet.getFleetData().getBurnLevel()),
                    Misc.getRoundedValueMaxOneAfterDecimal(Misc.getFuelPerDay(playerFleet,playerFleet.getFleetData().getBurnLevel())),
                    Misc.getRoundedValueOneAfterDecimalIfNotWhole(Misc.getLYPerDayAtBurn(playerFleet,playerFleet.getFleetData().getBurnLevel())));
        }
        tooltip.addSectionHeading("Compound infusion", Alignment.MID,5f);
        tooltip.addPara("By using Compound with Antimatter fuel ,we can achieve much higher yield, reducing usage of antimatter fuel!",Misc.getPositiveHighlightColor(),5f);
        tooltip.addPara("Fuel yield is increased by %s times",5f,Color.ORANGE,"10");
        tooltip.addPara("The fleet is carrying %s compound and %s fuel, which technically increases our effective fuel capacity by %s",5f,Color.ORANGE,"400",""+(int)playerFleet.getCargo().getFuel(),""+getCalculatedCompound(playerFleet.getCargo().getFuel(),400));

    }
    public static float getCalculatedCompound(float fuelHad,float compoundAmount){
        int toReturn = (int) compoundAmount;
        float effectiveFuel = fuelHad;
        if(effectiveFuel<compoundAmount){
            return (int)(effectiveFuel*5);
        }
        return toReturn*5;
    }
}
