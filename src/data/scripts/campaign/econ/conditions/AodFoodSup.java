package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.Ids.AoDConditions;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;
import data.plugins.AoDUtilis;

public class AodFoodSup extends BaseMarketConditionPlugin {
    float production =0.0f;
    @Override
    public void apply(String id) {
        super.apply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                production = (float) industry.getSupply(Commodities.FOOD).getQuantity().getModifiedInt();

            }
        }
        applyCommodities();
    }
    @Override
    public void unapply(String id) {
        for (Industry ind : market.getIndustries()) {
            unapplyRecitificatesDemand((BaseIndustry) ind);
        }

    }
    public void applyCommodities() {
        int quantity = market.getSize() + AoDUtilis.getFoodQuantityBonus(market);

        for (Industry ind : market.getIndustries()) {
            if (ind.getId().equals(Industries.FARMING)||ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)||ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)) {

                applyCommoditySupplyToIndustry((BaseIndustry) ind,(int)production);

            }


        }

    }
    public void unapplyRecitificatesDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.RECITIFICATES, 0, "");
        ind.supply(AodCommodities.BIOTICS, 0, "");
    }
    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand){

        if(ind.getId().equals(Industries.FARMING)){
            ind.supply(AodCommodities.BIOTICS, (int)(demand*0.5f));
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.RECITIFICATES, (int)(demand*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)){
            ind.supply(AodCommodities.BIOTICS, (int)((demand-2)*0.5f));
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.RECITIFICATES, (int)((demand-2)*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)){
            ind.supply(AodCommodities.BIOTICS, (int)((demand+2)*0.5));
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.RECITIFICATES, (int)((demand+2)*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }


    }

    public static void applyCondition(MarketAPI marketAPI){
        if(!marketAPI.hasCondition(AoDConditions.SWITCH_BIOTICS)&&!marketAPI.hasCondition(AoDConditions.SWITCH_RECITIFICATES)){
            marketAPI.addCondition(AoDConditions.SWITCH_FOOD);
        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}
