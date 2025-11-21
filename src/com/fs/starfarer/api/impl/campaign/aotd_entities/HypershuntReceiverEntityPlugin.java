package com.fs.starfarer.api.impl.campaign.aotd_entities;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.BaseCampaignObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastructure;

import java.awt.*;

public class HypershuntReceiverEntityPlugin extends BaseCampaignObjectivePlugin {
    boolean isAboutToBeRemoved = false;
    @Override
    public void advance(float amount) {
        if(!this.entity.isExpired()){
            boolean range = isWithinRange();
            for (MarketAPI market : Global.getSector().getEconomy().getMarkets(this.entity.getStarSystem())) {
                if(!market.isPlayerOwned()&&!market.getFaction().isPlayerFaction())continue;
                if(range){
                    market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat("aotd_hypershunt",2,"Hypershunt Receiver");
                }
                else{
                    market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodifyFlat("aotd_hypershunt");

                }
            }
        }
        else{
            unmodify();
        }

    }
    public boolean isWithinRange(){
        return HypershuntMegastructure.isWithinRangeOfAtLeastOneHypershunt(entity);
    }
    public void unmodify(){
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(this.entity.getStarSystem())) {
            market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodifyFlat("aotd_hypershunt");
        }
    }
    @Override
    public void printEffect(TooltipMakerAPI text, float pad) {
//		int bonus = Math.abs(Math.round(
//					CommRelayCondition.NO_RELAY_PENALTY - CommRelayCondition.COMM_RELAY_BONUS));
//		if (isMakeshift()) {
//			bonus = Math.abs(Math.round(
//					CommRelayCondition.NO_RELAY_PENALTY - CommRelayCondition.MAKESHIFT_COMM_RELAY_BONUS));
//		}
        int bonus = 2;

        text.addPara(BaseIntelPlugin.INDENT + "%s industry slots for all colonies in-system (within range of a hypershunt).",
                pad, Misc.getHighlightColor(), "+" + bonus);
        text.addPara(BaseIntelPlugin.INDENT+"Allow some megastructures to gain unique bonuses", Color.ORANGE,5f);
        if(!isWithinRange()){
            text.addPara("This receiver is not within range!",Misc.getNegativeHighlightColor(),5f);
        }
        else{
            text.addPara("Receiver within range!",Misc.getPositiveHighlightColor(),5f);
        }
    }


}
