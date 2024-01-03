package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

public class Dummy extends BaseIndustry {
    @Override
    public void apply() {
        super.apply(false);
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;

    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
}
