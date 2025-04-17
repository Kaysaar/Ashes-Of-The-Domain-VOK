package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory;
import com.fs.starfarer.api.impl.campaign.fleets.RouteManager;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.impl.campaign.intel.ResearchExpeditionIntel;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDMemFlags;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.campaign.econ.listeners.ResearchFleetDefeatListener;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.research.attitude.FactionResearchAttitudeData;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.plugins.AoTDSettingsManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import org.apache.log4j.Logger;

import java.util.*;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.checkForQolEnabled;

public class AoTDFactionResearchManager {
    private static final Logger logger = Global.getLogger(AoTDMainResearchManager.class);
    private float AIChrages = 0f;

    public ArrayList<ResearchOption> getResearchRepoOfFaction() {
        return researchRepoOfFaction;
    }

    public ResearchQueueManager queueManager;


    public ResearchQueueManager getQueueManager() {
        if (queueManager == null) queueManager = new ResearchQueueManager(this.getFaction().getId());
        return queueManager;
    }

    public ArrayList<ScientistAPI> researchCouncil = new ArrayList<>();
    public ScientistAPI currentHeadOfCouncil;
    public ArrayList<ResearchOption> researchRepoOfFaction;
    public float bonusToResearch = 0.0f;
    public float pointsTowardsUpgrade = 0.0f;
    public static float UPGRADE_THRESHOLD = 250f;


    public boolean canUpgrade = false;
    public float pointTowardsExpedition = 0.0f;
    public static float EXPEDITION_THRESHOLD = 5;

    public ResearchOption getCurrentFocus() {
        return getResearchOptionFromRepo(currentFocusId);
    }

    public transient FactionResearchAttitudeData attitudeData;

    public FactionResearchAttitudeData getAttitudeData() {
        return attitudeData;
    }

    public void setAttitudeData(FactionResearchAttitudeData attitudeData) {
        this.attitudeData = attitudeData;
    }

    public void setCurrentFocus(String currentFocus) {
        this.currentFocusId = currentFocus;
    }

    public String currentFocusId;
    public FactionAPI managerTiedToFaction = null;

    public AoTDFactionResearchManager(FactionAPI factionAPI, ArrayList<ResearchOption> researchOptions) {
        this.managerTiedToFaction = factionAPI;
        this.researchRepoOfFaction = researchOptions;
        this.queueManager = new ResearchQueueManager(this.getFaction().getId());
    }

    public void setCanUpgrade(boolean canUpgrade) {
        this.canUpgrade = canUpgrade;
    }

    public void manageForAI(float amount) {
        pointTowardsExpedition += Global.getSector().getClock().convertToDays(amount);
        if (haveMetCriteriaForUpgrade()) {
            pointsTowardsUpgrade += Global.getSector().getClock().convertToDays(amount);
        }
        if (pointsTowardsUpgrade >= UPGRADE_THRESHOLD) {
            pointsTowardsUpgrade = 0;
            canUpgrade = true;
        }
        Collections.shuffle(researchRepoOfFaction);
        if (currentFocusId == null && AIChrages >= 1) {
            for (ResearchOption option : researchRepoOfFaction) {
                if (canResearch(option.Id, true)) {
                    currentFocusId = option.Id;
                    AIChrages--;
                    if (Global.getSettings().isDevMode()) {
                        notifyFactionStartedResearchOnDevMode(getResearchOptionFromRepo(currentFocusId));
                    }
                    break;
                }
            }
        }
    }

    public boolean haveMetCriteriaForUpgrade() {

        return haveResearched(AoTDTechIds.DEEP_MINING_METHODS) && haveResearched(AoTDTechIds.STREAMLINED_PRODUCTION);
    }

    public void sentFleet() {
        pointTowardsExpedition -= 380;
        FactionAPI faction = getFaction();
        if (Misc.getFactionMarkets(getFaction()).isEmpty()) {
            for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(ResearchExpeditionIntel.class)) {
                ResearchExpeditionIntel intel = (ResearchExpeditionIntel) intelInfoPlugin;
                if (intel.idOfIntel.split("_")[1].equals(getFaction().getId())) {
                    Global.getSector().getIntelManager().removeIntel(intelInfoPlugin);
                    break;
                }
            }
            AoTDMainResearchManager.getInstance().expeditionCounter = AoTDMainResearchManager.getInstance().expeditionThreshold / 3;
            return;
        }
        CampaignFleetAPI fleet = FleetFactory.createGenericFleet(faction.getId(), "Expedition Fleet", faction.getDoctrine().getShipQuality(), 150);
        PlanetAPI targetPlanet = null;
        ArrayList<PlanetAPI> preCollapsePlanets = (ArrayList<PlanetAPI>) Global.getSector().getPersistentData().get(AoTDMemFlags.preCollapseFacList);
        if (preCollapsePlanets == null) return;
        Collections.shuffle(preCollapsePlanets);
        for (PlanetAPI preCollapsePlanet : preCollapsePlanets) {
            if (!preCollapsePlanet.hasCondition("pre_collapse_facility")) continue;
            if (!preCollapsePlanet.getMarket().getFaction().getId().equals(Factions.NEUTRAL)) continue;
            if (preCollapsePlanet.getMarket().getMemory().is("$aotd_fac_explored", true)) continue;
            if (preCollapsePlanet.getMarket().getMemory().is("$aotd_chosen_by_faction", true)) continue;
            targetPlanet = preCollapsePlanet;
            break;
        }
        if (targetPlanet == null) {
            return;
        }
        Long seed = new Random().nextLong();
        fleet.addTag("aotd_expedition");
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_FLEET_TYPE, "aotd_expedition");

        MarketAPI from = Misc.getFactionMarkets(getFaction()).get(0);
        from.getContainingLocation().addEntity(fleet);
        fleet.setFacing((float) Math.random() * 360f);
        for (IntelInfoPlugin intelInfoPlugin : Global.getSector().getIntelManager().getIntel(ResearchExpeditionIntel.class)) {
            ResearchExpeditionIntel intel = (ResearchExpeditionIntel) intelInfoPlugin;
            if (intel.idOfIntel.split("_")[1].equals(getFaction().getId())) {
                intel.setLaunchMarket(from);
                break;
            }

        }

        // this will get overridden by the patrol assignment AI, depending on route-time elapsed etc
        fleet.setLocation(from.getPrimaryEntity().getLocation().x, from.getPrimaryEntity().getLocation().y);
        RouteManager.RouteData data = new RouteManager.RouteData("research_" + faction.getId(), from, seed, new RouteManager.OptionalFleetData(from));
        data.addSegment(new RouteManager.RouteSegment(30000f, from.getPrimaryEntity()));
        fleet.removeAbility(Abilities.GO_DARK);
        fleet.setTransponderOn(true);
        assert targetPlanet != null;
        if (targetPlanet.getMarket() == null) return;
        targetPlanet.getMarket().getMemory().set("$aotd_chosen_by_faction", true, 300);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, false);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOLD_VS_STRONGER, true);
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, true);
        MessageIntel intel = new MessageIntel("Faction Expedition imminent from  " + from.getName() + " by " + getFaction().getDisplayName(), Misc.getBasePlayerColor());
        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO);
        assert targetPlanet != null;
        fleet.addEventListener(new ResearchFleetDefeatListener());
        fleet.addScript(new ResearchFleetRouteManager(fleet, data, targetPlanet));
    }

    public void setAICounter(float counter) {
        AIChrages += counter;
    }

    public void advance(float amount) {
        if (currentHeadOfCouncil != null) {
            currentHeadOfCouncil.advance(amount);
        }
        if (getCurrentFocus() == null) {
            notifyFactionBeginResearch(getQueueManager().removeFromTop());
        }
        if (!getFaction().isPlayerFaction() && !getFaction().getId().equals(Factions.PIRATES)) {
            manageForAI(amount);
        }
        for (ResearchOption option : researchRepoOfFaction) {
            if (!getFaction().isPlayerFaction()) {
                if (option.Tier.ordinal() <= 1) {
                    option.setResearched(true);
                }
            }
            if (option.otherReq != null) {
                if (Global.getSector().getMemory().is(option.otherReq.one, true)) {
                    option.metOtherReq = true;
                }
            }
        }


        boolean hadMetreq = false;
        for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
            if (marketAPI.hasIndustry(AoTDIndustries.RESEARCH_CENTER)) {
                if (marketAPI.getIndustry(AoTDIndustries.RESEARCH_CENTER).getSpecialItem() != null) {
                    hadMetreq = true;
                }
            }
        }

        if(getFaction().isPlayerFaction()){
            if (hadMetreq) {
                Global.getSector().getMemory().set("$aotd_experimetnal_tier", true);
            } else {
                Global.getSector().getMemory().set("$aotd_experimetnal_tier", false);
            }
        }



        if (this.getFaction().isPlayerFaction()) {
            if (this.haveResearched(AoTDTechIds.AGRICULTURE_INDUSTRIALIZATION)) {
                Global.getSettings().getIndustrySpec(AoTDIndustries.MONOCULTURE).setUpgrade(Industries.FARMING);
            }
            if (this.haveResearched(AoTDTechIds.EXO_SKELETONS)) {
                Global.getSettings().getIndustrySpec(AoTDIndustries.EXTRACTIVE_OPERATION).setUpgrade(Industries.MINING);
            }
            if (this.haveResearched(AoTDTechIds.NANOMETAL_FUSION_SYNTHESIS)) {
                Global.getSettings().getIndustrySpec(AoTDIndustries.SMELTING).setUpgrade(Industries.REFINING);
                Global.getSettings().getIndustrySpec(AoTDIndustries.LIGHT_PRODUCTION).setUpgrade(Industries.LIGHTINDUSTRY);
            }
            if (this.haveResearched(AoTDTechIds.BASE_SHIP_HULL_ASSEMBLY)) {
                Global.getSettings().getIndustrySpec(AoTDIndustries.HEAVY_PRODUCTION).setUpgrade(Industries.HEAVYINDUSTRY);
            }
            if (this.haveResearched(AoTDTechIds.INTERSTELLAR_LOGISTICS)) {
                Global.getSettings().getIndustrySpec(Industries.WAYSTATION).setUpgrade(AoTDIndustries.TERMINUS);
            }
            if (this.haveResearched(AoTDTechIds.ANTIMATTER_SYNTHESIS)) {
                Global.getSettings().getIndustrySpec(Industries.FUELPROD).setUpgrade(AoTDIndustries.BLAST_PROCESSING);

            }
        }

        if (currentFocusId != null && getAmountOfResearchFacilities() != 0) {

            ResearchOption researchOption = getResearchOptionFromRepo(currentFocusId);
            if (researchOption.getSpec().getId().equals(currentFocusId) && canResearch(currentFocusId, false)) {
                if (getFaction().isPlayerFaction() && Global.getSettings().isDevMode()) {
                    researchOption.daysSpentOnResearching += 100 * Global.getSector().getClock().convertToDays(amount);
                }
                researchOption.daysSpentOnResearching += Global.getSector().getClock().convertToDays(amount);
                if (researchOption.getPercentageProgress()>=100) {
                    researchOption.daysSpentOnResearching = 0;
                    researchOption.setResearched(true);
                    AoTDListenerUtilis.finishedResearch(researchOption.Id, this.getFaction());
                    currentFocusId = null;
                    if (getFaction().isPlayerFaction()) {
                        notifyResearchCompletion(researchOption);
                        Global.getSoundPlayer().playUISound("aotd_research_complete", 1f, 1f);

                    } else {
                        if (Global.getSettings().isDevMode()) {
                            notifyFactionFinishedResearchOnDevMode(researchOption);
                        }
                    }

                }

            }
        }


    }

    private void notifyResearchCompletion(ResearchOption researchOption) {
        MessageIntel intel = new MessageIntel("Researched technology - " + researchOption.Name, Misc.getBasePlayerColor());
        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO);
    }

    private void notifyFactionStartedResearchOnDevMode(ResearchOption researchOption) {
        MessageIntel intel = new MessageIntel("Faction " + getFaction().getDisplayName() + " Started Research - " + researchOption.Name, Misc.getBasePlayerColor());
        intel.setIcon(getFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO);
    }

    private void notifyFactionFinishedResearchOnDevMode(ResearchOption researchOption) {
        MessageIntel intel = new MessageIntel("Faction " + getFaction().getDisplayName() + " Researched Technology - " + researchOption.Name, Misc.getBasePlayerColor());
        intel.setIcon(getFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        CoreUITracker.setMemFlagForTechTab("research");
        Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
    }

    public void notifyFactionBeginResearch(ResearchOption researchOption) {
        if (researchOption == null) return;
        if (!this.canResearch(researchOption.Id, false)) {
            MessageIntel intel = new MessageIntel("Could not research - " + researchOption.Name + ": Requirements not met ", Misc.getNegativeHighlightColor());
            intel.setIcon(getFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
        } else {
            MessageIntel intel = new MessageIntel("Started Research - " + researchOption.Name, Misc.getBasePlayerColor());
            intel.setIcon(getFaction().getCrest());
            intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
            Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
            this.payForResearch(researchOption.Id);
            this.setCurrentFocus(researchOption.Id);
        }


    }

    public void pickResearchFocus(String id) {
        setCurrentFocus(id);
    }

    public void updateResearchFromSpec() {

    }

    public void addScientist(ScientistAPI scientist) {
        researchCouncil.add(scientist);
    }

    public FactionAPI getFaction() {
        return managerTiedToFaction;
    }

    public boolean haveResearched(String id) {
        for (ResearchOption researchOption : researchRepoOfFaction) {
            if (researchOption.getSpec().getId().equals(id)) {
                return researchOption.isResearched();
            }
        }
        return false;
    }

    public boolean canResearch(String id, boolean forAI) {
        if (!haveResearchedAllReq(id)) return false;
        if (!getFaction().isPlayerFaction()) {
            forAI = true;
        }
        if (haveResearched(id)) {
            return false;
        }
        if (!forAI) {
            if (!haveMetReqForItems(id)) {
                return false;
            }
        }
        if (getResearchOptionFromRepo(id).otherReq != null) {
            return getResearchOptionFromRepo(id).metOtherReq;
        }
        return true;
    }

    public boolean haveResearchedAllReq(String id) {
        for (String s : getResearchOptionFromRepo(id).ReqTechsToResearchFirst) {
            if (!haveResearched(s)) {
                return false;
            }
        }
        return true;
    }


    public ResearchOption getResearchOptionFromRepo(String id) {
        for (ResearchOption researchOption : researchRepoOfFaction) {
            if (researchOption.getSpec().getId().equals(id)) return researchOption;
        }
        return null;
    }

    public ResearchOption findNameOfTech(String id) {
        for (ResearchOption o : researchRepoOfFaction) {
            if (o.Id.equals(id)) return o;
        }
        return null;
    }

    public boolean haveMetReqForItems(String id) {
        if (getResearchOptionFromRepo(id).havePaidForResearch) return true;
        for (Map.Entry<String, Integer> entry : getResearchOptionFromRepo(id).ReqItemsToResearchFirst.entrySet()) {
            if (!haveMetReqForItem(entry.getKey(), entry.getValue())) return false;
        }
        return true;
    }

    public boolean haveMetReqForItem(String id, float value) {
        return value <= retrieveAmountOfItems(id);
    }

    public float retrieveAmountOfItems(String id) {
        float numberRemaining = 0;
        for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
            SubmarketAPI subMarket = marketAPI.getSubmarket(AoTDSubmarkets.RESEARCH_FACILITY_MARKET);
            if (Global.getSettings().getCommoditySpec(id) != null) {
                if (subMarket != null) {
                    numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, id);
                }
            }
            if (Global.getSettings().getSpecialItemSpec(id) != null) {
                if (subMarket != null) {
                    numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(id, null));
                }
            }

        }

        return numberRemaining;
    }

    public void payForResearch(String id) {
        if (getResearchOptionFromRepo(id).havePaidForResearch) return;
        for (Map.Entry<String, Integer> entry : getResearchOptionFromRepo(id).ReqItemsToResearchFirst.entrySet()) {
            AoTDMisc.eatItems(entry, AoTDSubmarkets.RESEARCH_FACILITY_MARKET, retrieveMarketsOfThatFaction());

        }
        getResearchOptionFromRepo(id).havePaidForResearch = true;


    }


    public List<MarketAPI> retrieveMarketsOfThatFaction() {
        ArrayList<MarketAPI> marketsToReturn = new ArrayList<>();
        if (getFaction().isPlayerFaction()) {
            return Misc.getPlayerMarkets(checkForQolEnabled());
        }
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
            if (marketAPI.getFactionId().equals(getFaction().getId())) {
                marketsToReturn.add(marketAPI);
            }

        }

        return marketsToReturn;
    }



    public int getAmountOfResearchFacilities() {
        int toReturn = 0;
        if (getFaction().isPlayerFaction()) {
            for (MarketAPI marketAPI : Misc.getPlayerMarkets(Global.getSettings().getModManager().isModEnabled("aotd_qol"))) {
                if (marketAPI.hasIndustry(AoTDIndustries.RESEARCH_CENTER)) {
                    toReturn++;
                }
            }
        } else {
            for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
                if (marketAPI.hasIndustry(AoTDIndustries.RESEARCH_CENTER)) {
                    toReturn++;
                }
            }
        }

        if (toReturn > 8) {
            toReturn = 8;
        }
        return toReturn;
    }
}
