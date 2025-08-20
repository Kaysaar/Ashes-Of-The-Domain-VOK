package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

public class PatherHiddenIndustry extends BaseIndustry {
    public MutableStat patherIntrestManipulator = new MutableStat(0f);
    public static PatherHiddenIndustry getInstance(MarketAPI market){
        if(!market.hasIndustry("aotd_hidden_pather_influencer")){
            market.addIndustry("aotd_hidden_pather_influencer");
            return (PatherHiddenIndustry) market.getIndustry("aotd_hidden_pather_influencer");
        }
        return (PatherHiddenIndustry) market.getIndustry("aotd_hidden_pather_influencer");
    }


    public MutableStat getPatherInterestManipulator() {
        return patherIntrestManipulator;
    }

    @Override
    public boolean canBeDisrupted() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public void apply() {
        setHidden(true);
    }

    @Override
    public float getPatherInterest() {
        return patherIntrestManipulator.getModifiedInt();
    }
}
