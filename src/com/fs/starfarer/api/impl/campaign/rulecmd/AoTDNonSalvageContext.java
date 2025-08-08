package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BattleAPI;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.ai.ModularFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.TOffAlarm;
import com.fs.starfarer.api.impl.campaign.events.BaseEventPlugin;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;

import java.util.List;

public class AoTDNonSalvageContext extends FleetEncounterContext {
    @Override
    public void applyAfterBattleEffectsIfThereWasABattle() {
            if (!hasWinnerAndLoser() || !engagedInHostilities) {
            for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                member.getStatus().resetAmmoState();
            }

            if (noHarryBecauseOfStation && battle != null) {
                List<CampaignFleetAPI> otherSide = battle.getNonPlayerSide();
                CampaignFleetAPI fleet = battle.getPrimary(otherSide);
                if (fleet.getAI() != null &&
                        !fleet.getAI().isCurrentAssignment(FleetAssignment.STANDING_DOWN)) {
                    fleet.getAI().addAssignmentAtStart(FleetAssignment.STANDING_DOWN, fleet, 0.5f + 0.5f * (float) Math.random(), null);
                }
            }

            //Global.getSector().setLastPlayerBattleTimestamp(Global.getSector().getClock().getTimestamp());
            Global.getSector().getPlayerFleet().setNoEngaging(3f);
            return;
        }

        gainXP();
        addPotentialOfficer();

//		CampaignFleetAPI winner = getWinnerData().getFleet();
//		CampaignFleetAPI loser = getLoserData().getFleet();
//		List<CampaignFleetAPI> winners = battle.getSideFor(getWinnerData().getFleet());
//		List<CampaignFleetAPI> losers = battle.getSideFor(getLoserData().getFleet());
        List<CampaignFleetAPI> winners = battle.getSnapshotSideFor(getWinnerData().getFleet());
        List<CampaignFleetAPI> losers = battle.getSnapshotSideFor(getLoserData().getFleet());
        if (winners == null || losers == null) return;

        for (CampaignFleetAPI loser : losers) {
            for (FleetMemberAPI member : loser.getFleetData().getMembersListCopy()) {
                member.getStatus().resetAmmoState();
            }

            loser.getVelocity().set(0, 0);
            if (loser.isPlayerFleet()) continue;
            if (loser.isPlayerFleet()) loser.setNoEngaging(3f);


        }
        for (CampaignFleetAPI winner : winners) {
            for (FleetMemberAPI member : winner.getFleetData().getMembersListCopy()) {
                member.getStatus().resetAmmoState();
            }

            winner.getVelocity().set(0, 0);
            if (winner.isPlayerFleet()) continue;
            if (winner.isPlayerFleet()) winner.setNoEngaging(3f);

        }

        if (battle.isPlayerSide(winners)) {
            for (CampaignFleetAPI fleet : battle.getPlayerSide()) {
                if (fleet.isPlayerFleet()) continue;

                Misc.forgetAboutTransponder(fleet);
            }
        }

        battle.setPlayerInvolvementFraction(computePlayerContribFraction());

        if (!isAutoresolve && engagedInActualBattle) {
            reportCoreBattleOccured(battle.getPrimary(winners),battle);
            Global.getSector().reportBattleFinished(battle.getPrimary(winners), battle);
        }

        CampaignFleetAPI largestWinner = battle.getPrimary(winners);
        for (CampaignFleetAPI loser : losers) {
            if (loser.getFleetData().getMembersListCopy().isEmpty()) {
                //Global.getSector().reportFleetDewspawned(loser, FleetDespawnReason.DESTROYED_BY_FLEET, winner);
                loser.despawn(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE, battle);
            }
        }

        for (CampaignFleetAPI winner : winners) {
            if (winner.getFleetData().getMembersListCopy().isEmpty()) {
                //Global.getSector().reportFleetDewspawned(loser, FleetDespawnReason.DESTROYED_BY_FLEET, winner);
                winner.despawn(CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE, battle);
            }
        }

        for (CampaignFleetAPI enemy : battle.getBothSides()) {
            if (enemy.getAI() instanceof ModularFleetAIAPI) {
                ModularFleetAIAPI mAI = (ModularFleetAIAPI) enemy.getAI();
                mAI.getTacticalModule().forceTargetReEval();
            }
        }

    }
    public void reportCoreBattleOccured(CampaignFleetAPI primaryWinner, BattleAPI battle){
        if (!battle.isPlayerInvolved()) return;


        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        if (!playerFleet.isValidPlayerFleet()) {
            float fp = 0;
            float crew = 0;
            for (FleetMemberAPI member : Misc.getSnapshotMembersLost(playerFleet)) {
                fp += member.getFleetPointCost();
                crew = member.getMinCrew();
            }
            SharedData  shared = SharedData.getData();
            shared.setPlayerPreLosingBattleFP(fp);
            shared.setPlayerPreLosingBattleCrew(crew);
            shared.setPlayerLosingBattleTimestamp(Global.getSector().getClock().getTimestamp());
        }


        for (final CampaignFleetAPI otherFleet : battle.getNonPlayerSideSnapshot()) {
            if (otherFleet.hasScriptOfClass(TOffAlarm.class)) continue;
            MemoryAPI memory = otherFleet.getMemoryWithoutUpdate();
            //if (!playerFleet.isTransponderOn()) {
            //if (!memory.getBoolean(MemFlags.MEMORY_KEY_LOW_REP_IMPACT)) {
            Misc.setFlagWithReason(memory, MemFlags.MEMORY_KEY_MAKE_HOSTILE_WHILE_TOFF, "battle", true, 7f + (float) Math.random() * 7f);
            //}
            //}

            if (!otherFleet.getMemoryWithoutUpdate().getBoolean(MemFlags.MEMORY_KEY_NO_REP_IMPACT)) {
                if (!otherFleet.getMemoryWithoutUpdate().getBoolean(MemFlags.MEMORY_KEY_LOW_REP_IMPACT) ||
                        otherFleet.getMemoryWithoutUpdate().getBoolean(MemFlags.SPREAD_TOFF_HOSTILITY_IF_LOW_IMPACT)) {
                    otherFleet.addScript(new TOffAlarm(otherFleet));
                }
            }


            float fpLost = Misc.getSnapshotFPLost(otherFleet);

            List<MarketAPI> markets = Misc.findNearbyLocalMarkets(otherFleet,
                    Global.getSettings().getFloat("sensorRangeMax") + 500f,
                    new BaseEventPlugin.MarketFilter() {
                        public boolean acceptMarket(MarketAPI market) {
                            //return market.getFaction().isAtWorst(otherFleet.getFaction(), RepLevel.COOPERATIVE);
                            return market.getFaction() != null && market.getFaction() == otherFleet.getFaction();
                        }
                    });

            for (MarketAPI market : markets) {
                MemoryAPI mem = market.getMemoryWithoutUpdate();
                float expire = fpLost;
                if (mem.contains(MemFlags.MEMORY_KEY_PLAYER_HOSTILE_ACTIVITY_NEAR_MARKET)) {
                    expire += mem.getExpire(MemFlags.MEMORY_KEY_PLAYER_HOSTILE_ACTIVITY_NEAR_MARKET);
                }
                if (expire > 180) expire = 180;
                if (expire > 0) {
                    mem.set(MemFlags.MEMORY_KEY_PLAYER_HOSTILE_ACTIVITY_NEAR_MARKET, true, expire);
                }
            }
        }

        Misc.getSimulatorPlugin().reportPlayerBattleOccurred(primaryWinner, battle);
    }
}
