package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;

public class WaterMinningCond extends BaseMarketConditionPlugin {
    public static final String UpgradeCond = "watterSupplyMining";
    @Override
    public void apply(String id) {
        super.apply(id);
    }
    @Override
    public void unapply(String id) {
        super.unapply(id);

    }
    public void ApplyWaterSupply() {
        int size = market.getSize();



    }



    public void applyCommoditySupplyToIndustry(BaseIndustry ind, int demand,boolean canApply) {
        if (!canApply) return;
        if (ind.getId().equals(Industries.MINING)) {
            ind.supply(AoTDCommodities.WATER, market.getSize()-2);
            ind.getSupply(AoTDCommodities.WATER).getQuantity().unmodify(getModId());


        }
    }
    public static void applyIndustryUpgradeCondition(MarketAPI marketAPI) {

    }
    @Override
    public boolean showIcon() {
        return false;
    }

    public String getModId() {
        return condition.getId();
    }

}
