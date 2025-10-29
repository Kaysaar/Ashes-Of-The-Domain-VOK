package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.AbyssalLightEntityPlugin;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.ShroudProjectMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AoTDDwellerCMD extends DwellerCMD{
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        CampaignFleetAPI pf = Global.getSector().getPlayerFleet();
        CargoAPI cargo = pf.getCargo();

        final SectorEntityToken entity = dialog.getInteractionTarget();
        long seed = Misc.getSalvageSeed(entity);
        Random random = Misc.getRandom(seed, 11);
        //random = new Random();

        String action = params.get(0).getString(memoryMap);

        MemoryAPI memory = memoryMap.get(MemKeys.LOCAL);
        if (memory == null) return false; // should not be possible unless there are other big problems already

        if ("smallFleet".equals(action)) {
            return engageFleet(dialog, memoryMap, memory, DwellerStrength.LOW, random);
        } else if ("mediumFleet".equals(action)) {
            return engageFleet(dialog, memoryMap, memory, DwellerStrength.MEDIUM, random);
        } else if ("largeFleet".equals(action)) {
            return engageFleet(dialog, memoryMap, memory, DwellerStrength.HIGH, random);
        } else if ("hugeFleet".equals(action)) {
            return engageFleet(dialog, memoryMap, memory, DwellerStrength.EXTREME, random);
        } else if ("showWeaponPicker".equals(action)) {
            showWeaponPicker(dialog, memoryMap);
            return true;
        } else if ("unlockHullmod".equals(action)) {
            unlockHullmod(dialog, memoryMap);
            return true;
        } else if ("checkForBetterSurvey".equals(action)) {
            checkForBetterSurvey(dialog,memoryMap,memory);
            return true;
        }
        return false;
    }
    public void checkForBetterSurvey(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap, MemoryAPI memory){
        if(ShroudProjectMisc.getBoolean(ShroudProjectMisc.hasAbilityToSummonGreatFleet)){
            dialog.getOptionPanel().addOption("Use a Shroud Beacon to lure them directly to us","abyssalLight_ultrasensors", Color.ORANGE,null);
            dialog.getOptionPanel().addOptionConfirmation("abyssalLight_ultrasensors","Are you sure about this?","Proceed","Abort");
        }
    }
    public static CampaignFleetAPI createDwellerFleet(DwellerStrength str, Random random) {
        CampaignFleetAPI f = Global.getFactory().createEmptyFleet(Factions.DWELLER, "Manifestation", true);

        FactionAPI faction = Global.getSector().getFaction(Factions.DWELLER);
        String typeKey = FleetTypes.PATROL_SMALL;
        if (str == DwellerStrength.MEDIUM) typeKey = FleetTypes.PATROL_MEDIUM;
        if (str == DwellerStrength.HIGH) typeKey = FleetTypes.PATROL_LARGE;
        if (str == DwellerStrength.EXTREME) typeKey = FleetTypes.PATROL_LARGE;
        f.setName(faction.getFleetTypeName(typeKey));
        if(str == DwellerStrength.EXTREME){
            f.setName("Abyssal Incursion");
        }
        f.setInflater(null);

        if (str == DwellerStrength.LOW) {
            addShips(f, 6, 8, random, ShipRoles.DWELLER_TENDRIL);
            addShips(f, 1, 1, random, ShipRoles.DWELLER_EYE);
            addShips(f, 1, 2, random, ShipRoles.DWELLER_MAELSTROM);
        } else if (str == DwellerStrength.MEDIUM) {
            addShips(f, 9, 12, random, ShipRoles.DWELLER_TENDRIL);
            int eyes = addShips(f, 1, 1, random, ShipRoles.DWELLER_EYE);
            addShips(f, 2 - eyes, 3 - eyes, random, ShipRoles.DWELLER_MAELSTROM);
            addShips(f, 1, 1, random, ShipRoles.DWELLER_MAW);
        } else if (str == DwellerStrength.HIGH) {
            addShips(f, 11, 14, random, ShipRoles.DWELLER_TENDRIL);
            int eyes = addShips(f, 2, 3, random, ShipRoles.DWELLER_EYE);
            addShips(f, 3 - eyes, 5 - eyes, random, ShipRoles.DWELLER_MAELSTROM);
            addShips(f, 1, 1, random, ShipRoles.DWELLER_MAW);
        } else if (str == DwellerStrength.EXTREME) {
            addShips(f, 14, 18, random, ShipRoles.DWELLER_TENDRIL);
            int eyes = addShips(f, 3, 5, random, ShipRoles.DWELLER_EYE);
            addShips(f, 3 - eyes, 9 - eyes, random, ShipRoles.DWELLER_MAELSTROM);
            addShips(f, 2, 4, random, ShipRoles.DWELLER_MAW);
        }

        f.getFleetData().setSyncNeeded();
        f.getFleetData().syncIfNeeded();
        f.getFleetData().sort();

        for (FleetMemberAPI curr : f.getFleetData().getMembersListCopy()) {
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());

            // tag is added to ships now
//			ShipVariantAPI v = curr.getVariant().clone();
//			v.addTag(Tags.LIMITED_TOOLTIP_IF_LOCKED);
//			curr.setVariant(v, false, false);
        }


//		f.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN,
//				   			new DwellerFIDConfig());
//		f.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);

        // required for proper music track to play, see: DwellerCMD
        f.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_HOSTILE, true);

//		//f.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_ALWAYS_PURSUE, true);
//		f.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_ALLOW_LONG_PURSUIT, true);
        f.getMemoryWithoutUpdate().set(MemFlags.MAY_GO_INTO_ABYSS, true);

        return f;
    }

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

                float chanceToFail = 0.8f;
                if(ShroudProjectMisc.getBoolean(ShroudProjectMisc.hasBetterContainmentMethods)){
                    chanceToFail = 0.4f;
                }
                int substrate = 0;
                long seed = Misc.getSalvageSeed(entity);
                Random random = Misc.getRandom(seed, 50);
                for (FleetMemberAPI member : losses) {
                    if (member.getHullSpec().hasTag(Tags.DWELLER)) {
                        int amount = member.getHullSpec().getHullSize().ordinal()-2;
                        if(ShroudProjectMisc.getBoolean(ShroudProjectMisc.hasBetterContainmentMethods)){
                            amount++;
                        }
                        float ch = random.nextFloat();
                        if(ch>=chanceToFail){
                            substrate+=amount;
                        }

                    }
                }


                if (substrate > 0) {
                    salvage.addItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData("aotd_"+Items.SHROUDED_SUBSTRATE, null), substrate);
                }
                salvage.removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.SHROUDED_SUBSTRATE,null),10000);
                salvage.removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.SHROUDED_LENS,null),10000);

                salvage.removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.SHROUDED_MANTLE,null),10000);

                salvage.removeItems(CargoAPI.CargoItemType.SPECIAL,new SpecialItemData(Items.SHROUDED_THUNDERHEAD,null),10000);

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
