package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSpec;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GpMegaStructureSectionsSpec;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.misc.SearchBarStringComparator;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;

public class GPManager {
    public static final Logger log = Global.getLogger(GPManager.class);
    transient ArrayList<GPSpec> specs = new ArrayList<GPSpec>();
    transient ArrayList<GPSpec> specialProjectSpecs = new ArrayList<GPSpec>();
    transient ArrayList<GPMegaStructureSpec> megaStructureSpecs = new ArrayList<>();
    transient ArrayList<GpMegaStructureSectionsSpec> megaStructureSectionsSpecs = new ArrayList<>();
    public static boolean isEnabled = true;
    public int amountShipsPerOnce = 1;
    public int amountWepPerOnce = 1;
    public int amountFighterPerOnce = 1;
    GPUIData gpuiData;
    public MutableStat specialProjSpeed = new MutableStat(1f);
    public MutableStat cruiserCapitalSpeed = new MutableStat(1f);
    public MutableStat frigateDestroyerSpeed = new MutableStat(1f);
    public IntervalUtil intervalUtil = new IntervalUtil(1f, 2f);

    public MutableStat getCruiserCapitalSpeed() {
        if (cruiserCapitalSpeed == null) {
            cruiserCapitalSpeed = new MutableStat(1f);
        }
        return cruiserCapitalSpeed;
    }

    public MutableStat getFrigateDestroyerSpeed() {
        if (frigateDestroyerSpeed == null) {
            frigateDestroyerSpeed = new MutableStat(1f);
        }
        return frigateDestroyerSpeed;
    }

    public MutableStat getSpecialProjSpeed() {
        if (specialProjSpeed == null) {
            specialProjSpeed = new MutableStat(1f);
        }
        return specialProjSpeed;
    }


    protected ArrayList<GPBaseMegastructure> megastructures;

    public GPUIData getUIData() {
        if (gpuiData == null) {
            gpuiData = new GPUIData();
        }
        return gpuiData;
    }

    public ArrayList<GPSpec> getSpecialProjectSpecs() {
        if (specialProjectSpecs == null) {
            specialProjectSpecs = new ArrayList<>();
        }
        return specialProjectSpecs;
    }

    public int getAmountForOrder(GPOrder order) {
        return order.getAtOnce();

    }

    ArrayList<GpManufacturerData> manufacturerData = new ArrayList<>();
    ArrayList<GPOption> shipProductionOption = new ArrayList<>();
    ArrayList<GPOption> weaponProductionOption = new ArrayList<>();
    ArrayList<GPOption> fighterProductionOption = new ArrayList<>();
    ArrayList<GPOption> specialProjectsOption = new ArrayList<>();
    ArrayList<GPOption> itemProductionOption = new ArrayList<>();
    ArrayList<GpSpecialProjectData> specialProjData = new ArrayList<>();
    ArrayList<GPOrder> productionOrders = new ArrayList<>();

    public MutableStat getProductionSpeedBonus() {
        return productionSpeedBonus;
    }

    public MutableStat productionSpeedBonus = new MutableStat(1f);

    public ArrayList<GPOrder> getProductionOrders() {
        return productionOrders;
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
        if (currentFocus != null) {
            currentFocus.hasStarted = true;
            if (!currentFocus.havePaidInitalCost) {
                currentFocus.havePaidInitalCost = true;
                for (Map.Entry<String, Integer> entry : currentFocus.getSpec().getItemInitCostMap().entrySet()) {
                    AoTDMisc.eatItems(entry, Submarkets.SUBMARKET_STORAGE, Misc.getPlayerMarkets(true));
                }
                if (currentFocus.getSpec().getCredistCost() > 0) {
                    Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(currentFocus.getSpec().getCredistCost());
                }
            }
            if (currentFocus.getCurrentStage() == -1) {
                currentFocus.currentStage = 0;
            }
            if (currentFocus.isFinished()) {
                currentFocus.totalDaysSpent = 0f;
                currentFocus.currentStage = -1;
                currentFocus.haveRecivedAward = false;
                currentFocus.havePaidInitalCost = false;
                new Color(58, 63, 58);
            }
        }
        this.currentFocus = currentFocus;
    }

    public LinkedHashMap<String, Integer> totalResources = new LinkedHashMap<>();
    public HashMap<String, Integer> reqResources = new HashMap<>();
    public static ArrayList<String> commodities = new ArrayList<>();

    static {

        commodities.add(Commodities.SHIPS);
        commodities.add(Commodities.HAND_WEAPONS);
        commodities.add("advanced_components");
        commodities.add("domain_heavy_machinery");
        commodities.add("refined_metal");
        commodities.add("purified_rare_metal");
    }

    public static String memkey = "aotd_gp_plugin";

    public ArrayList<GpSpecialProjectData> getSpecialProjects() {
        return specialProjData;
    }

    public boolean hasAtLestOneProjectUnlocked() {
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if (specialProjDatum.canShow) return true;
        }
        return false;
    }

    public static GPManager getInstance() {
        if (Global.getSector().getPersistentData().get(memkey) == null) {
            return setInstance();
        }
        return (GPManager) Global.getSector().getPersistentData().get(memkey);
    }

    public static ArrayList<String> getCommodities() {
        return commodities;
    }

    public ArrayList<GPBaseMegastructure> getMegastructures() {
        return megastructures;
    }

    public ArrayList<GPBaseMegastructure> getMegastructuresBasedOnClass(Class<?> t) {
        ArrayList<GPBaseMegastructure> mega = new ArrayList<>();
        for (GPBaseMegastructure megastructure : megastructures) {
            if (megastructure.getClass().equals(t)) {
                mega.add(megastructure);
            }
        }
        return mega;
    }

    public ArrayList<GPBaseMegastructure> getMegastructureBasedOnSpecID(Class<?> t) {
        ArrayList<GPBaseMegastructure> mega = new ArrayList<>();
        for (GPBaseMegastructure megastructure : megastructures) {
            if (megastructure.getClass().equals(t)) {
                mega.add(megastructure);
            }
        }
        return mega;
    }

    public float getTotalUpkeeepCreditsForMega() {
        float upkeep = 0f;
        for (GPBaseMegastructure megastructure : megastructures) {
            upkeep += megastructure.getUpkeep();
        }
        return upkeep;
    }

    public HashMap<String, Integer> getTotalUpkeepGPForMega() {
        HashMap<String, Integer> upkeep = new HashMap<>();
        for (GPBaseMegastructure megastructure : megastructures) {
            for (Map.Entry<String, Integer> s : megastructure.getDemand().entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(upkeep, s.getKey(), s.getValue());
            }
        }
        return upkeep;
    }

    public float getTotalPenaltyFromResources(String... resources) {
        float penalty = 1f;
        HashMap<String, Float> pen = getPenaltyMap();
        for (String resource : resources) {
            if (pen.get(resource) != null) {
                penalty *= pen.get(resource);
            }

        }
        return penalty;
    }

    public static GPManager setInstance() {
        GPManager manager = new GPManager();
        Global.getSector().getPersistentData().put(memkey, manager);
        Global.getSector().addScript(new GpManagerAdvance());
        return manager;
    }

    public void addMegastructureToList(GPBaseMegastructure megastructure) {
        megastructures.add(megastructure);
    }

    public void removeMegastructureFromList(GPBaseMegastructure megastructure) {
        megastructures.remove(megastructure);
    }

    public GPSpec getSpec(String id) {
        for (GPSpec spec : specs) {
            if (spec.getProjectId().equals(id)) {
                return spec;
            }
        }
        return null;
    }

    public GpManufacturerData getManData(String manufacturer) {
        for (GpManufacturerData manufacturerDatum : manufacturerData) {
            if (manufacturerDatum.getManufacturerId().equals(manufacturer)) {
                return manufacturerDatum;
            }
        }
        return null;
    }

    public void reInitalize() {
        if (manufacturerData != null) {
            manufacturerData.clear();
        }
        if (megastructures == null) {
            megastructures = new ArrayList<>();
        }

        isEnabled = AoTDSettingsManager.getBooleanValue("aotd_titans_of_industry");
        scale = AoTDSettingsManager.getIntValue("aotd_scale");
        manufacturerData = new ArrayList<>();
        manufacturerData.addAll(GpManufacturerData.getManufacturerDataFromCSV());
        loadProductionSpecs();
        loadProductionOptions();
        loadMegastructureSpecs();
        for (GPOrder productionOrder : productionOrders) {
            productionOrder.updateResourceCost();
        }
    }

    public LinkedHashMap<String, Integer> getTotalResources() {

        if (totalResources == null) totalResources = new LinkedHashMap<>();
        totalResources.clear();
        int amount = 0;
//        if(Global.getSettings().isDevMode()){
//            amount = 10000;
//        }
        for (String s : commodities) {
            totalResources.put(s, amount);
        }
        for (MarketAPI factionMarket : Misc.getPlayerMarkets(true)) {
            for (Industry ind : factionMarket.getIndustries()) {
                if (ind.getSpec().hasTag("ignore_gp")) continue;
                for (String commodity : commodities) {
                    int val = ind.getSupply(commodity).getQuantity().getModifiedInt() * scale;
                    AoTDMisc.putCommoditiesIntoMap(totalResources, commodity, val);
                }

            }
        }
        HashMap<String, Integer> resourcesFromMega = new HashMap<>();
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
            HashMap<String, Integer> megaMap = megastructure.getProductionWithoutPenalty();
            for (Map.Entry<String, Integer> entry : megaMap.entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(resourcesFromMega, entry.getKey(), entry.getValue());
            }
        }
        resourcesFromMega.putAll(totalResources);
        HashMap<String, Float> map = getPenaltyMap(getProductionOrders(), resourcesFromMega);
        for (GPBaseMegastructure megastructure : GPManager.getInstance().getMegastructures()) {
            HashMap<String, Integer> megaMap = megastructure.getProduction(map);
            for (Map.Entry<String, Integer> entry : megaMap.entrySet()) {
                AoTDMisc.putCommoditiesIntoMap(totalResources, entry.getKey(), entry.getValue());
            }
        }


        return totalResources;
    }


    public LinkedHashMap<MarketAPI, Integer> getTotalResourceProductionFromMarkets(String commodity) {
        LinkedHashMap<MarketAPI, Integer> map = new LinkedHashMap<>();
        for (MarketAPI factionMarket : Misc.getPlayerMarkets(true)) {
            for (Industry ind : factionMarket.getIndustries()) {
                if (ind.getSpec().hasTag("ignore_gp")) continue;
                int val = ind.getSupply(commodity).getQuantity().getModifiedInt() * scale;
                if (val == 0) continue;
                if (map.get(factionMarket) != null) {
                    if (val >= map.get(factionMarket)) {
                        map.put(factionMarket, val + map.get(factionMarket));
                    }
                } else {
                    map.put(factionMarket, val);
                }


            }
        }
        return map;
    }

    public HashMap<String, Integer> getReqResources(ArrayList<GPOrder> orders) {
        if (reqResources == null) reqResources = new HashMap<>();
        reqResources.clear();
        for (String s : commodities) {
            reqResources.put(s, 0);
        }
        if (currentFocus != null) {
            for (Map.Entry<String, Integer> entry : currentFocus.getSpec().getStageSupplyCost().get(currentFocus.currentStage).entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue());
                }
            }
        }
        for (GPOrder productionOrder : orders) {
            for (Map.Entry<String, Integer> entry : productionOrder.getReqResources().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue() * getAmountForOrder(productionOrder));
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() * getAmountForOrder(productionOrder));
                }
            }
        }
        for (GPBaseMegastructure megastructure : megastructures) {
            for (Map.Entry<String, Integer> entry : megastructure.getDemand().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue());
                }
            }
        }

        return reqResources;
    }


    public ArrayList<GPOption> getShipProductionOption() {
        return shipProductionOption;
    }

    public ArrayList<GPOption> getItemProductionOption() {

        return itemProductionOption;
    }

    public ArrayList<GPOption> getItemProductionOptionFiltered() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getItemProductionOption()) {
            if (option.getSpec().getItemSpecAPI() == null && option.getSpec().getAiCoreSpecAPI() != null) {
                option.getSpec().setType(GPSpec.ProductionType.AICORE);
                options.add(option);
                continue;
            }
            if (option.getSpec().getItemSpecAPI().hasTag("aotd_ignore_gp")) continue;
            if (option.getSpec().getItemSpecAPI().hasTag("mission_item")) continue;
            if (ItemEffectsRepo.ITEM_EFFECTS.get(option.getSpec().getProjectId()) != null) {
                options.add(option);
            }
        }
        return options;
    }

    public ArrayList<GPOption> getAICores() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getItemProductionOption()) {
            if (option.getSpec().getType().equals(GPSpec.ProductionType.AICORE)) {
                options.add(option);
            }
        }
        return options;
    }

    public void addOrderToDummy(String specId, int amount, ArrayList<GPOrder> dummyOrders) {
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


    public void removeOrderFromDummy(String specId, int amount, ArrayList<GPOrder> dummy) {
        for (GPOrder order : dummy) {
            if (order.getSpecFromClass().getProjectId().equals(specId)) {
                order.updateAmountToProduce(order.amountToProduce - amount);
                break;
            }
        }
    }

    public boolean hasSpecialProject(String rewardId) {
        for (GPSpec projectSpec : specialProjectSpecs) {
            if (projectSpec.getRewardId().equals(rewardId)) {
                return true;
            }
        }
        return false;
    }

    public void loadProductionSpecs() {
        if (specs != null) {
            specs.clear();
        } else {
            specs = new ArrayList<>();
        }
        if (specialProjectSpecs != null) {
            specialProjectSpecs.clear();
        } else {
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
            if (!hasSpecialProject(spec.getIdOfItemProduced())) {
                specs.add(spec);
            }
            ;


        }
        for (WeaponSpecAPI shipHullSpecAPI : Global.getSettings().getAllWeaponSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWeapon(shipHullSpecAPI);
            if (!hasSpecialProject(spec.getIdOfItemProduced())) {
                specs.add(spec);
            }
        }
        for (FighterWingSpecAPI shipHullSpecAPI : Global.getSettings().getAllFighterWingSpecs()) {
            GPSpec spec = GPSpec.getSpecFromWing(shipHullSpecAPI);
            if (!hasSpecialProject(spec.getIdOfItemProduced())) {
                specs.add(spec);
            }
        }
        for (SpecialItemSpecAPI s : Global.getSettings().getAllSpecialItemSpecs()) {
            GPSpec spec = GPSpec.getSpecFromItem(s);
            specs.add(spec);
        }
        for (CommoditySpecAPI s : Global.getSettings().getAllCommoditySpecs()) {
            if (s.hasTag("ai_core") && !s.hasTag("no_drop") && !s.getId().equals("ai_cores") && s.hasTag("aotd_ai_core")) {
                GPSpec spec = GPSpec.getSpecFromAICore(s);
                specs.add(spec);
            }

        }
    }

    public void loadMegastructureSpecs() {
        if (megaStructureSpecs == null) megaStructureSpecs = new ArrayList<>();
        if (megaStructureSectionsSpecs == null) megaStructureSectionsSpecs = new ArrayList<>();
        megaStructureSpecs.clear();
        megaStructureSectionsSpecs.clear();
        megaStructureSectionsSpecs.addAll(GpMegaStructureSectionsSpec.getSpecFromFiles());
        megaStructureSpecs.addAll(GPMegaStructureSpec.getSpecFromFiles());


    }

    public GPMegaStructureSpec getMegaSpecFromList(String id) {
        for (GPMegaStructureSpec spec : megaStructureSpecs) {
            if (spec.getMegastructureID().equals(id)) return spec;
        }
        return null;
    }

    public GPMegaStructureSpec getMegaSpecFromListByEntityId(String id) {
        for (GPMegaStructureSpec spec : megaStructureSpecs) {
            if (spec.getSectorEntityTokenId().equals(id)) return spec;
        }
        return null;
    }

    public GpMegaStructureSectionsSpec getMegaSectionSpecFromList(String id) {
        for (GpMegaStructureSectionsSpec spec : megaStructureSectionsSpecs) {
            if (spec.getSectionID().equals(id)) return spec;
        }
        return null;
    }

    public GpSpecialProjectData getSpecialProject(String id) {
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if (specialProjDatum.getSpec().getProjectId().equals(id)) {
                return specialProjDatum;
            }
        }
        return null;
    }

    public boolean haveMetReqForItems(String id) {
        if (getSpecialProject(id).havePaidInitalCost) return true;
        for (Map.Entry<String, Integer> entry : getSpecialProject(id).getSpec().getItemInitCostMap().entrySet()) {
            if (!haveMetReqForItem(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public boolean haveMetReqForItem(String id, float value) {
        return value <= AoTDMisc.retrieveAmountOfItems(id, Submarkets.SUBMARKET_STORAGE);
    }

    public LinkedHashMap<String, Integer> getExpectedCosts(ArrayList<GPOrder> ordersQueued) {
        LinkedHashMap<String, Integer> reqResources = new LinkedHashMap<>();
        reqResources.clear();
        for (String s : commodities) {
            reqResources.put(s, 0);
        }
        if (GPManager.getInstance().getCurrProjOnGoing() != null && !GPManager.getInstance().getCurrProjOnGoing().isFinished()) {
            for (Map.Entry<String, Integer> entry : GPManager.getInstance().getCurrProjOnGoing().retrieveCostForCurrStage().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue());
                }
            }
        }
        for (GPOrder productionOrder : ordersQueued) {
            for (Map.Entry<String, Integer> entry : productionOrder.getReqResources().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue() * GPManager.getInstance().getAmountForOrder(productionOrder));
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue() * GPManager.getInstance().getAmountForOrder(productionOrder));
                }
            }
        }
        for (GPBaseMegastructure s : megastructures) {
            for (Map.Entry<String, Integer> entry : s.getDemand().entrySet()) {
                if (reqResources.get(entry.getKey()) == null) {
                    reqResources.put(entry.getKey(), entry.getValue());
                } else {
                    int prev = reqResources.get(entry.getKey());
                    reqResources.put(entry.getKey(), prev + entry.getValue());
                }
            }
        }
        return reqResources;
    }

    public LinkedHashMap<String, Integer> getExpectedCostsFromManager() {
        return getExpectedCosts(getProductionOrders());
    }

    public void loadProductionOptions() {
        if (this.shipProductionOption == null) this.shipProductionOption = new ArrayList<>();
        if (this.fighterProductionOption == null) this.fighterProductionOption = new ArrayList<>();
        if (this.itemProductionOption == null) this.itemProductionOption = new ArrayList<>();
        LinkedHashMap<String, Integer> shipManInfo = new LinkedHashMap<>();
        shipProductionOption.clear();
        weaponProductionOption.clear();
        fighterProductionOption.clear();
        specialProjectsOption.clear();
        itemProductionOption.clear();
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
            if (spec.type.equals(GPSpec.ProductionType.ITEM)) {
                GPOption option = new GPOption(spec, true, GPSpec.ProductionType.ITEM);
                itemProductionOption.add(option);
            }
            if (spec.type.equals(GPSpec.ProductionType.AICORE)) {
                GPOption option = new GPOption(spec, true, GPSpec.ProductionType.AICORE);
                itemProductionOption.add(option);
            }

        }
        for (GPSpec specialProjectSpec : specialProjectSpecs) {
            try {
                Global.getSettings().getHullSpec(specialProjectSpec.getRewardId());
            } catch (Exception e) {
                //we do this to filter out projects that should not be (as long as mod is not enabled in save)
                continue;
            }
            boolean foundOne = false;
            for (GpSpecialProjectData datum : specialProjData) {
                if (datum.getSpec().getProjectId().equals(specialProjectSpec.getProjectId())) {
                    foundOne = true;
                    datum.setSpecID(specialProjectSpec.getProjectId());
                    break;
                }
            }
            if (!foundOne) {
                GpSpecialProjectData data = new GpSpecialProjectData(specialProjectSpec.getProjectId());
                specialProjData.add(data);
            }
        }


    }

    public ArrayList<GPOption> getLearnedShipPackages() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getShipProductionOption()) {
            if (option.getSpec().type.equals(GPSpec.ProductionType.SHIP)) {
                if (GpKnowledgeRepository.isKnownByPlayer(option)) {
                    options.add(option);
                }


            }
        }
        return options;
    }

    public ArrayList<GPOption> getLearnedItems() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getItemProductionOptionFiltered()) {
            if (option.getSpec().getType().equals(GPSpec.ProductionType.AICORE)) {
                if (AoTDMisc.doesPlayerHaveTuringEngine()) {
                    options.add(option);
                }
                continue;
            }
            if (option.getSpec().getItemSpecAPI().hasTag("aotd_ignore_gp")) continue;
            if (option.getSpec().getItemSpecAPI().hasTag("mission_item")) continue;
            if (AoTDMisc.knowsItem(option.getSpec().getItemSpecAPI().getId(), Global.getSector().getPlayerFaction()) || Global.getSettings().isDevMode()) {
                options.add(option);
            }
        }

        return options;
    }

    public ArrayList<GPOption> getLearnedWeapons() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getWeaponProductionOption()) {
            if (GpKnowledgeRepository.isKnownByPlayer(option)) {
                options.add(option);
            }
        }
        return options;
    }

    public ArrayList<GPOption> getLearnedFighters() {
        ArrayList<GPOption> options = new ArrayList<>();
        for (GPOption option : getFighterProductionOption()) {
            if (GpKnowledgeRepository.isKnownByPlayer(option)) {
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

    public ArrayList<GPOption> getMatchingItemGps(String value) {
        ArrayList<GPOption> options = new ArrayList<>();
        int threshold = 2; // Adjust the threshold based on your tolerance for misspellings
        SearchBarStringComparator comparator = new SearchBarStringComparator(value, threshold);
        for (GPOption learnedShipPackage : getLearnedItems()) {
            if (comparator.isValid(learnedShipPackage.getSpec().getItemSpecAPI().getName())) {
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

    public void mainAdvance(float amount) {
        if (intervalUtil == null) {
            intervalUtil = new IntervalUtil(1f, 2f);
        }
        intervalUtil.advance(amount);
        if (intervalUtil.intervalElapsed()) {
            advanceProductions(intervalUtil.getElapsed());
        }
    }

    public void advanceProductions(float amount) {

        if (!GPManager.isEnabled) {
            Global.getSector().getPlayerStats().getDynamic().getMod(Stats.CUSTOM_PRODUCTION_MOD).unmodifyMult("aotd_gp");
            return;
        }
        for (GPBaseMegastructure megastructure : megastructures) {
            megastructure.advance(amount);
        }
        advance(getProductionOrders());
        if (!AoTDMisc.isPLayerHavingHeavyIndustry()) return;
        for (GpSpecialProjectData specialProjDatum : specialProjData) {
            if (specialProjDatum.isDiscovered()) {
                if (!specialProjDatum.isShowedInfoAboutUnlocking()) {
                    specialProjDatum.setShowedInfoAboutUnlocking(true);
                    SpecialProjectUnlockingIntel intel = new SpecialProjectUnlockingIntel(specialProjDatum);
                    Global.getSector().getIntelManager().addIntel(intel, false);
                }
                specialProjDatum.canShow = true;

            }
        }
        if (currentFocus != null) {
            currentFocus.advance(amount);
        }
        for (GPOrder productionOrder : getProductionOrders()) {
            if (!productionOrder.isCountingToContribution()) continue;
            if (productionOrder.canProceed()) {
                productionOrder.advance(amount);
            }
        }
    }

    public HashMap<String, Float> advance(ArrayList<GPOrder> orders) {

        ArrayList<Integer> offsetOfOrdersToBeRemoved = retrieveOrdersToBeRemoved();
        if (!offsetOfOrdersToBeRemoved.isEmpty()) {
            removeDoneOrders(offsetOfOrdersToBeRemoved);
        }
        HashMap<String, Float> penaltyMap = getPenaltyMap(orders);

        for (GPOrder order : orders) {
            float totalPenalty = 1;
            for (String s : order.assignedResources.keySet()) {
                totalPenalty *= penaltyMap.get(s);
            }
            order.setPenalty(totalPenalty);
        }
        if (currentFocus != null) {
            float totalPenalty = 1;
            for (Map.Entry<String, Integer> stringIntegerEntry : currentFocus.retrieveCostForCurrStage().entrySet()) {
                totalPenalty *= penaltyMap.get(stringIntegerEntry.getKey());
            }
            currentFocus.setPenalty(totalPenalty);
        }


        return penaltyMap;


    }

    public @NotNull HashMap<String, Float> getPenaltyMap() {
        return getPenaltyMap(getProductionOrders());
    }

    private @NotNull HashMap<String, Float> getPenaltyMap(ArrayList<GPOrder> orders) {
        HashMap<String, Float> penaltyMap = new HashMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : getTotalResources().entrySet()) {
            Integer currentDemand = getReqResources(orders).get(stringIntegerEntry.getKey());
            Integer total = stringIntegerEntry.getValue();
            float penalty = (float) total / currentDemand;
            if (penalty >= 1) {
                penalty = 1;
            }
            penaltyMap.put(stringIntegerEntry.getKey(), penalty);
        }
        return penaltyMap;
    }

    private @NotNull HashMap<String, Float> getPenaltyMap(ArrayList<GPOrder> orders, HashMap<String, Integer> totalProd) {
        HashMap<String, Float> penaltyMap = new HashMap<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : totalProd.entrySet()) {
            Integer currentDemand = getReqResources(orders).get(stringIntegerEntry.getKey());
            Integer total = stringIntegerEntry.getValue();
            float penalty = (float) total / currentDemand;
            if (penalty >= 1) {
                penalty = 1;
            }
            penaltyMap.put(stringIntegerEntry.getKey(), penalty);
        }
        return penaltyMap;
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

    public ArrayList<Integer> retrieveOrdersToBeRemovedFromDummy(ArrayList<GPOrder> dummyOrders) {
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
            if (offset.intValue() >= productionOrders.size()) continue;
            productionOrders.remove(offset.intValue());
        }
    }

    public void removeDoneOrdersDummy(ArrayList<Integer> offsets, ArrayList<GPOrder> dummyOrders) {
        for (Integer offset : offsets) {
            if (offset.intValue() >= dummyOrders.size()) continue;
            dummyOrders.remove(offset.intValue());
        }
    }


}
