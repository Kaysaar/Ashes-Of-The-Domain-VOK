package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.Ids.AodCommodities;
import data.kaysaar_aotd_vok.plugins.AoDUtilis;

import java.util.HashMap;
import java.util.Map;

public class MaglevNetwork extends BaseIndustry {
    HashMap<String,String>commoditiesUsed = new HashMap<>();
    @Override
    public void apply() {
        super.apply(true);
        int size = market.getSize();
        demand(Commodities.HEAVY_MACHINERY,size);
        demand(Commodities.RARE_METALS,size-2);
        demand(AodCommodities.ELECTRONICS,3);
        if(getMaxDeficit(Commodities.HEAVY_MACHINERY,Commodities.RARE_METALS,AodCommodities.ELECTRONICS).two<=0){
            for (Industry industry : market.getIndustries()) {
                for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllSupply()) {
                    if((mutableCommodityQuantity.getQuantity().getModifiedInt()>=1)){
                        commoditiesUsed.put(mutableCommodityQuantity.getCommodityId(),"maglev_"+mutableCommodityQuantity.getCommodityId());
                    }
                }
            }
            for (Industry industry : market.getIndustries()) {
                for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                    String key = commoditiesUsed.get(mutableCommodityQuantity.getCommodityId());
                    if(key!=null){
                        mutableCommodityQuantity.getQuantity().modifyFlat(key,-1,"Maglev Network");
                    }
                }

            }
        }
    }


    @Override
    public void unapply() {
        super.unapply();
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                String key = commoditiesUsed.get(mutableCommodityQuantity.getCommodityId());
                if(key!=null){
                    mutableCommodityQuantity.getQuantity().unmodifyFlat(key);
                }
            }

        }
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        tooltip.addSectionHeading("Maglev Network", Alignment.MID,10f);
        tooltip.addPara("Effect: Decrease demand for commodity by 1, if this commodity is produced on market and demand for maintaining Maglev Network is met",10f);
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoDUtilis.isResearched(this.id);
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoDUtilis.isResearched(this.id);
    }

    @Override
    protected void applyAICoreModifiers() {
        super.applyAICoreModifiers();
    }
}
