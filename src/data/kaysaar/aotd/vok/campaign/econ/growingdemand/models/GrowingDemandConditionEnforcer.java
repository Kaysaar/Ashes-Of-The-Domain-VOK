package data.kaysaar.aotd.vok.campaign.econ.growingdemand.models;

import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;

public class GrowingDemandConditionEnforcer extends BaseMarketConditionPlugin {

    @Override
    public boolean showIcon() {
        return false;
    }

    @Override
    public void apply(String id) {
        super.apply(id);
        GrowingDemandManager.getInstance().getDemandScripts().forEach(x->x.applyDemandOnMarket(this.market));
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        GrowingDemandManager.getInstance().getDemandScripts().forEach(x->x.unapplyDemandOnMarket(this.market));

    }

}
