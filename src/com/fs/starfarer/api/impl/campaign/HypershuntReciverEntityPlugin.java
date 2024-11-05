package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.hypershunt.HypershuntMegastrcutre;

public class HypershuntReciverEntityPlugin extends BaseCustomEntityPlugin{
    boolean isAboutToBeRemoved = false;
    @Override
    public void advance(float amount) {
        if(!this.entity.isExpired()){
            boolean range = HypershuntMegastrcutre.isWithinRangeOfAtLeastOneHypershunt(entity);
            for (MarketAPI market : Global.getSector().getEconomy().getMarkets(this.entity.getStarSystem())) {
                if(!market.isPlayerOwned()&&!market.getFaction().isPlayerFaction())continue;
                if(range){
                    market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).modifyFlat("aotd_hypershunt",2,"Hypershunt Reciver");
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
    public void unmodify(){
        for (MarketAPI market : Global.getSector().getEconomy().getMarkets(this.entity.getStarSystem())) {
            market.getStats().getDynamic().getMod(Stats.MAX_INDUSTRIES).unmodifyFlat("aotd_hypershunt");
        }
    }



}
