package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.intel.AoTDCommIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.eventfactors.onetime.DatabankSellFactor;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDAIStance;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.attitude.FactionResearchAttitudeData;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResearchSell extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected PersonAPI person;
    protected FactionAPI faction;
    protected FactionResearchAttitudeData attitudeData;
    protected boolean buysDatabanks;
    protected float valueMult;
    protected float repMult;

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        memory = getEntityMemory(memoryMap);

        entity = dialog.getInteractionTarget();
        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        person = dialog.getInteractionTarget().getActivePerson();
        faction = person.getFaction();
        if (command.equals("personCanAcceptDbs")) {
            return personCanAcceptDbs();
        }
        try {
            attitudeData = AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).getAttitudeData();
        } catch (Exception e) {
            attitudeData = new FactionResearchAttitudeData(faction.getId(), AoTDAIStance.DEFAULT, 0.2f, 0.5f, null, new ArrayList<String>());
        }
        buysDatabanks = !faction.isPlayerFaction();
        valueMult = attitudeData.getDatabankCashMultiplier();
        repMult = attitudeData.getDatabankRepMultiplier();

        if (command.equals("selectDatabanks")) {
            selectDatabanks();
        } else if (command.equals("playerHasDbs")) {
            return playerHasDbs();
        } else if (command.equals("generateInitResponse")) {
            return generateInitResponse(attitudeData);

        } else if (command.equals("generateAfterResponse")) {
            return generateAfterResponse(attitudeData);

        }

        return true;
    }

    public boolean generateAfterResponse(FactionResearchAttitudeData data) {
        dialog.getTextPanel().addPara(data.getResponseAfter());
        return true;
    }

    public boolean generateInitResponse(FactionResearchAttitudeData data) {
        dialog.getOptionPanel().clearOptions();
        dialog.getTextPanel().addPara(data.getInitResponse());
        dialog.getOptionPanel().addOption(data.getDialogOptionSelect(), "databanks_selectDbs");
        dialog.getOptionPanel().addOption(data.getDialogOptionDissmay(), "databanks_neverMind");
        return true;
    }

    protected boolean personCanAcceptDbs() {
        if (person == null || !buysDatabanks) return false;
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction)==null)return false;
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).getAttitudeData()==null)return false;

        return Ranks.POST_BASE_COMMANDER.equals(person.getPostId()) ||
                Ranks.POST_STATION_COMMANDER.equals(person.getPostId()) ||
                Ranks.POST_ADMINISTRATOR.equals(person.getPostId()) ||
                Ranks.POST_OUTPOST_COMMANDER.equals(person.getPostId());
    }

    protected void selectDatabanks() {
        CargoAPI copy = Global.getFactory().createCargo(false);
        //copy.addAll(cargo);
        //copy.setOrigSource(playerCargo);
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getId().equals("research_databank")) {
                copy.addFromStack(stack);
            }
        }
        copy.sort();

        final float width = 310f;
        dialog.showCargoPickerDialog("Select Research databanks to turn in", "Confirm", "Cancel", true, width, copy, new CargoPickerListener() {
            public void pickedCargo(CargoAPI cargo) {
                if (cargo.isEmpty()) {
                    cancelledCargoSelection();
                    return;
                }

                cargo.sort();
                for (CargoStackAPI stack : cargo.getStacksCopy()) {
                    playerCargo.removeItems(stack.getType(), stack.getData(), stack.getSize());
                    if (stack.isCommodityStack()) { // should be always, but just in case
                        int num = (int) stack.getSize();
                        AddRemoveCommodity.addCommodityLossText(stack.getCommodityId(), num, text);

                        String key = "$aotd_turned_db" + stack.getCommodityId();
                        int turnedIn = faction.getMemoryWithoutUpdate().getInt(key);
                        faction.getMemoryWithoutUpdate().set(key, turnedIn + num);
                    }
                }

                float bounty = computeCoreCreditValue(cargo);
                float repChange = computeCoreReputationValue(cargo);

                if (bounty > 0) {
                    playerCargo.getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int) bounty, text);
                }

                if (repChange >= 1f) {
                    CoreReputationPlugin.CustomRepImpact impact = new CoreReputationPlugin.CustomRepImpact();
                    impact.delta = repChange * 0.01f;
                    Global.getSector().adjustPlayerReputation(
                            new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact,
                                    null, text, true),
                            faction.getId());

                    impact.delta *= 0.25f;
                    if (impact.delta >= 0.01f) {
                        Global.getSector().adjustPlayerReputation(
                                new CoreReputationPlugin.RepActionEnvelope(CoreReputationPlugin.RepActions.CUSTOM, impact,
                                        null, text, true),
                                person);
                    }
                }
                if(Global.getSettings().getModManager().isModEnabled("aotd_qol")){
                    AoTDCommIntelPlugin.get().addFactor(new DatabankSellFactor((int) computeDBCommisionPoints(cargo,attitudeData.getStance())),dialog);
                }
                FireBest.fire(null, dialog, memoryMap, "DatabanksTurnedIn");
            }

            public void cancelledCargoSelection() {
            }

            public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {

                float bounty = computeCoreCreditValue(combined);
                float repChange = computeCoreReputationValue(combined);

                float pad = 3f;
                float small = 5f;
                float opad = 10f;

                panel.setParaFontOrbitron();
                panel.addPara(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor(), 1f);
                //panel.addTitle(Misc.ucFirst(faction.getDisplayName()), faction.getBaseUIColor());
                //panel.addPara(faction.getDisplayNameLong(), faction.getBaseUIColor(), opad);
                //panel.addPara(faction.getDisplayName() + " (" + entity.getMarket().getName() + ")", faction.getBaseUIColor(), opad);
                panel.setParaFontDefault();

                panel.addImage(faction.getLogo(), width * 1f, 3f);


                //panel.setParaFontColor(Misc.getGrayColor());
                //panel.setParaSmallInsignia();
                //panel.setParaInsigniaLarge();
                panel.addPara("Compared to dealing with other factions, turning Research databanks in to " +
                        faction.getDisplayNameLongWithArticle() + " " +
                        "will result in:", opad);
                panel.beginGridFlipped(width, 2, 40f, 10f);
                //panel.beginGrid(150f, 1);
                panel.addToGrid(0, 0, "Bounty value", "" + (int) (valueMult * 100f) + "%");
                panel.addToGrid(0, 1, "Reputation gain", "" + (int) (repMult * 100f) + "%");
                panel.addGrid(pad);

                panel.addPara("If you turn in the selected databanks, you will receive a %s bounty " +
                                "and your standing with " + faction.getDisplayNameWithArticle() + " will improve by %s points.",
                        opad * 1f, Misc.getHighlightColor(),
                        Misc.getWithDGS(bounty) + Strings.C,
                        "" + (int) repChange);
                if (attitudeData.getStance().equals(AoTDAIStance.MERCENARY)) {
                    panel.addPara("Selling databanks to this faction won't make them progress technologically, as they likely will sell those elsewhere.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);

                } else if (attitudeData.getStance().equals(AoTDAIStance.RESTRICTIVE) || attitudeData.getStance().equals(AoTDAIStance.CLEANSE)) {
                    panel.addPara("Selling databanks to this faction will result in their destruction, to prevent them being used in further research.", Misc.getTooltipTitleAndLightHighlightColor(), 10f);

                } else {
                    panel.addPara("Warning! More databanks we sell to this faction, more technologically advanced they will become!", Misc.getNegativeHighlightColor(), 10f);

                }
                if(Global.getSettings().getModManager().isModEnabled("aotd_qol")){
                    if (QoLMisc.isCommissionedBy(faction.getId())) {
                        panel.addPara("This will result in increase of commission points by %s", 10f, Color.ORANGE, "" + computeDBCommisionPoints(combined,attitudeData.getStance()));
                    }
                }


                //panel.addPara("Bounty: %s", opad, Misc.getHighlightColor(), Misc.getWithDGS(bounty) + Strings.C);
                //panel.addPara("Reputation: %s", pad, Misc.getHighlightColor(), "+12");
            }
        });
    }

    protected float computeCoreCreditValue(CargoAPI cargo) {
        float bounty = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getId().equals("research_databank")) {
                bounty += spec.getBasePrice() * stack.getSize();
            }
        }
        bounty *= valueMult;
        return bounty;
    }

    protected float computeCoreReputationValue(CargoAPI cargo) {
        float rep = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getId().equals("research_databank")) {
                rep += getBaseRepValue(spec.getId()) * stack.getSize();
            }
        }
        rep *= repMult;
        //if (rep < 1f) rep = 1f;
        return rep;
    }
    protected float computeDBCommisionPoints(CargoAPI cargo,AoTDAIStance stance) {
        float rep = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getId().equals("research_databank")) {
                rep += getPointsPerDataabank(stance) * stack.getSize();
            }
        }
        return rep;
    }
    public static float getBaseRepValue(String coreType) {
        return 0.5f;
    }


    protected boolean playerHasDbs() {
        for (CargoStackAPI stack : playerCargo.getStacksCopy()) {
            CommoditySpecAPI spec = stack.getResourceIfResource();
            if (spec != null && spec.getId().equals("research_databank")) {
                return true;
            }
        }
        return false;
    }

    public int getPointsPerDataabank(AoTDAIStance stance) {
        if (stance.equals(AoTDAIStance.MERCENARY)) {
            return 2;
        } else if (stance.equals(AoTDAIStance.RESTRICTIVE) || stance.equals(AoTDAIStance.CLEANSE)) {
            return 1;
        } else {
            return 3;
        }
    }
}
