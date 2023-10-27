package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.Ids.AoDIndustries;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

public class AodSub extends BaseMarketConditionPlugin {
    float production =0.0f;
    @Override
    public void apply(String id) {
        super.apply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                 production = (float) industry.getSupply(Commodities.FOOD).getQuantity().getModifiedInt();

                industry.getSupply(Commodities.FOOD).getQuantity().modifyFlat("switchReciFoodBlock",(int)-production*0.5f,"Production Focus");


            }
        }
        applyBioticsDemand();
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                industry.getSupply(Commodities.FOOD).getQuantity().unmodifyFlat("switchReciFoodBlock");


            }
        }
        for (Industry ind : market.getIndustries()) {
            unapplyElectronicsDemand((BaseIndustry) ind);
        }

    }
    public void applyBioticsDemand() {
        int quantity = market.getSize() + AoDUtilis.getFoodQuantityBonus(market);
        for (Industry ind : market.getIndustries()) {
            if (ind.getId().equals(Industries.FARMING)||ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)||ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)) {
                applyCommoditySupplyToIndustry((BaseIndustry) ind, (int)production);

            }


        }

    }
    public void unapplyElectronicsDemand(BaseIndustry ind) {
        ind.supply(AodCommodities.RECITIFICATES, 0, "");
        ind.supply(AodCommodities.BIOTICS, 0, "");

    }
    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand){

        if(ind.getId().equals(Industries.FARMING)){
            ind.supply(AodCommodities.RECITIFICATES,(int)(demand*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.BIOTICS,demand);
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoDIndustries.ARTISANAL_FARMING)){
            ind.supply(AodCommodities.RECITIFICATES,(int)((demand-2)*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.BIOTICS, demand-2);
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoDIndustries.SUBSIDISED_FARMING)){
            ind.supply(AodCommodities.RECITIFICATES,(int)((demand+2)*0.5f));
            ind.getSupply(AodCommodities.RECITIFICATES).getQuantity().unmodify(getModId());
            ind.supply(AodCommodities.BIOTICS,demand+2);
            ind.getSupply(AodCommodities.BIOTICS).getQuantity().unmodify(getModId());

        }

    }



    @Override
    public boolean showIcon() {
        return false;
    }

}
