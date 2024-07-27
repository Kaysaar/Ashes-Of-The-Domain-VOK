package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.PCFPlanetIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.SortingState;
import data.kaysaar.aotd.vok.campaign.econ.listeners.NidavelirIndustryOptionProvider;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.SearchBarStringComparator;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import org.apache.log4j.Logger;

import java.util.*;

public class GPManager {
    public static final Logger log = Global.getLogger(GPManager.class);
    transient ArrayList<GPSpec> specs = new ArrayList<GPSpec>();
    transient ArrayList<GPSpec> specialProjectSpecs = new ArrayList<GPSpec>();
    public static boolean isEnabled = true;

    public ArrayList<GPSpec> getSpecialProjectSpecs() {
        return specialProjectSpecs;
    }

    ArrayList<GpManufacturerData>manufacturerData = new ArrayList<>();
    ArrayList<GPOption> shipProductionOption = new ArrayList<>();
    ArrayList<GPOption> weaponProductionOption = new ArrayList<>();
    ArrayList<GPOption> fighterProductionOption = new ArrayList<>();
    ArrayList<GPOption> specialProjectsOption = new ArrayList<>();
    ArrayList<GpSpecialProjectData>specialProjData = new ArrayList<>();
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
    public MutableStat productionSpeedBonus = new MutableStat(1f);

    public MutableStat getProductionSpeedBonus() {
        return productionSpeedBonus;
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
    public static int scale = 10;
    public ArrayList<GPSpec> getSpecs() {
        return specs;
    }
    public GpSpecialProjectData currentFocus;

    public GpSpecialProjectData getCurrProjOnGoing() {
        return currentFocus;
    }

    public void setCurrentFocus(GpSpecialProjectData currentFocus) {
        if(currentFocus!=null){
            currentFocus.hasStarted = true;
            if(!currentFocus.havePaidInitalCost){
                currentFocus.havePaidInitalCost = true;
                for (Map.Entry<String, Integer> entry : currentFocus.getSpec().getItemInitCostMap().entrySet()) {
                    AoTDMisc.eatItems(entry, Submarkets.SUBMARKET_STORAGE,Misc.getPlayerMarkets(true));
                }
                if(currentFocus.getSpec().getCredistCost()>0){
                    Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(currentFocus.getSpec().getCredistCost());
                }
            }
            if(currentFocus.getCurrentStage()==-1){
                currentFocus.currentStage=0;
            }
            if(currentFocus.isFinished()){
                currentFocus.totalDaysSpent = 0f;
                currentFocus.currentStage = -1;
                currentFocus.haveRecivedAward = false;
                currentFocus.havePaidInitalCost = false;

            }


        }
        this.currentFocus = currentFocus;
    }

    LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> weaponManInfo = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> fighterManInfo = new LinkedHashMap<>();
    public LinkedHashMap<String, Integer> totalResources = new LinkedHashMap<>();
    public HashMap<String, Integer> reqResources = new HashMap<>();
    public HashMap<String, Integer> availableResources = new HashMap<>();
    HashMap<String, Boolean> productionAvailbilityMap = new HashMap<>();
    public static ArrayList<String> commodities = new ArrayList<>();

    static {

        commodities.add(Commodities.SHIPS);
        commodities.add(Commodities.HAND_WEAPONS);
        commodities.add("advanced_components");
    }
    public static String memkey = "aotd_gp_plugin";

    public ArrayList<GPOption> getSpecialProjectsOption() {
        return specialProjectsOption;
    }
    public ArrayList<GpSpecialProjectData>getSpecialProjects(){
        return specialProjData;
    }
    public boolean hasAtLestOneProjectUnlocked(){
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if(specialProjDatum.canShow)return true;
        }
        return false;
    }

    public static GPManager getInstance() {
        if (Global.getSector().getPersistentData().get(memkey) == null) {
            return setInstance();
        }
        return (GPManager) Global.getSector().getPersistentData().get(memkey);
    }

    static GPManager setInstance() {
        GPManager manager = new GPManager();
        Global.getSector().getPersistentData().put(memkey, manager);
        Global.getSector().addScript(new GpManagerAdvance());
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
    public GpManufacturerData getManData(String manufacturer){
        for (GpManufacturerData manufacturerDatum : manufacturerData) {
            if(manufacturerDatum.getManufacturerId().equals(manufacturer)){
                return manufacturerDatum;
            }
        }
        return null;
    }

    public void reInitalize() {
        if(manufacturerData!=null){
            manufacturerData.clear();
        }
        isEnabled = AoTDSettingsManager.getBooleanValue("aotd_titans_of_industry");
        scale = AoTDSettingsManager.getIntValue("aotd_scale");
        if(isEnabled){
            manufacturerData = new ArrayList<>();
            manufacturerData.addAll(GpManufacturerData.getManufacturerDataFromCSV());
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


    }

    public LinkedHashMap<String, Integer> getTotalResources() {
        if (totalResources == null) totalResources = new LinkedHashMap<>();
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
        if(currentFocus!=null){
            for (Map.Entry<String, Integer> entry : currentFocus.getSpec().getStageSupplyCost().get(currentFocus.currentStage).entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() );
                }
            }
        }
        for (GPOrder productionOrder : productionOrders) {
            for (Map.Entry<String, Integer> entry : productionOrder.getReqResources().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() );
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
    public void addOrderToDummy(String specId, int amount,ArrayList<GPOrder>dummyOrders) {
        boolean foundOrderForSameItem = false;
        for (GPOrder order : dummyOrders) {
            if (order.getSpecFromClass().getProjectId().equals(specId)) {
                order.updateAmountToProduce(order.amountToProduce + amount);
                foundOrderForSameItem = true;
                break;
            }
        }
        if (!foundOrderForSameItem) {
            GPOrder order = new GPOrder(specId, amount);
            dummyOrders.add(order);

        }

    }
    public void removeOrder(String specId,int amount){
        for (GPOrder order : productionOrders) {
            if (order.getSpecFromClass().getProjectId().equals(specId)) {
                order.updateAmountToProduce(order.amountToProduce - amount);
                break;
            }
        }
    }
    public void removeOrderFromDummy(String specId,int amount,ArrayList<GPOrder>dummy){
        for (GPOrder order : dummy) {
            if (order.getSpecFromClass().getProjectId().equals(specId)) {
                order.updateAmountToProduce(order.amountToProduce - amount);
                break;
            }
        }
    }
    public boolean hasSpecialProject(String rewardId){
        for (GPSpec projectSpec : specialProjectSpecs) {
            if(projectSpec.getRewardId().equals(rewardId)){
                return true;
            }
        }
        return false;
    }
    public void loadProductionSpecs() {
        if(specs!=null){
            specs.clear();
        }
        else {
            specs = new ArrayList<>();
        }
        if(specialProjectSpecs!=null){
            specialProjectSpecs.clear();
        }
        else{
            specialProjectSpecs = new ArrayList<>();
        }
        specialProjectSpecs.addAll(GPSpec.loadSpecialProjects());
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
            if(!hasSpecialProject(spec.getIdOfItemProduced())){
                specs.add(spec);
            }
   ;


        }
        for (WeaponSpecAPI shipHullSpecAPI : Global.getSettings().getAllWeaponSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWeapon(shipHullSpecAPI);
            if(!hasSpecialProject(spec.getIdOfItemProduced())){
                specs.add(spec);
            }
        }
        for (FighterWingSpecAPI shipHullSpecAPI : Global.getSettings().getAllFighterWingSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWing(shipHullSpecAPI);
            if(!hasSpecialProject(spec.getIdOfItemProduced())){
                specs.add(spec);
            }
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
    public GpSpecialProjectData getSpecialProject(String id ){
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if(specialProjDatum.getSpec().getProjectId().equals(id)){
                return specialProjDatum;
            }
        }
        return null;
    }
    public boolean haveMetReqForItems(String id) {
        if (getSpecialProject(id).havePaidInitalCost)return true;
        for (Map.Entry<String, Integer> entry :getSpecialProject(id).getSpec().getItemInitCostMap().entrySet()) {
            if (!haveMetReqForItem(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public boolean haveMetReqForItem(String id, float value) {
        return value<=AoTDMisc.retrieveAmountOfItems(id, Submarkets.SUBMARKET_STORAGE);
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
        specialProjectsOption.clear();
        for (GPSpec spec : specs) {

            if (spec.type.equals(GPSpec.ProductionType.SHIP)) {
                GPOption option = new GPOption(spec, true, GPSpec.ProductionType.SHIP);
                shipProductionOption.add(option);
            }
            if (spec.type.equals(GPSpec.ProductionType.WEAPON)) {
                GPOption option = new GPOption(spec, true, GPSpec.ProductionType.WEAPON);
                weaponProductionOption.add(option);
            }
            if (spec.type.equals(GPSpec.ProductionType.FIGHTER)) {
                GPOption option = new GPOption(spec, true, GPSpec.ProductionType.FIGHTER);
                fighterProductionOption.add(option);
            }

        }
        for (GPSpec specialProjectSpec : specialProjectSpecs) {
            try {
                Global.getSettings().getHullSpec(specialProjectSpec.getRewardId());
            }
            catch (Exception e ){
                //we do this to filter out projects that should not be (as long as mod is not enabled in save)
                continue;
            }
            boolean foundOne = false;
            for (GpSpecialProjectData datum : specialProjData) {
                if(datum.getSpec().getProjectId().equals(specialProjectSpec.getProjectId())){
                    foundOne = true;
                    datum.setSpecID(specialProjectSpec.getProjectId());
                    break;
                }
            }
            if(!foundOne){
                GpSpecialProjectData data = new GpSpecialProjectData(specialProjectSpec.getProjectId());
                specialProjData.add(data);
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
    public ArrayList<GPOption> getShipsBasedOnSize(String size) {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getLearnedShipPackages()) {
            if(Misc.getHullSizeStr(option.getSpec().getShipHullSpecAPI().getHullSize()).equals(size)){
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getWeaponBasedOnSize(String size) {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getLearnedWeapons()) {
            if(option.getSpec().getWeaponSpec().getSize().getDisplayName().equals(size)){
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getShipBasedOnType(String type) {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getLearnedShipPackages()) {
            if(AoTDMisc.getType(option.getSpec().getShipHullSpecAPI()).equals(type)){
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getWeaponBasedOnType(String type) {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getLearnedWeapons()) {
            if(option.getSpec().getWeaponSpec().getType().getDisplayName().equals(type)){
                options.add(option);
            }
        }
        return options;
    }
    public ArrayList<GPOption> getFighterBasedOnType(String type) {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getLearnedFighters()) {
            if(AoTDMisc.getType(option.getSpec().getWingSpecAPI()).equals(type)){
                options.add(option);
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
            String indicator = AoTDMisc.getType(learnedShipPackage.getSpec().shipHullSpecAPI);
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
            String indicator = Misc.getHullSizeStr(learnedShipPackage.getSpec().getShipHullSpecAPI().getHullSize());
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
        if(!GPManager.isEnabled)return;
        Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).modifyMult("aotd_gp", 0, "Global Production Mechanic (AOTD)");
       if(!AoTDMisc.isPLayerHavingHeavyIndustry())return;
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if(specialProjDatum.isDiscovered()){
                if(!specialProjDatum.isShowedInfoAboutUnlocking()){
                    specialProjDatum.setShowedInfoAboutUnlocking(true);
                    SpecialProjectUnlockingIntel intel = new SpecialProjectUnlockingIntel(specialProjDatum);
                    Global.getSector().getIntelManager().addIntel(intel, false);
                }
                specialProjDatum.canShow = true;

            }
        }
        ArrayList<Integer> offsetOfOrdersToBeRemoved = retrieveOrdersToBeRemoved();
        if (!offsetOfOrdersToBeRemoved.isEmpty()) {
            removeDoneOrders(offsetOfOrdersToBeRemoved);
        }

        HashMap<String, Integer> available = new HashMap<>();
        available.putAll(getTotalResources());
        HashMap<String, Integer> availableUnchanged = new HashMap<>();
        availableUnchanged.putAll(getTotalResources());
        if(currentFocus!=null){
            if(currentFocus.canSupportStageConsumption(available)){
                for (Map.Entry<String, Integer> entry : currentFocus.getSpec().getStageSupplyCost().get(currentFocus.currentStage).entrySet()) {
                    int availableCommoditySize = available.get(entry.getKey());
                    available.put(entry.getKey(), availableCommoditySize - entry.getValue());

                }
                currentFocus.advance(amount);
            }

        }
        availableUnchanged.clear();
        availableUnchanged.putAll(available);
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
    public ArrayList<Integer> retrieveOrdersToBeRemovedFromDummy(ArrayList<GPOrder>dummyOrders) {
        ArrayList<Integer> map = new ArrayList<>();
        int i = 0;
        for (GPOrder productionOrder : dummyOrders) {
            if (productionOrder.isAboutToBeRemoved()) {
                map.add(i);
            }
            i++;
        }
        return map;
    }
    public void removeDoneOrders(ArrayList<Integer> offsets) {
        for (Integer offset : offsets) {
            if(offset.intValue()>=productionOrders.size())continue;
            productionOrders.remove(offset.intValue());
        }
    }
    public void removeDoneOrdersDummy(ArrayList<Integer> offsets,ArrayList<GPOrder>dummyOrders) {
        for (Integer offset : offsets) {
            if(offset.intValue()>=dummyOrders.size())continue;
            dummyOrders.remove(offset.intValue());
        }
    }
}
