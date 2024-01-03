package data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap;

import data.kaysaar.aotd.vok.campaign.econ.industry.Terminus;

public class CoronalPort extends Terminus {
    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }

}
