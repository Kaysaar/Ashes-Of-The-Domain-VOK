package data.kaysaar.aotd.vok.campaign.econ.patrolfleets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.plugins.impl.CoreAutofitPlugin;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.inflaters.AoTDFleetInflater;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.manager.FactionPatrolFleetManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AoTDPatrolFleetData implements FleetEventListener {
    CampaignFleetAPI fleet;

    public void setExpectedVesselsInFleet(LinkedHashMap<String, Integer> expectedVesselsInFleet) {
        this.expectedVesselsInFleet = expectedVesselsInFleet;
    }
   public PatrolFleetType.PatrolType type = PatrolFleetType.PatrolType.LARGE;

    LinkedHashMap<String,Integer> expectedVesselsInFleet = new LinkedHashMap<>(); // we need to know what hulls were in fleet;
    public void addExpectedVessel(String expectedVessel) {
        if(expectedVesselsInFleet.containsKey(expectedVessel)) {
            int curr = expectedVesselsInFleet.get(expectedVessel);
            expectedVesselsInFleet.put(expectedVessel, curr+1);
        }
        else{
            expectedVesselsInFleet.put(expectedVessel, 1);
        }
    }

    public void setType(PatrolFleetType.PatrolType type) {
        this.type = type;
    }

    public void removeExpectedVessel(String expectedVessel) {
        int curr = expectedVesselsInFleet.get(expectedVessel);
        if(curr<=1){
            expectedVesselsInFleet.remove(expectedVessel);
        }
        else{
            expectedVesselsInFleet.put(expectedVessel, curr-1);
        }
    }

    public LinkedHashMap<String,Integer> getExpectedVesselsInFleet() {
        return expectedVesselsInFleet;
    }
    public int getTotalExpectedVessels(){
        int am = 0;
        for (Integer value : expectedVesselsInFleet.values()) {
            am += value;
        }
        return am;
    }
    PatrolFleetRouteAI routeAI;
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(fleet!=null){
            this.fleet.setName(name);

        }
    }

    public AoTDPatrolFleetData( String name){
        expectedVesselsInFleet = new LinkedHashMap<>();
        this.setName(name);


    }
    public void init(PersonAPI admiral){
        MarketAPI market = Misc.getPlayerMarkets(false).get(0);
        createPatrol(PatrolFleetType.PatrolType.LARGE,name,Factions.PLAYER,market,null,admiral);

        market.getContainingLocation().addEntity(fleet);
        fleet.setLocation(market.getPrimaryEntity().getLocation().x, market.getPrimaryEntity().getLocation().y);
        fleet.setTransponderOn(true);

        fleet.getFleetData().setSyncNeeded();
        fleet.getFleetData().syncIfNeeded();
        routeAI = new PatrolFleetRouteAI(market.getPrimaryEntity(),fleet,market.getStarSystem());
        fleet.addScript(routeAI);
        fleet.getMemoryWithoutUpdate().set("$aotd_patrol_fleet",true);
        fleet.getMemoryWithoutUpdate().set("$isPatrol",true);
        fleet.getAI().setActionTextProvider(routeAI);
        Global.getSector().getListenerManager().addListener(this);
        FactionPatrolFleetManager.getInstance().addPatrolFleet(this);
    }
    public void giveNewOrders(PatrolFleetRouteAI.PatrolFleetMode order,SectorEntityToken target){

    }
    public int getCurrentlyMonthlyUpkeep(){
        if(fleet!=null) {
            int minCrew = 0;
            for (FleetMemberAPI fleetMemberAPI : fleet.getFleetData().getMembersListCopy()) {
                minCrew+= (int) fleetMemberAPI.getMinCrew();
            }
            return minCrew*10;

        }
        else {
            return 0;
        }
    }
    public int getEstimatedMonthlyUpkeep(){
        int am = 0;
        for (Map.Entry<String, Integer> entry : expectedVesselsInFleet.entrySet()) {
            ShipHullSpecAPI spec = Global.getSettings().getHullSpec(entry.getKey());
            am += (int) (spec.getMinCrew()*entry.getValue()*10);
        }
        return am;
    }
    public int getEstimatedDP(){
        int am = 0;
        for (Map.Entry<String, Integer> entry : expectedVesselsInFleet.entrySet()) {
            ShipHullSpecAPI spec = Global.getSettings().getHullSpec(entry.getKey());
            am += (int) (spec.getFleetPoints()*entry.getValue());
        }
        return am;
    }
    public CampaignFleetAPI createPatrol(PatrolFleetType.PatrolType type, String name, String factionId, MarketAPI market, Vector2f locInHyper, PersonAPI officer) {
        fleet =FleetFactoryV3.createEmptyFleet(Factions.PLAYER, FleetTypes.TASK_FORCE,market);
        fleet.getFleetData().setOnlySyncMemberLists(true);


        Misc.getSalvageSeed(fleet);
        for (Map.Entry<String, Integer> ship : expectedVesselsInFleet.entrySet()) {
            for (int i = 0; i < ship.getValue(); i++) {
                String variantId = AoTDMisc.getVaraint(Global.getSettings().getHullSpec(ship.getKey()));
                FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP,variantId);
                int index = 0;
                for (String fittedWing : member.getVariant().getFittedWings()) {
                    member.getVariant().setWingId(index,null);
                    index++;
                }
                member.getVariant().setSource(VariantSource.REFIT);
                fleet.getFleetData().addFleetMember(member);

            }


        }
        fleet.getFleetData().sort();

        fleet.getFleetData().ensureHasFlagship();
        fleet.forceSync();


        DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
        p.quality = 1f;
        p.persistent = true;
        p.seed = Misc.random.nextLong();
        p.mode = FactionAPI.ShipPickMode.PRIORITY_ONLY;
        p.timestamp = Global.getSector().getClock().getTimestamp();
        p.allWeapons = false;
        p.factionId = Factions.PLAYER;

        AoTDFleetInflater inflater = new AoTDFleetInflater(p);
        fleet.setInflater(inflater);
        inflater.inflate(fleet);
        fleet.setInflated(true);
        fleet.setNoAutoDespawn(true);
        fleet.getFleetData().setOnlySyncMemberLists(false);
        fleet.getFleetData().sort();
        fleet.setName(name);
        fleet.setNoFactionInName(true);
        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        for (FleetMemberAPI member : members) {
            member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
        }

        float requestedPoints = fleet.getFleetPoints();
        float actualPoints = fleet.getFleetPoints();

        Misc.setSpawnFPMult(fleet, actualPoints / Math.max(1f, requestedPoints));
        return fleet;
    }
    public void performUpdate(){

    }
    public CampaignFleetAPI getFleet() {
        return fleet;
    }

    @Override
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
            if(fleet.getId().equals(this.fleet.getId())){
            this.fleet = null;

        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {

    }
}
