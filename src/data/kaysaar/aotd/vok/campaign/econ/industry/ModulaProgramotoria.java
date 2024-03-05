package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;

import java.awt.*;

public class ModulaProgramotoria extends BaseIndustry {
    public Pair<String, Float> mapOfProduction;
    boolean canGoFuther = false;
    @Override
    public void apply() {
        super.apply(true);
        demand(Commodities.HEAVY_MACHINERY,market.getSize()-2);
        demand(AoTDCommodities.ELECTRONICS,5);
        int re = getMaxDeficit(Commodities.HEAVY_MACHINERY,AoTDCommodities.ELECTRONICS).two;
        canGoFuther = re<=0;
    }

    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    public boolean isAvailableToBuild() {
        return super.isAvailableToBuild();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (mapOfProduction != null) {
            mapOfProduction.two -= Global.getSector().getClock().convertToDays(amount);
            if (mapOfProduction.two <= 0) {
                market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(mapOfProduction.one, mapOfProduction.one.split("_")[2]), 1);
                mapOfProduction = null;
            }
        }


    }

    @Override
    public boolean canInstallAICores() {
        return false;

    }

    @Override
    public boolean canImprove() {
        return false;
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if (mapOfProduction != null) {
            if(canGoFuther){
                tooltip.addPara("Due to complexity of constructor's programming, we can only program one at time!", Misc.getNegativeHighlightColor(), 10f);
                String days = " days ";

                if (mapOfProduction.two.intValue() <= 1) {
                    days = " day ";
                }
                //FUNNI CODE
                tooltip.addPara("%s " + days + "left until constructor will be programmed", 10f, Color.ORANGE, "" + mapOfProduction.two.intValue());
            }
            else{
                tooltip.addPara("We need stable supply of resources to go further!", Misc.getNegativeHighlightColor(), 10f);

            }


        }


    }
}
