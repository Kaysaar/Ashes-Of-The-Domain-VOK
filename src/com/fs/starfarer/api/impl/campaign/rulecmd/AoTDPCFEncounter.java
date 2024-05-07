package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactory;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.ResearchExpeditionIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.FleetAdvanceScript;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.ResearchFleetRouteManager;
import org.lazywizard.lazylib.MathUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AoTDPCFEncounter extends BaseCommandPlugin {

    public static int waiting = 10;
    FleetInteractionDialogPluginImpl.FIDConfig config;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        String arg = params.get(0).getString(memoryMap);
        if (arg.equals("bribery")) {
            persuade(dialog, memoryMap);
            return true;
        }
        if (arg.equals("init")) {
            init(dialog, memoryMap);
            return true;
        }
        return false;
    }

    public void init(InteractionDialogAPI dialogAPI, Map<String, MemoryAPI> memoryMap) {
        dialogAPI.getOptionPanel().clearOptions();
        if(getNearby(dialogAPI.getInteractionTarget())!=null){
            dialogAPI.getTextPanel().addPara("We have been intercepted by fleet that is orbiting this planet.");
            if (dialogAPI.getInteractionTarget().getMemory().is("$aotd_r_expedition_present", true))
                initBattle(dialogAPI, memoryMap);
        }
        else{
            FactionAPI factionAPI;
            factionAPI = Global.getSector().getFaction(Factions.REMNANTS);
            if (dialogAPI.getInteractionTarget() instanceof PlanetAPI) {
                if (!dialogAPI.getInteractionTarget().getMemory().contains("$aotd_precollapse_fleet")) {
                    FleetParamsV3 params = new FleetParamsV3(
                            dialogAPI.getInteractionTarget().getLocation(),
                            Factions.REMNANTS,
                            1f,
                            FleetTypes.PATROL_LARGE,
                            MathUtils.getRandomNumberInRange(140,180), // combatPts
                            0f, // freighterPts
                            0f, // tankerPts
                            0f, // transportPts
                            0f, // linerPts
                            0f, // utilityPts
                            0f // qualityMod
                    );
                    CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

                    fleet.setName("Automatic Defence Fleet");
                    fleet.setNoFactionInName(true);
                    fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_FLEET_TYPE, "aotd_expedition");
                    if (factionAPI.getId().equals(Factions.LUDDIC_PATH)) {
                        fleet.getMemoryWithoutUpdate().set("$LP_titheAskedFor", true);
                    }
                    dialogAPI.getInteractionTarget().getMemory().set("$aotd_precollapse_fleet", fleet);

                }
            }

            initBattle(dialogAPI,memoryMap);

        }

    }

    public boolean bribery(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(500000);
        config.delegate.notifyLeave(dialog);
        return true;
    }

    public boolean persuade(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {

        if ((dialog.getInteractionTarget() instanceof PlanetAPI)) {
            FleetInteractionDialogPluginImpl conf = (FleetInteractionDialogPluginImpl) dialog.getPlugin();
            dialog.getInteractionTarget().getMemory().unset("$hasDefenders");
            dialog.getInteractionTarget().getMemory().unset("$defenderFleet");
            dialog.getInteractionTarget().getMemory().set("$defenderFleetDefeated", true);
            dialog.getInteractionTarget().getMemory().unset("$aotd_r_expedition_present");
            FleetInteractionDialogPluginImpl.FIDConfig config = (FleetInteractionDialogPluginImpl.FIDConfig) new ReflectionUtilis().getPrivateVariable("config", conf);
            conf.cleanUpBattle();
            config.delegate.notifyLeave(dialog);
            CampaignFleetAPI campaignFleetAPI = getNearby(dialog.getInteractionTarget());
            assert campaignFleetAPI != null;
            campaignFleetAPI.clearAssignments();
            campaignFleetAPI.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, Misc.getFactionMarkets(campaignFleetAPI.getFaction()).get(0).getPrimaryEntity(), 10000, "Returning to homeworld");
            dialog.getOptionPanel().clearOptions();
            dialog.getVisualPanel().finishFadeFast();
            dialog.getVisualPanel().showPlanetInfo(dialog.getInteractionTarget());
            dialog.getOptionPanel().addOption("Send a salvage team down to the facility.", "explore_PreFac");
            dialog.getOptionPanel().addOption("Leave", "defaultLeave");
        } else {
            FleetInteractionDialogPluginImpl conf = (FleetInteractionDialogPluginImpl) dialog.getPlugin();
            conf.cleanUpBattle();

            CampaignFleetAPI campaignFleetAPI = (CampaignFleetAPI) dialog.getInteractionTarget();
            for (EveryFrameScript script : campaignFleetAPI.getScripts()) {
                if(script instanceof ResearchFleetRouteManager){
                    ((ResearchFleetRouteManager) script).target.getMemory().set("$hasDefenders",false);
                    ((ResearchFleetRouteManager) script).target.getMemory().unset("$defenderFleet");
                    ((ResearchFleetRouteManager) script).target.getMemory().set("$defenderFleetDefeated", true);
                    ((ResearchFleetRouteManager) script).target.getMemory().unset("$aotd_r_expedition_present");
                }
            }
            campaignFleetAPI.clearAssignments();
            campaignFleetAPI.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, Misc.getFactionMarkets(campaignFleetAPI.getFaction()).get(0).getPrimaryEntity(), 10000, "Returning to homeworld");
            dialog.dismiss();

        }


        return true;
    }


    public boolean initBattle(InteractionDialogAPI dialog, final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        final SectorEntityToken entity = dialog.getInteractionTarget();
        CampaignFleetAPI dummy = getNearby(entity);
        if (dummy == null) {
            dummy = dialog.getInteractionTarget().getMemory().getFleet("$aotd_precollapse_fleet");

        }
        final CampaignFleetAPI defenders = dummy;
        if (defenders == null) return false;
        if(defenders.getFaction().getId().equals(Factions.REMNANTS)){
            defenders.getMemoryWithoutUpdate().set("$knowsWhoPlayerIs",false);

        }
        if(defenders.isEmpty()){
            defenders.deflate();
            dialog.getOptionPanel().addOption("Send a salvage team down to the facility", "explore_PreFac");
            dialog.getOptionPanel().addOption("Leave", "defaultLeave");
            return true;
        }
        dialog.setInteractionTarget(defenders);
        defenders.getMemoryWithoutUpdate().set("$LP_titheAskedFor", true);
        if (defenders.isEmpty()) {
            FireBest.fire(null, dialog, memoryMap, "BeginSalvage");
            return true;
        }


        config = new FleetInteractionDialogPluginImpl.FIDConfig();
        config.leaveAlwaysAvailable = true;
        config.showCommLinkOption = true;
        //config.showEngageText = false;
        if (defenders.getFaction().equals(Factions.INDEPENDENT)) {
            config.showCommLinkOption = true;
        }
        config.showFleetAttitude = false;
        //config.alwaysAttackVsAttack = true;
        config.pullInStations = false;
        config.impactsAllyReputation = false;
        config.showWarningDialogWhenNotHostile = false;
        config.impactsEnemyReputation = false;
        config.pullInEnemies = false;
        //config.pullInAllies = false;
        config.noSalvageLeaveOptionText = Misc.ucFirst("Continiue");
        config.dismissOnLeave = false;
        config.printXPToDialog = true;
        config.salvageRandom = new Random();
        if(defenders.getFaction().getId().equals(Factions.REMNANTS)){
            defenders.getMemoryWithoutUpdate().set("$knowsWhoPlayerIs",false);

        }
        final FleetInteractionDialogPluginImpl plugin = new FleetInteractionDialogPluginImpl(config);

        final InteractionDialogPlugin originalPlugin = dialog.getPlugin();
        config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {

            @Override
            public void notifyLeave(InteractionDialogAPI dialog) {

                dialog.setPlugin(originalPlugin);
                dialog.setInteractionTarget(entity);//Global.getSector().getCampaignUI().clearMessages();

                if (plugin.getContext() instanceof FleetEncounterContext) {
                    FleetEncounterContext context = (FleetEncounterContext) plugin.getContext();
                    if (context.didPlayerWinMostRecentBattleOfEncounter()) {
                        dialog.getInteractionTarget().getMemory().unset("$aotd_r_expedition_present");


                        SalvageGenFromSeed.SDMParams p = new SalvageGenFromSeed.SDMParams();
                        p.entity = entity;
                        p.factionId = defenders.getFaction().getId();

                        SalvageGenFromSeed.SalvageDefenderModificationPlugin plugin = Global.getSector().getGenericPlugins().pickPlugin(
                                SalvageGenFromSeed.SalvageDefenderModificationPlugin.class, p);
                        if (plugin != null) {
                            plugin.reportDefeated(p, entity, defenders);
                        }
                        FireBest.fire("pre_fix_salvage", dialog, memoryMap, "BeatDefendersContinue");
                        entity.removeScriptsOfClass(FleetAdvanceScript.class);
                        dialog.getOptionPanel().clearOptions();
                        dialog.getVisualPanel().finishFadeFast();
                        dialog.getVisualPanel().showPlanetInfo(dialog.getInteractionTarget());
                        defenders.deflate();
                        dialog.getOptionPanel().addOption("Send a salvage team down to the facility", "explore_PreFac");
                        dialog.getOptionPanel().addOption("Leave", "defaultLeave");

                    } else {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                bcc.aiRetreatAllowed = false;
            }
        };

        dialog.setPlugin(plugin);
        plugin.init(dialog);


        return true;
    }

    public static CampaignFleetAPI getNearby(SectorEntityToken target) {

        CampaignFleetAPI player = Global.getSector().getPlayerFleet();
        for (CampaignFleetAPI fleet : target.getContainingLocation().getFleets()) {
            if ((fleet.hasTag("aotd_expedition"))) {
                //Global.getLogger(this.getClass()).info("Checking vulture " + fleet.getNameWithFaction());
                if (fleet.getBattle() != null) {
                    continue;
                }

                if (!player.isVisibleToSensorsOf(fleet)) {
                    //Global.getLogger(this.getClass()).info("Cannot see");
                    continue;
                }

                // check join range
                if (MathUtils.getDistance(target, fleet) > Misc.getBattleJoinRange()) {
                    //Global.getLogger(this.getClass()).info("Out of range");
                    continue;
                }

                // don't pick a fight with a much stronger player
                CampaignFleetAIAPI ai = (CampaignFleetAIAPI) fleet.getAI();
                if (ai == null) continue;
                CampaignFleetAIAPI.EncounterOption option = ai.pickEncounterOption(null, player, true);
                if (option != CampaignFleetAIAPI.EncounterOption.ENGAGE)
                    continue;

                return fleet;
            }
        }
        return null;
    }

}
