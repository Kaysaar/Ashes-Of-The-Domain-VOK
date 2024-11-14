package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.OrbitalStationInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureUnlockIntel;
import com.fs.starfarer.api.impl.campaign.intel.SpecialProjectUnlockingIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPMegaStructureSpec;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class AoTDMegastructureRules extends BaseCommandPlugin {
    protected SectorEntityToken token;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        token = dialog.getInteractionTarget();
        String arg = params.get(0).getString(memoryMap);
        if (arg.contains("print")) {

            printMegastructureInfo(dialog, token.getCustomEntityType(), token);

        }
        if (arg.contains("claim")) {
            claimMegastructure(dialog, token);
            dialog.getOptionPanel().clearOptions();
            dialog.getOptionPanel().addOption("Leave", "defaultLeave");
            dialog.getOptionPanel().setShortcut("defaultLeave", Keyboard.KEY_ESCAPE, false, false, false, true);
        }
        if(arg.equals("researchedTech")){
            if(!AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.MEGA_ANALYSIS)){
                dialog.getOptionPanel().setEnabled("aotd_claim_megastructure",false);
                dialog.getOptionPanel().setTooltip("aotd_claim_megastructure","We need to research first Megastructure Analysis!");
                dialog.getTextPanel().addPara("With our current technological advancements, it is not advised to take control of this megastructure, due to massive logistical requirements to restore it!");
            }
            return AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.MEGA_ANALYSIS);
        }
        if(arg.equals("researchedReciver")){
            return AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(AoTDTechIds.HP_ENERGY_DISTRIB);
        }

        return true;
    }

    public void printMegastructureInfo(InteractionDialogAPI dialog, String megastructureID, SectorEntityToken token) {
        GPBaseMegastructure megastructure;
        GPMegaStructureSpec spec = GPManager.getInstance().getMegaSpecFromListByEntityId(megastructureID);
        if (token.getMemory().contains(GPBaseMegastructure.memKey)) {
            megastructure = (GPBaseMegastructure) token.getMemory().get(GPBaseMegastructure.memKey);
        } else {
            megastructure = spec.getScript();
        }
        if (!megastructure.wasInitalized) {
            megastructure.mockUpInit(spec.getMegastructureID());
        }
        megastructure.createTooltipInfoBeforeClaiming(dialog);
        dialog.getOptionPanel().clearOptions();
        dialog.getOptionPanel().addOption("Establish outpost", "aotd_claim_megastructure_complete", Color.ORANGE, "This megastructure will belong to our faction");
        dialog.getOptionPanel().addOption("Leave", "defaultLeave");

    }

    public static void claimMegastructure(InteractionDialogAPI dialogAPI, SectorEntityToken token) {
        GPBaseMegastructure megastructure;
        GPMegaStructureSpec spec = GPManager.getInstance().getMegaSpecFromListByEntityId(token.getCustomEntityType());

        if (token.getMemory().contains(GPBaseMegastructure.memKey)) {
            megastructure = (GPBaseMegastructure) token.getMemory().get(GPBaseMegastructure.memKey);
        } else {
            megastructure = spec.getScript();
        }
        if (!megastructure.wasInitalized) {
            megastructure.trueInit(spec.getMegastructureID(), token);
        }
        GPManager.getInstance().addMegastructureToList(megastructure);
        token.getMemory().set("$aotd_claimed", true);
        token.setFaction(Factions.PLAYER);
        token.getMemory().set(GPBaseMegastructure.memKey, megastructure);

        MegastructureUnlockIntel intel = new MegastructureUnlockIntel(megastructure);
        Global.getSector().getIntelManager().addIntel(intel, false);
        if (dialogAPI != null && dialogAPI.getTextPanel() != null) {
            intel.sendUpdateIfPlayerHasIntel(intel, dialogAPI.getTextPanel());
        }
        Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);

    }

    //Note : Used when for example megastructure is tied to planet, so you can tie it to planet manually
    public static void claimMegastructureManually(InteractionDialogAPI dialogAPI, SectorEntityToken token, String specOfmegastructure) {
        GPBaseMegastructure megastructure;
        GPMegaStructureSpec spec = GPManager.getInstance().getMegaSpecFromList(specOfmegastructure);
        token.setFaction(Factions.PLAYER);
        if (token.getMemory().contains(GPBaseMegastructure.memKey)) {
            megastructure = (GPBaseMegastructure) token.getMemory().get(GPBaseMegastructure.memKey);
        } else {
            megastructure = spec.getScript();
        }
        if (!megastructure.wasInitalized) {
            megastructure.trueInit(spec.getMegastructureID(), token);
        }
        GPManager.getInstance().addMegastructureToList(megastructure);
        token.getMemory().set(GPBaseMegastructure.memKey, megastructure);
        MegastructureUnlockIntel intel = new MegastructureUnlockIntel(megastructure);
        Global.getSector().getIntelManager().addIntel(intel, false);
        Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);
    }
    public static GPBaseMegastructure putMegastructure(SectorEntityToken token, String specOfmegastructure) {
        GPBaseMegastructure megastructure;
        GPMegaStructureSpec spec = GPManager.getInstance().getMegaSpecFromList(specOfmegastructure);
        megastructure = spec.getScript();
        if (!megastructure.wasInitalized) {
            megastructure.trueInit(spec.getMegastructureID(), token);
        }
        token.getMemory().set(GPBaseMegastructure.memKey,megastructure);
        return megastructure;
    }
}
