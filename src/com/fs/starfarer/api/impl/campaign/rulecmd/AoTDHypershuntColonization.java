package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;

import java.util.List;
import java.util.Map;

public class AoTDHypershuntColonization extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if(dialog==null)return false;
        MarketAPI m = createCoronalColony(Global.getSector(),Factions.PLAYER,dialog.getInteractionTarget());
        m.removeSubmarket(Submarkets.SUBMARKET_OPEN);
        m.removeSubmarket(Submarkets.SUBMARKET_BLACK);
        m.setFactionId(Factions.PLAYER);
        dialog.getInteractionTarget().removeTag("salvageable");
        dialog.getInteractionTarget().addTag("station");
        dialog.getInteractionTarget().getMemory().set("$usable",true);
        Global.getSector().getPlayerFleet().getCargo().removeCommodity(Commodities.CREW,100);
        Global.getSector().getPlayerFleet().getCargo().removeCommodity(Commodities.HEAVY_MACHINERY,1500);
        dialog.getInteractionTarget().setFaction(Factions.PLAYER);
        SectorEntityToken e = m.getPrimaryEntity();
        e.setFaction(Factions.PLAYER);
        m.addSubmarket(Submarkets.LOCAL_RESOURCES);
        m.setPlayerOwned(true);
        m.addTag(Tags.MARKET_NO_INDUSTRIES_ALLOWED);
        m.setAdmin(Global.getSector().getPlayerPerson());
        if(!Misc.isPlayerFactionSetUp())Global.getSector().addScript(new EveryFrameScript() {
            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public boolean runWhilePaused() {
                return false;
            }

            @Override
            public void advance(float amount) {
                if(Global.getSector().getCampaignUI().showPlayerFactionConfigDialog())Global.getSector().removeScript(this);
            }
        });
        Misc.stopPlayerFleet();
        dialog.dismiss();
       return true;
    }
    public  MarketAPI createCoronalColony(SectorAPI sector, String factionId, final SectorEntityToken entity) {
        MarketAPI market;
        if(factionId == null)factionId = Factions.INDEPENDENT;
        market = Global.getFactory().createMarket(Misc.genUID(), "Coronal Network Center", 3);
        market.getMemoryWithoutUpdate().set(MemFlags.STATION_MARKET,true);
        //market.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS,true);
        market.getMemoryWithoutUpdate().set("$nex_uninvadable",true);
        market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        market.setFactionId(factionId);
        market.setSize(2);
        market.addTag("aotd_hypershunt");
        market.addCondition(Conditions.POPULATION_2);//a few thousand homies in a boat
        market.addIndustry(Industries.POPULATION);
        market.addIndustry(Industries.MEGAPORT);
        market.getIndustry(Industries.MEGAPORT).setHidden(true);
        market.addIndustry(AoTDIndustries.TERMINUS);
        market.getIndustry(AoTDIndustries.TERMINUS).setHidden(true);
        market.addCondition("aotd_coronal_market_cond");
        market.addIndustry("coronal_port");
        market.addIndustry("coronal_drones");
        market.addIndustry("coronal_wormhole");
        market.addIndustry("coronal_reciver");
        market.addIndustry("coronal_defender");
        market.addIndustry("coronal_shield_generator");

        market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        Industry ind = market.getIndustry(Industries.POPULATION);
        ind.setHidden(true);
        market.addIndustry("coronal_network");
        market.getTariff().modifyFlat("default_tariff", market.getFaction().getTariffFraction());
        market.setPrimaryEntity(entity);
        entity.setMarket(market);

        entity.addScript(new EveryFrameScript() {
            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public boolean runWhilePaused() {
                return false;
            }

            @Override
            public void advance(float amount) {
                if(Misc.getDistance(Global.getSector().getPlayerFleet().getLocation(),entity.getLocation())<=entity.getRadius()*5.5F){
                    if(Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager()!=null){
                        if(Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare()!=null){
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare().arc=1;
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare().extraLengthFlat=0;
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare().extraLengthMult=0;
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare().direction =1;
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).getFlareManager().getActiveFlare().direction =1;
                            Misc.getCoronaFor(entity.getStarSystem().getStar()).setTerrainName("");
                        }

                    }

                }
                else{
                    Misc.getCoronaFor(entity.getStarSystem().getStar()).setTerrainName("Corona");
                }

            }
        });
        //entity.setSensorProfile(1f);
        //entity.setDiscoverable(true);
        //entity.getDetectedRangeMod().modifyFlat("gen", 5000f);

        //market.getEconGroup();
        //market.setEconGroup(market.getId());
        //market.getMemoryWithoutUpdate().set(DecivTracker.NO_DECIV_KEY, true);

        market.reapplyIndustries();

        sector.getEconomy().addMarket(market, false);

        return market;
    }

}
