package data.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;

public class AodSub extends BaseMarketConditionPlugin {
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
        unapplyBioticsDemand();
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
            unapplyElectronicsDemand((BaseIndustry) ind);
        }

    }
    public void unapplyBioticsDemand() {
        int size = market.getSize();

        for (Industry ind : market.getIndustries()) {
            if (ind.getId().equals(Industries.FARMING)||ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)||ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)) {
                applyCommoditySupplyToIndustry((BaseIndustry) ind, market.getSize());

            }


        }

    }
    public void unapplyElectronicsDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.BIOTICS, 0, "");

    }
    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand){

        if(ind.getId().equals(Industries.FARMING)){
            ind.supply(AodCommodities.BIOTICS, market.getSize());
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
        }
        if(ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)){
            ind.supply(AodCommodities.BIOTICS, market.getSize());
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
        }
        if(ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)){
            ind.supply(AodCommodities.BIOTICS, market.getSize());
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());
        }

    }



    @Override
    public boolean showIcon() {
        return false;
    }

}
