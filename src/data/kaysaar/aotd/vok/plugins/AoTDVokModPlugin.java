package data.kaysaar.aotd.vok.plugins;


import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.AoTDAiScientistEventCreator;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.ScientistAICoreBarEventCreator;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.Ids.AoTDMemFlags;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.listeners.NidavelirClaimMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDMegastructureProductionListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDMegastructureUpkeepListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDSupertencileListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.SpaceDrugsDemand;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandManager;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandMover;
import data.kaysaar.aotd.vok.campaign.econ.listeners.*;
import data.kaysaar.aotd.vok.campaign.econ.listeners.buildingmenu.IndustryBlockerListener;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedLensHullmod;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedMantleHullmod;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedThunderHeadHullmod;
import data.kaysaar.aotd.vok.listeners.*;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.bmo.VanillaTechReq;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.CoreUITrackerSop;
import data.kaysaar.aotd.vok.scripts.CurrentResearchProgressUI;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchProgressionScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.scientist.listeners.ScientistUpkeepListener;
import data.kaysaar.aotd.vok.scripts.research.scientist.listeners.ScientistValidationListener;
import data.kaysaar.aotd.vok.scripts.research.scientist.scripts.ForbiddenScientistUnlock;
import data.kaysaar.aotd.vok.scripts.research.scientist.scripts.SophiaScriptUnlock;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.listeners.NidavelirSPListener;
import data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.ShroudProjectMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.listeners.ShroudCommodityUpdater;
import data.kaysaar.aotd.vok.timeline.military.LockheedDomainEvent;
import data.kaysaar.aotd.vok.timeline.military.OrbitalFleetworkEvent;
import data.kaysaar.aotd.vok.timeline.prosperity.MiningMegaplexEvent;
import data.kaysaar.aotd.vok.timeline.prosperity.ResortCenterWorld;
import data.kaysaar.aotd.vok.timeline.research.JanusDeviceEvent;
import data.kaysaar.aotd.vok.timeline.research.ResearchFacilityEvent;
import data.kaysaar.aotd.vok.timeline.research.StellaManufactoriumResearch;
import data.kaysaar.aotd.vok.timeline.research.StreamlinedProductionResearch;
import data.kaysaar.aotd.vok.timeline.templates.MegastructureClaimEvent;
import data.kaysaar.aotd.vok.timeline.templates.MegastructureRestoredEvent;
import data.kaysaar.aotd.vok.timeline.templates.SpecialProjectCompletionEvent;
import data.kaysaar.aotd.vok.timeline.unique.BifrostNetworkEstablished;
import data.kaysaar.aotd.vok.timeline.unique.HyperdimensionalProcessorEvent;
import data.listeners.timeline.MiscEventListener;
import data.listeners.timeline.models.FirstIndustryListener;
import data.memory.AoTDSopMemFlags;
import data.scripts.managers.TimelineListenerManager;
import kaysaar.bmo.buildingmenu.additionalreq.AdditionalReqManager;
import kaysaar.bmo.buildingmenu.upgradepaths.CustomUpgradePath;
import kaysaar.bmo.buildingmenu.upgradepaths.UpgradePathManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.lazywizard.lazylib.ui.FontException;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class AoTDVokModPlugin extends BaseModPlugin {
    public static final String specsFilename = "data/campaign/aotd_tech_options.csv";
    private static Logger log = Global.getLogger(AoTDVokModPlugin.class);
    AoTDDataInserter aoTDDataInserter = new AoTDDataInserter();
    AoTDSpecialItemRepo aoTDSpecialItemRepo = new AoTDSpecialItemRepo();
    public static String fontInsigniaMedium = "graphics/fonts/insignia17LTaa.fnt";

    public void spwanMegas(){
        for (Object object : AoTDMisc.getStarSystemWithMegastructure("")) {
            
        }
        
    }
    @Override
    public void onApplicationLoad() throws Exception {
        Global.getSettings().loadFont(fontInsigniaMedium);
        for (Pair<String, String> industry : AoTDIndButtonsListener.industries) {
            VanillaTechReq req = new VanillaTechReq(industry.two);
            AdditionalReqManager.getInstance().addReq(industry.one, req);
        }
    }


    private void setListenersIfNeeded() {
        ListenerManagerAPI l = Global.getSector().getListenerManager();
        l.removeListenerOfClass(AoTDIndButtonsListener.class);
        AoTDIndButtonsListener listener = new AoTDIndButtonsListener();
        listener.updateIndustryRepo();
        l.addListener(listener);
        if (!l.hasListenerOfClass(ScientistUpkeepListener.class))
            l.addListener(new ScientistUpkeepListener(), true);
        if (!l.hasListenerOfClass(ResourceConditionApplier.class))
            l.addListener(new ResourceConditionApplier(), true);
        if (!l.hasListenerOfClass(ShroudCommodityUpdater.class))
            l.addListener(new ShroudCommodityUpdater(), true);
        if (!l.hasListenerOfClass(IndustryBlockerListener.class))
            l.addListener(new IndustryBlockerListener(), true);
        if (!l.hasListenerOfClass(AodAdvancedHeavyIndustryApplier.class))
            l.addListener(new AodAdvancedHeavyIndustryApplier(), true);
        if (!l.hasListenerOfClass(AoDIndustrialMightListener.class))
            l.addListener(new AoDIndustrialMightListener(), true);
        if (!l.hasListenerOfClass(PCFPlanetListener.class))
            l.addListener(new PCFPlanetListener(), true);
        if (!l.hasListenerOfClass(ResearchDatabankExtractionListener.class))
            l.addListener(new ResearchDatabankExtractionListener(), true);
        if (!l.hasListenerOfClass(NidavelirSPListener.class))
            l.addListener(new NidavelirSPListener(), true);
        if (!l.hasListenerOfClass(ScientistValidationListener.class))
            l.addListener(new ScientistValidationListener(), false);
        if (!l.hasListenerOfClass(TechModifiersApplier.class))
            l.addListener(new TechModifiersApplier(), true);
        if (!l.hasListenerOfClass(AIColonyManagerListener.class))
            l.addListener(new AIColonyManagerListener());
        if (!l.hasListenerOfClass(AoTDRaidListener.class))
            l.addListener(new AoTDRaidListener());

        l.removeListenerOfClass(CurrentResearchProgressUI.class);
        if (!l.hasListenerOfClass(CurrentResearchProgressUI.class)) {
            try {
                l.addListener(new CurrentResearchProgressUI(), true);
            } catch (FontException e) {
                throw new RuntimeException(e);
            }
        }
        l.addListener(new CoreUiInterceptor(), true);
        l.addListener(new AoTDMegastructureProductionListener(), true);
        l.addListener(new AoTDMegastructureUpkeepListener(), true);
        l.addListener(new NidavelirClaimMegastructure(), true);
        l.addListener(new AoTDSupertencileListener(), true);
        l.addListener(new BifrostReesarchListener(), true);
        l.addListener(new CompoundProducitonListener(), true);
        l.addListener(new EconomyTickListener() {
            @Override
            public void reportEconomyTick(int iterIndex) {

            }

            @Override
            public void reportEconomyMonthEnd() {
                GPManager.getInstance().getProductionHistory().endOfMonth();
            }
        },true
        );
    }

    @Override
    public void onAboutToStartGeneratingCodex() {
        aoTDSpecialItemRepo.putInfoForSpecialItems();
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SOIL_NANITES, "subfarming");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.MANTLE_BORE, "mining_megaplex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.BIOFACTORY_EMBRYO, "lightproduction,consumerindustry");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,stella_manufactorium");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CORRUPTED_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,stella_manufactorium");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(AoTDItems.TENEBRIUM_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,stella_manufactorium");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CATALYTIC_CORE, "crystalizator,isotope_separator");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SYNCHROTRON, "blast_processing");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PLASMA_DYNAMO, "fracking");
        aoTDSpecialItemRepo.absoluteSetItemParams(Items.CORONAL_PORTAL, "");
        Global.getSettings().getHullModSpec("shrouded_thunderhead").setEffectClass(AoTDShroudedThunderHeadHullmod.class.getName());
        Global.getSettings().getHullModSpec("shrouded_mantle").setEffectClass(AoTDShroudedMantleHullmod.class.getName());
        Global.getSettings().getHullModSpec("shrouded_lens").setEffectClass(AoTDShroudedLensHullmod.class.getName());
        if (Global.getSettings().getModManager().isModEnabled("uaf")) {
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_rice_cooker", "subfarming");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_dimen_nanoforge", "supplyheavy,weaponheavy,triheavy,hegeheavy,stella_manufactorium");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_modular_purifier", "crystalizator,isotope_separator");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_servosync_pump", "blast_processing");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_garrison_transmitter", AoTDIndustries.TERMINUS);
        }
    }

    public void populatePaths(){
        CustomUpgradePath path = new CustomUpgradePath(3,3);
        LinkedHashMap<String, Vector2f> map = new LinkedHashMap<>();
        map.put("extractive", new Vector2f(1,0));
        map.put("mining", new Vector2f(1,1));
        map.put("fracking", new Vector2f(0,2));
        map.put("mining_megaplex", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"extractive");

        path = new CustomUpgradePath(3,4);
        map = new LinkedHashMap<>();
        map.put("smelting", new Vector2f(1,0));
        map.put("refining", new Vector2f(1,1));
        map.put("crystalizator", new Vector2f(0,2));
        map.put("isotope_separator", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"smelting");

        path = new CustomUpgradePath(3,3);
        map = new LinkedHashMap<>();
        map.put("lightproduction", new Vector2f(1,0));
        map.put("lightindustry", new Vector2f(1,1));
        map.put("hightech", new Vector2f(0,2));
        map.put("druglight", new Vector2f(1,2));
        map.put("consumerindustry", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"lightproduction");

        path = new CustomUpgradePath(3,3);
        map = new LinkedHashMap<>();
        map.put("monoculture", new Vector2f(1,0));
        map.put("farming", new Vector2f(1,1));
        map.put("artifarming", new Vector2f(0,2));
        map.put("subfarming", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"monoculture");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();
        map.put("fuelprod", new Vector2f(0,0));
        map.put("blast_processing", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"fuelprod");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();
        map.put("waystation", new Vector2f(0,0));
        map.put("logisitcbureau", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"waystation");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();

        map.put("aquaculture", new Vector2f(0,0));
        map.put("fishery", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aquaculture");

        path = new CustomUpgradePath(4,5);
        map = new LinkedHashMap<>();
        map.put("shityheavy", new Vector2f(2,0));
        map.put("heavyindustry", new Vector2f(2,1));
        map.put("orbitalworks", new Vector2f(1,2));
        map.put("supplyheavy", new Vector2f(3,2));
        map.put("triheavy",new Vector2f(2,3));
        map.put("hegeheavy",new Vector2f(0,3));
        map.put("stella_manufactorium",new Vector2f(3,3));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"shityheavy");
    }
    @Override
    public void onNewGameAfterEconomyLoad() {
        SpecialProjectSpecManager.reLoad();
        BlackSiteProjectManager.getInstance().loadAdditionalData();
        GPManager.getInstance().reInitalize();
        super.onNewGameAfterEconomyLoad();
        Global.getSector().addListener(new AoTDxUafAfterCombatListener());
        aoTDDataInserter.generatePreCollapseFacilities();
        aoTDDataInserter.spawnVeilPlanet();
        aoTDDataInserter.spawnMegas();
        if (Global.getSettings().getModManager().isModEnabled("uaf")) {
            MarketAPI lunarium = AoTDDataInserter.getMarketBasedOnName("Aoi", "Lunamun");
            if (lunarium != null) {
                lunarium.getMemory().set("$uaf_novaeria_bp", true);

            }
            MarketAPI auroria = AoTDDataInserter.getMarketBasedOnName("Aoi", "Auroria");
            if (auroria != null) {
                auroria.getMemory().set("$uaf_cherry_bp", true);

            }
        }
    }

    public void initalizeNecessarySPListeners() {
        Global.getSector().addTransientListener(new AoTDxIndieCollabListener());

    }

    public void onGameLoad(boolean newGame) {

        super.onGameLoad(newGame);
        aoTDDataInserter.setVanilaIndustriesDowngrades();
        SpecialProjectSpecManager.reLoad();
        BlackSiteProjectManager.getInstance().loadAdditionalData();
        try {
            aoTDDataInserter.insertSpecItemsForManufactoriumData();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        aoTDDataInserter.insertSophia();
        setListenersIfNeeded();
//        for (Map.Entry<String, MagicAchievement> stringMagicAchievementEntry : MagicAchievementManager.getInstance().getAchievements().entrySet()) {
//            stringMagicAchievementEntry.getValue().uncompleteAchievement(true);
//        }
//        Global.getSector().addTransientScript(new TestScript());
        AoTDMainResearchManager.getInstance().updateResearchOptionsFromSpec();
        AoTDMainResearchManager.getInstance().updateManagerRepo();
        AoTDMainResearchManager.getInstance().setAttitudeDataForAllFactions();
        if (!Global.getSector().hasScript(AoTDFactionResearchProgressionScript.class)) {
            Global.getSector().addScript(new AoTDFactionResearchProgressionScript());
        }
        ShroudProjectMisc.updateCommodityInfo();
        BarEventManager bar = BarEventManager.getInstance();
        if(bar.hasEventCreator(ScientistAICoreBarEventCreator.class)){
            bar.getCreators().removeIf(x->x instanceof ScientistAICoreBarEventCreator);
            bar.addEventCreator(new AoTDAiScientistEventCreator());
        }
//        Global.getSector().addTransientScript(new EveryFrameScript() {
//            protected IntervalUtil util = new IntervalUtil(1f,1.5f);
//            @Override
//            public boolean isDone() {
//                return false;
//            }
//
//            @Override
//            public boolean runWhilePaused() {
//                return false;
//            }
//
//            @Override
//            public void advance(float amount) {
//                util.advance(amount);
//                Global.getSector().removeTransientScriptsOfClass(HostileActivityManager.class);
//                Global.getSector().removeScriptsOfClass(HostileActivityManager.class);
//                Global.getSector().removeTransientScriptsOfClass(NexHostileActivityManager.class);
//                Global.getSector().removeScriptsOfClass(NexHostileActivityManager.class);
//                if(util.intervalElapsed()){
//
//                    if(HostileActivityEventIntel.get()!=null) {
//                        HostileActivityEventIntel.get().endImmediately();
//                        Global.getSector().getMemoryWithoutUpdate().unset(HostileActivityEventIntel.KEY);
//                        Global.getSector().getListenerManager().removeListenerOfClass(HostileActivityEventIntel.class);
//                        Global.getSector().removeTransientScript(this);
//                    }
//                }
//
//
//            }
//        });
//        LinkedHashMap<String,Integer> expectedShips = new LinkedHashMap<>();
//        expectedShips.put("uaf_supercap_slv_core",1);
//        expectedShips.put("onslaught",3);
//        expectedShips.put("apogee",5);
//        expectedShips.put("hermes",3);
//        getTestingGroundSystem();
//
//        AoTDPatrolFleetData data = new AoTDPatrolFleetData("Test Name 1" );
//        data.setExpectedVesselsInFleet(expectedShips);
//        data.init(Global.getSettings().createPerson());
//        FactionPatrolFleetManager.getInstance().getPatrolFleets().get(0).getFleet().getFleetData().removeFleetMember( FactionPatrolFleetManager.getInstance().getPatrolFleets().get(0).getFleet().getFleetData().getMembersListCopy().get(2));
//        FactionPatrolFleetManager.getInstance().getPatrolFleets().get(0).getFleet().getFleetData().removeFleetMember( FactionPatrolFleetManager.getInstance().getPatrolFleets().get(0).getFleet().getFleetData().getMembersListCopy().get(2));

        Global.getSector().removeTransientScriptsOfClass(AoTDCompoundUIScript.class);
        Global.getSector().removeTransientScriptsOfClass(AoTDCompoundUIInMarketScript.class);
        if (Global.getSettings().getModManager().isModEnabled("aotd_sop")) {
            Global.getSector().addTransientScript(new CoreUITrackerSop());
        } else {
            Global.getSector().addTransientScript(new CoreUITracker());
        }

        if (Global.getSector().getMemory().is("$aotd_compound_unlocked", true)) {
            Global.getSector().addTransientScript(new AoTDCompoundUIScript());
            Global.getSector().addTransientScript(new AoTDCompoundUIInMarketScript());
        }
        ScientistValidationListener.getInstance().addScript(new SophiaScriptUnlock());
        ScientistValidationListener.getInstance().addScript(new ForbiddenScientistUnlock());
        Global.getSettings().getCommoditySpec(Commodities.SHIPS).setName("Ship hulls");
        AoTDMainResearchManager.getInstance().updateModIdRepo();
        if(!  GrowingDemandManager.getInstance().hasDemand("wwlb_cerulean_vapors")){
            LinkedHashMap<String,Integer> mapOfReplacement = new LinkedHashMap<>();
            mapOfReplacement.put(Commodities.DRUGS, 1);
            GrowingDemandManager.getInstance().addDemand("wwlb_cerulean_vapors", new SpaceDrugsDemand("wwlb_cerulean_vapors", mapOfReplacement));
        }
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (newGame) {
            if (haveNexerelin && Global.getSector().getMemoryWithoutUpdate().getBoolean("$nex_randomSector")) {
                aoTDDataInserter.RandomSetIndustryOnPlanet(AoTDIndustries.PURIFICATION_CENTER, 1, Planets.PLANET_WATER);
                aoTDDataInserter.initalizeEconomy(true);
            } else {
                aoTDDataInserter.initalizeEconomy(false);
            }
        }
        if (!Global.getSector().getMemory().is("$aotd_2.2.1_fix", true)) {
            Global.getSector().getMemory().set("$aotd_2.2.1_fix", true);
            aoTDDataInserter.initalizeEconomy(false);
        }

        Global.getSector().addTransientScript(new AoTDCollabSpScript());
        Global.getSector().addTransientListener(new AoTDxUafAfterCombatListener());
        Global.getSector().addTransientListener(new AoTDSPListener());
        aoTDSpecialItemRepo.putInfoForSpecialItems();
        aoTDDataInserter.setStarterIndustriesUpgrades();

        initalizeNecessarySPListeners();
        int highestTierUnlock = AoTDSettingsManager.getHighestTierEnabled();
        for (ResearchOption option : AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getResearchRepoOfFaction()) {
            if (option.Tier.ordinal() <= highestTierUnlock) {
                option.setResearched(true);
                option.havePaidForResearch = true;
                AoTDListenerUtilis.finishedResearch(option.getSpec().getId(), Global.getSector().getPlayerFaction());
            }
        }
        Global.getSector().addTransientScript(new AoTDFuelConsumptionScript());
        Global.getSettings().getCommoditySpec(Commodities.GAMMA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.BETA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE).getTags().add("aotd_ai_core");
        GPManager.getInstance().reInitalize();
        CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());
        Global.getSector().addTransientScript(new GrowingDemandMover());
        BlackSiteProjectManager.getInstance().addScriptInstance();
        clearListenersFromTemporaryMarket();
        populatePaths();
        if (Global.getSettings().getModManager().isModEnabled("aotd_sop")) {
            addEvents();
        }

    }

    public static StarSystemAPI getTestingGroundSystem() {
        if (Global.getSector().getStarSystem("testing_ground") == null) {
            StarSystemAPI system = Global.getSector().createStarSystem("testing_ground");
            system.initNonStarCenter();
            system.addTag(Tags.THEME_HIDDEN);
        }

        return Global.getSector().getStarSystem("testing_ground");
    }


    public static void clearListenersFromTemporaryMarket() {
        ArrayList<BaseIndustry> listeners = new ArrayList<>(Global.getSector().getListenerManager().getListeners(BaseIndustry.class));
        for (BaseIndustry listener : listeners) {
            if (AshMisc.isStringValid(listener.getMarket().getId()) && listener.getMarket().getId().equals("to_delete")) {
                Global.getSector().getListenerManager().removeListener(listener);
            }

        }
        listeners.clear();
    }

    public void addEvents() {
        TimelineListenerManager manager = TimelineListenerManager.getInstance();
        GPManager.getInstance().getMegaStructureSpecs().stream().filter(x -> !x.hasTag("ignore_timeline")).forEach(
                x -> {
                    manager.addNewListener(new MiscEventListener(AoTDMemFlags.MEGASTRUCUTRE_FLAG_DISCOVERY,
                            new MegastructureClaimEvent(x.getMegastructureID(), x.getName(), Global.getSettings().getSpriteName("megastructureImage", x.getImageForMegastructure()))));
                    manager.addNewListener(new MiscEventListener(AoTDMemFlags.MEGASTRUCUTRE_FLAG_RESTORE, new MegastructureRestoredEvent(x.getMegastructureID(), x.getName(), Global.getSettings().getSpriteName("megastructureImage", x.getImageForMegastructure()))));
                    ;
                }
        );
        BlackSiteProjectManager.getInstance().getProjects().values().forEach(x -> manager.addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_PROJECT_EVENT, new SpecialProjectCompletionEvent(x.getProjectSpec().getId()))));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.MEGASTRUCUTRE_FLAG_DISCOVERY, new BifrostNetworkEstablished()));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDSopMemFlags.FIRST_ITEM, new HyperdimensionalProcessorEvent()));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_TECH_EVENT, new StreamlinedProductionResearch()));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_TECH_EVENT, new StellaManufactoriumResearch()));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_TECH_EVENT, new JanusDeviceEvent()));

        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new LockheedDomainEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new OrbitalFleetworkEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new ResortCenterWorld(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new MiningMegaplexEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new ResearchFacilityEvent(null)));


    }
}






