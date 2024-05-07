package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.FuelProduction;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.plugins.AoDUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

public class FuelProdOverride extends FuelProduction {
    @Override
    public void startUpgrading() {
        super.startUpgrading();
        if(AoTDMainResearchManager.getInstance().getSpecificFactionManager(market.getFaction()).haveResearched(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION)){
            AoDUtilis.ensureIndustryHasNoItem(this);
        }
        else{
            setSpecialItem(null);
        }
    }

}
