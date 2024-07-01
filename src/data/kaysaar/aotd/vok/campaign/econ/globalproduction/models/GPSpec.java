package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GPSpec {
    public void  setObjectToBeProduced(String id,ProductionType type){
        if (type.equals(ProductionType.FIGHTER)) {
            this.wingSpecAPI = Global.getSettings().getFighterWingSpec(id);
        }
        if (type.equals(ProductionType.SHIP)) {
            this.shipHullSpecAPI = Global.getSettings().getHullSpec(id);
        }
        if (type.equals(ProductionType.WEAPON)) {
            this.weaponSpec = Global.getSettings().getWeaponSpec(id);
        }
        this.type = type;
    }

    public static GPSpec getSpecFromShip(ShipHullSpecAPI specAPI){
        int priceScaling = 10000;
        int dayScaling = 10000;
        int basePrice = (int) specAPI.getBaseValue();
        float days = 1;
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.FRIGATE)){
            days = Math.min(basePrice/dayScaling,30);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.DESTROYER)){
            days = Math.min(basePrice/dayScaling,40);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.CRUISER)){
            days = Math.min(basePrice/dayScaling,50);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)){
            days = Math.min(basePrice/dayScaling,60);
        }
        if(days<=0)days=1;
        float newPrice = basePrice*0.6f;
        newPrice = Math.round(newPrice);
        GPSpec spec = new GPSpec();
        spec.setProjectId(specAPI.getHullId());
        spec.setObjectToBeProduced(specAPI.getHullId(),ProductionType.SHIP);
        spec.setDays((int) days);
        spec.setType(ProductionType.SHIP);
        HashMap<String,Integer>commodityCost = new HashMap<>();
        int price =(int) basePrice/priceScaling;
        if(price==0){
            price =1;
        }
        commodityCost.put(Commodities.SHIPS,price);
        spec.setCredistCost(newPrice);
        spec.setSupplyCost(commodityCost);
        return spec;
    }
    public static GPSpec getSpecFromWeapon(WeaponSpecAPI specAPI){
        int priceScaling = 10000;
        int dayScaling = 10000;
        int basePrice = (int) specAPI.getBaseValue();
        float days = 1;
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
            days = Math.min(basePrice/dayScaling,30);
        }
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
            days = Math.min(basePrice/dayScaling,40);
        }
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
            days = Math.min(basePrice/dayScaling,50);
        }
        if(days<=0)days=1;
        float newPrice = basePrice*0.6f;
        newPrice = Math.round(newPrice);
        GPSpec spec = new GPSpec();
        spec.setProjectId(specAPI.getWeaponId());
        spec.setObjectToBeProduced(specAPI.getWeaponId(),ProductionType.WEAPON);
        spec.setDays((int) days);
        HashMap<String,Integer>commodityCost = new HashMap<>();
        int price =(int) basePrice/priceScaling;
        if(price==0){
            price =1;
        }
        commodityCost.put(Commodities.HAND_WEAPONS,price);
        spec.setCredistCost(newPrice);
        spec.setSupplyCost(commodityCost);
        return spec;
    }
    public static GPSpec getSpecFromWing(FighterWingSpecAPI specAPI){
        int priceScaling = 1000;
        int dayScaling = 1000;
        int basePrice = (int) specAPI.getBaseValue();
        float newDays = basePrice/priceScaling;
        if(newDays<=0){
            newDays = 1;
        }
        float newPrice = basePrice*0.6f;
        newPrice = Math.round(newPrice);
        GPSpec spec = new GPSpec();
        spec.setProjectId(specAPI.getId());
        spec.setObjectToBeProduced(specAPI.getId(),ProductionType.FIGHTER);
        spec.setDays((int) newDays);
        spec.setType(ProductionType.FIGHTER);
        HashMap<String,Integer>commodityCost = new HashMap<>();
        int price =(int) basePrice/priceScaling;
        if(price==0){
            price =2;
        }
        commodityCost.put(Commodities.SHIPS,price/2);
        commodityCost.put(Commodities.HAND_WEAPONS,price/2);
        spec.setCredistCost(newPrice);
        spec.setSupplyCost(commodityCost);
        return spec;
    }

    public void setCommodityCost(HashMap<String, Integer> commodityCost) {
        this.commodityCost = commodityCost;
    }

    public void setSupplyCost(HashMap<String, Integer> supplyCost) {
        this.supplyCost = supplyCost;
    }

    public void setType(ProductionType type) {
        this.type = type;
    }

    public void setCredistCost(float credistCost) {
        this.credistCost = credistCost;
    }

    public void setSpriteIdOverride(String spriteIdOverride) {
        this.spriteIdOverride = spriteIdOverride;
    }

    public void setNameOverride(String nameOverride) {
        this.nameOverride = nameOverride;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public enum ProductionType {
        FIGHTER,
        WEAPON,
        SHIP,
        SPECIAL_PROJECT

    }

    FighterWingSpecAPI wingSpecAPI;
    WeaponSpecAPI weaponSpec;
    ShipHullSpecAPI shipHullSpecAPI;
    HashMap<String, Integer> commodityCost;
    HashMap<String, Integer> supplyCost;
    ProductionType type;
    public float credistCost;
    public String spriteIdOverride;
    public String nameOverride;
    public String projectId;

    public String getNameOverride() {
        return nameOverride;
    }

    public String getSpriteIdOverride() {
        return spriteIdOverride;
    }

    public String getProjectId() {
        return projectId;
    }

    public ProductionType getType() {
        return type;
    }

    public String getIdOfItemProduced() {
        if (type.equals(ProductionType.FIGHTER)) {
            return wingSpecAPI.getVariantId();
        }
        if (type.equals(ProductionType.SHIP)) {
            return shipHullSpecAPI.getHullId();
        }
        if (type.equals(ProductionType.WEAPON)) {
            return weaponSpec.getWeaponId();
        }
        if (type.equals(ProductionType.SPECIAL_PROJECT)) {
            return projectId;
        }
        return "";

    }
    public  int days;
    public float getCredistCost() {
        return credistCost;
    }

    public FighterWingSpecAPI getWingSpecAPI() {
        return wingSpecAPI;
    }

    public WeaponSpecAPI getWeaponSpec() {
        return weaponSpec;
    }

    public HashMap<String, Integer> getCommodityCost() {
        return commodityCost;
    }

    public ShipHullSpecAPI getShipHullSpecAPI() {
        return shipHullSpecAPI;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public HashMap<String, Integer> getSupplyCost() {
        return supplyCost;
    }

    public static ArrayList<GPSpec> loadFromCSV() {
        ArrayList<GPSpec> specs = new ArrayList<>();
        try {
            JSONArray csvFile = Global.getSettings().loadCSV("data/campaign/aotd_production_data.csv");
            for (int i = 0; i < csvFile.length(); i++) {
                JSONObject entry = csvFile.getJSONObject(i);

                String id =entry.getString("id");
                Integer days = entry.getInt("days");
                String nameOverride = entry.getString("nameOverride");
                String spriteIdOverride = entry.getString("spriteOverride");
                String idOfItem = entry.getString("idOfItemProduced");
                Integer credistCost = entry.getInt("costInCredits");
                HashMap<String,Integer>supplyMap = loadCostMap(entry.getString("supplyCost"));
                ProductionType type = ProductionType.valueOf(entry.getString("productionType"));
                HashMap<String,Integer>commodityCostMap = loadCostMap(entry.getString("commodityCost"));

                GPSpec spec = new GPSpec();

                spec.setProjectId(id);
                spec.setCredistCost(credistCost);
                spec.setCommodityCost(commodityCostMap);
                spec.setType(type);
                spec.setNameOverride(nameOverride);
                spec.setObjectToBeProduced(idOfItem,type);
                spec.setSpriteIdOverride(spriteIdOverride);
                spec.setSupplyCost(supplyMap);
                spec.setDays(days);
                specs.add(spec);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return specs;
    }


    public static HashMap<String,Integer> loadCostMap(String rawMap) {
        HashMap<String,Integer>map = new HashMap<>();
        String[]splitted = rawMap.split(",");
        for (String s : splitted) {
            String[] extracted = s.split(":");
            map.put(extracted[0],Integer.valueOf(extracted[1]));
        }
        return map;
    }

}
