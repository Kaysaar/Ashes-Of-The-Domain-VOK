package data.kaysaar.aotd.vok.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.AoTDListenerUtilis;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.coreui.CoreUITracker;
import data.kaysaar.aotd.vok.scripts.research.attitude.FactionResearchAttitudeData;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.checkForQolEnabled;

public class AoTDFactionResearchManager {
    private static final Logger logger = Global.getLogger(AoTDMainResearchManager.class);
    private float AIChrages = 0f;
    public MutableStat researchSpeedBonus = new MutableStat(0f);
    public MutableStat blackSiteSpecialProjBonus = new MutableStat(0f);

    public MutableStat getResearchSpeedBonus() {
        return researchSpeedBonus;
    }

    public MutableStat getBlackSiteSpecialProjBonus() {
        return blackSiteSpecialProjBonus;
    }

    public ArrayList<ResearchOption> getResearchRepoOfFaction() {
        return researchRepoOfFaction;
    }

    public ResearchQueueManager queueManager;


    public ResearchQueueManager getQueueManager() {
        if (queueManager == null) queueManager = new ResearchQueueManager(this.getFaction().getId());
        return queueManager;
    }

    public ArrayList<ScientistPerson> researchCouncil = new ArrayList<>();

    public ArrayList<ScientistPerson> getResearchCouncil() {
        researchCouncil.sort((a, b) -> {
            boolean aIsHead = isHeadOfResearch(a);
            boolean bIsHead = isHeadOfResearch(b);
            return Boolean.compare(bIsHead, aIsHead); // head (true) comes first
        });
        return researchCouncil;
    }

    public ScientistPerson currentHeadOfCouncil;
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

    }

    public void setAICounter(float counter) {
        AIChrages += counter;
    }

    public void advance(float amount) {
        executeAdvance(amount);

    }

    public Boolean isHeadOfResearch(ScientistPerson person) {
        return currentHeadOfCouncil != null && currentHeadOfCouncil.equals(person);
    }

    private void executeAdvance(float amount) {
        boolean hadMetreq = false;
        for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
            if (marketAPI.getIndustries().stream().filter(x -> x.getSpecialItem() != null).anyMatch(x -> x.getSpecialItem().getId().equals("omega_processor"))) {
                hadMetreq = true;
                break;
            }
        }

        if (getFaction().isPlayerFaction()) {
            if (hadMetreq) {
                Global.getSector().getMemory().set("$aotd_experimetnal_tier", true);
            } else {
                Global.getSector().getMemory().set("$aotd_experimetnal_tier", false);
            }
        }
        executeResearchCouncilAdvance(amount);
        int amountB = getAmountOfBlackSites() - 1;
        int amountR = getAmountOfResearchFacilities() - 1;
        if (amountR > 0) {
            getResearchSpeedBonus().modifyFlat("aotd_def_bonus", amountR * 0.1f);
        }
        if (amountB > 0) {
            getBlackSiteSpecialProjBonus().modifyFlat("aotd_def_bonus", amountB * 0.1f);
        }
        if (haveResearched(AoTDTechIds.MEGA_ASSEMBLY_SYSTEMS)) {
            getBlackSiteSpecialProjBonus().modifyFlat("aotd_def_bonus2", 0.2f);
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



        if (currentFocusId != null && getAmountOfResearchFacilities() != 0) {

            ResearchOption researchOption = getResearchOptionFromRepo(currentFocusId);
            if (researchOption.getSpec().getId().equals(currentFocusId) && canResearch(currentFocusId, false)) {
                if (getFaction().isPlayerFaction() && Global.getSettings().isDevMode()) {
                    researchOption.daysSpentOnResearching += 100 * Global.getSector().getClock().convertToDays(amount);
                }
                researchOption.daysSpentOnResearching += Global.getSector().getClock().convertToDays(amount);
                if (researchOption.getPercentageProgress() >= 100) {
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

    public void executeResearchCouncilAdvance(float amount) {
        researchCouncil.forEach(x -> x.advance(amount));
        researchCouncil.forEach(ScientistPerson::unapplyPassiveSkill);
        researchCouncil.forEach(ScientistPerson::unapplyActiveSkill);
        researchCouncil.forEach(ScientistPerson::applyPassiveSkill);
        if (currentHeadOfCouncil != null) {
            currentHeadOfCouncil.applyActiveSkill();
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

    public void addScientist(ScientistPerson scientist) {
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

    public int getAmountOfBlackSites() {

        return getFacilities("blacksite").size();
    }
    public List<Industry> getFacilities(String facilityId) {
        ArrayList<Industry>industries = new ArrayList<>();
        if (getFaction().isPlayerFaction()) {
            for (MarketAPI marketAPI : Misc.getPlayerMarkets(Global.getSettings().getModManager().isModEnabled("aotd_qol"))) {
                if (isIndustryFunctional(marketAPI, facilityId)){
                    industries.add(marketAPI.getIndustry(facilityId));
                }
            }
        } else {
            for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
                if (isIndustryFunctional(marketAPI, facilityId)) {
                    industries.add(marketAPI.getIndustry(facilityId));
                }
            }
        }
        return industries;
    }
    public boolean isIndustryFunctional(MarketAPI market, String industryId) {
        if (market == null || industryId == null) {
            return false;
        }

        if (market.hasIndustry(industryId)) {
            Industry industry = market.getIndustry(industryId);
            return industry != null && industry.isFunctional();
        }

        return false;
    }

    public int getAmountOfResearchFacilities() {
        int toReturn = 0;
        if (getFaction().isPlayerFaction()) {
            for (MarketAPI marketAPI : Misc.getPlayerMarkets(Global.getSettings().getModManager().isModEnabled("aotd_qol"))) {
                if (isIndustryFunctional(marketAPI, AoTDIndustries.RESEARCH_CENTER) || isIndustryFunctional(marketAPI, "blacksite"))
                    toReturn++;
            }
        } else {
            for (MarketAPI marketAPI : retrieveMarketsOfThatFaction()) {
                if (isIndustryFunctional(marketAPI, AoTDIndustries.RESEARCH_CENTER) || isIndustryFunctional(marketAPI, "blacksite"))
                    toReturn++;
            }
        }

        if (toReturn > 8) {
            toReturn = 8;
        }
        return toReturn;
    }
}
