package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UiPackage;
import data.kaysaar.aotd.vok.campaign.econ.listeners.NidavelirIndustryOptionProvider;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.SearchBarStringComparator;

import java.util.*;

public class GPManager {
    ArrayList<GPSpec> specs = new ArrayList<GPSpec>() {
        @Override
        public boolean contains(Object o) {
            String cnt = (String) o;
            for (GPSpec spec : this) {
                if (spec.getProjectId().contains(cnt)) {
                    return true;
                }
            }
            return false;
        }
    };
    ArrayList<GPOption> shipProductionOption = new ArrayList<>();
    ArrayList<GPOption> weaponProductionOption = new ArrayList<>();
    ArrayList<GPOption> fighterProductionOption = new ArrayList<>();
    ArrayList<GPOrder> productionOrders = new ArrayList<>();
    public LinkedHashMap<String, Integer> shipSizeInfo = new LinkedHashMap<>();

    public LinkedHashMap<String, Integer> getShipSizeInfo() {
        return shipSizeInfo;
    }
    public LinkedHashMap<String, Integer> weaponSizeInfo = new LinkedHashMap<>();

    public LinkedHashMap<String, Integer> getWeaponSizeInfo() {
        return weaponSizeInfo;
    }
    public LinkedHashMap<String, Integer> getShipManInfo() {
        return shipManInfo;
    }

    public LinkedHashMap<String, Integer> getWeaponManInfo() {
        return weaponManInfo;
    }
    public LinkedHashMap<String, Integer> getFighterManInfo() {
        return fighterManInfo;
    }
    public LinkedHashMap<String, Integer> shipTypeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> weaponTypeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> fighterTypeInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> getShipTypeInfo() {
        return shipTypeInfo;
    }
    public LinkedHashMap<String, Integer> getWeaponTypeInfo() {
        return weaponTypeInfo;
    }
    public ArrayList<GPOrder> getProductionOrders() {
        return productionOrders;
    }
    public LinkedHashMap<String, Integer> getFighterTypeInfo() {
        return fighterTypeInfo;
    }
    public static final int scale = 100;
    public ArrayList<GPSpec> getSpecs() {
        return specs;
    }

    LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> weaponManInfo = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
    public HashMap<String, Integer> totalResources = new HashMap<>();
    public HashMap<String, Integer> reqResources = new HashMap<>();
    public HashMap<String, Integer> availableResources = new HashMap<>();
    HashMap<String, Boolean> productionAvailbilityMap = new HashMap<>();
    public static ArrayList<String> commodities = new ArrayList<>();

    static {
        commodities.add(Commodities.SHIP_WEAPONS);
        commodities.add(Commodities.SHIPS);
        commodities.add(Commodities.HAND_WEAPONS);
    }
    public static String memkey = "aotd_gp_plugin";

    public static GPManager getInstance() {
        if (Global.getSector().getPersistentData().get(memkey) == null) {
            return setInstance();
        }
        return (GPManager) Global.getSector().getPersistentData().get(memkey);
    }

    static GPManager setInstance() {
        GPManager manager = new GPManager();
        Global.getSector().getPersistentData().put(memkey, manager);
        Global.getSector().addScript(new EveryFrameScript() {
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
                GPManager.getInstance().advance(amount);
            }
        });
        return manager;
    }



    public GPSpec getSpec(String id) {
        for (GPSpec spec : specs) {
            if (spec.getProjectId().equals(id)) {
                return spec;
            }
        }
        return null;
    }

    public void reInitalize() {
        loadProductionSpecs();
        loadProductionOptions();
        for (GPOrder productionOrder : productionOrders) {
            productionOrder.updateResourceCost();
        }
        ListenerManagerAPI l = Global.getSector().getListenerManager();
        if (!l.hasListenerOfClass(NidavelirIndustryOptionProvider.class)) {
            l.addListener(new NidavelirIndustryOptionProvider(), true);
        }

    }

    public HashMap<String, Integer> getTotalResources() {
        if (totalResources == null) totalResources = new HashMap<>();
        totalResources.clear();
        for (String s : commodities) {
            totalResources.put(s, 0);
        }
        for (MarketAPI factionMarket : Misc.getPlayerMarkets(true)) {
            for (Industry ind : factionMarket.getIndustries()) {
                if (ind instanceof HeavyIndustry) {
                    for (String commodity : commodities) {
                        int val = ind.getSupply(commodity).getQuantity().getModifiedInt() * scale;
                        if (totalResources.get(commodity) == null) {
                            totalResources.put(commodity, val);
                        } else {
                            int prev = totalResources.get(commodity);
                            totalResources.put(commodity, val + prev);
                        }
                    }
                }
            }
        }
        return totalResources;
    }

    public HashMap<String, Integer> getReqResources() {
        if (reqResources == null) reqResources = new HashMap<>();
        reqResources.clear();
        for (String s : commodities) {
            reqResources.put(s, 0);
        }
        for (GPOrder productionOrder : productionOrders) {
            for (Map.Entry<String, Integer> entry : productionOrder.getReqResources().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue() * productionOrder.amountToProduce);
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() * productionOrder.amountToProduce);
                }
            }
        }
        return reqResources;
    }

    public HashMap<String, Integer> getAvailableResources() {
        if (availableResources == null) availableResources = new HashMap<>();
        availableResources.clear();
        return reqResources;
    }

    public ArrayList<GPOption> getShipProductionOption() {
        return shipProductionOption;
    }



    public ArrayList<GPOption> getShipPackagesBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getShipHullSpecAPI().getHullName();
                    String s2 = o2.getSpec().getShipHullSpecAPI().getHullName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Size")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = Misc.getHullSizeStr(o1.getSpec().getShipHullSpecAPI().getHullSize());
                    String s2 = Misc.getHullSizeStr(o2.getSpec().getShipHullSpecAPI().getHullSize());
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = AoTDMisc.getType(o1.getSpec().getShipHullSpecAPI());
                    String s2 = AoTDMisc.getType(o2.getSpec().getShipHullSpecAPI());
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getShipHullSpecAPI().getManufacturer();
                    String s2 = o2.getSpec().getShipHullSpecAPI().getManufacturer();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }
    public ArrayList<GPOption> getFighterBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWingSpecAPI().getWingName();
                    String s2 = o2.getSpec().getWingSpecAPI().getWingName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWingSpecAPI().getRoleDesc();
                    String s2 = o2.getSpec().getWingSpecAPI().getRoleDesc();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getShipHullSpecAPI().getManufacturer();
                    String s2 = o2.getSpec().getShipHullSpecAPI().getManufacturer();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }
    public ArrayList<GPOption> getWeaponPackagesBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getWeaponName();
                    String s2 = o2.getSpec().getWeaponSpec().getWeaponName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Size")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getSize().getDisplayName();
                    String s2 = o2.getSpec().getWeaponSpec().getSize().getDisplayName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getType().getDisplayName();
                    String s2 = o2.getSpec().getWeaponSpec().getType().getDisplayName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getManufacturer();
                    String s2 = o2.getSpec().getWeaponSpec().getManufacturer();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }
    public ArrayList<GPOption> getShipPackagesOrderedByPrice(boolean ascending) {
        ArrayList<GPOption> packages = new ArrayList<>(getLearnedShipPackages());

        // Comparator for sorting by credistCost
        Comparator<GPOption> comparator = new Comparator<GPOption>() {
            @Override
            public int compare(GPOption o1, GPOption o2) {
                float price1 = o1.getSpec().getCredistCost();
                float price2 = o2.getSpec().getCredistCost();
                return Float.compare(price1, price2);
            }
        };

        // Sort the packages
        if (ascending) {
            Collections.sort(packages, comparator);
        } else {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }

        return packages;
    }



    public GPOption getOption(String id) {
        for (GPOption productionOption : shipProductionOption) {
            if (productionOption.spec.getProjectId().equals(id)) {
                return productionOption;
            }
        }
        return null;
    }

    public GPOrder getOrderViaSpec(String specID) {
        for (GPOrder productionOrder : productionOrders) {
            if (productionOrder.getSpecFromClass().getProjectId().equals(specID)) {
                return productionOrder;
            }
        }
        return null;
    }

    public void addOrder(String specId, int amount) {
        boolean foundOrderForSameItem = false;
        for (GPOrder order : productionOrders) {
            if (order.getSpecFromClass().getProjectId().equals(specId)) {
                order.updateAmountToProduce(order.amountToProduce + amount);
                foundOrderForSameItem = true;
                break;
            }
        }
        if (!foundOrderForSameItem) {
            GPOrder order = new GPOrder(specId, amount);
            productionOrders.add(order);

        }

    }

  ;
    public void loadProductionSpecs() {
        specs.clear();
        for (ShipHullSpecAPI shipHullSpecAPI : Global.getSettings().getAllShipHullSpecs()) {
            if (shipHullSpecAPI.getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) continue;
            if (shipHullSpecAPI.getHullSize().equals(ShipAPI.HullSize.FIGHTER)) continue;
            if (Global.getSettings().getHullIdToVariantListMap().get(shipHullSpecAPI.getHullId()).isEmpty()) {
                boolean found = false;
                for (String allVariantId : Global.getSettings().getAllVariantIds()) {
                    if (allVariantId.contains(shipHullSpecAPI.getHullId())) {
                        found = true;
                        break;
                    }

                }
                if (!found) continue;
            }
            GPSpec spec = GPSpec.getSpecFromShip(shipHullSpecAPI);
            specs.add(spec);


        }
        for (WeaponSpecAPI shipHullSpecAPI : Global.getSettings().getAllWeaponSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWeapon(shipHullSpecAPI);
            specs.add(spec);
        }
        for (FighterWingSpecAPI shipHullSpecAPI : Global.getSettings().getAllFighterWingSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWing(shipHullSpecAPI);
            specs.add(spec);
        }
    }

    public ArrayList<GPOption> getShipPackagesByManu(ArrayList<String> values) {

        ArrayList<GPOption> result = new ArrayList<>();
        for (GPOption uiPackage : getShipPackagesOrderedByPrice(false)) {
            for (String arg : values) {
                if (uiPackage.getSpec().getShipHullSpecAPI().getManufacturer().equals(arg)) {
                    result.add(uiPackage);
                    break;
                }
            }

        }
        return result;
    }
    public ArrayList<GPOption> getWeaponsByManu(ArrayList<String> values) {

        ArrayList<GPOption> result = new ArrayList<>();
        for (GPOption uiPackage : getLearnedWeapons()) {
            for (String arg : values) {
                if (uiPackage.getSpec().getWeaponSpec().getManufacturer().equals(arg)) {
                    result.add(uiPackage);
                    break;
                }
            }

        }
        return result;
    }
    public ArrayList<GPOption> getFightersByManu(ArrayList<String> values) {

        ArrayList<GPOption> result = new ArrayList<>();
        for (GPOption uiPackage : getLearnedFighters()) {
            for (String arg : values) {
                if (uiPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer().equals(arg)) {
                    result.add(uiPackage);
                    break;
                }
            }

        }
        return result;
    }
    public void loadProductionOptions() {
        if (this.shipManInfo == null) this.shipManInfo = new LinkedHashMap<>();
        if(this.fighterProductionOption == null)this.fighterProductionOption = new ArrayList<>();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        shipProductionOption.clear();
        weaponProductionOption.clear();
        fighterProductionOption.clear();
        for (GPSpec spec : specs) {
            if (spec.type.equals(GPSpec.ProductionType.SHIP)) {
                GPOption option = new GPOption(spec, true);
                shipProductionOption.add(option);
            }
            if (spec.type.equals(GPSpec.ProductionType.WEAPON)) {
                GPOption option = new GPOption(spec, true);
                weaponProductionOption.add(option);
            }
            if (spec.type.equals(GPSpec.ProductionType.FIGHTER)) {
                GPOption option = new GPOption(spec, true);
                fighterProductionOption.add(option);
            }

        }


    }

    public ArrayList<GPOption> getLearnedShipPackages() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getShipProductionOption()) {
            if (option.getSpec().type.equals(GPSpec.ProductionType.SHIP)) {
                if (Global.getSector().getPlayerFaction().knowsShip(option.getSpec().getShipHullSpecAPI().getHullId()) || Global.getSettings().isDevMode()) {
                    options.add(option);
                }


            }
        }
        return options;
    }

    public ArrayList<GPOption> getLearnedWeapons() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getWeaponProductionOption()) {
            if (Global.getSector().getPlayerFaction().knowsWeapon(option.getSpec().getWeaponSpec().getWeaponId()) || Global.getSettings().isDevMode()) {
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getLearnedFighters() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getFighterProductionOption()) {
            if (Global.getSector().getPlayerFaction().knowsFighter(option.getSpec().getWingSpecAPI().getId()) || Global.getSettings().isDevMode()) {
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getWeaponProductionOption() {
        return weaponProductionOption;
    }

    public ArrayList<GPOption> getFighterProductionOption() {
        return fighterProductionOption;
    }

    public ArrayList<GPOption> getMatchingShipGps(String value) {
        ArrayList<GPOption> options = new ArrayList<>();
        int threshold = 2; // Adjust the threshold based on your tolerance for misspellings
        SearchBarStringComparator comparator = new SearchBarStringComparator(value, threshold);
        for (GPOption learnedShipPackage : getLearnedShipPackages()) {
            if (comparator.isValid(learnedShipPackage.getSpec().getShipHullSpecAPI().getHullName())) {
                options.add(learnedShipPackage);
            }
        }
        Collections.sort(options, comparator);
        return options;
    }
    public ArrayList<GPOption> getMatchingWeaponGps(String value) {
        ArrayList<GPOption> options = new ArrayList<>();
        int threshold = 2; // Adjust the threshold based on your tolerance for misspellings
        SearchBarStringComparator comparator = new SearchBarStringComparator(value, threshold);
        for (GPOption learnedShipPackage : getLearnedWeapons()) {
            if (comparator.isValid(learnedShipPackage.getSpec().getWeaponSpec().getWeaponName())) {
                options.add(learnedShipPackage);
            }
        }
        Collections.sort(options, comparator);
        return options;
    }
    public ArrayList<GPOption> getMatchingFighterGPs(String value) {
        ArrayList<GPOption> options = new ArrayList<>();
        int threshold = 2; // Adjust the threshold based on your tolerance for misspellings
        SearchBarStringComparator comparator = new SearchBarStringComparator(value, threshold);
        for (GPOption learnedShipPackage : getLearnedFighters()) {
            if (comparator.isValid(learnedShipPackage.getSpec().getWingSpecAPI().getWingName())) {
                options.add(learnedShipPackage);
            }
        }
        Collections.sort(options, comparator);
        return options;
    }


    public void populateShipTypeInfo() {
        if (this.shipTypeInfo == null) this.shipTypeInfo = new LinkedHashMap<>();
        this.shipTypeInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : getLearnedShipPackages()) {
            String indicator = AoTDMisc.getType(learnedShipPackage.spec.shipHullSpecAPI);
            if (shipManInfo.get(indicator) == null) {
                shipManInfo.put(indicator, 1);
            } else {
                int amount = shipManInfo.get(indicator);
                shipManInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All types", val);
        this.shipTypeInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }
    public void populateWeaponTypeInfo() {
        if (this.weaponTypeInfo == null) this.weaponTypeInfo = new LinkedHashMap<>();
        this.weaponTypeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapons : getLearnedWeapons()) {
            String indicator =learnedWeapons.getSpec().getWeaponSpec().getType().getDisplayName();
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All types", val);
        this.weaponTypeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }
    public void populateFighterTypeInfo() {
        if (this.fighterTypeInfo == null) this.fighterTypeInfo = new LinkedHashMap<>();
        this.fighterTypeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapons : getLearnedFighters()) {
            String indicator =AoTDMisc.getType(learnedWeapons.getSpec().getWingSpecAPI());
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All types", val);
        this.fighterTypeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }
    public void populateShipInfo() {
        this.shipManInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : getLearnedShipPackages()) {
            if (shipManInfo.get(learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer()) == null) {
                shipManInfo.put(learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer(), 1);
            } else {
                int amount = shipManInfo.get(learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer());
                shipManInfo.put(learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer(), amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All designs", val);

        this.shipManInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }
    public void populateWeaponInfo() {
        if (this.weaponManInfo == null) this.weaponManInfo = new LinkedHashMap<>();
        this.weaponManInfo.clear();
        LinkedHashMap<String, Integer> weaponManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : getLearnedWeapons()) {
            if (weaponManInfo.get(learnedShipPackage.getSpec().getWeaponSpec().getManufacturer()) == null) {
                weaponManInfo.put(learnedShipPackage.getSpec().getWeaponSpec().getManufacturer(), 1);
            } else {
                int amount = weaponManInfo.get(learnedShipPackage.getSpec().getWeaponSpec().getManufacturer());
                weaponManInfo.put(learnedShipPackage.getSpec().getWeaponSpec().getManufacturer(), amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponManInfo.values()) {
            val += value;
        }
        weaponManInfo.put("All designs", val);

        this.weaponManInfo.putAll(AoTDMisc.sortByValueDescending(weaponManInfo));
    }
    public void populateFighterInfo() {
        if (this.fighterManInfo == null) this.fighterManInfo = new LinkedHashMap<>();
        this.fighterManInfo.clear();
        LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : getLearnedFighters()) {
            if (fighterManInfo.get(learnedShipPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer()) == null) {
                fighterManInfo.put(learnedShipPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer(), 1);
            } else {
                int amount = fighterManInfo.get(learnedShipPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer());
                fighterManInfo.put(learnedShipPackage.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer(), amount + 1);
            }
        }
        int val = 0;
        for (Integer value : fighterManInfo.values()) {
            val += value;
        }
        fighterManInfo.put("All designs", val);

        this.fighterManInfo.putAll(AoTDMisc.sortByValueDescending(fighterManInfo));
    }
    public void populateShipSizeInfo() {
        if (this.shipSizeInfo == null) this.shipSizeInfo = new LinkedHashMap<>();
        this.shipSizeInfo.clear();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        for (GPOption learnedShipPackage : getLearnedShipPackages()) {
            String indicator = Misc.getHullSizeStr(learnedShipPackage.spec.getShipHullSpecAPI().getHullSize());
            if (shipManInfo.get(indicator) == null) {
                shipManInfo.put(indicator, 1);
            } else {
                int amount = shipManInfo.get(indicator);
                shipManInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : shipManInfo.values()) {
            val += value;
        }
        shipManInfo.put("All sizes", val);
        this.shipSizeInfo.putAll(AoTDMisc.sortByValueDescending(shipManInfo));
    }

    public void populateWeaponSizeInfo() {
        if (this.weaponSizeInfo == null) this.weaponSizeInfo = new LinkedHashMap<>();
        this.weaponSizeInfo.clear();
        LinkedHashMap<String, Integer> weaponInfo = new LinkedHashMap<>();
        for (GPOption learnedWeapon : getLearnedWeapons()) {
            String indicator = learnedWeapon.getSpec().getWeaponSpec().getSize().getDisplayName();
            if (weaponInfo.get(indicator) == null) {
                weaponInfo.put(indicator, 1);
            } else {
                int amount = weaponInfo.get(indicator);
                weaponInfo.put(indicator, amount + 1);
            }
        }
        int val = 0;
        for (Integer value : weaponInfo.values()) {
            val += value;
        }
        weaponInfo.put("All sizes", val);
        this.weaponSizeInfo.putAll(AoTDMisc.sortByValueDescending(weaponInfo));
    }
    public void advance(float amount) {
        Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).modifyMult("aotd_gp", 0, "Global Production Mechanic (AOTD)");

        ArrayList<Integer> offsetOfOrdersToBeRemoved = retrieveOrdersToBeRemoved();
        if (!offsetOfOrdersToBeRemoved.isEmpty()) {
            removeDoneOrders(offsetOfOrdersToBeRemoved);
        }

        HashMap<String, Integer> available = new HashMap<>(getTotalResources());
        HashMap<String, Integer> availableUnchanged = new HashMap<>(getTotalResources());
        for (GPOrder productionOrder : getProductionOrders()) {
            if (!productionOrder.isCountingToContribution()) continue;
            boolean metCriteriaWithResources = true;
            for (Map.Entry<String, Integer> entry : productionOrder.assignedResources.entrySet()) {
                int availableCommoditySize = available.get(entry.getKey());
                if (entry.getValue() > availableCommoditySize) {
                    metCriteriaWithResources = false;
                    break;
                } else {
                    available.put(entry.getKey(), availableCommoditySize - entry.getValue());
                    productionOrder.resourcesGet.put(entry.getKey(), entry.getValue());
                }
            }
            if (!metCriteriaWithResources) {
                available.clear();
                available.putAll(availableUnchanged);
            } else {
                availableUnchanged.clear();
                availableUnchanged.putAll(available);
            }
            if (productionOrder.canProceed()) {
                productionOrder.advance(amount);
            }
        }

    }

    public ArrayList<Integer> retrieveOrdersToBeRemoved() {
        ArrayList<Integer> map = new ArrayList<>();
        int i = 0;
        for (GPOrder productionOrder : productionOrders) {
            if (productionOrder.isAboutToBeRemoved()) {
                map.add(i);
            }
            i++;
        }
        return map;
    }

    public void removeDoneOrders(ArrayList<Integer> offsets) {
        for (Integer offset : offsets) {
            productionOrders.remove(offset.intValue());
        }
    }

}
