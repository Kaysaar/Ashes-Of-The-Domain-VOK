package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.util.Misc;

public class AotdFuelConsumptionV2 implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        float fuelPerLightYear = fleet.getLogistics().getFuelCostPerLightYear();
        float computedFuel = fuelPerLightYear*fleet.getStats().getFuelUseHyperMult().getModifiedValue();
        if(fleet.isInHyperspace()){
            computedFuel = 0;
        }
        float notShownOnMap  = fleet.getStats().getDynamic().getStat("fuel_use_not_shown_on_map_mult").getModifiedValue();
        computedFuel*=notShownOnMap;
        if(computedFuel>0f){
            float velocityLength = fleet.getVelocity().length();
            float maxSpeedForBurn = Misc.getSpeedForBurnLevel(20f);
            float correction = 1f;
            if(velocityLength>maxSpeedForBurn){
                correction = maxSpeedForBurn/velocityLength;
            }
            velocityLength*=Global.getSector().getClock().getSecondsPerDay();
            float computed = computedFuel*velocityLength/amount*Global.getSettings().getFloat("unitsPerLightYear");
            computed*=correction;
            if(computed>0f){
                fleet.getCargo().addFuel(computed);
            }

        }
    }
}
