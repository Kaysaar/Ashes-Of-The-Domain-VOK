package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.impl.campaign.JumpPointAoTD;
import com.fs.starfarer.api.impl.campaign.abilities.SustainedBurnAbility;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.procgen.OrionSectorData;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.CampaignUIPersistentData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.scripts.listeners.AoTDBlockMouseWhenStrangled;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class AoTDFuelConsumptionScript implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    public Vector2f preLocation;
    public Vector2f currentLocation;


    public float currentFuel;
    public float accumulatedFuel;
    public  transient boolean removed= false;
    public void trueAdvance(float amount) {
        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        if(!fleet.isInHyperspace())return;
        if(!removed){
            UIPanelAPI coreUI = ProductionUtil.getCoreUI();
            UIPanelAPI leChildren = null;
            UIPanelAPI leGrandChildren = null;
            UIPanelAPI testing = null;
            if(coreUI!=null){
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(coreUI)) {
                    if(ReflectionUtilis.hasMethodOfName("getDisplaySensorRange",componentAPI)){
                        leChildren = (UIPanelAPI) componentAPI;
                        break;
                    }
                }
            }
            if(leChildren!=null) { //14
                ArrayList<UIComponentAPI> componentAPIS = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leChildren);
               testing = (UIPanelAPI) componentAPIS.get(12);
                for (UIComponentAPI componentAPI : componentAPIS) {
                    if(ReflectionUtilis.hasMethodOfName("getFuelPerDay",componentAPI)){
                        leGrandChildren = (UIPanelAPI) componentAPI;
                        break;
                    }
                }
            }

            if(leGrandChildren!=null) {
                ArrayList<UIComponentAPI> grandChildrenComponents = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leGrandChildren);
                if(grandChildrenComponents.size()==4){
                    UIComponentAPI grandGrandChild = grandChildrenComponents.get(2);
                    Vector2f xy = new Vector2f(grandGrandChild.getPosition().getX(), grandGrandChild.getPosition().getY());
                    UILinesRenderer renderer = new UILinesRenderer(0f);
                    renderer.setBoxColor(Color.MAGENTA);

                    CustomPanelAPI testings = Global.getSettings().createCustom(grandGrandChild.getPosition().getWidth(),grandGrandChild.getPosition().getHeight(),renderer);
                    CustomPanelAPI insider = testings.createCustomPanel(grandGrandChild.getPosition().getWidth(),grandGrandChild.getPosition().getHeight()-1,null);
                    testings.addComponent(insider).inTL(-5,5);
                    insider.getPosition().setSuspendRecompute(false);
                    insider.addComponent(new AoTDCompoundShowcase(insider.getPosition().getWidth(),insider.getPosition().getHeight()).getMainPanel());
                    TooltipMakerAPI tooltip = testings.createUIElement(1,1,true);
                    leGrandChildren.removeComponent(grandChildrenComponents.get(3));
                    leGrandChildren.removeComponent(grandGrandChild);

                    leGrandChildren.addComponent(testings).inTL(0,0);

                    removed = true;
                }


            }


        }
        if(fleet.getCargo().getCommodityQuantity("purified_rare_metal")>0){
            fleet.getStats().getFuelUseHyperMult().modifyMult("aotd_compound",0.1f,"Compound");
            float fuelConsumed = getComputedFuel(amount,fleet);
            fleet.getCargo().removeCommodity("purified_rare_metal",fuelConsumed);
        }
        else{
            fleet.getStats().getFuelUseHyperMult().unmodifyMult("aotd_compound");

        }



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

}

