package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import org.magiclib.achievements.MagicAchievementManager;


import java.util.*;

public class PreCollapseFacLoot extends BaseCommandPlugin {

    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected PlanetAPI planet;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected OptionPanelAPI options;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected MarketAPI market;
    protected InteractionDialogAPI dialog;
    protected Map<String, MemoryAPI> memoryMap;
    protected FactionAPI faction;


    public PreCollapseFacLoot() {
    }

    public PreCollapseFacLoot(SectorEntityToken entity) {
        init(entity);
    }

    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        planet = (PlanetAPI) entity;
        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        faction = entity.getFaction();

        market = entity.getMarket();


    }

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        this.dialog = dialog;
        this.memoryMap = memoryMap;

        String command = params.get(0).getString(memoryMap);
        if (command == null) return false;

        entity = dialog.getInteractionTarget();
        init(entity);

        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();

        if (command.equals("genLoot")) {
            genLoot();
        }

        return true;
    }
    protected void genLoot() {

        OptionPanelAPI options = dialog.getOptionPanel();

        MemoryAPI memory = planet.getMemoryWithoutUpdate();
        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        Random random = Misc.getRandom(seed, 100);

        SalvageEntityGenDataSpec.DropData d = new SalvageEntityGenDataSpec.DropData();
        d.chances = 2;
        d.group = "blueprints";
        planet.addDropRandom(d);

        d = new SalvageEntityGenDataSpec.DropData();
        d.chances = 1;
        d.group = "rare_tech";
        planet.addDropRandom(d);

        d = new SalvageEntityGenDataSpec.DropData();
        d.chances = 25;
        d.group = "ashes_research";
        planet.addDropRandom(d);
        int rand = getRandomNumber(1,10);

        CargoAPI salvage = SalvageEntity.generateSalvage(random, 1f, 1f, 1f, 1f, planet.getDropValue(), planet.getDropRandom());

        if(rand<2){
            for (int i = 0; i < rand; i++) {
                salvage.addSpecial(new SpecialItemData("aotd_item_bp",GPManager.getInstance().getItemsProductionWithoutAICores().get(getRandomNumber(0,GPManager.getInstance().getItemProductionOptionFiltered().size()-1)).getSpec().getItemSpecAPI().getId()),1);
            }
        }
        BaseSalvageSpecial.clearExtraSalvage(memoryMap);
        salvage.sort();
        if(Global.getSettings().getModManager().isModEnabled("MagicLib")){
            try {
                MagicAchievementManager.getInstance().getAchievement("aotd_pcf").completeAchievement();
            }
            catch (Exception e) {

            }

        }
        dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, new CoreInteractionListener() {
            public void coreUIDismissed() {
                dialog.dismiss();
                dialog.hideTextPanel();
                dialog.hideVisualPanel();

            }
        });
        options.clearOptions();

        dialog.setPromptText("");
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public String retrieveFromRepo(ArrayList<String> repo){
        if(repo.isEmpty()){
            return null;
        }
        else {
            return repo.remove(0);
        }
    }
}
