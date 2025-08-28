package data.kaysaar.aotd.vok.campaign.econ.synergies.impl.sources;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Industries;

public class SpaceportSource extends BaseIndustrySynergySource{


    public SpaceportSource(float baseEfficiency, String id) {
        super(baseEfficiency, id);
    }

    @Override
    public float getBonusForImproved() {
        if(id.equals(Industries.SPACEPORT)){
            return 0.05f;
        }
        return 0.1f;
    }

    @Override
    public float getBonusForAICore(String aiCoreID) {
        if(Commodities.ALPHA_CORE.equals(aiCoreID)){
            if(this.id.equals(Industries.SPACEPORT)){
                return 0.05f;
            }
            return 0.1f;
        }
        return 0f;
    }
}
