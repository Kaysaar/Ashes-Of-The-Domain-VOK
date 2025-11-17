package data.kaysaar.aotd.vok.plugins;


import ashlib.data.plugins.misc.AshMisc;
import com.fs.graphics.Sprite;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.AoTDAiScientistEventCreator;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.ScientistAICoreBarEventCreator;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.Ids.AoTDMemFlags;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl.CentralizedCore;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl.DistributedRegionalNetwork;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.impl.PerseanStandardized;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.listeners.NidavelirClaimMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDMegastructureUpkeepListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDSupertencileListener;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.TierFourStationResourceApplier;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.SpaceDrugsDemand;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandManager;
import data.kaysaar.aotd.vok.campaign.econ.growingdemand.models.GrowingDemandMover;
import data.kaysaar.aotd.vok.campaign.econ.industry.AoTDHeavyIndustry;
import data.kaysaar.aotd.vok.campaign.econ.industry.TierFourStation;
import data.kaysaar.aotd.vok.campaign.econ.listeners.*;
import data.kaysaar.aotd.vok.campaign.econ.listeners.buildingmenu.IndustryBlockerListener;
import data.kaysaar.aotd.vok.campaign.econ.synergies.IndustrySynergiesMisc;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.ColonyDevelopmentApplier;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.HypercognitionTestSynergy;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.MaglevSource;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources.SpaceportSource;
import data.kaysaar.aotd.vok.campaign.econ.synergies.impl.synergies.*;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.ui.SynergyUiInjector;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedLensHullmod;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedMantleHullmod;
import data.kaysaar.aotd.vok.hullmods.AoTDShroudedThunderHeadHullmod;
import data.kaysaar.aotd.vok.listeners.*;
import data.kaysaar.aotd.vok.plugins.bmo.VanillaTechReq;
import data.kaysaar.aotd.vok.plugins.coreui.RnDTabListener;
import data.kaysaar.aotd.vok.scripts.CurrentResearchProgressUI;
import data.kaysaar.aotd.vok.scripts.coreui.*;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.ColonyUIListener;
import data.kaysaar.aotd.vok.scripts.coreui.listeners.MarketContextListenerInjector;
import data.kaysaar.aotd.vok.scripts.cutscene.CutScenePlayer;
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
import data.kaysaar.aotd.vok.timeline.research.MacroIndustrialComplexResearch;
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
import lunalib.backend.ui.settings.LunaSettingsData;
import lunalib.backend.ui.settings.LunaSettingsLoader;
import lunalib.lunaSettings.LunaSettings;
import lunalib.lunaSettings.LunaSettingsListener;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.lazywizard.lazylib.JSONUtils;
import org.lazywizard.lazylib.ui.FontException;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class AoTDVokModPlugin extends BaseModPlugin implements MarketContextListenerInjector {
    public static final String specsFilename = "data/campaign/aotd_tech_options.csv";
    private static Logger log = Global.getLogger(AoTDVokModPlugin.class);
    AoTDDataInserter aoTDDataInserter = new AoTDDataInserter();
    AoTDSpecialItemRepo aoTDSpecialItemRepo = new AoTDSpecialItemRepo();
    public static final String subDirectoryName = "swappable";
    public static String fontInsigniaMedium = "graphics/fonts/insignia17LTaa.fnt";
    public static String getModBasePath(String modId) {
        String path =Global.getSettings().getModManager().getModSpec(modId).getPath();

        return path.replace("\\","/");
    }
    public static boolean moveFileOneLevelUpInModGraphics(String modId, String absolutePath) {
        try {
            String modBase = Global.getSettings()
                    .getModManager()
                    .getModSpec(modId)
                    .getPath(); // e.g. .../mods/YourMod/
            modBase.replace("\\","/");
            Object modGraphicsFile = ReflectionUtilis.getFile(modBase + "graphics");
            String modGraphicsCanonical = ReflectionUtilis.getCanonicalPath(modGraphicsFile);

            Object fileObj = ReflectionUtilis.getFile(absolutePath);
            String fileCanonical = ReflectionUtilis.getCanonicalPath(fileObj);

            // safety: ensure it's under /mods/YourMod/graphics
            if (!fileCanonical.startsWith(modGraphicsCanonical)) {
                // not our file, do nothing
                return false;
            }

            return ReflectionUtilis.moveFileOneLevelUp(fileCanonical);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationLoad() throws Exception {
        Global.getSettings().loadFont(fontInsigniaMedium);
        for (Pair<String, String> industry : AoTDIndButtonsListener.industries) {
            VanillaTechReq req = new VanillaTechReq(industry.two);
            AdditionalReqManager.getInstance().addReq(industry.one, req);
        }

        if (!LunaSettingsLoader.INSTANCE.getHasLoaded()) {
            LunaSettingsLoader.INSTANCE.load();
        }

        addSetting();
        LunaSettings.addSettingsListener(new LunaSettingsListener() {
            @Override
            public void settingsChanged(@NotNull String s) {
                try {
                    Boolean bool = LunaSettings.getBoolean("aotd_vok","aotd_vok_theme_swapper");
                    JSONUtils.CommonDataJSONObject ob = JSONUtils.loadCommonJSON("aotd_hidden_settings.json");
                    ob.put("aotd_vok_theme_swapper", bool);
//                    SpriteAPI sprite = Global.getSettings().getSprite("graphics/cursors/cursor_blue.png");
//                    SpriteAPI second = Global.getSettings().getSprite("graphics/cursors/" + subDirectoryName + "/cursor_blue.png");
//
//                    Object texObj1 = TexReflection.getSpriteTexObj((Sprite) sprite);
//                    Object texObj2 = TexReflection.getSpriteTexObj((Sprite) second);
//                    TexReflection.setTexObjId(texObj1,TexReflection.getTexObjId(texObj2));
                    String base = getModBasePath("aotd_vok");


                    if (bool) {
                        String absolute = base + "/graphics/cursors/" + subDirectoryName + "/cursor_blue.png";
                        boolean ok1 = ReflectionUtilis.moveFileOneLevelUpInModGraphics("aotd_vok", absolute);

                        String absolute2 = base + "/graphics/cursors/" + subDirectoryName + "/cursor_blue_2x.png";
                        boolean ok2 = ReflectionUtilis.moveFileOneLevelUpInModGraphics("aotd_vok", absolute2);

                    } else {
                        String absolute = base + "/graphics/cursors/cursor_blue.png";
                        boolean ok1 = moveFileIntoStuffSubdirInModGraphics("aotd_vok", absolute);

                        String absolute2 = base + "/graphics/cursors/cursor_blue_2x.png";
                        boolean ok2 = moveFileIntoStuffSubdirInModGraphics("aotd_vok", absolute2);
                    }

                    ob.save();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }});
    }
    public static boolean moveFileIntoStuffSubdirInModGraphics(String modId, String absolutePath) {
        try {
            String modBase = Global.getSettings()
                    .getModManager()
                    .getModSpec(modId)
                    .getPath();  // .../mods/YourMod/
            modBase= modBase.replace("\\","/");
            Object modGraphicsFile = ReflectionUtilis.getFile(modBase + "/graphics");
            String modGraphicsCanonical = ReflectionUtilis.getCanonicalPath(modGraphicsFile);

            Object fileObj = ReflectionUtilis.getFile(absolutePath);
            String fileCanonical = ReflectionUtilis.getCanonicalPath(fileObj);

            // Only touch files under /mods/YourMod/graphics
            if (!fileCanonical.startsWith(modGraphicsCanonical)) {
                return false;
            }

            return ReflectionUtilis.moveFileIntoStuffSubdir(fileCanonical);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void addSetting(){
        try {
            JSONUtils.CommonDataJSONObject ob= JSONUtils.loadCommonJSON("aotd_hidden_settings.json");
            if(!ob.has("aotd_vok_theme_swapper_valid")){
                ob.put("aotd_vok_theme_swapper_valid",true);
                ob.put("aotd_vok_theme_swapper",false);
                ob.save();
            }
            if(ob.getBoolean("aotd_vok_theme_swapper_valid")){
                Boolean bool = ob.getBoolean("aotd_vok_theme_swapper");
                LunaSettingsLoader.getSettings().get("aotd_vok").put("aotd_vok_theme_swapper",ob.getBoolean("aotd_vok_theme_swapper"));
                if(LunaSettingsLoader.getSettingsData().stream().noneMatch(x->x.getFieldID().equals("aotd_vok_hidden_header"))){
                    LunaSettingsLoader.getSettingsData().add(new LunaSettingsData("aotd_vok","aotd_vok_hidden_header","Hidden Settings","Header","Hidden settings unlocked through game","Settings","",0.0f,0.0f,"Hidden Settings"));
                    LunaSettingsLoader.getSettingsData().add(new LunaSettingsData("aotd_vok","aotd_vok_theme_swapper","Swap themes","Boolean","Hidden settings unlocked through game",bool,"",0.0f,0.0f,"Hidden Settings"));
                }

            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        if (!l.hasListenerOfClass(PCFPlanetListener.class))
            l.addListener(new PCFPlanetListener(), true);
        if (!l.hasListenerOfClass(ResearchDatabankExtractionListener.class))
            l.addListener(new ResearchDatabankExtractionListener(), true);
        if (!l.hasListenerOfClass(SubmarketOpenedListener.class))
            l.addListener(new SubmarketOpenedListener(), true);
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
        if (!l.hasListenerOfClass(AoTDTierFourAutoresolveListener.class))
            l.addListener(new AoTDTierFourAutoresolveListener(),true);

        l.removeListenerOfClass(CurrentResearchProgressUI.class);
        if (!l.hasListenerOfClass(CurrentResearchProgressUI.class)) {
            try {
                l.addListener(new CurrentResearchProgressUI(), true);
            } catch (FontException e) {
                throw new RuntimeException(e);
            }
        }
        l.addListener(new CoreUiInterceptor(), true);
        l.addListener(new TierFourStationResourceApplier(), true);
        l.addListener(new AoTDMegastructureUpkeepListener(), true);
        l.addListener(new NidavelirClaimMegastructure(), true);
        l.addListener(new AoTDSupertencileListener(), true);
        l.addListener(new BifrostReesarchListener(), true);
        l.addListener(new AoDIndustrialMightListener(),true);
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
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SOIL_NANITES, "aotd_subsidised_farming");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.MANTLE_BORE, "aotd_mining_megaplex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.BIOFACTORY_EMBRYO, "aotd_light_production,consumerindustry");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PRISTINE_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,aotd_macro_industrial_complex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CORRUPTED_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,aotd_macro_industrial_complex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(AoTDItems.TENEBRIUM_NANOFORGE, "supplyheavy,weaponheavy,triheavy,hegeheavy,aotd_macro_industrial_complex");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.CATALYTIC_CORE, "aotd_crystalizator,aotd_enrichment_facility");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(AoTDItems.TENEBRIUM_CATALYTIC_CORE, "aotd_crystalizator,aotd_enrichment_facility");

        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.SYNCHROTRON, "aotd_fuel_refinery");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.PLASMA_DYNAMO, "aotd_plasma_harvester");
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(Items.DEALMAKER_HOLOSUITE, AoTDIndustries.UNDERWORLD);
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(AoTDItems.TENEBRIUM_SYNCHROTRON, AoTDIndustries.FUEL_REFINERY);
        aoTDSpecialItemRepo.setSpecialItemNewIndustries(AoTDItems.TENEBRIUM_ATMOSPHERIC_DRIVE, "aquaculture,aotd_subsidised_farming,"+AoTDIndustries.ARTISANAL_FARMING+","+AoTDIndustries.FISHING_HARBOUR);

        aoTDSpecialItemRepo.absoluteSetItemParams(Items.CORONAL_PORTAL, "");
        Global.getSettings().getHullModSpec("shrouded_thunderhead").setEffectClass(AoTDShroudedThunderHeadHullmod.class.getName());
        Global.getSettings().getHullModSpec("shrouded_mantle").setEffectClass(AoTDShroudedMantleHullmod.class.getName());
        Global.getSettings().getHullModSpec("shrouded_lens").setEffectClass(AoTDShroudedLensHullmod.class.getName());
        Global.getSettings().getIndustrySpec(Industries.STARFORTRESS).setPluginClass(TierFourStation.class.getName());
        Global.getSettings().getIndustrySpec(Industries.HEAVYINDUSTRY).setPluginClass(AoTDHeavyIndustry.class.getName());
        Global.getSettings().getIndustrySpec(Industries.ORBITALWORKS).setPluginClass(AoTDHeavyIndustry.class.getName());


        if (Global.getSettings().getModManager().isModEnabled("uaf")) {
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_rice_cooker", "aotd_subsidised_farming");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_dimen_nanoforge", "supplyheavy,weaponheavy,triheavy,hegeheavy,aotd_macro_industrial_complex");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_modular_purifier", "aotd_crystalizator,aotd_enrichment_facility");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_servosync_pump", "aotd_fuel_refinery");
            aoTDSpecialItemRepo.setSpecialItemNewIndustries("uaf_garrison_transmitter", AoTDIndustries.TERMINUS);
        }
    }

    public void populatePaths(){
        CustomUpgradePath path = new CustomUpgradePath(3,3);
        LinkedHashMap<String, Vector2f> map = new LinkedHashMap<>();
        map.put("aotd_extractive_operation", new Vector2f(1,0));
        map.put("mining", new Vector2f(1,1));
        map.put("aotd_plasma_harvester", new Vector2f(0,2));
        map.put("aotd_mining_megaplex", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aotd_extractive_operation");

        path = new CustomUpgradePath(3,3);
        map = new LinkedHashMap<>();
        map.put("aotd_smelting", new Vector2f(1,0));
        map.put("refining", new Vector2f(1,1));
        map.put("aotd_crystalizator", new Vector2f(0,2));
        map.put("aotd_enrichment_facility", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aotd_smelting");

        path = new CustomUpgradePath(3,2);
        map = new LinkedHashMap<>();
        map.put(AoTDIndustries.TRADE_OUTPOST, new Vector2f(1,0));
        map.put(Industries.COMMERCE, new Vector2f(0,1));
        map.put(AoTDIndustries.UNDERWORLD, new Vector2f(2,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,AoTDIndustries.TRADE_OUTPOST);

        path = new CustomUpgradePath(3,3);
        map = new LinkedHashMap<>();
        map.put("aotd_light_production", new Vector2f(1,0));
        map.put("lightindustry", new Vector2f(1,1));
        map.put("aotd_hightech_industry", new Vector2f(0,2));
        map.put("aotd_druglight", new Vector2f(1,2));
        map.put("consumerindustry", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aotd_light_production");

        path = new CustomUpgradePath(3,3);
        map = new LinkedHashMap<>();
        map.put("aotd_monoculture_plots", new Vector2f(1,0));
        map.put("farming", new Vector2f(1,1));
        map.put("aotd_artisanal_farming", new Vector2f(0,2));
        map.put("aotd_subsidised_farming", new Vector2f(2,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aotd_monoculture_plots");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();
        map.put("fuelprod", new Vector2f(0,0));
        map.put("aotd_fuel_refinery", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"fuelprod");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();
        map.put("waystation", new Vector2f(0,0));
        map.put("aotd_terminus", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"waystation");

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();

        map.put("aquaculture", new Vector2f(0,0));
        map.put("aotd_fishing_harbour", new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,"aquaculture");

        path = new CustomUpgradePath(1,4);
        map = new LinkedHashMap<>();

        map.put(Industries.ORBITALSTATION, new Vector2f(0,0));
        map.put(Industries.BATTLESTATION, new Vector2f(0,1));
        map.put(Industries.STARFORTRESS, new Vector2f(0,2));
        map.put(AoTDIndustries.STAR_CITADEL_LOW, new Vector2f(0,3));
        path.setIndustryCoordinates(map);

        UpgradePathManager.getInstance().addNewCustomPath(path,Industries.ORBITALSTATION);

        path = new CustomUpgradePath(1,4);
        map = new LinkedHashMap<>();

        map.put(Industries.ORBITALSTATION_HIGH, new Vector2f(0,0));
        map.put(Industries.BATTLESTATION_HIGH, new Vector2f(0,1));
        map.put(Industries.STARFORTRESS_HIGH, new Vector2f(0,2));
        map.put(AoTDIndustries.STAR_CITADEL_HIGH, new Vector2f(0,3));
        path.setIndustryCoordinates(map);

        UpgradePathManager.getInstance().addNewCustomPath(path,Industries.ORBITALSTATION_HIGH);

        path = new CustomUpgradePath(1,2);
        map = new LinkedHashMap<>();

        map.put(AoTDIndustries.RESEARCH_CENTER, new Vector2f(0,0));
        map.put(AoTDIndustries.BLACK_SITE, new Vector2f(0,1));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,AoTDIndustries.RESEARCH_CENTER);

        path = new CustomUpgradePath(4,3);
        map = new LinkedHashMap<>();
        map.put("heavyindustry", new Vector2f(2,0));
        map.put("orbitalworks", new Vector2f(1,1));
        map.put("supplyheavy", new Vector2f(3,1));
        map.put("triheavy",new Vector2f(2,2));
        map.put("hegeheavy",new Vector2f(0,2));
        map.put("aotd_macro_industrial_complex",new Vector2f(3,2));
        path.setIndustryCoordinates(map);
        UpgradePathManager.getInstance().addNewCustomPath(path,Industries.HEAVYINDUSTRY);
    }
    @Override
    public void onNewGameAfterEconomyLoad() {
        SpecialProjectSpecManager.reLoad();
        BlackSiteProjectManager.getInstance().loadAdditionalData();
        GPManager.getInstance().reInitalize();
        super.onNewGameAfterEconomyLoad();

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
    public void populateColonyDevelopment(){
        ColonyDevelopmentManager.getInstance().addDevelopmentScriptBase("core",new CentralizedCore());
        ColonyDevelopmentManager.getInstance().addDevelopmentScriptBase("standard",new PerseanStandardized());
        ColonyDevelopmentManager.getInstance().addDevelopmentScriptBase("distributed",new DistributedRegionalNetwork());

        DistributedRegionalNetwork.addNewIndustries(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.MONOCULTURE));
        DistributedRegionalNetwork.addNewIndustries(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.AQUACULTURE));
        DistributedRegionalNetwork.addNewIndustries(IndustrySynergiesMisc.getIdsOfTreeFromIndustry(AoTDIndustries.EXTRACTIVE_OPERATION));



    }
    public void populateSynergies(){
        IndustrySynergiesManager.getInstance().ensureHasMoverScript();
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.SPACEPORT)) {
            if(id.equals(Industries.SPACEPORT)){
                IndustrySynergiesManager.getInstance().addSynergySource(id,new SpaceportSource(0.05f,id));
            }
            else{
                IndustrySynergiesManager.getInstance().addSynergySource(id,new SpaceportSource(0.1f,id));

            }
        }
        for (String id : IndustrySynergiesMisc.getIdsOfTreeFromIndustry(Industries.WAYSTATION)) {
            if(id.equals(Industries.WAYSTATION)){
                IndustrySynergiesManager.getInstance().addSynergySource(id,new SpaceportSource(0.1f,id));
            }
            else{
                IndustrySynergiesManager.getInstance().addSynergySource(id,new SpaceportSource(0.2f,id));

            }
        }
        IndustrySynergiesManager.getInstance().addSynergySource(AoTDIndustries.MAGLEV_CENTRAL_HUB,new MaglevSource(0.7f,AoTDIndustries.MAGLEV_CENTRAL_HUB));
        IndustrySynergiesManager.getInstance().addSynergySource("test",new HypercognitionTestSynergy(0.2f,Industries.POPULATION));
        IndustrySynergiesManager.getInstance().addSynergySource("colonyDevelopmentMan",new ColonyDevelopmentApplier(0.2f,Industries.POPULATION));

        IndustrySynergiesManager.getInstance().addSynergy("agro_tourism",new AgroTourism());
        IndustrySynergiesManager.getInstance().addSynergy("aotd_mining_feed",new OreToCore());
        IndustrySynergiesManager.getInstance().addSynergy("aotd_refining_feed",new CoreToForge());
        IndustrySynergiesManager.getInstance().addSynergy("cabbage_comm_materials",new CommissionedMaterials());
        IndustrySynergiesManager.getInstance().addSynergy("ildrenium_black_site_fools",new PlausibleDeniability());
        IndustrySynergiesManager.getInstance().addSynergy("darkhellthepro_syndicate_line",new SyndicateLine());
        IndustrySynergiesManager.getInstance().addSynergy("aotd_lockheed_martin",new LockheedInitiative());
        IndustrySynergiesManager.getInstance().addSynergy("cabbage_gas_station",new InterstellarGasStation());
        IndustrySynergiesManager.getInstance().addSynergy("touchOfVanilla_deep_sea_scan",new DeepSeaScan());
        IndustrySynergiesManager.getInstance().addSynergy("tschudy_emergency_measures",new EmergencyMeasures());
        IndustrySynergiesManager.getInstance().addSynergy("omeganavie_fishing_resort",new FishingResort());
        IndustrySynergiesManager.getInstance().addSynergy("tata_mono_bio_synthesis",new BioSynthesis());
        IndustrySynergiesManager.getInstance().addSynergy("seventhslayer_defensive_network",new DefensiveNetwork());
        IndustrySynergiesManager.getInstance().addSynergy("mstachife_rock_and_stone",new RockAndStone());
        IndustrySynergiesManager.getInstance().addSynergy("aotd_rapid_transportation",new RapidTransportation());
        IndustrySynergiesManager.getInstance().addSynergy("r3dstylum_frl",new FacilitatedResearchLogistics());
        IndustrySynergiesManager.getInstance().addSynergy("ildrenium_aep",new ArgentEnergyProcessing());
        IndustrySynergiesManager.getInstance().addSynergy("aotd_volatile_line",new VolatileLine());
        IndustrySynergiesManager.getInstance().addSynergy("somerandomsmuck_ditd",new DrumsInTheDeep());
        if(Global.getSettings().getModManager().isModEnabled("uaf")){
            IndustrySynergiesManager.getInstance().addSynergy("bunchienumbies_fmp",new UAFFederallyMandatedPastries());
            IndustrySynergiesManager.getInstance().addSynergy("bunchienumbies_od",new UAFOutsourcedDesserts());
            IndustrySynergiesManager.getInstance().addSynergy("omeganavie_ap",new UAFAddictivePastries());
        }
    }

    public void onGameLoad(boolean newGame) {

        super.onGameLoad(newGame);
        aoTDDataInserter.setVanilaIndustriesDowngrades();
        Global.getSector().addTransientScript(new DelayedActionScript(3f) {
            @Override
            public void doAction() {
                new CutScenePlayer();
            }
        });
        MarketAPI chico = AoTDDataInserter.getMarketBasedOnName("Aztlan", "Chicomoztoc");
        if (chico != null) {
            chico.getMemory().set("$aotd_tier_4_bp_key", "aotd_citadel");
        }
        MarketAPI culann = AoTDDataInserter.getMarketBasedOnName("Hybrasil", "Culann");
        if (culann != null) {
            culann.getMemory().set("$aotd_tier_4_bp_key", "aotd_citadel_hightech");
        }
        Global.getSettings().getAllWeaponSpecs().stream().filter(x->x.getManufacturer().equals("Shrouded Dweller")).forEach(x->x.setManufacturer("Abyss-Tech"));
        SpecialProjectSpecManager.reLoad();
        BlackSiteProjectManager.getInstance().loadAdditionalData();
        GPManager.reloadCommoditiesMap();
        Global.getSector().addTransientScript(new DialogPlanetTracker());
        Global.getSector().addTransientScript(new PlanetBackgroundTracker());
        Global.getSector().addTransientScript(new IndustryTooltipPlacer());
        Global.getSector().addTransientListener(new AoTDxUafAfterCombatListener());
        Global.getSector().addTransientListener(new AoTDCampaignListener());
        try {
            aoTDDataInserter.insertSpecItemsForMacroIndustrialComplexData();
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
//        if (Global.getSettings().getModManager().isModEnabled("aotd_sop")) {
//            Global.getSector().addTransientScript(new CoreUITrackerSop());
//        } else {
//            Global.getSector().addTransientScript(new CoreUITracker());
//        }
        Global.getSector().getListenerManager().addListener(new RnDTabListener(),true);
        Global.getSector().addTransientScript(new DelayedActionScript(0.1f) {
            @Override
            public void doAction() {
                BarEventManager bar = BarEventManager.getInstance();
                if(bar.hasEventCreator(ScientistAICoreBarEventCreator.class)){
                    bar.getCreators().removeIf(x->x instanceof ScientistAICoreBarEventCreator);
                        bar.addEventCreator(new AoTDAiScientistEventCreator());
                }
            }
        });
        if (Global.getSector().getMemory().is("$aotd_compound_unlocked", true)) {
            Global.getSector().addTransientScript(new AoTDCompoundUIScript());
        }
        Global.getSector().registerPlugin(new AoTDCoreCampaignPluginImpl());
        ScientistValidationListener.getInstance().addScript(new SophiaScriptUnlock());
        ScientistValidationListener.getInstance().addScript(new ForbiddenScientistUnlock());
        Global.getSettings().getCommoditySpec(Commodities.SHIPS).setName("Ship hulls");
        AoTDMainResearchManager.getInstance().updateModIdRepo();
        if(!  GrowingDemandManager.getInstance().hasDemand("wwlb_cerulean_vapors")){
            LinkedHashMap<String,Integer> mapOfReplacement = new LinkedHashMap<>();
            mapOfReplacement.put(Commodities.DRUGS, 1);
            GrowingDemandManager.getInstance().addDemand("wwlb_cerulean_vapors", new SpaceDrugsDemand("wwlb_cerulean_vapors", mapOfReplacement));
        }
        ColonyUIListener.refresh();
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (newGame) {
            if (haveNexerelin && Global.getSector().getMemoryWithoutUpdate().getBoolean("$nex_randomSector")) {
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
        Global.getSector().addTransientScript(new ShroudProjectMisc());
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
        Global.getSector().addTransientScript(new GrowingDemandMover());
        BlackSiteProjectManager.getInstance().addScriptInstance();
        clearListenersFromTemporaryMarket();
        populatePaths();
        populateSynergies();
        populateColonyDevelopment();
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
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_TECH_EVENT, new MacroIndustrialComplexResearch()));
        TimelineListenerManager.getInstance().addNewListener(new MiscEventListener(AoTDMemFlags.RESEARCH_TECH_EVENT, new JanusDeviceEvent()));

        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new LockheedDomainEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new OrbitalFleetworkEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new ResortCenterWorld(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new MiningMegaplexEvent(null)));
        TimelineListenerManager.getInstance().addNewListener(new FirstIndustryListener(AoTDSopMemFlags.FIRST_INDUSTRY, new ResearchFacilityEvent(null)));


    }

    @Override
    public void reloadListenerContext() {
        ColonyUIListener.addMarketListener(new SynergyUiInjector());
        ColonyUIListener.addMarketListener(new TradeOutpostAndSurveyInterceptor());
//        ColonyUIListener.addMarketListener(new GrandProjectLabelInjector());
    }
}






