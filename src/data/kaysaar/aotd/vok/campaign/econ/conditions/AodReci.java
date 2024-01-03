package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;

import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;


public class AodReci extends BaseMarketConditionPlugin {
    float production =0.0f;
    @Override
    public void apply(String id) {
        super.apply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoTDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoTDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                 production = (float) industry.getSupply(Commodities.FOOD).getQuantity().getModifiedInt();
                industry.getSupply(Commodities.FOOD).getQuantity().modifyFlat("switchReciFoodBlock",(int)-production*0.5f,"Production Focus");


            }
        }
        applyCommodities();
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);
        for (Industry industry : market.getIndustries()) {
            if(industry.getId().equals(AoTDIndustries.ARTISANAL_FARMING)
                    ||industry.getId().equals(AoTDIndustries.SUBSIDISED_FARMING)
                    ||industry.getId().equals(Industries.FARMING)){
                industry.getSupply(Commodities.FOOD).getQuantity().unmodifyFlat("switchReciFoodBlock");


            }
        }
        for (Industry ind : market.getIndustries()) {
            unapplyRecitificatesDemand((BaseIndustry) ind);
        }

    }
    public void applyCommodities() {
        int quantity = market.getSize() + AoDUtilis.getFoodQuantityBonus(market);

        for (Industry ind : market.getIndustries()) {
            if (ind.getId().equals(Industries.FARMING)||ind.getId().equals(AoTDIndustries.ARTISANAL_FARMING)||ind.getId().equals(AoTDIndustries.SUBSIDISED_FARMING)) {

                applyCommoditySupplyToIndustry((BaseIndustry) ind,(int)production);

            }


        }

    }
    public void unapplyRecitificatesDemand(BaseIndustry ind) {
        ind.supply(AoTDCommodities.RECITIFICATES, 0, "");
        ind.supply(AoTDCommodities.BIOTICS, 0, "");
    }
    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand){

        if(ind.getId().equals(Industries.FARMING)){
            ind.supply(AoTDCommodities.BIOTICS, (int)(demand*0.5f));
            ind.getSupply(AoTDCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AoTDCommodities.RECITIFICATES, demand);
            ind.getSupply(AoTDCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoTDIndustries.ARTISANAL_FARMING)){
            ind.supply(AoTDCommodities.BIOTICS, (int)((demand-2)*0.5f));
            ind.getSupply(AoTDCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AoTDCommodities.RECITIFICATES, demand-2);
            ind.getSupply(AoTDCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }
        if(ind.getId().equals(AoTDIndustries.SUBSIDISED_FARMING)){
            ind.supply(AoTDCommodities.BIOTICS, (int)((demand+2)*0.5f));
            ind.getSupply(AoTDCommodities.BIOTICS).getQuantity().unmodify(getModId());
            ind.supply(AoTDCommodities.RECITIFICATES, demand+2);
            ind.getSupply(AoTDCommodities.RECITIFICATES).getQuantity().unmodify(getModId());

        }


    }



        @Override
    public boolean showIcon() {
        return false;
    }

}
