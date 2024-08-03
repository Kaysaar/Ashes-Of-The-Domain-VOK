package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.misc.AoTDMisc;


import java.util.HashMap;
import java.util.Map;

public class GPOrder implements Cloneable{
    int amountToProduce;
    int alreadyProduced;
    float dummyCounter;
    float daysSpentDoingOrder;// between 0 and 1
    boolean contributingToOrder;
    int priority = 0;


    public GPOrder cloneOrder(){
        try {
            return (GPOrder) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    String specId;
    public int getAmountToProduce(){
        return  amountToProduce - alreadyProduced;
    }
    public float getDaysTillOrderFinished(){
        float defDeays =  (getSpecFromClass().days);
        if(defDeays<=1)defDeays = 1;
        return defDeays-daysSpentDoingOrder;
    }
    HashMap<String, Integer>assignedResources = new HashMap<>();
    HashMap<String, Integer>resourcesGet = new HashMap<>();
    public boolean canProceed() {
        boolean haveHeavy = false;
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if(industry instanceof HeavyIndustry){
                    haveHeavy = true;
                    break;
                }
            }
        }
        if(!haveHeavy)return false;
        if(!isCountingToContribution()||isAboutToBeRemoved())return false;
        // Check if the obtained resources meet or exceed the required resources
        for (Map.Entry<String, Integer> entry : assignedResources.entrySet()) {
            String resourceKey = entry.getKey();
            Integer requiredAmount = entry.getValue();

            // Get the amount of the resource obtained, handling cases where the key might not be present
            Integer obtainedAmount = resourcesGet.containsKey(resourceKey) ? resourcesGet.get(resourceKey) : 0;

            // If the required amount is greater than the obtained amount, return false
            if (requiredAmount > obtainedAmount) {
                return false;
            }
        }
        // If all required resources meet or exceed the required amounts, return true
        return true;
    }


    public GPOrder(String specID, int amountToProduce){
        this.specId = specID;
        updateAmountToProduce(amountToProduce);
        assignedResources =  getSpec(specID).supplyCost;
        contributingToOrder = true;
    }
    public void updateResourceCost(){
        assignedResources = getSpecFromClass().supplyCost;
    }
    public static GPSpec getSpec(String id){
        for (GPSpec spec : GPManager.getInstance().getSpecs()) {
            if(spec.getProjectId().equals(id)){
                return spec;
            }
        }
        return null;
    }
    public GPSpec getSpecFromClass(){
        for (GPSpec spec : GPManager.getInstance().getSpecs()) {
            if(spec.getProjectId().equals(this.specId)){
                return spec;
            }
        }
        return null;
    }
    public void setContributingToOrder(boolean contributingToOrder) {
        this.contributingToOrder = contributingToOrder;
    }
    public  boolean isAboutToBeRemoved(){
        return amountToProduce<=0;
    }
    public  boolean isCountingToContribution(){
        return contributingToOrder&&amountToProduce>0;
    }

    public void updateAmountToProduce(int newValue){
        this.amountToProduce = newValue;
    }
    public boolean haveMetQuota(){
        return alreadyProduced >=amountToProduce;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public HashMap<String,Integer>getReqResources(){
        return getSpecFromClass().supplyCost;
    }

    public void advance(float amount){
        daysSpentDoingOrder+=Global.getSector().getClock().convertToDays(amount);
        if(getDaysTillOrderFinished()<0){
            amountToProduce--;
            FactionAPI pf = Global.getSector().getPlayerFaction();
            FactionProductionAPI prod = pf.getProduction();

            MarketAPI gatheringPoint = prod.getGatheringPoint();
            if (gatheringPoint == null) return;

            //CargoAPI local = Misc.getLocalResourcesCargo(gatheringPoint);
            CargoAPI local = Misc.getStorageCargo(gatheringPoint);
            if(getSpecFromClass().type== GPSpec.ProductionType.WEAPON){
                local.addWeapons(getSpecFromClass().getIdOfItemProduced(), 1);
            }
            if(getSpecFromClass().type== GPSpec.ProductionType.SHIP){
                FleetMemberAPI member = local.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(getSpecFromClass().getShipHullSpecAPI()));
                member.getVariant().clear();
            }
            if(getSpecFromClass().type== GPSpec.ProductionType.FIGHTER){
                local.addFighters(getSpecFromClass().getIdOfItemProduced(), 1);
            }
            if(!isAboutToBeRemoved()){
                daysSpentDoingOrder = 0;
            }
        }
    }
    

}
