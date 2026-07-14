package data.kaysaar.aotd.vok.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin2;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.industry.FloatingCityThrusters;

import java.awt.*;

public class FloatingCityCondition extends BaseMarketConditionPlugin2 {
    public enum FloatingCityStage {
        VERY_GOOD,
        GOOD,
        BAD,
        OH_FUCK
    }

    public static String keyForLevel = "$aotd_memkey_floating_city_level";
    public static float maxLevel = 1000f;
    public static void setCurrentLevel(MarketAPI market,float level) {
         market.getMemoryWithoutUpdate().set(keyForLevel,level);
    }
    public static float getCurrentLevel(MarketAPI market) {
        if (!market.getMemoryWithoutUpdate().contains(keyForLevel)) {
            market.getMemoryWithoutUpdate().set(keyForLevel, maxLevel);
        }
        return market.getMemoryWithoutUpdate().getFloat(keyForLevel);
    }

    public static FloatingCityStage getCurrentStage(MarketAPI market) {
        float level = getCurrentLevel(market);
        if (level > 750) {
            return FloatingCityStage.VERY_GOOD;
        }
        if (level < 250) {
            return FloatingCityStage.OH_FUCK;
        }
        if (level < 500) {
            return FloatingCityStage.BAD;
        }
        return FloatingCityStage.GOOD;
    }
    public static String getStageName(MarketAPI market) {
        FloatingCityStage stage = getCurrentStage(market);
        return switch (stage) {
            case VERY_GOOD -> "Stable";
            case GOOD -> "Drifting";
            case BAD -> "Unstable";
            case OH_FUCK ->"Critical";
        };
    }
    @Override
    public String getName() {
        String firstPart = super.getName();
        return firstPart+"- "+getStageName(market);
    }

    @Override
    public void apply(String id) {
        FloatingCityStage stage = getCurrentStage(market);
        if(stage==FloatingCityStage.GOOD){
            market.getStability().modifyFlat(id,-2,"Current Altitude Level");
        }
        if(stage==FloatingCityStage.BAD){
            market.getStability().modifyFlat(id,-5,"Current Altitude Level");

        }
        if(stage==FloatingCityStage.OH_FUCK){
            market.getStability().modifyFlat(id,-12,"Current Altitude Level");
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        market.getStability().unmodifyFlat(id);
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        super.createTooltipAfterDescription(tooltip, expanded);
        tooltip.addPara("Current Altitude : %s ( %s )",10f, Color.ORANGE,getStageName(market),Math.round(getCurrentLevel(market))+"");
        Industry ind = market.getIndustry("aotd_city_thrusters");
        if(ind!=null) {
            int deficit = ind.getMaxDeficit(Commodities.FUEL).two;
            int demand = ind.getDemand(Commodities.FUEL).getQuantity().getModifiedInt();
            int met = demand-deficit;
            float total = (float) deficit /demand;
            demand = AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(demand,true,Commodities.FUEL);
            met = AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(met,true,Commodities.FUEL);
            if(deficit==0){
                tooltip.addPara("Fuel fully supplied! (Current demand required for thrusters: %s)", 10f,Misc.getPositiveHighlightColor(),Color.ORANGE,demand+"");

            }
            else{
                tooltip.addPara("There is fuel deficit! ( %s / %s ) City is descending!!!",10f,Misc.getNegativeHighlightColor(),Color.ORANGE,met+"",demand+"");
                float descendRate = Math.round(total* FloatingCityThrusters.dailyMaxDecreaseLevel);
                tooltip.addPara("Platform descends at the rate of approx. %s each day, further it falls, the more penalties market gets. If altitude reaches %s, %s",5f,new Color[]{Color.ORANGE,Color.ORANGE,Misc.getNegativeHighlightColor()},""+descendRate,"0","colony will be destroyed!");
            }
        }
        FloatingCityStage stage = getCurrentStage(market);
        if(stage==FloatingCityStage.VERY_GOOD||stage==FloatingCityStage.GOOD) {
            tooltip.addPara("Due to how high city is positioned, it negates these market conditions:",10f);
            tooltip.setBulletedListMode(BaseIntelPlugin.BULLET);
            tooltip.addPara("Toxic Atmosphere",Color.ORANGE,3f);
            tooltip.addPara("High Gravity",Color.ORANGE,3f);
            tooltip.setBulletedListMode(null);
        }
        if(stage!=FloatingCityStage.VERY_GOOD) {
            tooltip.addPara("Given current altitude following effects occur:",10f);
            tooltip.setBulletedListMode(BaseIntelPlugin.BULLET);
            if(stage==FloatingCityStage.GOOD){
                tooltip.addPara("Stability reduced by %s",3f,Misc.getNegativeHighlightColor(),"2");
            }
            if(stage==FloatingCityStage.BAD){
                tooltip.addPara("Stability reduced by %s",3f,Misc.getNegativeHighlightColor(),"5");
                tooltip.addPara("Market growth points reduced by %s",3f,Misc.getNegativeHighlightColor(),"50");
            }
            if(stage==FloatingCityStage.OH_FUCK){
                tooltip.addPara("Stability reduced by %s",3f,Misc.getNegativeHighlightColor(),"12");
                tooltip.addPara("Market growth points reduced by %s",3f,Misc.getNegativeHighlightColor(),"100");
                tooltip.setBulletedListMode(null);
                tooltip.addPara("If fuel deficit won't be managed properly this colony will be soon destroyed!",Misc.getNegativeHighlightColor(),5f);
            }

            tooltip.setBulletedListMode(null);
        }
    }

    @Override
    public String getIconName() {
        FloatingCityStage stage = getCurrentStage(market);
        return switch (stage) {
            case VERY_GOOD -> Global.getSettings().getSpriteName("aotd_market", "floating_city_1");
            case GOOD -> Global.getSettings().getSpriteName("aotd_market", "floating_city_2");
            case BAD -> Global.getSettings().getSpriteName("aotd_market", "floating_city_3");
            case OH_FUCK -> Global.getSettings().getSpriteName("aotd_market", "floating_city_4");
            default -> Global.getSettings().getSpriteName("aotd_market", "floating_city_1");
        };
    }


}
