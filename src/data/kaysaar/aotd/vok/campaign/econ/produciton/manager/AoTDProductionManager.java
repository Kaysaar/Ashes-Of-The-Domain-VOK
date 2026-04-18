package data.kaysaar.aotd.vok.campaign.econ.produciton.manager;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;

import java.util.ArrayList;

public class AoTDProductionManager {
    public static String memKey = "$aotd_production_manager";
    public MutableStat frigSpeed = new MutableStat(1f);
    public MutableStat cruiserSpeed = new MutableStat(1f);
    public MutableStat destroyerSpeed = new MutableStat(1f);
    public MutableStat capitalSpeed = new MutableStat(1f);
    public MutableStat weaponProdSpeed = new MutableStat(1f);
    public MutableStat fighterSpeed = new MutableStat(1f);
    public MutableStat itemProdSpeed = new MutableStat(1f);
    public void ensureScriptExists(){
        if(!Global.getSector().hasScript(AoTDProductionMover.class)){
            Global.getSector().addScript(new AoTDProductionMover());
        }
    }

    public MutableStat getCapitalSpeed() {
        if(capitalSpeed==null )capitalSpeed = new MutableStat(1f);
        return capitalSpeed;
    }

    public MutableStat getCruiserSpeed() {
        if(cruiserSpeed==null )cruiserSpeed = new MutableStat(1f);
        return cruiserSpeed;
    }

    public MutableStat getDestroyerSpeed() {
        if(destroyerSpeed==null )destroyerSpeed = new MutableStat(1f);
        return destroyerSpeed;
    }

    public MutableStat getFighterSpeed() {
        if(fighterSpeed==null )fighterSpeed = new MutableStat(1f);
        return fighterSpeed;
    }

    public MutableStat getFrigSpeed() {
        if(frigSpeed==null )frigSpeed = new MutableStat(1f);
        return frigSpeed;
    }

    public MutableStat getItemProdSpeed() {
        if(itemProdSpeed==null )itemProdSpeed = new MutableStat(1f);
        return itemProdSpeed;
    }

    public MutableStat getWeaponProdSpeed() {
        if(weaponProdSpeed==null)weaponProdSpeed = new MutableStat(1f);
        return weaponProdSpeed;
    }
    public MutableStat getSpeedStatForOrder(AoTDProductionOrderData orderData){
        String subType = null;
        if(orderData.getSpec().getUnderlyingSpec() instanceof ShipHullSpecAPI specAPI){
            subType = specAPI.getHullSize().toString();
        }
        return getSpeedStatForProdType(orderData.getType(),subType);
    }
    public MutableStat getSpeedStatForProdType(AoTDProductionSpec.AoTDProductionSpecType type, String subType) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case SHIP -> {
                if (subType == null) {
                    return getFrigSpeed();
                }
                try {
                    ShipAPI.HullSize hullSize = ShipAPI.HullSize.valueOf(subType);

                    return switch (hullSize) {
                        case FIGHTER -> getFighterSpeed();
                        case FRIGATE -> getFrigSpeed();
                        case DESTROYER -> getDestroyerSpeed();
                        case CRUISER -> getCruiserSpeed();
                        case CAPITAL_SHIP -> getCapitalSpeed();
                        default -> getFrigSpeed();
                    };
                } catch (IllegalArgumentException ex) {
                    return getFrigSpeed();
                }
            }
            case FIGHTER -> {
                return getFighterSpeed();
            }
            case WEAPON -> {
                return getWeaponProdSpeed();
            }

            case COMMODITY_ITEM , SPECIAL_ITEM -> {
                return getItemProdSpeed();
            }
        }
        return null;
    }
    public ArrayList<AoTDProductionOrderSnapshot> getCurrentSnapshots() {
        return currentSnapshots;
    }
    public void ensureFieldsAreInit(){

    }
    public static AoTDProductionManager getInstance(){
        if(!Global.getSector().getPersistentData().containsKey(memKey)){
            AoTDProductionManager mover = new AoTDProductionManager();
            Global.getSector().getPersistentData().put(memKey, mover);
        }
        AoTDProductionManager manager =(AoTDProductionManager)Global.getSector().getPersistentData().get(memKey);
        manager.ensureFieldsAreInit();
        return manager;
    }

    public void addSnapshot(AoTDProductionOrderSnapshot snapshot){
        currentSnapshots.add(snapshot);
    }
    public ArrayList<AoTDProductionOrderSnapshot>currentSnapshots = new ArrayList<>();

}
