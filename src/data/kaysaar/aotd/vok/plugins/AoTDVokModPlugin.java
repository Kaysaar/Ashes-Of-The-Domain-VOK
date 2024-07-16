package data.kaysaar.aotd.vok.plugins;


import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.GpProductionButtonRenderer;
import data.kaysaar.aotd.vok.campaign.econ.listeners.*;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderInfoRepo;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.CurrentResearchProgressUI;
import data.kaysaar.aotd.vok.scripts.UiInitalizerScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchProgressionScript;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.listeners.ScientistValidationListener;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.lazywizard.lazylib.ui.FontException;

import java.io.IOException;


public class AoTDVokModPlugin extends BaseModPlugin {
    public static final String specsFilename = "data/campaign/aotd_tech_options.csv";
    private static Logger log = Global.getLogger(AoTDVokModPlugin.class);
    AoTDDataInserter aoTDDataInserter = new AoTDDataInserter();
    AoTDSpecialItemRepo aoTDSpecialItemRepo = new AoTDSpecialItemRepo();
    private void setListenersIfNeeded() {
        ListenerManagerAPI l = Global.getSector().getListenerManager();
        if(!l.hasListenerOfClass(UiInitalizerScript.class)){
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
        if (!l.hasListenerOfClass(ScientistValidationListener.class)&&!Global.getSector().getMemory().contains("$aotd_passed_validation"+ScientistValidationListener.class.getName()))
            l.addListener(new ScientistValidationListener(), false);
        if (!l.hasListenerOfClass(TechModifiersApplier.class))
            l.addListener(new TechModifiersApplier(), true);
        if(!l.hasListenerOfClass(AIColonyManagerListener.class))
            l.addListener(new AIColonyManagerListener());
        l.removeListenerOfClass(CurrentResearchProgressUI.class);
        if(!l.hasListenerOfClass(CurrentResearchProgressUI.class)) {
            try {
                l.addListener(new CurrentResearchProgressUI(),true);
            } catch (FontException e) {
                throw new RuntimeException(e);
            }
        }
        l.removeListenerOfClass(GpProductionButtonRenderer.class);
        if(!l.hasListenerOfClass(GpProductionButtonRenderer.class)) {
            try {
                l.addListener(new GpProductionButtonRenderer(),true);
            } catch (FontException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onApplicationLoad() throws Exception {
        ShipRenderInfoRepo.populateRenderInfoRepo();
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        super.onNewGameAfterEconomyLoad();
        aoTDDataInserter.generatePreCollapseFacilities();
        aoTDDataInserter.spawnVeilPlanet();
    }

    public void onGameLoad(boolean newGame) {
        GPManager.getInstance().reInitalize();
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
        AoTDMainResearchManager.getInstance().updateResearchOptionsFromSpec();
        AoTDMainResearchManager.getInstance().updateManagerRepo();
        if(!Global.getSector().hasScript(AoTDFactionResearchProgressionScript.class)){
            Global.getSector().addScript(new AoTDFactionResearchProgressionScript());
        }
        AoTDMainResearchManager.getInstance().updateModIdRepo();
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (newGame) {
            if (haveNexerelin && Global.getSector().getMemoryWithoutUpdate().getBoolean("$nex_randomSector")) {

                aoTDDataInserter.RandomSetIndustryOnPlanet(AoTDIndustries.CLEANROOM_MANUFACTORY, 2, null);
                aoTDDataInserter. RandomSetIndustryOnPlanet(AoTDIndustries.PURIFICATION_CENTER, 1, Planets.PLANET_WATER);
                aoTDDataInserter.initalizeEconomy(true);
            } else {
                aoTDDataInserter.initalizeEconomy(false);
            }
        }
        aoTDSpecialItemRepo.putInfoForSpecialItems();
        aoTDDataInserter.setStarterIndustriesUpgrades();
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SOIL_NANITES, "subfarming");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.MANTLE_BORE, "mining_megaplex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.BIOFACTORY_EMBRYO, "lightproduction,consumerindustry");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CORRUPTED_NANOFORGE, "supplyheavy,weaponheavy");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,orbitalheavy");
        if(Global.getSettings().getModManager().isModEnabled("uaf")){
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_rice_cooker" ,"subfarming,artifarming");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_garrison_transmitter" ,AoTDIndustries.TERMINUS);

        }
            int highestTierUnlock = AoTDSettingsManager.getHighestTierEnabled();
            for (ResearchOption option : AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getResearchRepoOfFaction()) {
                if(option.Tier.ordinal()<=highestTierUnlock){
                    option.setResearched(true);
                    option.havePaidForResearch = true;
                }
            }



    }
    }






