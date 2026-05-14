package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;

public abstract class BaseIndustrySynergySource implements IndustrySynergySourceAPI {
    public float baseEfficiency = 0f;
    public String id;
    public BaseIndustrySynergySource(float baseEfficiency, String id) {
        this.baseEfficiency = baseEfficiency;
        this.id = id;
    }
    @Override
    public float calculateEfficiencyFromIndustry(Industry ind,boolean includeDemand) {
        if(ind.isDisrupted()|ind.isBuilding()){
            return 0f;
        }
        int demandMisisng = 0;
        if(includeDemand){
            ArrayList<String>demands = new ArrayList<>();
            ind.getAllDemand().forEach(x->demands.add(x.getCommodityId()));
            demandMisisng = ind.getMaxDeficit(demands.toArray(new String[0])).two;
        }

        float improved = 0f;
        if(ind.isImproved()){
            improved = getBonusForImproved();
        }
        return Math.max(0f,baseEfficiency+getBonusForAICore(ind.getAICoreId())+improved-(demandMisisng*0.1f));

    }

    @Override
    public void addToTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {
        tooltip.addSectionHeading("Synergy Efficiency", Alignment.MID,5f);
        tooltip.addPara("When built, this industry will provide %s efficiency.",3f, Color.ORANGE, AoTDMisc.getPercentageString(calculateEfficiencyFromIndustry(ind,false)));
        if(ind.isImproved()&&getBonusForImproved()>0){
            tooltip.addPara("Due to improvement, %s provides additionally %s.",3f,Color.ORANGE,ind.getCurrentName(),AoTDMisc.getPercentageString(getBonusForImproved()));
        }
        if(ind.getAICoreId()!=null&&getBonusForAICore(ind.getAICoreId())>0){
            tooltip.addPara("%s installed: provides additional %s efficiency.",3f,Color.ORANGE, Global.getSettings().getCommoditySpec(ind.getAICoreId()).getName(),AoTDMisc.getPercentageString(getBonusForAICore(ind.getAICoreId())));
        }
    }

    @Override
    public void addToTooltipForInfo(Industry industry, TooltipMakerAPI tooltip) {
        tooltip.addPara("%s provides %s efficiency.",3f,Color.ORANGE,industry.getCurrentName(),AoTDMisc.getPercentageString(calculateEfficiencyFromIndustry(industry,true)));
    }

    @Override
    public String getId() {
        return id;
    }
}
