package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

import static data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript.getCalculatedCompound;

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
        tooltip.addTitle("Compound infusion");
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        tooltip.addPara("By using Compound with Antimatter fuel ,we can achieve much higher yield, reducing usage of antimatter fuel!",Misc.getPositiveHighlightColor(),5f);
        tooltip.addPara("Fuel yield is increased by %s times",5f,Color.ORANGE,"10");
        tooltip.addPara("The fleet is carrying %s compound and %s fuel, which technically increases our effective fuel capacity by %s",5f,Color.ORANGE,""+AoTDFuelConsumptionScript.getCompound(playerFleet.getCargo()),""+(int)playerFleet.getCargo().getFuel(),""+getCalculatedCompound(playerFleet.getCargo().getFuel(),AoTDFuelConsumptionScript.getCompound(playerFleet.getCargo())));
        tooltip.addPara("Using compound in abyssal hyperspace will reduce penalty to burn level by %s",10f,Color.ORANGE,"50%");

    }

}
