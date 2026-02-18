package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentCondition;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentManager;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.LinkedHashMap;

public class ColonyDevelopmentApplier extends BaseIndustrySynergySource{
    public static LinkedHashMap<String,Float>values = new LinkedHashMap<>();
    static {
        values.put("core",0.15f);
        values.put("distributed",-0.10f);
    }
    public ColonyDevelopmentApplier(float baseEfficiency, String id) {
        super(baseEfficiency, id);
    }

    @Override
    public float calculateEfficiencyFromIndustry(Industry ind, boolean includeDemand) {
        if(ind.getMarket().hasCondition(BaseColonyDevelopment.condIdApplier)){
            ColonyDevelopmentCondition cond  = (ColonyDevelopmentCondition) ind.getMarket().getCondition(BaseColonyDevelopment.condIdApplier).getPlugin();
            return values.getOrDefault(cond.getIdOfDevelopment(),0f);
        }
        return 0f;
    }

    @Override
    public float getBonusForImproved() {
        return 0;
    }

    @Override
    public float getBonusForAICore(String aiCoreID) {
        return 0;
    }

    @Override
    public void addToTooltipForInfo(Industry industry, TooltipMakerAPI tooltip) {
        if(industry.getMarket().hasCondition(BaseColonyDevelopment.condIdApplier)){
            ColonyDevelopmentCondition cond  = (ColonyDevelopmentCondition) industry.getMarket().getCondition(BaseColonyDevelopment.condIdApplier).getPlugin();
            tooltip.addPara("Colony Development Path: %s provides %s efficiency.",3f, Color.ORANGE, ColonyDevelopmentManager.getInstance().getColonyDevelopment(cond.getIdOfDevelopment()).getName(), AoTDMisc.getPercentageString(values.getOrDefault(cond.getIdOfDevelopment(),0f)));
        }

    }

    @Override
    public void addToTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {

    }
}
