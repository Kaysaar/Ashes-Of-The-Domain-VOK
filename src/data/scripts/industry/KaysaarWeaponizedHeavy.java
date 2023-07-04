package data.scripts.industry;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Pair;

public class KaysaarWeaponizedHeavy extends HeavyIndustry {

    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.METALS, size);
        demand(Commodities.RARE_METALS, size - 2);

        supply(Commodities.HEAVY_MACHINERY, size-3);
        supply(Commodities.SUPPLIES, size-3);
        supply(Commodities.HAND_WEAPONS, size );
        supply(Commodities.SHIPS, size  );
        Pair<String, Integer> deficit = getMaxDeficit(Commodities.METALS,Commodities.RARE_ORE);
        int maxDeficit =  size - 3; // to allow *some* production so economy doesn't get into an unrecoverable state
        if (deficit.two > maxDeficit) deficit.two = maxDeficit;

        applyDeficitToProduction(2, deficit,
                Commodities.HEAVY_MACHINERY,
                Commodities.SUPPLIES,
                Commodities.HAND_WEAPONS,
                Commodities.SHIPS);


//		if (market.getId().equals("chicomoztoc")) {
//			System.out.println("efwefwe");
//		}



        if (!isFunctional()) {
            supply.clear();
            unapply();
        }

        else{
            for (Industry industry : market.getIndustries()) {
                for (String tag : industry.getSpec().getTags()) {
                    if(tag.contains("military")){
                        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyFlat("military_synergy",1.5f,"Test_Synergy");
                    }
                }
            }
        }
    }
    @Override
    public void unapply() {
        market.getStability().unmodifyFlat("military_synergy");
    }
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }

    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
