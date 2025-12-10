package data.kaysaar.aotd.vok.campaign.econ.patrolfleets.manager;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.PatrolFleetType;
import data.kaysaar.aotd.vok.plugins.AoTDVokModPlugin;

import java.util.ArrayList;

public class FactionPatrolFleetManager {
    public int armadaPointsGained;
    public int armadaPointsUsed;
    String factionId;
    public static String memKey = "$aotd_fleet_manager";
    public static FactionPatrolFleetManager getInstance() {
        if(Global.getSector().getPersistentData().get(memKey)==null) {
            setInstance();
        }
        return (FactionPatrolFleetManager) Global.getSector().getPersistentData().get(memKey);
    }
    private static void setInstance(){
        FactionPatrolFleetManager manager = new FactionPatrolFleetManager(Global.getSector().getPlayerFaction().getId());
        Global.getSector().getPersistentData().put(memKey, manager);
    }
    public FactionPatrolFleetManager(String factionId) {
        this.factionId = factionId;
    }
    public ArrayList<AoTDPatrolFleetData>patrolFleets = new ArrayList<>();

    public ArrayList<AoTDPatrolFleetData> getPatrolFleets() {
        return patrolFleets;
    }
    public void addPatrolFleet(AoTDPatrolFleetData patrolFleet) {
        patrolFleets.add(patrolFleet);
    }


    public int getArmadaPoints(){
        int armadaPoints = 0;
        for (MarketAPI market : Misc.getFactionMarkets(factionId)) {
            for (Industry industry : market.getIndustries()) {
                armadaPoints +=(int)(market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).computeEffective(0f)*100);
                break;

            }
        }
        return armadaPoints+700;
    }
    public int getArmadaPointsInUse(){
        int am = 0;
        for (AoTDPatrolFleetData patrolFleet : patrolFleets) {
            am += PatrolFleetType.getAPPoints( patrolFleet.type);
        }
        return am;
    }
    public int getFleetsOfCertainType(PatrolFleetType.PatrolType type) {
        int fleets = 0;
        for (AoTDPatrolFleetData patrolFleet : patrolFleets) {
            if(patrolFleet.type.equals(type)) {
                fleets++;
            }
        }
        return fleets;
    }
    public int getAvailableArmadaPoints(){
        return getArmadaPoints() - getArmadaPointsInUse();
    }
    public CampaignFleetAPI createCopyOfFleet(CampaignFleetAPI original){
        CampaignFleetAPI newFleet = Global.getFactory().createEmptyFleet(original.getFaction().getId(),"test",true);
        if(original.getCommander()!=null){
            PersonAPI personAPI = Global.getFactory().createPerson();
            for (MutableCharacterStatsAPI.SkillLevelAPI skillLevelAPI : original.getCommander().getStats().getSkillsCopy()) {
                personAPI.getStats().setSkillLevel(skillLevelAPI.getSkill().getId(),skillLevelAPI.getLevel());
            }
            newFleet.setCommander(personAPI);
        }
        for (FleetMemberAPI fleetDatum : original.getFleetData().getMembersListCopy()) {
            FleetMemberAPI memberAPI = newFleet.getFleetData().addFleetMember(fleetDatum.getVariant().getHullVariantId());
            if(fleetDatum.getCaptain()!=null){
                PersonAPI person = fleetDatum.getCaptain();
                PersonAPI personCopy = Global.getFactory().createPerson();
                for (MutableCharacterStatsAPI.SkillLevelAPI skillLevelAPI : person.getStats().getSkillsCopy()) {
                    personCopy.getStats().setSkillLevel(skillLevelAPI.getSkill().getId(),skillLevelAPI.getLevel());
                }
                memberAPI.setCaptain(personCopy);
            }
            memberAPI.getRepairTracker().setCR(fleetDatum.getRepairTracker().getCR());

        }
    return newFleet;
    }
    public float getApproxDaysForBattle(CampaignFleetAPI fleet1, CampaignFleetAPI fleet2){
        StarSystemAPI testingGround = AoTDVokModPlugin.getTestingGroundSystem();
        CampaignFleetAPI ours = FactionPatrolFleetManager.getInstance().createCopyOfFleet(fleet1);
        CampaignFleetAPI theirs = FactionPatrolFleetManager.getInstance().createCopyOfFleet(fleet2);
        testingGround.getCenter().getContainingLocation().spawnFleet(testingGround.getCenter(),100,100,ours);
        testingGround.getCenter().getContainingLocation().spawnFleet(testingGround.getCenter(),100,100, theirs);
        BattleAPI battle = Global.getFactory().createBattle(ours,theirs);
        float seconds = 0f;

        while (!battle.isDone()){
            seconds+=Global.getSettings().getFloat("autoresolveBaseInterval");
            EveryFrameScript battleScript = (EveryFrameScript) battle;
            battleScript.advance(1f);
        }
        seconds+=3;
//                  List<CampaignFleetAPI>fleets =  Misc.findNearbyFleets(ours, fleet.getSensorRangeMod().computeEffective(0f), new Misc.FleetFilter() {
//                        @Override
//                        public boolean accept(CampaignFleetAPI curr) {
//                            if(curr.isHostileTo(fleet)){
//                                return true;
//                            }
//                            return false;
//                        }
//                    });
        ours.despawn(null,null);
        theirs.despawn(null,null);
        return seconds;
    }
}
