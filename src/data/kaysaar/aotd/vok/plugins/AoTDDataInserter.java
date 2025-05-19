package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl;
import com.fs.starfarer.api.impl.campaign.econ.ResourceDepositsCondition;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.AoTDMegastructureRules;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDConditions;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.campaign.econ.SMSpecialItem;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import static data.kaysaar.aotd.vok.Ids.AoTDMemFlags.preCollapseFacList;

public class AoTDDataInserter {
    public static String sophia = "aotd_sophia";
    private int maxTriTachyonElectronics = 2;
    public static String AOTD = "aotd_";
    public void generatePreCollapseFacilities(){
        int preCollapseFacAmount = AoTDSettingsManager.getIntValue(AoTDSettingsManager.AOTD_PCF_AMOUNT);
        List<StarSystemAPI> starSystems = Global.getSector().getStarSystems();
        List<PlanetAPI> planetsWithFac = new ArrayList<>();
        Collections.shuffle(starSystems);
        for (StarSystemAPI starSystem : starSystems) {
            if (starSystem.getTags().contains(Tags.THEME_RUINS_MAIN) || starSystem.getTags().contains(Tags.THEME_REMNANT) || starSystem.getTags().contains(Tags.THEME_DERELICT) || starSystem.getTags().contains("")) {
                for (PlanetAPI planet : starSystem.getPlanets()) {
                    if (planet.isStar()) continue;
                    if (!planet.getMarket().isPlanetConditionMarketOnly()) continue;
                    if (planet.hasTag(Tags.NOT_RANDOM_MISSION_TARGET)) continue;
                    if (planet.hasTag(Tags.MISSION_ITEM)) continue;
                    if (planet.isGasGiant()) continue;
                    String token = planet.getMarket().addCondition("pre_collapse_facility");
                    MarketConditionAPI marketConditionAPI = planet.getMarket().getSpecificCondition(token);
                    planet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SPEC_ID_OVERRIDE, "aotd_pre_collapse_fac");
                    planet.getMemoryWithoutUpdate().set("$hasDefenders", "aotd_pre_collapse_fac");
                    planet.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
                    marketConditionAPI.setSurveyed(false);
                    planetsWithFac.add(planet);
                    preCollapseFacAmount--;
                    break;
                }
                if(preCollapseFacAmount<=0)break;

            }
        }
        Global.getSector().getPersistentData().put(preCollapseFacList, planetsWithFac);
    }
    public  void insertSpecItemsForManufactoriumData() throws JSONException, IOException {
        JSONArray json =Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/stella_manufactorium.csv", "aod_core");
        ArrayList<SMSpecialItem> insertedSpecItemForManufactorium = new ArrayList<SMSpecialItem>();
        for(int i=0;i<json.length();i++){
            JSONObject obj = json.getJSONObject(i);
            String id = obj.getString("id");
            if(id==null||id.isEmpty()) continue;

            String itemCostRaw = obj.getString("resoruces_to_make_one");
            float dayCost = Float.parseFloat(obj.getString("time_to_make_one"));
            HashMap<String,Integer> itemCost = getItemCost(itemCostRaw);
            insertedSpecItemForManufactorium.add(new SMSpecialItem(itemCost,id,dayCost));

        }
        Global.getSector().getPersistentData().put("$stella_manufactorium_items",insertedSpecItemForManufactorium);
    }
    public  void insertSophia() {
        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        PersonAPI sophiaAshley = Global.getFactory().createPerson();
        sophiaAshley.setId(sophia);
        sophiaAshley.setFaction(Factions.INDEPENDENT);
        sophiaAshley.setGender(FullName.Gender.FEMALE);
        sophiaAshley.setRankId(Ranks.POST_SCIENTIST);
        sophiaAshley.setPostId(Ranks.POST_SCIENTIST);
        sophiaAshley.setImportance(PersonImportance.HIGH);
        sophiaAshley.setVoice(Voices.SCIENTIST);
        sophiaAshley.getName().setFirst("Sophia");
        sophiaAshley.getName().setLast("Anderson");
        sophiaAshley.getTags().add("aotd_researcher");
        sophiaAshley.getTags().add("aotd_resourceful");
        sophiaAshley.setPortraitSprite(Global.getSettings().getSpriteName("characters", "sophia"));
        if (!ip.containsPerson(sophiaAshley)) {
            ip.addPerson(sophiaAshley);
        }
    }
    public void setVanilaIndustriesDowngrades() {
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.getId().equals(Industries.FARMING)) {
                allIndustrySpec.setDowngrade(AoTDIndustries.MONOCULTURE);
                allIndustrySpec.addTag(AOTD+Industries.FARMING);
            }
            if (allIndustrySpec.getId().equals(Industries.AQUACULTURE)) {
                List<String> str = new ArrayList<>();
                for (String tag : allIndustrySpec.getTags()) {
                    if (tag.equals("farming")) continue;
                    str.add(tag);
                }
                allIndustrySpec.addTag("starter");
                allIndustrySpec.getTags().clear();
                str.add(AOTD+Industries.AQUACULTURE);
                for (String s : str) {
                    allIndustrySpec.addTag(s);
                }
            }
            if (allIndustrySpec.getId().equals(Industries.MINING)) {
                allIndustrySpec.setDowngrade(AoTDIndustries.EXTRACTIVE_OPERATION);
                allIndustrySpec.addTag(AOTD+Industries.MINING);
            }
            if (allIndustrySpec.getId().equals(Industries.REFINING)) {
                allIndustrySpec.setDowngrade(AoTDIndustries.SMELTING);
                allIndustrySpec.addTag(AOTD+Industries.REFINING);
            }
            if (allIndustrySpec.getId().equals(Industries.LIGHTINDUSTRY)) {
                allIndustrySpec.setDowngrade(AoTDIndustries.LIGHT_PRODUCTION);
                allIndustrySpec.addTag(AOTD+Industries.LIGHTINDUSTRY);
            }
            if (allIndustrySpec.getId().equals(Industries.HEAVYINDUSTRY)) {
                allIndustrySpec.setDowngrade(AoTDIndustries.HEAVY_PRODUCTION);
                allIndustrySpec.addTag(AOTD+Industries.HEAVYINDUSTRY);
                allIndustrySpec.setUpgrade(null);
            }
            if (allIndustrySpec.getId().equals(Industries.ORBITALWORKS)) {
                allIndustrySpec.addTag(AOTD+Industries.HEAVYINDUSTRY);
                allIndustrySpec.addTag("advanced_heavy_industry");

            }
            if (allIndustrySpec.getId().equals(Industries.WAYSTATION)) {
                allIndustrySpec.addTag("starter");
                allIndustrySpec.addTag(AOTD+Industries.WAYSTATION);
            }
        }
    }
    public void setStarterIndustriesUpgrades() {
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();

    }
    public  void spawnVeilPlanet() {
        if (!Global.getSector().getPersistentData().containsKey("$aotd_v_planet")) {
            List<StarSystemAPI> starSystems = Global.getSector().getStarSystems();
            Collections.shuffle(starSystems);
            for (StarSystemAPI starSystem : starSystems) {
                if (starSystem.getTags().contains(Tags.THEME_RUINS_MAIN)) {
                    for (PlanetAPI planet : starSystem.getPlanets()) {
                        if (planet.isStar()) continue;
                        if (planet.isMoon()) continue;
                        if (!planet.getMarket().isPlanetConditionMarketOnly()) continue;
                        if (planet.hasTag(Tags.NOT_RANDOM_MISSION_TARGET)) continue;
                        if (planet.hasTag(Tags.MISSION_ITEM)) continue;
                        if (planet.isStar()) continue;
                        if (planet.isGasGiant()) continue;

                        if (planet.getMemory().contains("$IndEvo_ArtilleryStation")) continue;
                        long seed = StarSystemGenerator.random.nextLong();
                        planet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SEED, seed);
                        planet.getMemoryWithoutUpdate().set(MemFlags.SALVAGE_SPEC_ID_OVERRIDE, "aotd_beyond_veil");
                        planet.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
                        Global.getSector().getPersistentData().put("$aotd_v_planet", planet);
                        planet.getMemoryWithoutUpdate().set("$aotd_quest_veil", true);
                        planet.setName("Veil of Knowledge");
                        break;
                    }
                }
                if (Global.getSector().getPersistentData().containsKey("$aotd_v_planet")) {
                    break;
                }
            }
        }
    }
    public  void spawnNidavleir() {
        SectorEntityToken planet = getEntityWithCriteria(null);
        GPBaseMegastructure mega = AoTDMegastructureRules.putMegastructure(planet,"aotd_nidavelir");
        planet.getStarSystem().setBaseName("Nidavelir");
        planet.addTag(Tags.NOT_RANDOM_MISSION_TARGET);
        String token = planet.getMarket().addCondition("aotd_nidavelir_complex");
        planet.getMarket().getSpecificCondition(token).setSurveyed(false);
        planet.getMarket().removeCondition(Conditions.RUINS_EXTENSIVE);
        planet.getMarket().removeCondition(Conditions.RUINS_SCATTERED);
        planet.getMarket().removeCondition(Conditions.RUINS_WIDESPREAD);
        if(!planet.getMarket().hasCondition(Conditions.RUINS_VAST)){
            planet.getMarket().addCondition(Conditions.RUINS_VAST);
        }



    }

    private  SectorEntityToken getEntityWithCriteria(String ... criteria) {
        List<StarSystemAPI> starSystems = Global.getSector().getStarSystems();
        Collections.shuffle(starSystems);
        for (StarSystemAPI starSystem : starSystems) {
            if (!starSystem.getTags().contains(Tags.THEME_CORE)&&!starSystem.getTags().contains(Tags.THEME_CORE_POPULATED)&&!starSystem.getTags().contains(Tags.THEME_CORE_UNPOPULATED)) {
                for (PlanetAPI planet : starSystem.getPlanets()) {
                    if (planet.isStar()) continue;
                    if (planet.isMoon()) continue;
                    if (!planet.getMarket().isPlanetConditionMarketOnly()) continue;
                    if (planet.hasTag(Tags.NOT_RANDOM_MISSION_TARGET)) continue;
                    if (planet.hasTag(Tags.MISSION_ITEM)) continue;
                    if (planet.isStar()) continue;
                    if (planet.isGasGiant()) continue;
                    if(planet.getRadius()<=70)continue;
                    if (planet.getMemory().contains("$IndEvo_ArtilleryStation")) continue;
                    if(planet.getMemory().contains(GPBaseMegastructure.memKey))continue;
                    if(criteria!=null){
                        for (String criterion : criteria) {
                            if(planet.getTypeId().equals(criterion))return planet;

                        }
                    }
                    return planet;
                }
            }
        }
        return null;
    }

    public  void spawnPluto() {
        PlanetAPI planet = (PlanetAPI) getEntityWithCriteria(Planets.PLANET_LAVA,Planets.PLANET_LAVA_MINOR);
        if(planet==null){
            planet = (PlanetAPI) getEntityWithCriteria();
            StarSystemAPI starSystem = planet.getStarSystem();
            planet  = starSystem.addPlanet(null, starSystem.getStar(), "Pluto", Planets.PLANET_LAVA, 0, 120, 1200, 360);

        }
        for (Map.Entry<String, String> entry : ResourceDepositsCondition.COMMODITY.entrySet()) {
            if(entry.getValue().equals(Commodities.RARE_ORE)||entry.getValue().equals(Commodities.ORE)){
                planet.getMarket().removeCondition(entry.getKey());
            }
        }
        planet.getMarket().addCondition(Conditions.VERY_HOT);
        planet.getMarket().addCondition(Conditions.RARE_ORE_ULTRARICH);
        planet.getMarket().addCondition(Conditions.ORE_ULTRARICH);

        String t = planet.getMarket().addCondition("aotd_pluto_station");
        planet.getMarket().getSpecificCondition(t).setSurveyed(false);
        SectorEntityToken token = planet.getMarket().getStarSystem().addCustomEntity("aotd_pluto_station","Pluto Mining Station","aotd_pluto_station",Factions.NEUTRAL);
        float angle = planet.getCircularOrbitAngle();
        float period = planet.getCircularOrbitPeriod(); // 270 : height
        token.setCircularOrbitPointingDown(planet,angle, planet.getRadius()+270+70,period);
        token.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.MUSIC_SET_MEM_KEY,"aotd_mega");
        MiscellaneousThemeGenerator.makeDiscoverable(token,40000,3000f);
        AoTDMisc.getStarSystemWithMegastructure("aotd_pluto_station");
    }

    public  HashMap<String,Integer>getItemCost(String reqItems){
        String[] splitedAll = reqItems.split(",");
        HashMap<String, Integer> itemsReq = new HashMap<>();
        for (String s : splitedAll) {
            String[] splitedInstance = s.split(":");
            if (Integer.parseInt(splitedInstance[1]) > 0) {
                itemsReq.put(splitedInstance[0], Integer.parseInt(splitedInstance[1]));
            }

        }

        return itemsReq;
    }
    public void RandomSetIndustryOnPlanet(String industryId, int amount, String PlanetType) {
        int count = 0;
        int max_tritachyon = 0;
        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            if (count > amount) {
                break;
            }
            if (faction.isPlayerFaction() || faction.getId().equals("luddic_church") || faction.getId().equals("luddic_path") || faction.getId().equals("pirates") || faction.getId().equals("derelicts")) {
                continue;
            }
            for (MarketAPI aiMarket : Misc.getFactionMarkets(faction.getId())) {
                if (PlanetType != null) {
                    if (aiMarket.getPlanetEntity() != null) {
                        if (aiMarket.getPlanetEntity().getTypeId().contains(PlanetType) && industryId.equals(AoTDIndustries.PURIFICATION_CENTER)) {
                            aiMarket.addIndustry(AoTDIndustries.PURIFICATION_CENTER);
                            return;
                        }
                    }

                }

                for (Industry industry : aiMarket.getIndustries()) {
                    if (industry.isIndustry()) {
                        if (industry.getId().equals("heavyindustry")
                                || industry.getId().equals("orbitalworks")
                                || industry.getId().equals("militarybase")
                                || industry.getId().equals("highcommand")
                        ) {
                            continue;
                        }
                        aiMarket.removeIndustry(industry.getId(), null, false);
                        aiMarket.addIndustry(industryId);
                        count++;
                        break;
                    }

                }

                if (aiMarket.getFactionId().equals("tritachyon")) {
                    if (max_tritachyon >= maxTriTachyonElectronics) {
                        break;
                    }
                    max_tritachyon++;

                } else {
                    break;
                }

            }
        }
    }
    public void setIndustryOnPlanet(String SystemName, String Planetname, String industryId, String removeIndustry, String potentialSwitch, boolean toImprove, String aiCore,String itemToInsert) {
        if (Global.getSector().getStarSystem(SystemName) == null) return;
        List<PlanetAPI> planets = Global.getSector().getStarSystem(SystemName).getPlanets();
        for (PlanetAPI planet : planets) {
            if (planet.getName().equals(Planetname)) {

                if (planet.getMarket() == null) continue;
                SpecialItemData data = null;
                if (removeIndustry != null&&planet.getMarket().getIndustry(removeIndustry)!=null) {

                    data = planet.getMarket().getIndustry(removeIndustry).getSpecialItem();
                    planet.getMarket().removeIndustry(removeIndustry, null, false);

                }
                if (industryId != null) {
                    planet.getMarket().addIndustry(industryId);
                    planet.getMarket().getIndustry(industryId).setImproved(toImprove);
                    planet.getMarket().getIndustry(industryId).setAICoreId(aiCore);
                    if(data!=null){
                        planet.getMarket().getIndustry(industryId).setSpecialItem(data);
                    }
                    if(itemToInsert!=null){
                        SpecialItemData daten = new SpecialItemData(itemToInsert,null);
                        planet.getMarket().getIndustry(industryId).setSpecialItem(daten);
                    }
                }


            }
        }
    }
    public static MarketAPI getMarketBasedOnName(String SystemName, String Planetname){
        if (Global.getSector().getStarSystem(SystemName) == null) return null;
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(    Global.getSector().getStarSystem(SystemName).getCenter().getContainingLocation())) {
            if(market.getName().equals(Planetname)){
                return market;
            }
        }

        return null;
    }
    private void setIndustriesOnModdedPlanets() {
        int increased_farming = 3;
        int increased_biotiocs = 0;
        int increased_reci = 0;
        boolean chooseReci = false;
        List<FactionAPI> factionAPIS = Global.getSector().getAllFactions();
        Collections.shuffle(factionAPIS);
        for (FactionAPI faction :factionAPIS) {
            if (faction.getId().equals(Factions.HEGEMONY)) continue;
            if (faction.getId().equals(Factions.TRITACHYON)) continue;
            if (faction.getId().equals(Factions.DIKTAT)) continue;
            if (faction.getId().equals(Factions.PIRATES)) continue;
            if (faction.getId().equals(Factions.LUDDIC_PATH)) continue;
            if (faction.getId().equals(Factions.LUDDIC_CHURCH)) continue;
            if (faction.getId().equals(Factions.INDEPENDENT)) continue;

            for (MarketAPI market : Misc.getFactionMarkets(faction)) {
                if (market.getPlanetEntity() == null) continue;
                if (AoDUtilis.getFoodQuantityBonus(market) >= -1) {
                    if (market.hasIndustry(Industries.FARMING)) {
                        if (increased_biotiocs + increased_reci >= increased_farming) {
                            increased_farming++;

                        } else {
                            if (chooseReci) {
                                setIndustryOnPlanet(market.getStarSystem().getBaseName(), market.getPlanetEntity().getName(), null, null, AoTDConditions.SWITCH_RECITIFICATES, false, null,null);
                                increased_reci++;
                                chooseReci = false;
                            } else {
                                setIndustryOnPlanet(market.getStarSystem().getBaseName(), market.getPlanetEntity().getName(), null, null, AoTDConditions.SWITCH_BIOTICS, false, null,null);
                                increased_biotiocs++;
                                chooseReci = true;
                            }


                        }

                    }

                }
            }

        }
    }
    public void initalizeEconomy(boolean random){
        if(!random){
            setIndustryOnPlanet("Hybrasil", "Culann", AoTDIndustries.ORBITAL_SKUNKWORK, Industries.ORBITALWORKS, null, false, Commodities.ALPHA_CORE,null);
            setIndustryOnPlanet("Aztlan", "Chicomoztoc", AoTDIndustries.ORBITAL_FLEETWORK, Industries.ORBITALWORKS, null, false, null,Items.PRISTINE_NANOFORGE);
            setIndustryOnPlanet("Hybrasil", "Culann", AoTDIndustries.PLANETARY_DEFENCE_FORCE, null, null, true, Commodities.ALPHA_CORE,null);
            setIndustryOnPlanet("Aztlan", "Chicomoztoc", AoTDIndustries.PLANETARY_DEFENCE_FORCE, null, null, false, null,null);
            setIndustryOnPlanet("Canaan", "Gilead", AoTDIndustries.ARTISANAL_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Hybrasil", "Eouchu Bres", AoTDIndustries.ARTISANAL_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Zagan", "Mazalot", AoTDIndustries.ARTISANAL_FARMING, Industries.FARMING, AoTDConditions.SWITCH_RECITIFICATES, false, null,null);
            setIndustryOnPlanet("Samarra", "Tartessus", AoTDIndustries.ARTISANAL_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Corvus", "Jangala", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Naraka", "Yama", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Westernesse", "Ailmar", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, AoTDConditions.SWITCH_BIOTICS, false, null,null);
            setIndustryOnPlanet("Kumari Kandam", "Chalcedon", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Yma", "Qaras", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, AoTDConditions.SWITCH_BIOTICS, false, null,null);
            setIndustryOnPlanet("Galatia", "Ancyra", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, null, false, null,null);
            setIndustryOnPlanet("Mayasura", "Mairaath", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, AoTDConditions.SWITCH_BIOTICS, false, null,null);
            setIndustryOnPlanet("Corvus", "Asharu", AoTDIndustries.SUBSIDISED_FARMING, Industries.FARMING, AoTDConditions.SWITCH_RECITIFICATES, false, null,null);
            setIndustryOnPlanet("Askonia", "Volturn", AoTDIndustries.PURIFICATION_CENTER, null, null, false, null,null);
            setIndustryOnPlanet("Eos Exodus", "Baetis", AoTDIndustries.SUBLIMATION, null, null, true, null,null);
            setIndustryOnPlanet("Aztlan", "Chicomoztoc", AoTDIndustries.CRYSTALIZATOR, Industries.REFINING, null, false, null,null);
            setIndustryOnPlanet("Aztlan", "Chicomoztoc",Industries.MEGAPORT, Industries.MEGAPORT, null, true, null,null);
            setIndustryOnPlanet("Canaan", "Gilead", AoTDIndustries.BENEFICATION, null, null, true, null,null);
            setIndustryOnPlanet("Askonia", "Volturn", AoTDIndustries.SUBLIMATION, Industries.MINING, null, false, Commodities.GAMMA_CORE,null);
            setIndustryOnPlanet("Hybrasil", "Culann", AoTDIndustries.ISOTOPE_SEPARATOR, Industries.REFINING, null, true, Commodities.ALPHA_CORE,null);
            setIndustryOnPlanet("Hybrasil", "Culann", Industries.HEAVYBATTERIES, Industries.HEAVYBATTERIES, null, true, Commodities.ALPHA_CORE,null);
            setIndustryOnPlanet("Thule", "Kazeron",AoTDIndustries.BENEFICATION, Industries.MINING, null, false, null,Items.MANTLE_BORE);
            setIndustryOnPlanet("Thule", "Kazeron",AoTDIndustries.TERMINUS,null, null, true, null,null);
            setIndustryOnPlanet("Aztlan", "Chicomoztoc", AoTDIndustries.TERMINUS, Industries.MINING, null, false, null,null);

        }

        setIndustriesOnModdedPlanets();

    }
}
