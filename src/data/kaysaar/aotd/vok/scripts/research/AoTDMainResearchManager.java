package data.kaysaar.aotd.vok.scripts.research;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.attitude.FactionResearchAttitudeData;
import data.kaysaar.aotd.vok.scripts.research.models.*;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.lazylib.MathUtils;

import java.io.IOException;
import java.util.*;

public class AoTDMainResearchManager {
    private static final Logger logger = Global.getLogger(AoTDMainResearchManager.class);
    public static final String specsFilename = "data/campaign/aotd_tech_options.csv";
    public static ArrayList<ResearchOption> researchOptions = new ArrayList<>();
    public static final String managerMemo = "$aotd_vok_manager";
    public static float BONUS_PER_RESEARACH_FAC = 0.1f;

    public ArrayList<AoTDFactionResearchManager> getFactionResearchManagers() {
        return factionResearchManagers;
    }

    public static String specialProjetsPath = "data/campaign/aotd_research_project.csv";

    HashMap<String, Float> weight = new HashMap<>();
    public ArrayList<String> modIDsRepo = new ArrayList<>();
    public ArrayList<AoTDFactionResearchManager> factionResearchManagers = new ArrayList<>();
    public ArrayList<ResearchProject> researchProjects = new ArrayList<>();

    public float expeditionCounter = 0;
    public String expeditionSender = null;
    public float expeditionThreshold = 120;
    public IntervalUtil util = new IntervalUtil(2.5f,2.5f);

    @NotNull
    private Map<String, ResearchOptionSpec> researchOptionSpec = new HashMap<>();
    @NotNull
    private Map<String, ResearchProjectSpec> researchProjectSpec = new HashMap<>();

    public ArrayList<String> getModIDsRepo() {
        modIDsRepo.removeIf(x->!Global.getSettings().getModManager().isModEnabled(x));
        return modIDsRepo;
    }

    public void setCurrentProject(ResearchProject currentProject) {
        this.currentProject = currentProject;
    }

    public ResearchProject currentProject;

    public static @NotNull Map<String, ResearchOptionSpec> getSpecsFromFiles() {
        HashMap<String, ResearchOptionSpec> toReturn = new HashMap<>();
        try {
            JSONArray resArray = Global.getSettings().getMergedSpreadsheetDataForMod("id", specsFilename, "aotd_vok");
            for (int i = 0; i < resArray.length(); i++) {
                JSONObject obj = resArray.getJSONObject(i);
                ResearchOptionSpec spec = ResearchOptionSpec.initSpecFromJson(obj);
                if (spec != null) {
                    toReturn.put(spec.getId(), spec);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return toReturn;

    }

    public static @NotNull Map<String, ResearchProjectSpec> getResearchProjectSpecFromFiles() {
        Map<String, ResearchProjectSpec> newEventSpecs = new HashMap<>();
        JSONArray projectCsvFromMod;
        try {
            projectCsvFromMod = Global.getSettings().getMergedSpreadsheetDataForMod("id", specialProjetsPath, "aotd_vok");
            for (int i = 0; i < projectCsvFromMod.length(); i++) {
                JSONObject obj = projectCsvFromMod.getJSONObject(i);
                ResearchProjectSpec spec = ResearchProjectSpec.initSpecFromJson(obj);
                if (spec != null) {
                    newEventSpecs.put(spec.getId(), spec);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return newEventSpecs;

    }

    public void updateModIdRepo() {
        if (modIDsRepo == null) modIDsRepo = new ArrayList<>();
        for (ResearchOptionSpec spec : researchOptionSpec.values()) {
            if(!AshMisc.isStringValid(spec.getModId()))continue;
            boolean shouldAdd = true;
            if (modIDsRepo.isEmpty()) {
                modIDsRepo.add(spec.getModId());
            }
            for (String s : modIDsRepo) {
                if (s.equals(spec.getModId())) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                modIDsRepo.add(spec.getModId());
            }

        }
    }

    public static @NotNull ArrayList<ResearchOption> generateResearchOptions(@NotNull Map<String, ResearchOptionSpec> specs) {
        ArrayList<ResearchOption> newResearchOptionsIds = new ArrayList<>();
        for (ResearchOptionSpec value : specs.values()) {
            try {
                ResearchOption researchOption = new ResearchOption(value);
                newResearchOptionsIds.add(researchOption);
                researchOptions.add(researchOption);
            } catch (Exception e) {
                throw new RuntimeException("Sth fucked");
            }
        }
        return newResearchOptionsIds;
    }

    public static @NotNull ArrayList<ResearchProject> generateResearchProjects(@NotNull Map<String, ResearchProjectSpec> specs) {
        ArrayList<ResearchProject> researchProjects = new ArrayList<>();
        for (ResearchProjectSpec value : specs.values()) {
            try {
                final Class<?> eventPlugin = Global.getSettings().getScriptClassLoader().loadClass(value.getPlugin());
                if (!ResearchProject.class.isAssignableFrom(eventPlugin)) {
                    throw new RuntimeException(String.format("%s does not extend %s", eventPlugin.getCanonicalName(), ResearchProject.class.getCanonicalName()));
                }
                ResearchProject project = (ResearchProject) eventPlugin.newInstance();
                project.init(value);
                researchProjects.add(project);
            } catch (Exception e) {
                throw new RuntimeException("Sth fucked");
            }
        }
        return researchProjects;
    }

    public ResearchProject getResearchProjectFromRepo(String id) {
        for (ResearchProject project : researchProjects) {
            if (project.id.equals(id)) {
                return project;
            }
        }
        return null;
    }

    public void updateResearchOptionsFromSpec() {
        researchOptionSpec.clear();
        researchOptionSpec = getSpecsFromFiles();
        researchProjectSpec.clear();
        researchProjectSpec = getResearchProjectSpecFromFiles();
        ArrayList<ResearchProject> dummyList = generateResearchProjects(researchProjectSpec);
        for (ResearchProject researchProject : dummyList) {
            ResearchProject original = getResearchProjectFromRepo(researchProject.id);
            if (original == null) continue;
            researchProject.currentlyOngoing = original.currentlyOngoing;
            researchProject.haveDoneIt = original.haveDoneIt;
            researchProject.optionsTakenIds = original.optionsTakenIds;
            researchProject.currentValueOfOptions = original.currentValueOfOptions;
            researchProject.haveMetReqOnce = original.haveMetReqOnce;
            researchProject.currentProgress = original.currentProgress;
            researchProject.haveReachedCriticalMoment = original.haveReachedCriticalMoment;
            for (SpecialProjectStage stageNew : researchProject.stages) {
                if (original.getCertainStage(stageNew.numberOfStage) != null) {
                    stageNew.chosenOption = original.getCertainStage(stageNew.numberOfStage).chosenOption;
                    if (stageNew.chosenOption != null) {
                        stageNew.ensureDecisionExist();
                    }

                }

            }

        }
        researchProjects.clear();
        researchProjects.addAll(dummyList);
        for (AoTDFactionResearchManager factionResearchManager : factionResearchManagers) {
            for (ResearchOption option : factionResearchManager.getResearchRepoOfFaction()) {
                ResearchOptionSpec spec = getSpecForSpecificResearch(option.Id);
                if (spec != null) {
                    option.setSpec(spec);
                    option.update();
                }


            }
        }
        for (Map.Entry<String, ResearchOptionSpec> entry : researchOptionSpec.entrySet()) {
            for (AoTDFactionResearchManager factionResearchManager : factionResearchManagers) {
                if (factionResearchManager.getResearchOptionFromRepo(entry.getValue().getId()) == null) {
                    factionResearchManager.getResearchRepoOfFaction().add(new ResearchOption(entry.getValue()));
                }
            }
        }


    }

    public ResearchOptionSpec getSpecForSpecificResearch(String id) {
        for (Map.Entry<String, ResearchOptionSpec> entry : researchOptionSpec.entrySet()) {
            if (entry.getKey().equals(id)) {
                return entry.getValue();
            }
        }
        return null;
    }


    public void initalizeFactionManagers() {
        List<FactionAPI> factions = Global.getSector().getAllFactions();
        Collections.shuffle(factions);
        for (FactionAPI allFaction : factions) {
            if ((allFaction.isShowInIntelTab() || allFaction.isPlayerFaction()) && !allFaction.getId().equals(Factions.PIRATES)) {
                factionResearchManagers.add(new AoTDFactionResearchManager(allFaction, generateResearchOptions(getSpecsFromFiles())));
                logger.info("Faction of name " + allFaction.getDisplayName() + " has been added to research manager");
            }

        }
    }

    public void updateManagerRepo() {
        List<FactionAPI> factions = Global.getSector().getAllFactions();
        Collections.shuffle(factions);
        for (FactionAPI allFaction : factions) {
            if (allFaction.isShowInIntelTab() || allFaction.isPlayerFaction()) {
                addNewFactionIfNotPresent(allFaction);
            }
        }
    }

    public void setAttitudeDataForAllFactions() {
        for (AoTDFactionResearchManager factionResearchManager : getFactionResearchManagers()) {
            factionResearchManager.setAttitudeData(new FactionResearchAttitudeData(factionResearchManager.getFaction().getId(), AoTDAIStance.DEFAULT, 0.2f, 0.5f, null, new ArrayList<String>()));
        }
        try {
            for (FactionResearchAttitudeData factionResearchAttitudeData : FactionResearchAttitudeData.getDataFromCSV()) {
                if (getSpecificFactionManager(Global.getSector().getFaction(factionResearchAttitudeData.factionID)) != null) {
                    getSpecificFactionManager(Global.getSector().getFaction(factionResearchAttitudeData.factionID)).setAttitudeData(factionResearchAttitudeData);
                }
            }
        } catch (JSONException e) {
        } catch (IOException e) {

        }

    }

    public void addNewFactionIfNotPresent(FactionAPI factionAPI) {
        boolean isPresent = false;
        for (AoTDFactionResearchManager factionResearchManager : factionResearchManagers) {
            if (factionResearchManager.getFaction().getId().equals(factionAPI.getId())) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            if ((factionAPI.isShowInIntelTab() || factionAPI.isPlayerFaction())) {
                factionResearchManagers.add(new AoTDFactionResearchManager(factionAPI, generateResearchOptions(getSpecsFromFiles())));
                logger.info("Faction " + factionAPI.getDisplayName() + " has been added to research manager.");
            }

        }


    }

    public static void setInstance() {
        AoTDMainResearchManager manager = new AoTDMainResearchManager();
        manager.researchOptionSpec = getSpecsFromFiles();
        manager.researchProjectSpec = getResearchProjectSpecFromFiles();
        manager.researchProjects = generateResearchProjects(manager.researchProjectSpec);
        manager.initalizeFactionManagers();
        manager.saveData();
    }

    public static AoTDMainResearchManager getInstance() {
        AoTDMainResearchManager manager = loadData();
        if (manager == null) setInstance();
        manager = loadData();
        return manager;

    }

    public String getNameForResearchBd(String id) {
        return "Required Technology : " + AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(id).getName();
    }

    public ResearchProject getCurrentProject() {
        return currentProject;
    }

    public ArrayList<ResearchProject> getResearchProjects() {
        return this.researchProjects;
    }

    public String pickFactionForExpedition() {
        ArrayList<String> poll = new ArrayList<>();
        float biggest = 0f;
        String faction = null;

        for (Map.Entry<String, Float> entry : weight.entrySet()) {
            if (Misc.getFactionMarkets(entry.getKey()).isEmpty() || entry.getKey().equals(Factions.PIRATES)) {
                continue;
            }
            if (biggest == 0f) {
                biggest = entry.getValue();
                continue;
            }

            if (entry.getValue() > biggest) {
                biggest = entry.getValue();
            }

        }
        for (Map.Entry<String, Float> entry : weight.entrySet()) {
            if (Misc.getFactionMarkets(entry.getKey()).isEmpty()) {
                continue;
            }
            if (entry.getValue() >= biggest) {
                poll.add(entry.getKey());
            }
        }

        return poll.get(MathUtils.getRandomNumberInRange(0, poll.size() - 1));
    }

    public void setExpeditionFleet(String factionId) {
        if (this.getSpecificFactionManager(Global.getSector().getFaction(factionId)) != null) {
            this.getSpecificFactionManager(Global.getSector().getFaction(factionId)).sentFleet();
        }

    }

    public void saveData() {
        Global.getSector().getPersistentData().put(managerMemo, this);
    }

    public static AoTDMainResearchManager loadData() {
        return (AoTDMainResearchManager) Global.getSector().getPersistentData().get(managerMemo);
    }

    public void advance(float amount) {
        util.advance(amount);
        if(util.intervalElapsed()){
            getManagerForPlayer().advance(amount);
        }

    }

    public AoTDFactionResearchManager getSpecificFactionManager(FactionAPI factionAPI) {
        if (factionAPI == null) return null;
        for (AoTDFactionResearchManager factionResearchManager : factionResearchManagers) {
            if (factionResearchManager.getFaction().getId().equals(factionAPI.getId())) return factionResearchManager;
        }
        return null;
    }


    public boolean haveFactionResearchedCertainTech(FactionAPI factionAPI, String id) {
        if (factionAPI == null) return false;
        for (AoTDFactionResearchManager factionResearchManager : factionResearchManagers) {
            if (factionResearchManager.getFaction().getId().equals(factionAPI.getId())) {
                return factionResearchManager.haveResearched(id);
            }
        }
        return false;
    }

    public boolean isResearchedForPlayer(String id) {
        FactionAPI factionCommingPlayer = Misc.getCommissionFaction();

        return haveFactionResearchedCertainTech(factionCommingPlayer, id) || haveFactionResearchedCertainTech(Global.getSector().getPlayerFaction(), id);
    }

    public boolean isAvailableForThisMarket(String id, MarketAPI market) {
        if(market==null)return true;
        if (getSpecificFactionManager(market.getFaction()) == null) return false;
        if (market.isPlayerOwned() || market.getFaction().isPlayerFaction()) {
            return isResearchedForPlayer(id);
        }
        return getSpecificFactionManager(market.getFaction()).haveResearched(id);
    }

    public AoTDFactionResearchManager getManagerForPlayer() {
        return getManagerForPlayerFaction();
    }

    public AoTDFactionResearchManager getManagerForPlayerFaction() {
        return getSpecificFactionManager(Global.getSector().getPlayerFaction());
    }
}
