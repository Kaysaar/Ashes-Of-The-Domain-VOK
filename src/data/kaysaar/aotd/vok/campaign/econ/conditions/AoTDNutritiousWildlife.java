package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;

import java.awt.*;

public class AoTDNutritiousWildlife extends BaseMarketConditionPlugin {
    public float percentageOfBonusMult = 1.2f;
    public int reducitonOfFoodDemand = 3;

    @Override
    public void apply(String id) {
        if(market.hasIndustry(Industries.POPULATION)){
            market.getIndustry(Industries.POPULATION).getDemand(Commodities.FOOD).getQuantity().modifyFlat(id,-reducitonOfFoodDemand);

        }
        if(market.hasIndustry(AoTDIndustries.ARTISANAL_FARMING)){
            Industry ind = market.getIndustry(AoTDIndustries.ARTISANAL_FARMING);
            for (MutableCommodityQuantity mutableCommodityQuantity : ind.getAllSupply()) {
                mutableCommodityQuantity.getQuantity().modifyMult(id,percentageOfBonusMult);
            }
        }
    }

    @Override
    public boolean showIcon() {
        return super.showIcon();
    }

    @Override
    public void unapply(String id) {
        if(market.hasIndustry(Industries.POPULATION)){
            market.getIndustry(Industries.POPULATION).getDemand(Commodities.FOOD).getQuantity().unmodifyFlat(id);
        }

        if(market.hasIndustry(AoTDIndustries.ARTISANAL_FARMING)){
            Industry ind = market.getIndustry(AoTDIndustries.ARTISANAL_FARMING);
            for (MutableCommodityQuantity mutableCommodityQuantity : ind.getAllSupply()) {
                mutableCommodityQuantity.getQuantity().unmodifyMult(id);
            }
        }
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara("Increases production of %s by %s",10f, Color.ORANGE, Global.getSettings().getIndustrySpec(AoTDIndustries.ARTISANAL_FARMING).getName(),"20%");
        tooltip.addPara("Reduces demand of food on market by %s units",5f,Color.ORANGE,"3");
    }

}
