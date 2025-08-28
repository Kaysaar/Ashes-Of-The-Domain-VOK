package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;

import java.util.Random;

public class MiscHiddenIndustry extends BaseIndustry {
    public MutableStat patherIntrestManipulator = new MutableStat(0f);
    public static MiscHiddenIndustry getInstance(MarketAPI market){
        if(!market.hasIndustry("aotd_misc_hidden_industry")){
            market.addIndustry("aotd_misc_hidden_industry");
            return (MiscHiddenIndustry) market.getIndustry("aotd_misc_hidden_industry");
        }
        return (MiscHiddenIndustry) market.getIndustry("aotd_misc_hidden_industry");
    }


    public MutableStat getPatherInterestManipulator() {
        return patherIntrestManipulator;
    }

    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        super.notifyBeingRemoved(mode, forUpgrade);
        IndustrySynergiesManager.getInstance().getSynergyScripts().forEach(x->x.advance(market,0f,true));
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
    public CargoAPI generateCargoForGatheringPoint(Random random) {
        CargoAPI result = Global.getFactory().createCargo(true);
        IndustrySynergiesManager.getInstance().getSynergyScriptsValidForMarket(market).forEach(x->{
            CargoAPI cargo = x.generateCargoForGatheringPoint(market,random);
            if(cargo!=null){
                result.addAll(cargo,true);
            }

        });
        return result;
    }

    @Override
    public float getPatherInterest() {
        return patherIntrestManipulator.getModifiedInt();
    }
}
