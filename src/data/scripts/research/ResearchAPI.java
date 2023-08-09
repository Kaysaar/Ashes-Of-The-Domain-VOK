package data.scripts.research;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.util.A;
import data.Ids.AoDIndustries;
import data.Ids.AodMemFlags;
import data.Ids.AodResearcherSkills;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import lunalib.lunaSettings.LunaSettings;

public class ResearchAPI {


    public String idLabel = "id";
    public String tierLabel = "tier";
    public String subMarketResearch = "researchfacil";
    public String researchCostLabel = "research_cost";
    public String mustResearchedLabel = "must_researched";
    public String reqItemsLabel = "req_items";
    public String hasDowngradeLabel = "has_downgrade";
    public String downgradeIdLabel = "downgrade";
    public String isDissabled = "is_disabled";
    public String modIdLabel = "mod_id";
    public String dissabledFactor = "disabled_factor";
    public ArrayList<ResearchOption> researchOptions = new ArrayList<>();
    public  ArrayList<ResearchOption> researchQueue = new ArrayList<>();
    public ResearchOption currentResearching = null;
    JSONArray allResearches;
    public boolean researching = false;
    public ArrayList<PersonAPI> researchersInPossetion = new ArrayList<>();

    public PersonAPI currentResearcher = null;

    public ArrayList<ResearchOption> getResearchOptions() {
        return researchOptions;
    }

    public ResearchOption getCurrentResearching() {
        return currentResearching;
    }

    public void setCurrentResearching(ResearchOption currentResearching) {
        this.currentResearching = currentResearching;
    }

    public ArrayList<PersonAPI> getResearchersInPossetion() {
        return researchersInPossetion;
    }


    public void addResearchersInPossetion(PersonAPI personAPI) {
        if (personAPI == null) return;
        this.researchersInPossetion.add(personAPI);
    }

    public PersonAPI getCurrentResearcher() {
        return currentResearcher;
    }

    public void setCurrentResearcher(PersonAPI currentResearcher) {
        this.currentResearcher = currentResearcher;
    }


    public void setResearching(boolean researching) {
        this.researching = researching;
    }


    public ResearchOption getResearchOption(String id) {
        for (ResearchOption researchOption : researchOptions) {
            if (researchOption.industryId.equals(id)) {
                return researchOption;
            }
        }
        return null;
    }


    public void setResearchOption(String industryId, int researchCost, int researchTier, boolean researched,
                                  ArrayList<String> requiredResearches, HashMap<String, Integer> itemsRequired,
                                  boolean hasDowngrade, String downgradeId, boolean isHidden, String modId, List<String> hiddenFactor) {
        if (getIndustryName(industryId) == null) {
            return;
        }

        researchOptions.add(createResearchOption(industryId, researchCost, researchTier, researched, requiredResearches, itemsRequired, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor));
    }

    public ResearchOption createResearchOption(String industryId, int researchCost, int researchTier, boolean researched,
                                               ArrayList<String> requiredResearches, HashMap<String, Integer> itemsRequired,
                                               boolean hasDowngrade, String downgradeId, boolean isHidden, String modId, List<String> hiddenFactor) {
        return new ResearchOption(industryId, researchCost, researchTier, researched, requiredResearches, itemsRequired, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor);
    }

    public boolean searchForError() {
        for (ResearchOption researchOption : researchOptions) {
            if (researchOption.industryName.equals("Sth fucked")) {
                return true;
            }
        }
        return false;
    }

    public boolean searchForSameNameTwice(String industryId) {
        boolean once = false;
        for (ResearchOption researchOption : researchOptions) {
            if (once && researchOption.industryId.equals(industryId)) {
                return true;
            }
            if (researchOption.industryId.equals(industryId)) {
                once = true;
            }

        }
        return false;
    }

    public void removeCopies() {
        boolean stillCopies = true;

        while (stillCopies) {
            for (ResearchOption researchOption : researchOptions) {
                if (searchForSameNameTwice(researchOption.industryId)) {
                    researchOptions.remove(researchOption);
                    stillCopies = true;
                    break;

                } else {
                    stillCopies = false;
                }
            }

        }
    }


    public void clearResearchFromErrors() {
        while (true) {
            if (searchForError()) {
                for (ResearchOption researchOption : researchOptions) {
                    if (researchOption.industryName.equals("Sth fucked")) {
                        researchOptions.remove(researchOption);
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }


    public void updateResearchOption(ResearchOption researchOption) {
        if (getResearchOption(researchOption.industryId) != null) {
            float curr = getResearchOption(researchOption.industryId).currentResearchDays;
            boolean isResearched = getResearchOption(researchOption.industryId).isResearched;
            boolean hasMetCriteriaInCost = getResearchOption(researchOption.industryId).hasTakenResearchCost;
            researchOptions.remove(getResearchOption(researchOption.industryId));
            researchOption.currentResearchDays = curr;
            researchOption.isResearched = isResearched;
            researchOptions.add(researchOption);


        } else {
            researchOptions.add(researchOption);
        }

    }

    public ArrayList<ResearchOption> getAllResearchOptions() {
        return researchOptions;
    }

    public ArrayList<ResearchOption> getResearchOptionsSorted() {
        ArrayList<ResearchOption> toReturn;
        toReturn = getAvailableToResearch();
        toReturn.addAll(getNotAvailableToResearch());
        toReturn.addAll(getAllResearchedOptions());

        return toReturn;
    }

    public ArrayList<ResearchOption> getAvailableToResearch() {
        ArrayList<ResearchOption> toReturn = new ArrayList<>();
        for (ResearchOption researchOption : researchOptions) {
            if (!researchOption.isResearched && canResearch(researchOption.industryId, true)) {
                toReturn.add(researchOption);
            }
        }
        return toReturn;
    }

    public ArrayList<ResearchOption> getAllResearchedOptions() {
        ArrayList<ResearchOption> toReturn = new ArrayList<>();
        for (ResearchOption researchOption : researchOptions) {
            if (researchOption.isResearched) {
                toReturn.add(researchOption);
            }
        }
        return toReturn;
    }

    public ArrayList<ResearchOption> getNotAvailableToResearch() {
        ArrayList<ResearchOption> toReturn = new ArrayList<>();
        for (ResearchOption researchOption : researchOptions) {
            if (!researchOption.isResearched && !canResearch(researchOption.industryId, true)) {
                toReturn.add(researchOption);
            }
        }
        return toReturn;
    }

    public MarketAPI firstMarketThatHaveResearchFacility() {
        for (MarketAPI factionMarket : Misc.getFactionMarkets(Global.getSector().getPlayerFaction().getId())) {
            for (Industry industry : factionMarket.getIndustries()) {
                if (!factionMarket.isPlayerOwned()) continue;
                if (industry.getId().equals(AoDIndustries.RESEARCH_CENTER)) {
                    return factionMarket;
                }
            }
        }
        return null;
    }

    public List<MarketAPI> getAllMarketsWithResearch() {
        List<MarketAPI> toReturn = new ArrayList<>();
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(false)) {
            if (!playerMarket.isPlayerOwned()) continue;
            if (playerMarket.hasIndustry(AoDIndustries.RESEARCH_CENTER)) {
                if (playerMarket.getIndustry(AoDIndustries.RESEARCH_CENTER).isFunctional()) {
                    toReturn.add(playerMarket);
                }
            }
        }
        return toReturn;
    }

    public boolean hasMetReq(Map.Entry<String, Integer> req) {
        if (req == null) return true;

        int reqAmount = req.getValue();
        boolean isSpecial = req.getKey().equals("hegeheavy_databank") || req.getKey().equals("triheavy_databank") || req.getKey().equals("ii_ind_databank");
        if (!isSpecial && currentResearcher != null && currentResearcher.hasTag("aotd_resourceful")) {
            reqAmount -= 1;
            if (req.getKey().equals("domain_artifacts") || req.getKey().equals("water")) {
                reqAmount -= 100;
            }
        }
        if(isSpecial&&currentResearcher!=null&&currentResearcher.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)){
            reqAmount -= 1;
        }

        for (MarketAPI allMarketsWithResearch : getAllMarketsWithResearch()) {
            if (allMarketsWithResearch == null) {
                continue;
            }
            if (allMarketsWithResearch.getSubmarket(subMarketResearch) == null) continue;
            if (allMarketsWithResearch.getSubmarket(subMarketResearch).getCargo() == null) continue;

            if (Global.getSettings().getSpecialItemSpec(req.getKey()) != null) {
                SpecialItemData sec = new SpecialItemData(Global.getSettings().getSpecialItemSpec(req.getKey()).getId(), null);
                float amountFromOneMarket = allMarketsWithResearch.getSubmarket(subMarketResearch).getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, sec);
                reqAmount -= amountFromOneMarket;
            } else {
                float amountFromOneMarket = allMarketsWithResearch.getSubmarket(subMarketResearch).getCargo().getCommodityQuantity(req.getKey());
                reqAmount -= amountFromOneMarket;
            }
        }
        return reqAmount <= 0;
    }

    public void removeItemReqFromMarkets(Map.Entry<String, Integer> req) {
        int reqAmount = req.getValue();
        boolean isNotSpecial = req.getKey().equals("hegeheavy_databank") || req.getKey().equals("triheavy_databank") || req.getKey().equals("ii_ind_databank");
        if (isNotSpecial && currentResearcher != null && currentResearcher.hasTag("aotd_resourceful")) {
            reqAmount -= 1;
            if (req.getKey().equals("domain_artifacts") || req.getKey().equals("water")) {
                reqAmount -= 100;
            }
        }
        for (MarketAPI allMarketsWithResearch : getAllMarketsWithResearch()) {
            if (Global.getSettings().getSpecialItemSpec(req.getKey()) != null) {
                SpecialItemData sec = new SpecialItemData(Global.getSettings().getSpecialItemSpec(req.getKey()).getId(), null);
                float amountFromOneMarket = allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().getQuantity(CargoAPI.CargoItemType.SPECIAL, sec);
                if (reqAmount <= 0) {
                    break;
                }
                if (amountFromOneMarket <= reqAmount) {
                    allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().removeItems(CargoAPI.CargoItemType.SPECIAL, sec, amountFromOneMarket);

                } else {
                    allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().removeItems(CargoAPI.CargoItemType.SPECIAL, sec, reqAmount);

                }
                reqAmount -= amountFromOneMarket;

            } else {
                float amountFromOneMarket = allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().getCommodityQuantity(req.getKey());
                if (amountFromOneMarket <= reqAmount) {
                    allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().removeCommodity(req.getKey(), amountFromOneMarket);

                } else {
                    allMarketsWithResearch.getSubmarket(subMarketResearch).getCargoNullOk().removeCommodity(req.getKey(), reqAmount);
                }
                reqAmount -= amountFromOneMarket;
            }


        }
    }

    public void removeReq(ResearchOption researchOption) {
        researchOption.requieredItems.clear();
    }


    public boolean isResearching() {
        return researching;
    }


    public void handleOtherModsAvailbility() {
        for (ResearchOption researchOption : researchOptions) {
            boolean chooseFate = false;
            if (researchOption.hiddenFactor != null) {
                for (String decider : researchOption.hiddenFactor) {
                    if (Global.getSettings().getModManager().isModEnabled("lunalib"))
                        chooseFate = !Boolean.TRUE.equals(LunaSettings.getBoolean(researchOption.modId, decider));
                    else chooseFate = !Global.getSettings().getBoolean(decider);
                    if (chooseFate) {
                        break;
                    }
                }
                researchOption.isDisabled = chooseFate;
            }


        }
    }

    public boolean canResearch(String industryId, boolean forQueue) {

        if (isResearching() && !forQueue) {
            return false;
        }
        ResearchOption wantsToResearch = getResearchOption(industryId);
        if (getResearchFacilitiesQuantity() == 0) {
            return false;
        }
        if (getResearchOption(industryId).requieredItems != null && !getResearchOption(industryId).requieredItems.isEmpty()&&!getResearchOption(industryId).hasTakenResearchCost) {
            for (Map.Entry<String, Integer> requieredItem : getResearchOption(industryId).requieredItems.entrySet()) {
                if (!hasMetReq(requieredItem)) {
                    return false;
                }

            }
        }

        if (!wantsToResearch.requieredIndustriesToResearchIds.isEmpty()) {
            for (String requieremnt : wantsToResearch.requieredIndustriesToResearchIds) {
                if (requieremnt.equals("")) {
                    continue;
                }
                if (getResearchOption(requieremnt) == null) {
                    continue;
                }
                if (isDisabled(requieremnt)) {
                    continue;
                }
                if (!isResearched(requieremnt)) {
                    return false;
                }

            }
        }

        return true;
    }

    public int getResearchFacilitiesQuantity() {
        int counter = 0;
        for (MarketAPI factionMarket : Misc.getFactionMarkets(Global.getSector().getPlayerFaction().getId())) {
            if (!factionMarket.isPlayerOwned()) continue;
            if (factionMarket.hasIndustry(AoDIndustries.RESEARCH_CENTER)) {
                if (factionMarket.getIndustry(AoDIndustries.RESEARCH_CENTER).isFunctional()) {
                    counter++;
                }

            }
        }
        return counter;
    }

    public boolean isResearched(String industryId) {

        return getResearchOption(industryId).isResearched;
    }

    public boolean isDisabled(String industryId) {
        return getResearchOption(industryId).isDisabled;
    }




    public String getIndustryName(String industryId) {
        if (Global.getSettings().getIndustrySpec(industryId) != null) {
            return Global.getSettings().getIndustrySpec(industryId).getName();
        }
        return null;
    }
    public boolean isInQueue(String id){
        for (ResearchOption researchOption : researchQueue) {
            if(researchOption.industryId.equals(id)){
                return true;
            }
        }
        return false;
    }
    public boolean canMoveUpOrDown(boolean up,String id){
        int index = 0;
        for (ResearchOption researchOption : researchQueue) {
            if(researchOption.industryId.equals(id)){
                break;
            }
            index++;
        }
        if(!up){
            index++;
            if(index>=researchQueue.size()){
                return false;
            }
        }
        else{
            index--;
            if(index<0){
                return false;
            }
        }
        return true;
    }
    public void moveUpOrDownInQueue(String id,boolean up){
        if(!canMoveUpOrDown(up,id)){
            return;
        }
        if(!up){
            int index = 0;
            for (ResearchOption researchOption : researchQueue) {
                if(researchOption.industryId.equals(id)){
                    break;
                }
                index++;
            }
            int highindex = index+1;
            ResearchOption lower = getResearchOption(researchQueue.get(highindex).industryId);
            ResearchOption higher = getResearchOption(researchQueue.get(index).industryId);;
            researchQueue.set(highindex,higher);
            researchQueue.set(index,lower);
        }
        else{
            int index = 0;
            for (ResearchOption researchOption : researchQueue) {
                if(researchOption.industryId.equals(id)){
                    break;
                }
                index++;
            }
            int lowindex = index-1;
            ResearchOption lower = getResearchOption(researchQueue.get(lowindex).industryId);
            ResearchOption higher = getResearchOption(researchQueue.get(index).industryId);;
            researchQueue.set(lowindex,higher);
            researchQueue.set(index,lower);
        }

    }
    public void moveToTopOfQueue(String id ){
        int index = researchQueue.indexOf(getResearchOption(id));
        ResearchOption prevTop = researchQueue.get(0);
        ResearchOption newTop = researchQueue.get(index);
        researchQueue.set(index,prevTop);
        researchQueue.set(0,newTop);
    }
    public void moveToBottomOfQueue(String id ){
        int index = researchQueue.indexOf(getResearchOption(id));
        ResearchOption prevBot = researchQueue.get(researchQueue.size()-1);
        ResearchOption newBot = researchQueue.get(index);
        researchQueue.set(index,prevBot);
        researchQueue.set(researchQueue.size()-1,newBot);
    }
    public void removeFromQueue(String id ){
        researchQueue.remove(getResearchOption(id));
    }

    public boolean loadMergedCSV() {
        try {
            allResearches = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/research_options.csv", "aod_core");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return true;
    }


    public void initializeResearchList() throws JSONException {
        for (int i = 0; i < allResearches.length(); i++) {
            JSONObject jsonObject = allResearches.getJSONObject(i);
            String id = jsonObject.getString(idLabel);
            if (id.isEmpty()) {
                continue;
            }
            if (id.contains("#")) continue;
            int tier = jsonObject.getInt(tierLabel);
            int cost = jsonObject.getInt(researchCostLabel);
            boolean isResearched = jsonObject.getBoolean("is_researched");
            ArrayList<String> mustResearched = getResearchRequiredFromCSV(jsonObject.getString(mustResearchedLabel));
            HashMap<String, Integer> itemsReqToStartResearch = getItemsRequiredFromCSV(jsonObject.getString(reqItemsLabel), tier);
            boolean hasDowngrade = jsonObject.getBoolean(hasDowngradeLabel);
            String downgradeId;
            if (hasDowngrade) {
                downgradeId = jsonObject.getString(downgradeIdLabel);
            } else {
                downgradeId = null;
            }
            boolean isHidden = jsonObject.getBoolean(isDissabled);
            String modId = jsonObject.getString(modIdLabel);

            String hiddenFactorExtraced = jsonObject.getString(dissabledFactor);
            if (tier >= 3) tier = 3;
            if (hiddenFactorExtraced.isEmpty()) {
                List<String> hiddenFactor = null;
                setResearchOption(id, cost, tier, isResearched, mustResearched, itemsReqToStartResearch, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor);
            } else {
                String[] splited = hiddenFactorExtraced.split(",");
                List<String> hiddenFactor = new ArrayList<>(Arrays.asList(splited));
                setResearchOption(id, cost, tier, isResearched, mustResearched, itemsReqToStartResearch, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor);
            }


        }
        allResearches = null;

    }

    public void updateResearchListFromCSV() throws JSONException {
        for (int i = 0; i < allResearches.length(); i++) {
            JSONObject jsonObject = allResearches.getJSONObject(i);
            String id = jsonObject.getString(idLabel);
            if (id.isEmpty()) {
                continue;
            }
            int tier = jsonObject.getInt(tierLabel);
            int cost = jsonObject.getInt(researchCostLabel);
            boolean isResearched = jsonObject.getBoolean("is_researched");

            ArrayList<String> mustResearched = getResearchRequiredFromCSV(jsonObject.getString(mustResearchedLabel));
            HashMap<String, Integer> itemsReqToStartResearch = getItemsRequiredFromCSV(jsonObject.getString(reqItemsLabel), tier);
            boolean hasDowngrade = jsonObject.getBoolean(hasDowngradeLabel);
            String downgradeId;
            if (hasDowngrade) {
                downgradeId = jsonObject.getString(downgradeIdLabel);
            } else {
                downgradeId = null;
            }
            if (getResearchOption(id) != null) {
                isResearched = getResearchOption(id).isResearched;
                if(getResearchOption(id).requieredItems==null||getResearchOption(id).requieredItems.isEmpty()){
                    itemsReqToStartResearch = null;
                }
            }
            boolean isHidden = jsonObject.getBoolean(isDissabled);
            String modId = jsonObject.getString(modIdLabel);
            String hiddenFactorExtraced = jsonObject.getString(dissabledFactor);
            if (tier >= 3) tier = 3;
            if (hiddenFactorExtraced.isEmpty()) {
                List<String> hiddenFactor = null;
                updateResearchOption(createResearchOption(id, cost, tier, isResearched, mustResearched, itemsReqToStartResearch, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor));
            } else {
                String[] splited = hiddenFactorExtraced.split(",");
                List<String> hiddenFactor = new ArrayList<>(Arrays.asList(splited));
                updateResearchOption(createResearchOption(id, cost, tier, isResearched, mustResearched, itemsReqToStartResearch, hasDowngrade, downgradeId, isHidden, modId, hiddenFactor));
            }


        }
    }


    public ArrayList<ResearchOption> getRequierements(String id, boolean researched) {
        if (researched) {
            return getRequieredResearched(id);
        }
        return getRequieredToResearch(id);
    }

    public ArrayList<ResearchOption> getRequieredResearched(String id) {
        ResearchOption research;
        ArrayList<ResearchOption> toReturn = new ArrayList<>();
        for (String s : getResearchOption(id).requieredIndustriesToResearchIds) {
            research = getResearchOption(s);
            if (research != null) {
                if (research.isResearched) {
                    toReturn.add(research);
                }
            }

        }
        return toReturn;
    }

    public ArrayList<ResearchOption> getRequieredToResearch(String id) {
        ResearchOption research;
        ArrayList<ResearchOption> toReturn = new ArrayList<>();
        for (String s : getResearchOption(id).requieredIndustriesToResearchIds) {
            research = getResearchOption(s);
            if (research != null) {
                if (!research.isResearched) {
                    toReturn.add(research);
                }
            }

        }
        return toReturn;
    }

    public ArrayList<String> getResearchRequiredFromCSV(String toConvert) throws JSONException {
        ArrayList<String> req = new ArrayList<>();
        String[] convert = toConvert.split(",");
        for (String s : convert) {
            req.add(s);
        }
        return req;
    }


    public HashMap<String, Integer> getItemsRequiredFromCSV(String reqItems, int tier) throws JSONException {
        String[] splitedAll = reqItems.split(",");
        HashMap<String, Integer> itemsReq = new HashMap<>();
        if (tier > 1) {
            String req = "research_databank";
            itemsReq.put(req, tier);
        }
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");

        for (String s : splitedAll) {
            String[] splitedInstance = s.split(":");
            if (splitedInstance.length != 2) {
                return null;
            }
            if (haveNexerelin && Global.getSector().getMemoryWithoutUpdate().is("$nexRandAod", true)) {
                if (splitedInstance[0].contains("triheavy_databank") || splitedInstance[0].contains("hegeheavy_databank") || splitedInstance[0].contains("ii_ind_databank")) {
                    itemsReq.put("pristine_nanoforge", 1);
                    continue;
                }
            }
            if (splitedInstance[0].equals("research_databank")) {
                itemsReq.remove(splitedInstance[0]);
            }

            if (Integer.parseInt(splitedInstance[1]) > 0) {
                itemsReq.put(splitedInstance[0], Integer.parseInt(splitedInstance[1]));
            }

        }

        return itemsReq;
    }


    //    public int getTier(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            return research.researchTier;
//        }
//        return -1;
//    }
//
//
//    public int getResearchCost(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            return research.researchCost;
//        }
//        return -1;
//    }
//
//
//    public ArrayList<String> getResearchRequired(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            return research.requieredIndustriesToResearchIds;
//        }
//        return null;
//    }
//
//
//    public HashMap<String, Integer> getItemsRequired(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            return research.requieredItems;
//        }
//        return null;
//    }
//
//
//    public boolean getHasDowngrade(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            return research.hasDowngrade;
//        }
//        return false;
//    }
//
//
//    public String getDowngrade(String id) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            if (research.hasDowngrade) {
//                return research.downgradeId;
//            }
//        }
//        return null;
//    }
//
//
//    public void setTier(String id, int tier) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            research.researchTier = tier;
//        }
//    }
//
//
    public int numberOfCertainTierIndustries(int tier) {
        int amount = 0;
        for (ResearchOption researchOption : researchOptions) {
            if (researchOption.researchTier == tier) {
                amount++;
            }
        }
        return amount;
    }

    public List<ResearchOption> reesarchOptionsFromSameGroup(String group) {
        List<ResearchOption> result = new ArrayList<>();
        for (ResearchOption researchOption : researchOptions) {
            if (Global.getSettings().getIndustrySpec(researchOption.industryId).hasTag(group)) {
                result.add(researchOption);
            }
        }
        return result;
    }

    public HashMap<List<ResearchOption>, Integer> parentGroup(List<ResearchOption> members) {
        HashMap<List<ResearchOption>, Integer> sortedPartents = new HashMap<>();
        List<ResearchOption> sideMemberTier3 = new ArrayList<>();
        List<ResearchOption> sideMemberTier2 = new ArrayList<>();
        List<ResearchOption> sideMemberTier1 = new ArrayList<>();
        List<ResearchOption> sideMemberTier0 = new ArrayList<>();
        for (ResearchOption member : members) {
            if (member.researchTier == 3) {
                sideMemberTier3.add(member);

            }
        }
        for (ResearchOption member : members) {
            if (member.researchTier == 2) {
                sideMemberTier2.add(member);
            }
        }
        for (ResearchOption member : members) {
            if (member.researchTier == 1) {
                sideMemberTier1.add(member);
            }
        }
        for (ResearchOption member : members) {
            if (member.researchTier == 0) {
                sideMemberTier0.add(member);
            }
        }
        sortedPartents.put(sideMemberTier0, 0);
        sortedPartents.put(sideMemberTier1, 1);
        sortedPartents.put(sideMemberTier2, 2);
        sortedPartents.put(sideMemberTier3, 3);


        return sortedPartents;
    }

    public List<String> getAllPossibleUpgrades(String industryId) {
        List<String> allIds = new ArrayList<>();
        for (ResearchOption researchOption : researchOptions) {
            if (!researchOption.hasDowngrade) continue;
            if (researchOption.downgradeId.equals(industryId)) {
                allIds.add(researchOption.industryId);
            }
        }
        return allIds;
    }

//
//    public void setResearchRequired(String id, ArrayList<String> reqResearch) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            research.requieredIndustriesToResearchIds = reqResearch;
//        }
//
//    }
//
//
//    public void setItemsRequired(String id, HashMap<String, Integer> itemsReq) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            research.requieredItems = itemsReq;
//        }
//    }

    public void saveResearch(boolean newGame) {

        Map<String, Boolean> researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get(AodMemFlags.RESEARCH_SAVED);
        if (!researchSaved.isEmpty() && newGame) {
            researchSaved.clear();
        }
        for (ResearchOption researchOption : researchOptions) {
            researchSaved.put(researchOption.industryId, researchOption.isResearched);
        }
    }

    public int alreadyResearchedAmount() {
        int counter = 0;
        for (ResearchOption researchOption : researchOptions) {
            if (!researchOption.isResearched) continue;
            if(researchOption.isDisabled)continue;
            counter++;
        }
        return counter;
    }
    public ArrayList<ResearchOption> getResearchQueue(){
        return this.researchQueue;
    }

    public int alreadyResearchedAmountCertainTier(int tier) {
        int counter = 0;
        for (ResearchOption researchOption : researchOptions) {
            if(researchOption.isDisabled)continue;
            if (researchOption.researchTier != tier && tier != 3) continue;
            if (tier == 3) {
                if (researchOption.researchTier <= 2) {
                    continue;
                }
            }
            if (!researchOption.isResearched) continue;
            counter++;
        }
        return counter;
    }


//    public void setIsUpgradeToIndustry(String id, boolean hasDowngrade) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            research.hasDowngrade = hasDowngrade;
//        }
//    }
//
//
//    public void setDowngrade(String id, String downgradeId) {
//        ResearchOption research = getResearchOption(id);
//        if (research != null) {
//            if (research.hasDowngrade) {
//                research.downgradeId = downgradeId;
//            }
//
//        }
//    }
public void addResearchToQueue(String id){
        ResearchOption researchOptionToInsert = getResearchOption(id);
        if (researchOptionToInsert.requieredItems != null) {
        for (Map.Entry<String, Integer> stringIntegerEntry : researchOptionToInsert.requieredItems.entrySet()) {
            removeItemReqFromMarkets(stringIntegerEntry);
        }
            researchOptionToInsert.requieredItems.clear();
            researchOptionToInsert.hasTakenResearchCost = true;

        removeReq(researchOptionToInsert);

    }
        researchQueue.add(researchOptionToInsert);
}
public void removeResearchFromQueue(String id ){
        researchQueue.remove(getResearchOption(id));
}
public void clearEntireResearchQueue(){
        researchQueue.clear();
}

    public void startResearch(String industryId) {
        if (!canResearch(industryId, false)) {
            return;
        }
        if (researching) {
            stopResearch();
        }
        currentResearching = getResearchOption(industryId);
        if (!currentResearching.initalized) {
            currentResearching.initalized = true;
            if(currentResearcher!=null&&currentResearcher.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)){
                if(currentResearching.industryId.equals("triheavy")||currentResearching.industryId.equals("hegeheavy")||currentResearching.industryId.equals("ii_stellacastellum")){
                    currentResearching.currentResearchDays = currentResearching.researchCost*3;
                }
            }
            else{
                currentResearching.currentResearchDays = currentResearching.researchCost;
            }



        }
        if (currentResearching.requieredItems != null) {
            for (Map.Entry<String, Integer> stringIntegerEntry : currentResearching.requieredItems.entrySet()) {
                removeItemReqFromMarkets(stringIntegerEntry);
            }
            currentResearching.requieredItems.clear();
            currentResearching.hasTakenResearchCost = true;

            removeReq(currentResearching);

        }
        researching = true;
    }

    public void stopResearch() {
        researching = false;
        currentResearching = null;
    }

    public void finishResearch() {
        getResearchOption(currentResearching.industryId).isResearched = true;
        if (currentResearching.hasDowngrade) {
            IndustrySpecAPI specApi = Global.getSettings().getIndustrySpec(currentResearching.downgradeId);
            for (String tag : specApi.getTags()) {
                if (tag.contains("starter")) {
                    specApi.setUpgrade(currentResearching.industryId);
                }
            }
        }


        MessageIntel intel = new MessageIntel("Researched technology - " + currentResearching.industryName, Misc.getBasePlayerColor());
        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO);
        saveResearch(true);
        stopResearch();
    }


}
