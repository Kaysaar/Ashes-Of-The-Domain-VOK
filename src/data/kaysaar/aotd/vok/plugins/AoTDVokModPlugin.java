package data.kaysaar.aotd.vok.plugins;


import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
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
import data.kaysaar.aotd.vok.scripts.CoreUITracker2;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.CurrentResearchProgressUI;
import data.kaysaar.aotd.vok.scripts.UiInitalizerScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchProgressionScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.listeners.ScientistValidationListener;
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
        if (!l.hasListenerOfClass(UiInitalizerScript.class)) {
            l.addListener(new UiInitalizerScript());
        }
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
        AoTDMainResearchManager.getInstance().updateResearchOptionsFromSpec();
        AoTDMainResearchManager.getInstance().updateManagerRepo();
        AoTDMainResearchManager.getInstance().setAttitudeDataForAllFactions();
        if (!Global.getSector().hasScript(AoTDFactionResearchProgressionScript.class)) {
            Global.getSector().addScript(new AoTDFactionResearchProgressionScript());
        }
        Global.getSector().addTransientScript(new CoreUITracker2());
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
        Global.getSector().addTransientScript(new CoreUITracker());
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
        for (GPSpec specialProjectSpec : GPManager.getInstance().getSpecialProjectSpecs()) {
            try {
                Global.getSettings().getHullSpec(specialProjectSpec.getRewardId()).getHints().add(ShipHullSpecAPI.ShipTypeHints.UNBOARDABLE);
            } catch (Exception e) {

            }

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
        Global.getSettings().getCommoditySpec(Commodities.GAMMA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.BETA_CORE).getTags().add("aotd_ai_core");
        Global.getSettings().getCommoditySpec(Commodities.ALPHA_CORE).getTags().add("aotd_ai_core");
        GPManager.getInstance().reInitalize();
        CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());

//        for (PlanetAPI planet : Global.getSector().getPlayerFleet().getStarSystem().getPlanets()) {
//            if(planet.isStar())continue;
//            NidavelirShipyard shipyard = (NidavelirShipyard)planet.getStarSystem().addCustomEntity(null,"Nid","nid_shipyards",null).getCustomPlugin();
//            shipyard.trueInit("aotd_nidavelir","aotd_nidavelir_shadow",planet);
//
//        }
    }
}






