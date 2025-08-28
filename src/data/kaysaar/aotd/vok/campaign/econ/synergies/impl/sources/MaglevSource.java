package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class MaglevSource extends BaseIndustrySynergySource{
    public MaglevSource(float baseEfficiency, String id) {
        super(baseEfficiency, id);
    }

    @Override
    public float getBonusForImproved() {
        return 0.3f;
    }

    @Override
    public float getBonusForAICore(String aiCoreID) {
        if(Commodities.ALPHA_CORE.equals(aiCoreID)){
            return 0.2f;
        }
        return 0;
    }
}
