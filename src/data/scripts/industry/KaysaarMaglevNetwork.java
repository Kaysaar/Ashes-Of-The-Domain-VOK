package data.scripts.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class KaysaarMaglevNetwork extends BaseIndustry {
    @Override
    public void apply() {

    }

    @Override
    public void unapply() {
        super.unapply();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        int size = market.getSize();

    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }


    @Override
    protected void applyAICoreModifiers() {
        super.applyAICoreModifiers();
    }
}
