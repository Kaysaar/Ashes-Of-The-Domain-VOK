package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.AbyssalLightEntityPlugin;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AoTDDwellerCMD extends DwellerCMD{
    @Override
    protected boolean engageFleet(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap, MemoryAPI memory, DwellerStrength str, Random random) {
        CampaignFleetAPI fleet = createDwellerFleet(str, random);
        if (fleet == null) return false;

        CampaignFleetAPI pf = Global.getSector().getPlayerFleet();
        fleet.setContainingLocation(pf.getContainingLocation());

        final SectorEntityToken entity = dialog.getInteractionTarget();

        dialog.setInteractionTarget(fleet);

        Global.getSector().getCampaignUI().restartEncounterMusic(fleet);

        FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();

        config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
            public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
                if (!(dialog.getInteractionTarget() instanceof CampaignFleetAPI)) return;

                float mult = context.computePlayerContribFraction();

                CampaignFleetAPI fleet = (CampaignFleetAPI) dialog.getInteractionTarget();

                FleetEncounterContextPlugin.DataForEncounterSide data = context.getDataFor(fleet);
                List<FleetMemberAPI> losses = new ArrayList<FleetMemberAPI>();
                for (FleetEncounterContextPlugin.FleetMemberData fmd : data.getOwnCasualties()) {
                    losses.add(fmd.getMember());
                }

                float min = 0f;
                float max = 0f;
                boolean gotGuaranteed = false;
                for (FleetMemberAPI member : losses) {
                    if (member.getHullSpec().hasTag(Tags.DWELLER)) {
                        String key = "substrate_";
                        float [] sDrops = Misc.getFloatArray(key + member.getHullSpec().getHullId());
                        if (sDrops == null) {
                            sDrops = Misc.getFloatArray(key + member.getHullSpec().getHullSize().name());
                        }
                        if (sDrops == null) continue;

                        min += sDrops[0];
                        max += sDrops[1];

                        String hullId = member.getHullSpec().getRestoredToHullId();
                        String defeatedKey = "$defeatedDweller_" + hullId;
                        boolean firstTime = !Global.getSector().getPlayerMemoryWithoutUpdate().getBoolean(defeatedKey);
                        Global.getSector().getPlayerMemoryWithoutUpdate().set(defeatedKey, true);
                        if (firstTime && !gotGuaranteed) {
                            List<String> drops = GUARANTEED_FIRST_TIME_ITEMS.get(hullId);
                            for (String itemId : drops) {
                                SpecialItemData sid = new SpecialItemData(itemId, null);
                                boolean add = firstTime && salvage.getQuantity(CargoAPI.CargoItemType.SPECIAL, sid) <= 0;
                                if (add) {
                                    salvage.addItems(CargoAPI.CargoItemType.SPECIAL, sid, 1);
                                    gotGuaranteed = true;
                                }
                            }
                        }
                    }
                }

                long seed = Misc.getSalvageSeed(entity);
                Random random = Misc.getRandom(seed, 50);
                int substrate = 0;
                if (min + max < 1f) {
                    if (random.nextFloat() < (min + max) / 2f) {
                        substrate = 1;
                    }
                } else {
                    substrate = (int) Math.round(min + (max - min) * random.nextFloat());
                }

                if (substrate > 0) {
                    salvage.addItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(Items.SHROUDED_SUBSTRATE, null), substrate);
                }
            }

            public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                bcc.aiRetreatAllowed = false;
                bcc.fightToTheLast = true;
                bcc.objectivesAllowed = false;
                bcc.enemyDeployAll = true;

                // despawn the light here - the salvage gen method is only called if the player won
                // but want to despawn the light after any fight, regardless
                if (entity.getCustomPlugin() instanceof AbyssalLightEntityPlugin) {
                    AbyssalLightEntityPlugin plugin = (AbyssalLightEntityPlugin) entity.getCustomPlugin();
                    plugin.despawn(AbyssalLightEntityPlugin.DespawnType.FADE_OUT);
                }
            }
        };

        config.alwaysAttackVsAttack = true;
        //config.alwaysPursue = true;
        config.alwaysHarry = true;
        config.showTransponderStatus = false;
        //config.showEngageText = false;
        config.lootCredits = false;

        config.showCommLinkOption = false;
        config.showEngageText = false;
        config.showFleetAttitude = false;
        config.showTransponderStatus = false;
        config.showWarningDialogWhenNotHostile = false;
        config.impactsAllyReputation = false;
        config.impactsEnemyReputation = false;
        config.pullInAllies = false;
        config.pullInEnemies = false;
        config.pullInStations = false;

        config.showCrRecoveryText = false;
        config.firstTimeEngageOptionText = "\"Battle stations!\"";
        config.afterFirstTimeEngageOptionText = "Move in to re-engage";

        if (str == DwellerStrength.LOW) {
            config.firstTimeEngageOptionText = null;
            config.leaveAlwaysAvailable = true;
        } else {
            config.leaveAlwaysAvailable = true; // except for first engagement
            config.noLeaveOptionOnFirstEngagement = true;
        }
        //config.noLeaveOption = true;

//		config.noSalvageLeaveOptionText = "Continue";

//		config.dismissOnLeave = false;
//		config.printXPToDialog = true;

        long seed = Misc.getSalvageSeed(entity);
        config.salvageRandom = Misc.getRandom(seed, 75);

        Global.getSector().getPlayerMemoryWithoutUpdate().set("$encounteredDweller", true);
        Global.getSector().getPlayerMemoryWithoutUpdate().set("$encounteredMonster", true);
        Global.getSector().getPlayerMemoryWithoutUpdate().set("$encounteredWeird", true);

        final FleetInteractionDialogPluginImpl plugin = new FleetInteractionDialogPluginImpl(config);

        //final InteractionDialogPlugin originalPlugin = dialog.getPlugin();

        dialog.setPlugin(plugin);
        plugin.init(dialog);


        return true;
    }
}
