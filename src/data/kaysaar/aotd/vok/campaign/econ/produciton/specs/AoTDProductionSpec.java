package data.kaysaar.aotd.vok.campaign.econ.produciton.specs;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.LinkedHashMap;

public class AoTDProductionSpec {
    public enum AoTDProductionSpecType {
        SHIP,
        WEAPON,
        FIGHTER,
        COMMODITY_ITEM,
        SPECIAL_ITEM

    }
    String id;
    public AoTDProductionSpecType type;
    public LinkedHashMap<String,Integer>mapOfResourcesNeeded = new LinkedHashMap<>();

    public LinkedHashMap<String, Integer> getMapOfResourcesNeeded() {
        return mapOfResourcesNeeded;
    }
    public int daysToBeCreated = 1;

    public int getDaysToBeCreated() {
        return daysToBeCreated;
    }

    public Object getUnderlyingSpec() {
        if (id == null || id.isEmpty()) return null;
        if (type == null) return null;
        return switch (type) {
            case SHIP -> Global.getSettings().getHullSpec(id);
            case WEAPON -> Global.getSettings().getWeaponSpec(id);
            case FIGHTER -> Global.getSettings().getFighterWingSpec(id);
            case COMMODITY_ITEM -> Global.getSettings().getCommoditySpec(id);
            case SPECIAL_ITEM -> Global.getSettings().getSpecialItemSpec(id);
            default -> null;
        };
    }

    public void setDaysToBeCreated(int daysToBeCreated) {
        this.daysToBeCreated = daysToBeCreated;
        if(daysToBeCreated<=0){
           this.daysToBeCreated =1;
        }
    }

    public AoTDProductionSpec(String id, Object spec){
        this.id = id;
        if(spec instanceof CommoditySpecAPI){
            type = AoTDProductionSpecType.COMMODITY_ITEM;
            int price = (int) getMoneyPrice();
            int needed = Math.round(price*0.1f);
            int advanced_component_mult = 10000;
            int domain_grade_mult = 2000;
            int tenebriumMult = 200000;
            int daysMult = 2500;
            // Remember to do commodity cost
            int basePrice = (int) getMoneyPrice();
            float newDays = basePrice/daysMult;
            mapOfResourcesNeeded.put("advanced_components",  Math.max(1, needed/200));
            setDaysToBeCreated((int) newDays);
        }
        if(spec instanceof SpecialItemSpecAPI specAPI){
            type = AoTDProductionSpecType.SPECIAL_ITEM;
            int advanced_component_mult = 10000;
            int domain_grade_mult = 2000;
            int tenebriumMult = 200000;
            int daysMult = 2500;
            if(specAPI.getManufacturer().equals("Abyss-Tech")){
                advanced_component_mult= 20000;
                domain_grade_mult= 10000;
                daysMult = 10000;
            }
            // Remember to do commodity cost
            int basePrice = (int) getMoneyPrice();
            int needed = Math.round(basePrice*0.1f);
            float newDays = basePrice/daysMult;
            setDaysToBeCreated((int) newDays);
            mapOfResourcesNeeded.put("advanced_components",  Math.max(1, needed/200));
            mapOfResourcesNeeded.put(AoTDCommodities.DOMAIN_GRADE_MACHINERY,  Math.max(1, needed/600));
            if(specAPI.getManufacturer().equals("Abyss-Tech")){
                mapOfResourcesNeeded.put(AoTDItems.TENEBRIUM_CELL,  Math.max(1, basePrice/tenebriumMult));
            }
        }
        if(spec instanceof ShipHullSpecAPI specAPI){
            type = AoTDProductionSpecType.SHIP;
            int basePrice = (int) getMoneyPrice();
            int needed = Math.round(basePrice*0.1f);
            int dayScaling = 10000;
            int advancedComponentsScaling = 30000;
            int days =1;
            int tenebriumMult = 200000;
            mapOfResourcesNeeded.put(Commodities.SHIPS, Math.max(1, needed/200));
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
            if(AoTDProductionSpecManager.getManDataIfPresent(specAPI.getManufacturer())!=null){
                int advanced_Components = basePrice/advancedComponentsScaling;
                mapOfResourcesNeeded.put("advanced_components",Math.min(advanced_Components,AoTDProductionSpecManager.getManDataIfPresent(specAPI.getManufacturer()).getMaxACCostForShip()));
            }
            if(specAPI.getManufacturer().equals("Abyss-Tech")){
                int tenebrium = Math.max(basePrice/tenebriumMult,1);
                mapOfResourcesNeeded.put(AoTDItems.TENEBRIUM_CELL,tenebrium);
            }
            if(days<=0)days=1;

            this.setDaysToBeCreated(days);
        }
        if(spec instanceof WeaponSpecAPI specAPI){
            type = AoTDProductionSpecType.WEAPON;
            int basePrice = (int) getMoneyPrice();
            int needed = Math.round(basePrice*0.1f);
            int dayScaling = 1000;
            int tenebriumMult =2;

            mapOfResourcesNeeded.put(Commodities.HAND_WEAPONS,  Math.max(1, needed/200));
            int days =0;
            if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
                days = Math.min(basePrice/dayScaling,30);
            }
            if(specAPI.getSize().equals(WeaponAPI.WeaponSize.MEDIUM)){
                days = Math.min(basePrice/dayScaling,40);
            }
            if(specAPI.getSize().equals(WeaponAPI.WeaponSize.LARGE)){
                days = Math.min(basePrice/dayScaling,50);
            }
            if(AoTDProductionSpecManager.getManDataIfPresent(specAPI.getManufacturer())!=null){
                int advanced_Components = AoTDProductionSpecManager.getManDataIfPresent(specAPI.getManufacturer()).getMaxAcCostForWeapon(specAPI);
                mapOfResourcesNeeded.put("advanced_components",advanced_Components);
            }
            if(specAPI.getManufacturer().toLowerCase().equals("abyss-tech")){
                String substrate = specAPI.getTags().stream().filter(x->x.contains("substrate")).findFirst().orElse(null);
                if(substrate!=null){
                    int number = Integer.parseInt(substrate.split("_")[1]);
                    mapOfResourcesNeeded.put(AoTDItems.TENEBRIUM_CELL,number*tenebriumMult);
                }

            }
            setDaysToBeCreated(days);

        }
        if(spec instanceof FighterWingSpecAPI specAPI ){
            type = AoTDProductionSpecType.FIGHTER;
            int priceScaling = 5000;
            int price = (int) getMoneyPrice();
            int needed = Math.round(price*0.1f);
            int dayScaling = 10000;
            int advanced_comp_scaling = 10000;
            float newDays = price/dayScaling;
            if(newDays<=0){
                newDays = 2;
            }
            int priceSc =(int) price/priceScaling;
            if(priceSc<=2){
                priceSc =2;
            }

            setDaysToBeCreated((int) newDays);
            mapOfResourcesNeeded.put(Commodities.SHIPS,priceSc/2);

            mapOfResourcesNeeded.put(Commodities.HAND_WEAPONS,priceSc/2);
            if(AoTDProductionSpecManager.getManDataIfPresent(specAPI.getVariant().getHullSpec().getManufacturer())!=null){
                int advanced_Components = price/advanced_comp_scaling;
                if(advanced_Components==0)advanced_Components =1;
                mapOfResourcesNeeded.put("advanced_components",Math.min(advanced_Components,AoTDProductionSpecManager.getManDataIfPresent(specAPI.getVariant().getHullSpec().getManufacturer()).getMaxACCostForFighter()));
            }
        }

    }
    public int getProductionCost(){
        return Math.round(getMoneyPrice()*0.6f);
    }
    public String getId() {
        return id;
    }
    public AoTDProductionSpecType getProductionType(){
        return type;
    }


    public float getMoneyPrice() {
        return switch (type) {
            case SHIP -> Global.getSettings().getHullSpec(id).getBaseValue();
            case WEAPON -> Global.getSettings().getWeaponSpec(id).getBaseValue();
            case FIGHTER -> Global.getSettings().getFighterWingSpec(id).getBaseValue();
            case COMMODITY_ITEM -> Global.getSettings().getCommoditySpec(id).getBasePrice();
            case SPECIAL_ITEM -> Global.getSettings().getSpecialItemSpec(id).getBasePrice();
            default -> 0f;
        };
    }

    public String getName() {
        return switch (type) {
            case SHIP -> Global.getSettings().getHullSpec(id).getHullName();
            case WEAPON -> Global.getSettings().getWeaponSpec(id).getWeaponName();
            case FIGHTER -> Global.getSettings().getFighterWingSpec(id).getWingName();
            case COMMODITY_ITEM -> Global.getSettings().getCommoditySpec(id).getName();
            case SPECIAL_ITEM -> Global.getSettings().getSpecialItemSpec(id).getName();
            default -> null;
        };
    }

    public String getSize() {
        return switch (type) {
            case SHIP -> {
                ShipHullSpecAPI spec = Global.getSettings().getHullSpec(id);
                ShipAPI.HullSize size = spec.getHullSize();
                yield Misc.getHullSizeStr(size);
            }
            case WEAPON -> {
                WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
                WeaponAPI.WeaponSize size = spec.getSize();
                yield switch (size) {
                    case SMALL -> "Small";
                    case MEDIUM -> "Medium";
                    case LARGE -> "Large";
                    default -> "Undefined";
                };
            }
            default -> null;
        };
    }

    public String getTypeString() {
        return switch (type) {
            case SHIP -> {
                ShipHullSpecAPI spec = Global.getSettings().getHullSpec(id);
                yield AoTDMisc.getType(spec);
            }
            case WEAPON -> {
                WeaponSpecAPI spec = Global.getSettings().getWeaponSpec(id);
                yield spec.getType().getDisplayName();
            }
            case FIGHTER -> {
                FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(id);
                yield AshMisc.getType(spec);
            }
            default -> null;
        };
    }
    public String getManufacturer(){
        return switch (type) {
            case SHIP -> Global.getSettings().getHullSpec(id).getManufacturer();
            case WEAPON -> Global.getSettings().getWeaponSpec(id).getManufacturer();
            case FIGHTER -> Global.getSettings().getFighterWingSpec(id).getVariant().getHullSpec().getManufacturer();
            case COMMODITY_ITEM -> "AI Cores";
            case SPECIAL_ITEM -> Global.getSettings().getSpecialItemSpec(id).getManufacturer();
            default -> "Unknown";
        };
    }
    public Color getManufacturerColor(){
        return Global.getSettings().getDesignTypeColor(getManufacturer());
    }

    public boolean isLearnedByFaction(FactionAPI faction){
        if(Global.getSettings().isDevMode())return true;
        return switch (type) {
            case SHIP -> faction.knowsShip(getId());
            case WEAPON -> faction.knowsWeapon(getId());
            case FIGHTER -> faction.knowsFighter(getId());
            case COMMODITY_ITEM, SPECIAL_ITEM -> AoTDMisc.knowsItem(getId(),faction);
            default ->false;
        };
    }
}
