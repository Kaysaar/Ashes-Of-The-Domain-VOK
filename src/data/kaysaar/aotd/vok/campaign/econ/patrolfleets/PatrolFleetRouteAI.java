package data.kaysaar.aotd.vok.campaign.econ.patrolfleets;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.ModularFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BattleAutoresolverPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.RouteLocationCalculator;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseAssignmentAI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.Battle;
import data.kaysaar.aotd.vok.plugins.AoTDVokModPlugin;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatrolFleetRouteAI extends BaseAssignmentAI implements FleetActionTextProvider {
    @Override
    public String getActionText(CampaignFleetAPI fleet) {
        return "Testing Kaysaar's Tomfuckery";
    }
    protected boolean playerPursuitAutoresolveMode = false;
    public enum PatrolFleetMode {
        PATROLLING_SYSTEM,
        ORBITING,
        DEFENDING_SINGLE_LOCATION
    }

    public enum PatrolFleetState {
        TRANSIT,
        REFIT,
        RALLY,
        PATROL,
        PURSUIT
    }
    public PatrolFleetState state;
    public abstract class PatrolFleetScript implements Script {
        PatrolFleetRouteAI ai;

        public PatrolFleetScript(PatrolFleetRouteAI ai) {
            this.ai = ai;
        }
    }

    PatrolFleetMode currentMode;
    SectorEntityToken tiedTowardsEntity;
    StarSystemAPI currentStarSystem;

    public SectorEntityToken rallyPoint;

    public SectorEntityToken getRallyPoint(){
        return rallyPoint;
    }

    public void setRallyPoint(SectorEntityToken rallyPoint) {
        this.rallyPoint = rallyPoint;
    }

    boolean finishedPatrolSegment  = false;
    boolean recivedSignalForRallying = false;
    public class PatrolAIData {
        public SectorEntityToken target;
        public int staysAtTargetDays;
        public PatrolAIData(SectorEntityToken target, int staysAtTargetDays) {
            this.target = target;
            this.staysAtTargetDays = staysAtTargetDays;
        }
    }

    public PatrolFleetRouteAI(SectorEntityToken tiedTowardsEntity, CampaignFleetAPI fleet, StarSystemAPI currentStarSystem) {
        this.tiedTowardsEntity = tiedTowardsEntity;
        currentMode = PatrolFleetMode.ORBITING;
        this.currentStarSystem = currentStarSystem;
        this.fleet = fleet;
       giveOrder(PatrolFleetMode.PATROLLING_SYSTEM,tiedTowardsEntity);
    }

    ArrayList<PatrolAIData> data = new ArrayList<PatrolAIData>();
    public int indexOfTasks = 0;
    public void generatePathOfPatrol(){
        indexOfTasks = 0;
        data.clear();
        data.add(new PatrolAIData(fleet.getStarSystem().getCenter(),5));
        List<SectorEntityToken> tokens =  fleet.getStarSystem().getAllEntities();
        Collections.shuffle(tokens);
        for (SectorEntityToken token:tokens ) {
            if(token instanceof CampaignFleetAPI )continue;
            if(token.getMarket()!=null&&token.getMarket().getFaction()!=null)continue;
            if(token instanceof JumpPointAPI){
                data.add(new PatrolAIData(token,3));
            }

            if(token.getFaction()!=null&&token.getFaction().isPlayerFaction()){
                data.add(new PatrolAIData(token,1));

            }
        }
        fleet.clearAssignments();
        pickNext();

    }
    public void giveOrder(PatrolFleetMode mode, SectorEntityToken param) {
        fleet.clearAssignments();
        if (mode == PatrolFleetMode.PATROLLING_SYSTEM) {
            state = PatrolFleetState.PATROL;
                fleet.addAssignment(FleetAssignment.GO_TO_LOCATION, param, 10000, new PatrolFleetScript(this) {
                    @Override
                    public void run() {
                        ai.currentMode = PatrolFleetMode.PATROLLING_SYSTEM;
                        generatePathOfPatrol();
                    }
                });


        }
    }

    @Override
    protected void giveInitialAssignments() {
        if (currentMode == PatrolFleetMode.ORBITING) {
            fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, tiedTowardsEntity, 100000);
        }
    }

    @Override
    public void advance(float amount) {
        if(currentMode == PatrolFleetMode.PATROLLING_SYSTEM&&state==PatrolFleetState.PATROL&& fleet.getCurrentAssignment()==null&&finishedPatrolSegment){
            pickNext();
        }
        if(currentMode == PatrolFleetMode.PATROLLING_SYSTEM &&fleet.getCurrentAssignment()!=null){
            float points = computeDataForFleet(fleet).fightingStrength;
            ModularFleetAIAPI ai = (ModularFleetAIAPI) fleet.getAI();
            if(ai.getTacticalModule().getTarget()!=null&&fleet.getBattle()==null){
                if(ai.getTacticalModule().getTarget() instanceof CampaignFleetAPI){
                    float pointsCounter = computeDataForFleet((CampaignFleetAPI) ai.getTacticalModule().getTarget()).fightingStrength;
                    float simDays = 0;

                }
            }
        }

    }

    @Override
    protected void pickNext() {
        final PatrolAIData data = this.data.get(indexOfTasks);
        final float travelDays = RouteLocationCalculator.getTravelDays(fleet,data.target);
        fleet.addAssignment(FleetAssignment.GO_TO_LOCATION,data.target, (float) (travelDays+Math.random()*3f),new PatrolFleetScript(this) {

            @Override
            public void run() {
                if(data.target.isSystemCenter()){
                    ai.fleet.getMemory().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT,true);

                }
            }
        });
        fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, data.target, data.staysAtTargetDays, new PatrolFleetScript(this) {
            @Override
            public void run() {
                ai.indexOfTasks++;
                ai.fleet.getMemory().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT,false);
                if(ai.indexOfTasks>=ai.data.size()){
                    ai.indexOfTasks = 0;
                }
                ai.finishedPatrolSegment = true;
            }
        });
    }
    protected BattleAutoresolverPluginImpl.FleetAutoresolveData computeDataForFleet(CampaignFleetAPI fleet) {
        BattleAutoresolverPluginImpl.FleetAutoresolveData fleetData = new BattleAutoresolverPluginImpl.FleetAutoresolveData();
        fleetData.fleet = fleet;

        fleetData.fightingStrength = 0;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            BattleAutoresolverPluginImpl.FleetMemberAutoresolveData data = computeDataForMember(member);
            fleetData.members.add(data);
                fleetData.fightingStrength += data.strength;

        }

        return fleetData;
    }
    protected BattleAutoresolverPluginImpl.FleetMemberAutoresolveData computeDataForMember(FleetMemberAPI member) {
        BattleAutoresolverPluginImpl.FleetMemberAutoresolveData data = new BattleAutoresolverPluginImpl.FleetMemberAutoresolveData();

        data.member = member;
        ShipHullSpecAPI hullSpec = data.member.getHullSpec();
        if ((member.isCivilian() && !playerPursuitAutoresolveMode) || !member.canBeDeployedForCombat()) {
            data.strength = 0.25f;
            if (hullSpec.getShieldType() != ShieldAPI.ShieldType.NONE) {
                data.shieldRatio = 0.5f;
            }
            data.combatReady = false;
            return data;
        }

        data.combatReady = true;

//		if (data.member.getHullId().contains("astral")) {
//			System.out.println("testtesttest");
//		}

        MutableShipStatsAPI stats = data.member.getStats();

        float normalizedHullStr = stats.getHullBonus().computeEffective(hullSpec.getHitpoints()) +
                stats.getArmorBonus().computeEffective(hullSpec.getArmorRating()) * 10f;

        float normalizedShieldStr = stats.getFluxCapacity().getModifiedValue() +
                stats.getFluxDissipation().getModifiedValue() * 10f;


        if (hullSpec.getShieldType() == ShieldAPI.ShieldType.NONE) {
            normalizedShieldStr = 0;
        } else {
            float shieldFluxPerDamage = hullSpec.getBaseShieldFluxPerDamageAbsorbed();
            shieldFluxPerDamage *= stats.getShieldAbsorptionMult().getModifiedValue() * stats.getShieldDamageTakenMult().getModifiedValue();;
            if (shieldFluxPerDamage < 0.1f) shieldFluxPerDamage = 0.1f;
            float shieldMult = 1f / shieldFluxPerDamage;
            normalizedShieldStr *= shieldMult;
        }

        if (normalizedHullStr < 1) normalizedHullStr = 1;
        if (normalizedShieldStr < 1) normalizedShieldStr = 1;

        data.shieldRatio = normalizedShieldStr / (normalizedShieldStr + normalizedHullStr);
        if (member.isStation()) {
            data.shieldRatio = 0.5f;
        }

//		float strength = member.getMemberStrength();
//
//		strength *= 0.5f + 0.5f * member.getStatus().getHullFraction();
//		float captainMult = 0.5f;
//		if (member.getCaptain() != null) {
//			//captainMult = (10f + member.getCaptain().getStats().getAptitudeLevel("combat")) / 20f;
//			float captainLevel = member.getCaptain().getStats().getLevel();
//			captainMult += captainLevel / 20f;
//		}
//
//		strength *= captainMult;

        float strength = Misc.getMemberStrength(member, true, true, true);

        strength *= 0.85f + 0.3f * (float) Math.random();

        data.strength = Math.max(strength, 0.25f);

        return data;
    }
    public float getCombinedPower(){
        float currPower = 0;
        for (CampaignFleetAPI campaignFleetAPI : fleet.getStarSystem().getFleets()) {
            if(fleet.isPlayerFleet()||fleet.isStationMode())continue;
            if(fleet.getFaction().isPlayerFaction()&&fleet.getMemory().is("$aotd_patrol_fleet",true)){
                currPower+= computeDataForFleet(campaignFleetAPI).fightingStrength;
            }
        }
        return currPower;
    }

}
