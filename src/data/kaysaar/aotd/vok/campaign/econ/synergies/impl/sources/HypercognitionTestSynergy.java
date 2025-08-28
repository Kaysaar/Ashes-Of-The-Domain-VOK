package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;

public class HypercognitionTestSynergy extends BaseIndustrySynergySource {
    public HypercognitionTestSynergy(float baseEfficiency, String id) {
        super(baseEfficiency, id);
    }

    @Override
    public float calculateEfficiencyFromIndustry(Industry ind, boolean includeDemand) {
        float efficency = 0.2f;
        MarketAPI market = ind.getMarket();
        if(market!=null) {
            if(market.getAdmin()!=null&&market.getAdmin().getStats().hasSkill(Skills.HYPERCOGNITION)){
                return efficency;
            }
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
        tooltip.addPara("Admin skill : %s provides %s efficiency",3f, Color.ORANGE,"Hypercognition", AoTDMisc.getPercentageString(0.2f));
    }

    @Override
    public void addToTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {

    }
}
