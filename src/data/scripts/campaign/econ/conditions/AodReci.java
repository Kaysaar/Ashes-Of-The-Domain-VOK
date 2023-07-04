package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;
import data.plugins.AoDUtilis;

public class AodReci extends BaseMarketConditionPlugin {
    @Override
    public void apply(String id) {
        super.apply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                industry.getSupply(Commodities.FOOD).getQuantity().modifyMult("switchReciFoodBlock",0);


            }
        }
        applyCommodities();
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                industry.getSupply(Commodities.FOOD).getQuantity().unmodifyMult("switchReciFoodBlock");


            }
        }
        for (Industry ind : market.getIndustries()) {
            unapplyRecitificatesDemand((BaseIndustry) ind);
        }

    }
    public void applyCommodities() {
        int quantity = market.getSize() + AoDUtilis.getFoodQuantityBonus(market);

        for (Industry ind : market.getIndustries()) {
            if (ind.getId().equals(Industries.FARMING)||ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)||ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)) {

                applyCommoditySupplyToIndustry((BaseIndustry) ind,quantity);

            }


        }

    }
    public void unapplyRecitificatesDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.RECITIFICATES, 0, "");

    }
    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand){

        if(ind.getId().equals(Industries.FARMING)){
            ind.supply(AodCommodities.RECITIFICATES, demand);
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
        }
        if(ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)){
            ind.supply(AodCommodities.RECITIFICATES, demand-2);
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
        }
        if(ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)){
            ind.supply(AodCommodities.RECITIFICATES, demand+2);
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
        }


    }



        @Override
    public boolean showIcon() {
        return false;
    }

}
