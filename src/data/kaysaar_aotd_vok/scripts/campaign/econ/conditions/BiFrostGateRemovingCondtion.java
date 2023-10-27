package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar_aotd_vok.scripts.campaign.econ.industry.BiFrostGate;

public class BiFrostGateRemovingCondtion extends BaseMarketConditionPlugin {
    public SectorEntityToken gate = null;
    @Override
    public void apply(String id) {
        if (this.market.hasIndustry("bifrost")) {
            BiFrostGate biFrostGate = (BiFrostGate) this.market.getIndustry("bifrost");
            if (biFrostGate.isFunctional()) {
                this.gate = biFrostGate.gate;
            }
        } else {
            if (this.gate != null) {
                Misc.fadeAndExpire(gate);
                this.market.removeCondition(this.getModId());
            }
        }
    }

    @Override
    public boolean showIcon() {
        return false;
    }
}
