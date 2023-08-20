package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.Ids.AodCommodities;
import data.plugins.AoDUtilis;

import java.awt.*;

public class KaysaarBiFrostGate extends BaseIndustry {
    public SectorEntityToken gate ;
    public float  BASE_ACCESSIBILITY = 0.5f;
    @Override
    public void apply() {
        super.apply(true );
        demand("bifrost", AodCommodities.PURIFIED_TRANSPLUTONICS,5,"Bi Frost Gate Stabilization");
        Pair<String,Integer> max = getMaxDeficit(AodCommodities.PURIFIED_TRANSPLUTONICS);
        if(max.two>0&&gate!=null){
            gate.getMemory().set("$supplied",false);
        }
        if(max.two==0&&gate!=null){
            gate.getMemory().set("$supplied",true);
        }
        String desc = getNameForModifier();

        float a = BASE_ACCESSIBILITY;
        if (a > 0) {
            market.getAccessibilityMod().modifyFlat(getModId(0), a, desc);
        }
    }
    @Override
    public boolean showWhenUnavailable() {
        return AoDUtilis.isResearched(this.getId());
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
       if(gate!=null){
           if(gate.getMemory().is("$used",true)){
               float value = gate.getMemory().getFloat("$cooldown");
               value-= Global.getSector().getClock().convertToDays(amount);
               gate.getMemory().set("$cooldown",value);
               if(value<=0){
                   gate.getMemory().set("$cooldown",0);
                   gate.getMemory().set("$used",false);
               }
           }
       }
       else{
           spawnGate();
       }
    }

    @Override
    public void unapply() {
        super.unapply();
        market.getAccessibilityMod().unmodifyFlat(getModId(0));
    }

    @Override
    public void finishBuildingOrUpgrading() {
        super.finishBuildingOrUpgrading();
        spawnGate();

    }

    private void spawnGate() {
        SectorEntityToken primary = getMarket().getPrimaryEntity();
        float orbitRadius = primary.getRadius() + 150.0F;
        SectorEntityToken test = market.getContainingLocation().addCustomEntity((String) null, market.getName()+" Bifrost Gate" , "bifrost_gate", market.getFactionId());
        test.setCircularOrbitWithSpin(primary, (float) Math.random() * 360.0F, orbitRadius, orbitRadius / 10.0F, 5.0F, 5.0F);
        getMarket().getConnectedEntities().add(test);
        test.setMarket(getMarket());
        test.setDiscoverable(false);
        this.market.addCondition("bifrost_removal");
        test.getMemory().set("$used",false);
        test.getMemory().set("$cooldown",0f);
        test.getMemory().set("$supplied",true);
        gate = test;
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        for (MarketAPI marketAPI : Misc.getMarketsInLocation(market.getContainingLocation(), this.market.getFactionId())) {
            if(marketAPI.hasIndustry("bifrost")){
                if(marketAPI.getId().equals(this.market.getId())) continue;
                return false;
            }
        }
        return AoDUtilis.isResearched(this.getId());
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if(mode.equals(IndustryTooltipMode.NORMAL)){
            if(gate!=null){
                if(gate.getMemory().is("$used",true)){
                    float value = gate.getMemory().getFloat("$cooldown");
                    String days = " days";
                    if (value<=1){
                        days=" day";
                    }
                    tooltip.addPara("Curently gateway in "+market.getName()+" is inactive for "+(int)value + days,Misc.getNegativeHighlightColor(),10f);
                }
            }
            tooltip.addPara("Accessibility bonus : 50%" , Color.ORANGE,10f);
        }
    }

}
