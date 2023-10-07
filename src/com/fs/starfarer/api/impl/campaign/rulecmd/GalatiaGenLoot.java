package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.GalatiaOfferIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.util.Misc;
import data.plugins.AoDUtilis;
import data.scripts.research.ResearchOption;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class GalatiaGenLoot extends BaseCommandPlugin {
    protected CampaignFleetAPI playerFleet;
    protected SectorEntityToken entity;
    protected FactionAPI playerFaction;
    protected FactionAPI entityFaction;
    protected TextPanelAPI text;
    protected CargoAPI playerCargo;
    protected MemoryAPI memory;
    protected MarketAPI market;
    protected InteractionDialogAPI dialog;
    protected FactionAPI faction;

    protected OptionPanelAPI options;

    public GalatiaGenLoot() {
    }

    public GalatiaGenLoot(SectorEntityToken entity) {
        init(entity);
    }

    protected void init(SectorEntityToken entity) {
        memory = entity.getMemoryWithoutUpdate();
        this.entity = entity;
        playerFleet = Global.getSector().getPlayerFleet();
        playerCargo = playerFleet.getCargo();

        playerFaction = Global.getSector().getPlayerFaction();
        entityFaction = entity.getFaction();

        faction = entity.getFaction();

        market = entity.getMarket();


    }

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        entity = dialog.getInteractionTarget();
        init(entity);

        memory = getEntityMemory(memoryMap);

        text = dialog.getTextPanel();
        options = dialog.getOptionPanel();


        genLoot();

        return true;
    }

    protected void genLoot() {
        for (ResearchOption researchOption : AoDUtilis.getResearchAPI().getAllResearchOptions()) {
            if ((researchOption.modId.contains("vanilla"))) {
                Global.getSector().getPlayerFleet().getCargo().addSpecial(new SpecialItemData("aotd_vok_databank_decayed", researchOption.industryId), 1);
            }
        }
        IntelInfoPlugin intel = Global.getSector().getIntelManager().getFirstIntel(GalatiaOfferIntel.class);
       if( intel!=null){
           Global.getSector().getIntelManager().removeIntel(intel);
       }
    }
}