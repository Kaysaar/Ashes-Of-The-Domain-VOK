package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.util.Misc;

public class TierFourStation extends OrbitalStation {


    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(stationFleet!=null){
            stationFleet.getMemoryWithoutUpdate().set(Misc.DANGER_LEVEL_OVERRIDE,10);
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        return market.getFaction().knowsIndustry(this.getSpec().getId());
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
}
