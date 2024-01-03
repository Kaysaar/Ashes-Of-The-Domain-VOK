package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalCenter;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoronalPylon extends BaseIndustry {
    public boolean allSectionsRestored = false;
    @Override
    public void apply() {
        super.apply(true);
        if(this.special!=null){
            demand(Commodities.RARE_METALS,10);
        }
        else{
            demand(Commodities.RARE_METALS,0);
        }

        if(checkIfIsConnected()&&this.special!=null&&isFunctional()&&getMaxDeficit(Commodities.RARE_METALS).two==0){
                for (MarketAPI marketAPI : Misc.getMarketsInLocation(this.market.getContainingLocation())) {
                    if (marketAPI.getFactionId().equals(this.market.getFactionId())) {
                        marketAPI.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat("hypershunt", 1, "Coronal Hypershunt");
                        for (Industry industry : marketAPI.getIndustries()) {
                            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllSupply()) {
                                if(mutableCommodityQuantity.getQuantity().getModifiedValue()>0){
                                    mutableCommodityQuantity.getQuantity().modifyFlat("aotd_hypershunt",1,"Coronal Hypershunt");
                                }
                            }
                        }
                    }

                }


        }
        else{
            clearEffects();
        }
    }

    private void clearEffects() {
        for (MarketAPI marketAPI : Misc.getMarketsInLocation(this.market.getContainingLocation())) {
            if (marketAPI.getFactionId().equals(this.market.getFactionId())) {
                marketAPI.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodifyFlat("aotd_hypershunt");
                for (Industry industry : marketAPI.getIndustries()) {
                    for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllSupply()) {
                        mutableCommodityQuantity.getQuantity().unmodifyFlat("aotd_hypershunt");

                    }
                }
            }

        }
    }

    @Override
    public void unapply() {
        clearEffects();
        super.unapply();
    }

    public  Pair<CoronalCenter, Float> getNearestCoronalCenter(Vector2f locInHyper, String factionId) {
        CoronalCenter nearest = null;
        float minDist = Float.MAX_VALUE;
        for (MarketAPI marketAPI : Misc.getFactionMarkets(factionId)) {
            if (marketAPI.hasIndustry("coronalcenter")) {
                CoronalCenter ind = (CoronalCenter) marketAPI.getIndustry("coronal_network");
                float dist = Misc.getDistanceLY(locInHyper, marketAPI.getLocationInHyperspace());
                if (dist < minDist) {
                    minDist = dist;
                    nearest = ind;
                }
            }
        }

        if (nearest == null) return null;
        return new Pair<CoronalCenter, Float>(nearest, minDist);
    }
    public List<Pair<CoronalPylon,Float> > getAllPylonsInRadius(Vector2f locInHyper, String factionId, int radius) {
        List <Pair<CoronalPylon,Float>> all = new ArrayList<>();
        CoronalPylon nearest = null;
        for (MarketAPI marketAPI : Misc.getFactionMarkets(factionId)) {
            if (marketAPI.hasIndustry("coronal_pylon")) {
                float dist = Misc.getDistanceLY(locInHyper, marketAPI.getLocationInHyperspace());
                if (dist <= (float) radius) {
                    all.add(new Pair<>((CoronalPylon) marketAPI.getIndustry("coronal_pylon"),dist));
                }
            }
        }

        if (all.isEmpty()) return null;

        return all;
    }
    public boolean checkIfIsConnected(){
        for (MarketAPI factionMarket : Misc.getFactionMarkets(this.getMarket().getFaction())) {
            if(factionMarket.hasIndustry("coronal_network")){
                CoronalCenter center = (CoronalCenter) market.getIndustry("coronal_network");
                if(center.tenLY){
                   float distance= Misc.getDistanceLY(factionMarket.getPrimaryEntity(),this.market.getPrimaryEntity());
                   if(distance<10){
                       return true;
                   }
                   else{
                       if(distance<50&&center.extendedRange){
                           return true;
                       }
                   }
                }
            }
        }
        return false;
    }
    public boolean isThereControlInSystemOrHub() {
        for (MarketAPI marketAPI : Misc.getMarketsInLocation(this.market.getContainingLocation())) {
            if (marketAPI.getFactionId().equals(this.market.getFactionId())) {
                if (marketAPI.hasIndustry("coronalpylon") && marketAPI.getId() != this.market.getId()) {
                    return true;
                }
            }

        }
        return false;
    }
    @Override
    public boolean isAvailableToBuild() {
        if(Global.getSettings().getModManager().isModEnabled("aod_core")){
            Map<String,Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
            return researchSaved != null && researchSaved.get(id) && checkIfIsConnected() && !isThereControlInSystemOrHub();
        }

        return checkIfIsConnected()&&!isThereControlInSystemOrHub()&&super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        Pair<CoronalCenter, Float> nearest= getNearestCoronalCenter(this.market.getLocationInHyperspace(),this.market.getFactionId());
        return nearest != null&&super.isAvailableToBuild();
    }

    @Override
    public String getUnavailableReason() {
        Pair<CoronalCenter, Float> nearest= getNearestCoronalCenter(this.market.getLocationInHyperspace(),this.market.getFactionId());
        if(nearest==null)return "There is no Hypershunt in your control";
        if(!nearest.one.tenLY) return "Hypershunt is currently not harvesting any energy!";
        if(nearest.two>10){
            if(!nearest.one.extendedRange){
                return "Wormhole Stabilizer is not working \n";

            }
        }
        if(isThereControlInSystemOrHub()){
            return "There is already Coronal Energetic Hub in that system";
        }
        return "";
    }
}
