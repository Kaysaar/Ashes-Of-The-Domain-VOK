package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.fleet.FleetMemberViewAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class AoTDFuelConsumptionScript implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }
    public void trueAdvance(float amount) {
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();

        if(getCompound(fleet.getCargo())>0&&Global.getSector().getPlayerMemoryWithoutUpdate().is(AoTDCompoundShowcase.memKey,true)){
            fleet.getStats().getFuelUseHyperMult().modifyMult("aotd_compound",0.1f,"Compound");
            float fuelConsumed = getComputedFuel(amount,fleet);
            if(fuelConsumed>0){
                Color c = new Color(140,0, 255,255);
                Color cDim = new Color(102,0,255,50);
                Color cDim2 = new Color(102,0,255,120);
                for (FleetMemberViewAPI view : fleet.getViews()) {
                    //view.getContrailColor().shift(getModId(), view.getEngineColor().getBase(), 1f, 1f, 0.25f);
                    view.getContrailColor().shift("aotd_compound", cDim2, 1f, 1f, .75f);
                    view.getEngineGlowColor().shift("aotd_compound", cDim, 1f, 1f, .5f);
                    view.getEngineGlowSizeMult().shift("aotd_compound", 3f, 1f, 1f, 1f);
                    //view.getEngineHeightMult().shift(getModId(), 5f, 1f, 1f, 1f);
                    //view.getEngineWidthMult().shift(getModId(), 10f, 1f, 1f, 1f);
                }
                if(fleet.getStats().getFleetwideMaxBurnMod().getMultBonus("hyperspace_stat_mod_5")!=null){
                    // 2 IS max while 1 is min
                    fleet.getStats().getFleetwideMaxBurnMod().modifyMult("aotd_compound",closenessToOne(fleet.getStats().getFleetwideMaxBurnMod().getMultBonus("hyperspace_stat_mod_5").getValue()),"Compound infusion (Abyssal Hyperspace");

                }
                else{
                    fleet.getStats().getFleetwideMaxBurnMod().unmodifyMult("aotd_compound");

                }
            }
            fleet.getCargo().removeCommodity("compound",fuelConsumed);

        }
        else{
            fleet.getStats().getFleetwideMaxBurnMod().unmodifyMult("aotd_compound");

            fleet.getStats().getFuelUseHyperMult().unmodifyMult("aotd_compound");

        }



    }
    public  float closenessToOne(double num) {
        if (num < 0 || num > 1) {
           return 1f;
        }
        if (num == 1) return 1;
        return (float) (1f / num)*0.5f;
    }

    public float getComputedFuel(float amount,CampaignFleetAPI fleet){
        float fuelPerLightYear = fleet.getLogistics().getBaseFuelCostPerLightYear();
        float computedFuel = fuelPerLightYear * fleet.getStats().getFuelUseHyperMult().getModifiedValue();

        if (!fleet.isInHyperspace()) {
            computedFuel = 0;
        }
        float notShownOnMap = fleet.getStats().getDynamic().getStat("fuel_use_not_shown_on_map_mult").getModifiedValue();
        computedFuel *= notShownOnMap;
        if (computedFuel > 0f) {
            float velocityLength = fleet.getVelocity().length();
            float maxSpeedForBurn = Misc.getSpeedForBurnLevel(20f);
            float correction = 1f;
            if (velocityLength > maxSpeedForBurn) {
                correction = maxSpeedForBurn / velocityLength;
            }
            velocityLength *= Global.getSector().getClock().getSecondsPerDay();
            float computed = computedFuel * velocityLength / Global.getSettings().getFloat("unitsPerLightYear") * amount;
            computed *= correction;
            return computed;

        }
        return 0;
    }
    @Override
    public void advance(float amount) {
        trueAdvance(Global.getSector().getClock().convertToDays(amount));
    }
    public static float getCalculatedCompound(float fuelHad,float compoundAmount){
        int toReturn = (int) compoundAmount;
        float effectiveFuel = fuelHad;
        if(effectiveFuel<compoundAmount){
            return (int)(effectiveFuel*5);
        }
        return toReturn*5;
    }
    public static float getCompound(CargoAPI cargo){
       return cargo.getCommodityQuantity("compound");
    }
}

