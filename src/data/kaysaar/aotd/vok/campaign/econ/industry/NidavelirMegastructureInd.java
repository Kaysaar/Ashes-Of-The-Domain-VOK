package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;

public class NidavelirMegastructureInd extends BaseMegastructureIndustry{
    @Override
    public void applyInMega() {
        if(megastructure.isFullyRestored()){
            this.getMarket().getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                    .modifyFlat(spec.getId(), 4f, Misc.ucFirst(spec.getName().toLowerCase()));
            market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).modifyFlat(getModId(), 2f,"Nidavelir Megastructure");

        }

    }

    @Override
    public void unapply() {
        super.unapply();
        this.getMarket().getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT)
                .unmodifyFlat(spec.getId());
        market.getStats().getDynamic().getMod(Stats.PRODUCTION_QUALITY_MOD).unmodifyFlat(getModId());
    }

}
