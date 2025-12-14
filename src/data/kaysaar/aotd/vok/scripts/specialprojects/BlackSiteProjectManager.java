package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.BlackSiteIntel;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.*;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.BaseImageHologram;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.ShipHologram;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.WeaponHologram;

import java.awt.*;
import java.util.List;
import java.util.*;

public class BlackSiteProjectManager {

    private IntervalUtil intervalUtil;

    public class BlackSiteProjectAdvancer implements EveryFrameScript {

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
            BlackSiteProjectManager.getInstance().advance(amount);
        }
    }

    public LinkedHashMap<String, AoTDSpecialProject> projects = new LinkedHashMap<>();
    public AoTDSpecialProject currentlyOnGoingProject;
    public static String memflag = "$aotd_special_proj_manager";
    public static String memflagBlacksite = "$aotd_black_site";
    public static String marketId = AoTDSubmarkets.RESEARCH_FACILITY_MARKET;
    public transient ArrayList<AoTDSpecializationSpec> specializationSpecs = new ArrayList<>();
    float daysSinceBuiltFacility = 0;

    public static BlackSiteProjectManager getInstance() {
        if (Global.getSector().getPersistentData().get(memflag) == null) {
            setInstance();
        }
        return (BlackSiteProjectManager) Global.getSector().getPersistentData().get(memflag);
    }

    public void setCurrentlyOnGoingProject(AoTDSpecialProject currentlyOnGoingProject) {
        this.currentlyOnGoingProject = currentlyOnGoingProject;
    }

    public void addScriptInstance() {
        Global.getSector().addTransientScript(new BlackSiteProjectAdvancer());
    }

    public AoTDSpecialProject getCurrentlyOnGoingProject() {
        return currentlyOnGoingProject;
    }

    public boolean isCurrentOnGoing(AoTDSpecialProject project) {

        return project != null && getCurrentlyOnGoingProject() != null &&
                getCurrentlyOnGoingProject().getProjectSpec().getId().equals(project.getProjectSpec().getId()
                );
    }

    private static void setInstance() {

        Global.getSector().getPersistentData().put(memflag, new BlackSiteProjectManager());
    }

    public BlackSiteProjectManager() {
        projects = new LinkedHashMap<>();
        loadAdditionalData();
    }

    public LinkedHashMap<String, AoTDSpecialProject> getProjects() {
        return projects;
    }

    public AoTDSpecialProject getProject(String id) {
        if(projects.isEmpty()){
            loadAdditionalData();
        }
        return projects.get(id);
    }

    public void loadAdditionalData() {
        for (Map.Entry<String, AoTDSpecialProjectSpec> entry : SpecialProjectSpecManager.getSpecs().entrySet()) {
            if (!projects.containsKey(entry.getKey())) {
                AoTDSpecialProject project = entry.getValue().getPlugin();
                project.init();
                projects.put(entry.getKey(), project);
            } else {
                projects.get(entry.getKey()).update();
            }
        }
        projects.entrySet().removeIf(entry -> entry.getValue().getProjectSpec() == null || !Global.getSettings().getModManager().isModEnabled(entry.getValue().getProjectSpec().getModId()));
    }

    public float getDaysSinceBuiltFacility() {
        return daysSinceBuiltFacility;
    }

    public void advance(float amount) {
        if (intervalUtil == null) {
            intervalUtil = new IntervalUtil(10f, 10f); //
        }
        intervalUtil.advance(amount);
        if (intervalUtil.intervalElapsed()) {
            if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfResearchFacilities() > 0 && daysSinceBuiltFacility < 10) {
                daysSinceBuiltFacility += Global.getSector().getClock().convertToDays(intervalUtil.getElapsed());
            }
            if (daysSinceBuiltFacility > 5) {
                if (AoTDMainResearchManager.getInstance().getManagerForPlayer().getAmountOfBlackSites() > 0) {
                    Global.getSector().getPlayerMemoryWithoutUpdate().set(memflagBlacksite, true);
                    projects.values().forEach(AoTDSpecialProject::doCheckForProjectUnlock);

                    if (currentlyOnGoingProject != null) {
                        currentlyOnGoingProject.advance(intervalUtil.getElapsed());
                    }
                } else {
                    for (AoTDSpecialProject value : projects.values()) {
                        if (value.doCheckForBlacksiteUnlock()) {
                            if (!canEngageInBlackSite()) {
                                Global.getSector().getPlayerMemoryWithoutUpdate().set(memflagBlacksite, true);
                                Global.getSector().getIntelManager().addIntel(new BlackSiteIntel());
                            }

                        }
                    }
                }
            }


        }
    }

    public boolean canEngageInBlackSite() {

        return Global.getSector().getPlayerMemoryWithoutUpdate().is(memflagBlacksite, true);
    }

    public static HologramViewer createHologramViewer(AoTDSpecialProjectSpec spec, boolean isForButton, boolean isForBigButton) {
        float iconSize = 70;
        if (!isForButton) {
            iconSize = spec.getIconData().getSize();
        }
        if (isForBigButton) {
            iconSize = 100;
        }
        SpecialProjectIconData data = spec.getIconData();
        HologramViewer viewer = null;
        Color color = Color.cyan;
        if (spec.hasTag("dangerous")) {
            color = (Global.getSector().getFaction(Factions.DWELLER).getBrightUIColor().brighter());
        }
        if (data.getType().equals(SpecialProjectIconData.IconType.COMMODITY)) {
            viewer = new HologramViewer(iconSize, iconSize, new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getCommoditySpec(data.getIconId()).getIconName()), color));

        }
        if (data.getType().equals(SpecialProjectIconData.IconType.SHIP)) {
            viewer = new HologramViewer(iconSize, iconSize, new ShipHologram(data.getIconId(), color));
        }
        if (data.getType().equals(SpecialProjectIconData.IconType.ITEM)) {
            viewer = new HologramViewer(iconSize, iconSize, new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getSpecialItemSpec(data.getIconId()).getIconName()), color));

        }
        if (data.getType().equals(SpecialProjectIconData.IconType.WEAPON)) {
            viewer = new HologramViewer(iconSize, iconSize, new WeaponHologram(data.getIconId(), color));

        }

        return viewer;
    }

    public static HologramViewer createHologramViewer(AoTDSpecialProjectSpec spec, float overrideSize) {
        float iconSize = overrideSize;
        SpecialProjectIconData data = spec.getIconData();
        HologramViewer viewer = null;
        Color color = Color.cyan;
        if (spec.hasTag("dangerous")) {
            color = (Global.getSector().getFaction(Factions.DWELLER).getBrightUIColor());
        }
        if (data.getType().equals(SpecialProjectIconData.IconType.COMMODITY)) {
            viewer = new HologramViewer(iconSize, iconSize, new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getCommoditySpec(data.getIconId()).getIconName()), color));

        }
        if (data.getType().equals(SpecialProjectIconData.IconType.SHIP)) {
            viewer = new HologramViewer(iconSize, iconSize, new ShipHologram(data.getIconId(), color));
        }
        if (data.getType().equals(SpecialProjectIconData.IconType.ITEM)) {
            viewer = new HologramViewer(iconSize, iconSize, new BaseImageHologram(Global.getSettings().getSprite(Global.getSettings().getSpecialItemSpec(data.getIconId()).getIconName()), color));

        }
        if (data.getType().equals(SpecialProjectIconData.IconType.WEAPON)) {
            viewer = new HologramViewer(iconSize, iconSize, new WeaponHologram(data.getIconId(), color));

        }
        return viewer;
    }


    public static void eatItems(OtherCostData entry, String submarketId, List<MarketAPI> affectedMarkets) {
        float numberRemaining = entry.getAmount();
        OtherCostData.ItemType itemType = entry.itemType;
        for (MarketAPI marketAPI : affectedMarkets) {
            if (numberRemaining <= 0) break;

            SubmarketAPI subMarket = marketAPI.getSubmarket(submarketId);
            if (subMarket == null) continue;

            String id = entry.getId();

            switch (itemType) {
                case COMMODITY:
                    if (Global.getSettings().getCommoditySpec(id) != null) {
                        float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, id);
                        float toRemove = Math.min(numberRemaining, onMarket);
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, id, toRemove);
                        numberRemaining -= toRemove;
                    }
                    break;

                case ITEM:
                    if (Global.getSettings().getSpecialItemSpec(id) != null) {
                        SpecialItemData data = new SpecialItemData(id, null);
                        float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, data);
                        float toRemove = Math.min(numberRemaining, onMarket);
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL, data, toRemove);
                        numberRemaining -= toRemove;
                    }
                    break;

                case SHIP:
                    if (Global.getSettings().getHullSpec(id) != null) {
                        List<FleetMemberAPI> toRemove = new ArrayList<>();
                        for (FleetMemberAPI member : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                            if (member.getHullSpec().getBaseHullId().equals(id)) {
                                for (String fittedWeaponSlot : member.getVariant().getFittedWeaponSlots()) {
                                    if (member.getVariant().getSlot(fittedWeaponSlot).isBuiltIn()) continue;
                                    if (member.getVariant().getSlot(fittedWeaponSlot).isWeaponSlot()) {
                                        subMarket.getCargo().addWeapons(member.getVariant().getWeaponSpec(fittedWeaponSlot).getWeaponId(), 1);
                                    }
                                }
                                if (member.getVariant().getSMods() != null) {
                                    Global.getSector().getPlayerStats().addStoryPoints(member.getVariant().getSMods().size());

                                }
                                toRemove.add(member);
                            }
                        }
                        for (FleetMemberAPI member : toRemove) {
                            subMarket.getCargo().getMothballedShips().removeFleetMember(member);
                            numberRemaining--;
                            if (numberRemaining <= 0) break;
                        }
                    }
                    break;

                case WEAPON:
                    if (Global.getSettings().getWeaponSpec(id) != null) {
                        float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.WEAPONS, id);
                        float toRemove = Math.min(numberRemaining, onMarket);
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.WEAPONS, id, toRemove);
                        numberRemaining -= toRemove;
                    }
                    break;

                case FIGHTER:
                    if (Global.getSettings().getFighterWingSpec(id) != null) {
                        float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.FIGHTER_CHIP, id);
                        float toRemove = Math.min(numberRemaining, onMarket);
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.FIGHTER_CHIP, id, toRemove);
                        numberRemaining -= toRemove;
                    }
                    break;
            }
        }
    }

    public static float retrieveAmountOfItems(String id, String submarketID, OtherCostData.ItemType itemType) {
        float numberRemaining = 0;
        for (MarketAPI marketAPI : Misc.getPlayerMarkets(true)) {
            SubmarketAPI subMarket = marketAPI.getSubmarket(submarketID);
            if (itemType.equals(OtherCostData.ItemType.COMMODITY)) {
                if (Global.getSettings().getCommoditySpec(id) != null) {
                    if (subMarket != null) {
                        numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, id);
                        continue;
                    }
                }
            }
            if (itemType.equals(OtherCostData.ItemType.ITEM)) {
                if (Global.getSettings().getSpecialItemSpec(id) != null) {
                    if (subMarket != null) {
                        numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(id, null));
                        continue;
                    }
                }
            }
            if (itemType.equals(OtherCostData.ItemType.SHIP)) {
                if (Global.getSettings().getHullSpec(id) != null) {
                    if (subMarket != null) {
                        int sameHull = 0;
                        for (FleetMemberAPI o : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                            if (o.getHullSpec().getBaseHullId().equals(id)) {
                                sameHull++;
                            }
                        }
                        numberRemaining += sameHull;
                    }
                }
            }
            if (itemType.equals(OtherCostData.ItemType.WEAPON)) {
                if (Global.getSettings().getWeaponSpec(id) != null) {
                    if (subMarket != null) {
                        numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.WEAPONS, id);
                        continue;
                    }
                }
            }
            if (itemType.equals(OtherCostData.ItemType.FIGHTER)) {
                if (Global.getSettings().getFighterWingSpec(id) != null) {
                    if (subMarket != null) {
                        numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.FIGHTER_CHIP, id);
                        continue;
                    }
                }
            }


        }

        return numberRemaining;
    }

    public static boolean haveMetReqForItem(String id, float value, OtherCostData.ItemType tyoe) {
        return value <= retrieveAmountOfItems(id, marketId, tyoe);
    }

    public List<AoTDSpecialProject> getProjectMatchingReward(ProjectReward.ProjectRewardType type, String id) {
        ArrayList<AoTDSpecialProject> projects = new ArrayList<>();
        return getProjects().values().stream().filter(x -> x.getProjectSpec().getRewards().stream().anyMatch(y -> y.type == type && y.id.equals(id))).toList();

    }

    public List<AoTDSpecializationSpec> getSpecializationSpecsOrdered() {
        return SpecialProjectSpecManager.getSpecializations().stream().sorted(Comparator.comparing(AoTDSpecializationSpec::getOrder)).toList();
    }

    public LinkedHashMap<String, List<AoTDSpecialProject>> getProjectsForUIListOrdered() {
        LinkedHashMap<String, List<AoTDSpecialProject>> projects = new LinkedHashMap<>();

        for (AoTDSpecializationSpec spec : getSpecializationSpecsOrdered()) {
            ArrayList<AoTDSpecialProject> allProjects = new ArrayList<>(getProjectMatchingType(spec.getId()));
            allProjects.sort(Comparator
                    .comparing(AoTDSpecialProject::checkIfProjectWasCompleted) // incomplete first
                    .thenComparing(p -> {
                        if (!p.checkIfProjectWasCompleted()) return 0;
                        return p.canDoProject() ? 0 : 1; // among completed, repeatable first
                    })
            );


            projects.put(spec.getId(), allProjects);
        }

        return projects;
    }

    public List<AoTDSpecialProject> getProjectMatchingType(String type) {
        ArrayList<AoTDSpecialProject> projects = new ArrayList<>();
        return getProjects().values().stream().filter(x -> x.getSpecialization().getId().equals(type)).toList();

    }
}
