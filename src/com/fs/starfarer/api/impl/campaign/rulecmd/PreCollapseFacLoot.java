package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BeyondVeilIntel;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.BaseSalvageSpecial;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.plugins.AoDUtilis;
import data.scripts.research.items.VoKDatabank;
import data.scripts.research.items.VoKDatabankType;

import java.util.*;

public class PreCollapseFacLoot extends BaseCommandPlugin{

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
        TextPanelAPI text = dialog.getTextPanel();

        MemoryAPI memory = planet.getMemoryWithoutUpdate();
        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        Random random = Misc.getRandom(seed, 100);

        SalvageEntityGenDataSpec.DropData d = new SalvageEntityGenDataSpec.DropData();
        d.chances = 5;
        d.group = "blueprints";
        planet.addDropRandom(d);

        d = new SalvageEntityGenDataSpec.DropData();
        d.chances = 1;
        d.group = "rare_tech";
        planet.addDropRandom(d);
        List<Pair<String, String>> databankRepo = AoDUtilis.getDatabankRepo();
        Collections.shuffle(databankRepo);
        CargoAPI salvage = SalvageEntity.generateSalvage(random, 1f, 1f, 1f, 1f, planet.getDropValue(), planet.getDropRandom());
        CargoAPI extra = BaseSalvageSpecial.getCombinedExtraSalvage(memoryMap);
        salvage.addAll(extra);
        BaseSalvageSpecial.clearExtraSalvage(memoryMap);
        if (!extra.isEmpty()) {
            ListenerUtil.reportExtraSalvageShown(planet);
        }
        for(int i=0;i<3;i++){
            Pair<String,String> retrieved = retrieveFromRepo(databankRepo);
            if(retrieved!=null){
                salvage.addSpecial(new SpecialItemData(chooseType(), retrieved.two), 1);
            }
        }
        salvage.sort();

        dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, new CoreInteractionListener() {
            public void coreUIDismissed() {
                dialog.dismiss();
                dialog.hideTextPanel();
                dialog.hideVisualPanel();

            }
        });
        options.clearOptions();
        dialog.setPromptText("");


//		if (keptPromise) {
//			if (random.nextFloat() > 0.5f) {
//				SectorEntityToken loc = planet.getContainingLocation().createToken(planet.getLocation());
//				spawnPiratesToInvestigate(loc, 50f + random.nextFloat() * 50f);
//				if (random.nextFloat() > 0.5f) {
//					spawnPiratesToInvestigate(loc, 50f + random.nextFloat() * 50f);
//				}
//			}
//		}
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    public String chooseType(){
        int rand = getRandomNumber(0,2);
        VoKDatabankType type = VoKDatabankType.fromInteger(rand);
        if(type.equals(VoKDatabankType.PRISTINE)){
            return "aotd_vok_databank_pristine";
        }
        if(type.equals(VoKDatabankType.DECAYED)){
            return "aotd_vok_databank_decayed";
        }
        return "aotd_vok_databank_damaged";
    }
    public Pair<String, String >retrieveFromRepo( List<Pair<String, String>> repo){
        if(repo.isEmpty()){
            return null;
        }
        else {
            return repo.remove(0);
        }
    }
}
