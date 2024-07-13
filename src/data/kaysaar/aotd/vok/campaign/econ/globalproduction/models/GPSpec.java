package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionProductionAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
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
            days = Math.min(basePrice/dayScaling,10);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.DESTROYER)){
            days = Math.min(basePrice/dayScaling,20);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.CRUISER)){
            days = Math.min(basePrice/dayScaling,40);
        }
        if(specAPI.getHullSize().equals(ShipAPI.HullSize.CAPITAL_SHIP)){
            days = Math.min(basePrice/dayScaling,80);
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

        int priceScaling = 1000;
        int dayScaling = 1000;
        int basePrice = Global.getSector().getPlayerFaction().getProduction().createSampleItem(FactionProductionAPI.ProductionItemType.WEAPON, specAPI.getWeaponId(),1).getBaseCost();
        float days = 1;
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
            days = Math.min(basePrice/dayScaling,30);
        }
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.MEDIUM)){
            days = Math.min(basePrice/dayScaling,40);
        }
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.LARGE)){
            days = Math.min(basePrice/dayScaling,50);
        }

        if(days<=0)days=2;
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
        int priceScaling = 5000;
        int dayScaling = 10000;
        int basePrice = (int) specAPI.getBaseValue();
        float newDays = basePrice/dayScaling;
        if(newDays<=0){
            newDays = 2;
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

    }
    public String descriptionOfProject;

    public String getDescriptionOfProject() {
        return descriptionOfProject;
    }

    public void setDescriptionOfProject(String descriptionOfProject) {
        this.descriptionOfProject = descriptionOfProject;
    }

    boolean isSpecialProject;
    FighterWingSpecAPI wingSpecAPI;
    WeaponSpecAPI weaponSpec;
    ShipHullSpecAPI shipHullSpecAPI;
    boolean isRepeatable = true;
    public int  amountOfStages;
    HashMap<String, Integer> commodityCost;
    HashMap<String, Integer> supplyCost;
    ArrayList<HashMap<String,Integer>>stageSupplyCost;

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(boolean repeatable) {
        isRepeatable = repeatable;
    }

    public int getAmountOfStages() {
        return amountOfStages;
    }

    public void setAmountOfStages(int amountOfStages) {
        this.amountOfStages = amountOfStages;
    }

    public ArrayList<HashMap<String, Integer>> getStageSupplyCost() {
        return stageSupplyCost;
    }

    public void setStageSupplyCost(ArrayList<HashMap<String, Integer>> stageSupplyCost) {
        this.stageSupplyCost = stageSupplyCost;
    }

    public ArrayList<Integer> getDaysPerStage() {
        return daysPerStage;
    }

    public void setDaysPerStage(ArrayList<Integer> daysPerStage) {
        this.daysPerStage = daysPerStage;
    }

    public HashMap<Integer, Pair<String, String>> getStageNameAndDecsMap() {
        return stageNameAndDecsMap;
    }

    public void setStageNameAndDecsMap(HashMap<Integer, Pair<String, String>> stageNameAndDecsMap) {
        this.stageNameAndDecsMap = stageNameAndDecsMap;
    }

    public String getRewardId() {
        return rewardId;
    }

    public void setRewardId(String rewardId) {
        this.rewardId = rewardId;
    }

    public boolean isDiscoverable() {
        return isDiscoverable;
    }

    public void setDiscoverable(boolean discoverable) {
        isDiscoverable = discoverable;
    }

    public ArrayList<String> getMemFlagsToMetForDiscovery() {
        return memFlagsToMetForDiscovery;
    }

    public void setMemFlagsToMetForDiscovery(ArrayList<String> memFlagsToMetForDiscovery) {
        this.memFlagsToMetForDiscovery = memFlagsToMetForDiscovery;
    }

    ArrayList<Integer> daysPerStage;
    HashMap<Integer, Pair<String,String>> stageNameAndDecsMap;

    ProductionType type;
    public float credistCost;
    public String spriteIdOverride;
    public String nameOverride;
    public String projectId;
    public String rewardId;

    public boolean isDiscoverable;
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
            return wingSpecAPI.getId();
        }
        if (type.equals(ProductionType.SHIP)) {
            return shipHullSpecAPI.getHullId();
        }
        if (type.equals(ProductionType.WEAPON)) {
            return weaponSpec.getWeaponId();
        }
        if(isSpecialProject){
            return rewardId;
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
    ArrayList<String>memFlagsToMetForDiscovery = new ArrayList<>();

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

    public void setSpecialProject(boolean specialProject) {
        isSpecialProject = specialProject;
    }

    public boolean isSpecialProject() {
        return isSpecialProject;
    }

    public static ArrayList<GPSpec> loadSpecialProjects() {
        ArrayList<GPSpec> specs = new ArrayList<>();
        try {
            JSONArray csvFile = Global.getSettings().loadCSV("data/campaign/aotd_production_projects.csv");
            for (int i = 0; i < csvFile.length(); i++) {
                JSONObject entry = csvFile.getJSONObject(i);

                String id =entry.getString("id");
                if(id==null||id.isEmpty())continue;
                boolean isDiscoverable = entry.getBoolean("isDiscoverable");
                boolean isRepeatable = entry.getBoolean("isRepeatable");
                ArrayList<String>memFlags = new ArrayList<>();
                if(isDiscoverable){
                    memFlags.addAll(loadEntries(entry.getString("memFlagsForDiscovery"),";"));
                }
                String name  = entry.getString("name");
                Integer amountOfStages = entry.getInt("amountOfStages");
                String descp = entry.getString("projectDescription");
                HashMap<Integer,Pair<String,String>>stageMapNames = getStageMap(entry.getString("stageNames"),entry.getString("stageDescriptions"),amountOfStages);
                ArrayList<String>duration = loadEntries(entry.getString("stageDuration"),";");

                ArrayList<Integer> stageDuration = new ArrayList<>();
                for (String s : duration) {
                    stageDuration.add(Integer.parseInt(s));
                }

                ArrayList<HashMap<String,Integer>>stageCosts = loadCostMapStages(entry.getString("stageCost"));
                String rewardId = entry.getString("rewardId");
                GPSpec spec = new GPSpec();
                spec.setProjectId(id);
                spec.setRewardId(rewardId);
                spec.setNameOverride(name);
                spec.setSpecialProject(true);
                spec.setDescriptionOfProject(descp);
                spec.setStageNameAndDecsMap(stageMapNames);
                spec.setDaysPerStage(stageDuration);
                spec.setMemFlagsToMetForDiscovery(memFlags);
                spec.setDiscoverable(isDiscoverable);
                spec.setAmountOfStages(amountOfStages);
                spec.setStageSupplyCost(stageCosts);
                spec.setRepeatable(isRepeatable);
                specs.add(spec);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return specs;
    }

    public static HashMap<String,Integer> loadCostMap(String rawMap) {
        HashMap<String,Integer>map = new HashMap<>();
        for (String s : loadEntries(rawMap,",")) {
            String[] extracted = s.split(":");
            map.put(extracted[0],Integer.valueOf(extracted[1]));
        }
        return map;
    }
    public static ArrayList<String> loadEntries(String rawMap,String seperator) {
        if(!AoTDMisc.isStringValid(rawMap)){
            return  new ArrayList<>();
        }
        String[]splitted = rawMap.split(seperator);
        ArrayList<String> map = new ArrayList<>(Arrays.asList(splitted));

        return map;
    }
    public static HashMap<Integer,Pair<String,String>>getStageMap(String rawmap,String descMap, int stageAmount){
        HashMap<Integer,Pair<String,String>>stageMap = new HashMap<>();
        ArrayList<String>names = loadEntries(rawmap,";");
        ArrayList<String>descriptions = loadEntries(descMap,";");
        for (int i = 0; i < stageAmount; i++) {
            Pair<String,String> nameAndDesc = new Pair<>(names.get(i),descriptions.get(i));
            stageMap.put(i,nameAndDesc);
        }
        return stageMap;
    }
    public static ArrayList<HashMap<String,Integer>>loadCostMapStages(String rawMap){
        ArrayList<HashMap<String,Integer>> costs = new ArrayList<>();
        ArrayList<String>map = loadEntries(rawMap,";");
        for (String s : map) {
            costs.add(loadCostMap(s));
        }
        return costs;
    }

}
