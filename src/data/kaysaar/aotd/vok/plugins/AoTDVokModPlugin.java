package data.kaysaar.aotd.vok.plugins;


import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.listeners.NidavelirClaimMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDMegastructureProductionListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDMegastructureUpkeepListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDSupertencileListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;
import data.kaysaar.aotd.vok.campaign.econ.listeners.*;
import data.kaysaar.aotd.vok.listeners.*;
import data.kaysaar.aotd.vok.plugins.bmo.VanillaTechReq;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIInMarketScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDCompoundUIScript;
import data.kaysaar.aotd.vok.scripts.misc.AoTDFuelConsumptionScript;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.CurrentResearchProgressUI;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchProgressionScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.listeners.ScientistValidationListener;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectSpecManager;
import kaysaar.bmo.buildingmenu.additionalreq.AdditionalReqManager;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.lazywizard.lazylib.ui.FontException;

import java.io.IOException;


public class AoTDVokModPlugin extends BaseModPlugin {
    public static final String specsFilename = "data/campaign/aotd_tech_options.csv";
    private static Logger log = Global.getLogger(AoTDVokModPlugin.class);
    AoTDDataInserter aoTDDataInserter = new AoTDDataInserter();
    AoTDSpecialItemRepo aoTDSpecialItemRepo = new AoTDSpecialItemRepo();
    public static String fontInsigniaMedium = "graphics/fonts/insignia17LTaa.fnt";

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
        if (!l.hasListenerOfClass(ResourceConditionApplier.class))
            l.addListener(new ResourceConditionApplier(), true);
        if (!l.hasListenerOfClass(AodAdvancedHeavyIndustryApplier.class))
            l.addListener(new AodAdvancedHeavyIndustryApplier(), true);
        if (!l.hasListenerOfClass(AoDIndustrialMightListener.class))
            l.addListener(new AoDIndustrialMightListener(), true);
        if (!l.hasListenerOfClass(PCFPlanetListener.class))
            l.addListener(new PCFPlanetListener(), true);
        if (!l.hasListenerOfClass(ScientistValidationListener.class) && !Global.getSector().getMemory().contains("$aotd_passed_validation" + ScientistValidationListener.class.getName()))
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
    }


    @Override
    public void onNewGameAfterEconomyLoad() {
        SpecialProjectSpecManager.reLoad();
        SpecialProjectManager.getInstance().loadAdditionalData();
        GPManager.getInstance().reInitalize();
        super.onNewGameAfterEconomyLoad();
        Global.getSector().addListener(new AoTDxUafAfterCombatListener());
        aoTDDataInserter.generatePreCollapseFacilities();
        aoTDDataInserter.spawnVeilPlanet();
        aoTDDataInserter.spawnNidavleir();
        aoTDDataInserter.spawnPluto();
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
        SpecialProjectManager.getInstance().loadAdditionalData();
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

        Global.getSector().addTransientScript(new CoreUITracker());
        if( Global.getSector().getMemory().is("$aotd_compound_unlocked",true)){
            Global.getSector().addTransientScript(new AoTDCompoundUIScript());
            Global.getSector().addTransientScript(new AoTDCompoundUIInMarketScript());
        }

        Global.getSettings().getCommoditySpec(Commodities.SHIPS).setName("Ship hulls");
        AoTDMainResearchManager.getInstance().updateModIdRepo();
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
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SOIL_NANITES, "subfarming");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.MANTLE_BORE, "mining_megaplex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.BIOFACTORY_EMBRYO, "lightproduction,consumerindustry");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,orbitalheavy,stella_manufactorium");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CORRUPTED_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,orbitalheavy,stella_manufactorium");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CATALYTIC_CORE, "crystalizator,isotope_separator,policrystalizator,cascade_reprocesor");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SYNCHROTRON, "blast_processing");

        if (Global.getSettings().getModManager().isModEnabled("uaf")) {
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_rice_cooker", "subfarming,artifarming");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_garrison_transmitter", AoTDIndustries.TERMINUS);
        }
        initalizeNecessarySPListeners();
        int highestTierUnlock = AoTDSettingsManager.getHighestTierEnabled();
        for (ResearchOption option : AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getResearchRepoOfFaction()) {
            if (option.Tier.ordinal() <= highestTierUnlock) {
                option.setResearched(true);
                option.havePaidForResearch = true;
                AoTDListenerUtilis.finishedResearch(option.getSpec().getId(),Global.getSector().getPlayerFaction());
            }
        }
        Global.getSector().addTransientScript(new AoTDFuelConsumptionScript());
        Global.getSettings().getCommoditySpec(Commodities.GAMMA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.BETA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE).getTags().add("aotd_ai_core");
        GPManager.getInstance().reInitalize();
        CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());
        SpecialProjectManager.getInstance().addScriptInstance();
//        for (PlanetAPI planet : Global.getSector().getPlayerFleet().getStarSystem().getPlanets()) {
//            if(planet.isStar())continue;
//            NidavelirShipyard shipyard = (NidavelirShipyard)planet.getStarSystem().addCustomEntity(null,"Nid","nid_shipyards",null).getCustomPlugin();
//            shipyard.trueInit("aotd_nidavelir","aotd_nidavelir_shadow",planet);
//
//        }
    }

    public static StarSystemAPI  getTestingGroundSystem() {
        if(Global.getSector().getStarSystem("testing_ground")==null){
            StarSystemAPI system = Global.getSector().createStarSystem("testing_ground");
            system.initNonStarCenter();
            system.addTag(Tags.THEME_HIDDEN);
        }

        return Global.getSector().getStarSystem("testing_ground");
    }
//    public static CodexEntryV2 createMegastructuresTab() {
//        CodexEntryV2 cat = new CodexEntryV2("aotd_megastructures", "Planetary conditions", CodexDataV2.getIcon("aotd_megastructures")) {
//            @Override
//            public boolean hasTagDisplay() {
//                return true;
//            }
//            @Override
//            public void configureTagDisplay(TagDisplayAPI tags) {
//                int resource = 0;
//                int other = 0;
//                int total = 0;
//                for (CodexEntryPlugin curr : getChildren()) {
//                    if (!curr.isVisible() || curr.isLocked() || curr.skipForTags()) continue;
//                    if (!(curr.getParam() instanceof MarketConditionSpecAPI)) continue;
//                    MarketConditionSpecAPI spec = (MarketConditionSpecAPI) curr.getParam();
//                    if (ResourceDepositsCondition.COMMODITY.containsKey(spec.getId())) resource++;
//                    else other++;
//
//                    total++;
//                }
//                tags.beginGroup(false, ALL_TYPES);
//                tags.addTag(RESOURCES, resource);
//                tags.addTag(OTHER, other);
//                tags.setTotalOverrideForCurrentGroup(total);
//                tags.addGroup(0f);
//
//                tags.checkAll();
//            }
//        };
//        return cat;
//    }
}






