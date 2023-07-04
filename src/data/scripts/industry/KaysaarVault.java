package data.scripts.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class KaysaarVault extends BaseIndustry {
    @Override
    public void apply() {
        this.setHidden(true);
        if(market.getFaction().isPlayerFaction()&&this.special!=null){
            Misc.getStorageCargo(this.getMarket()).addSpecial(this.special, 1);
            this.special=null;
        }
    }
    @Override
    public boolean isAvailableToBuild() {
        return false;
    }
    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
    @Override
    public boolean canBeDisrupted(){
        return false;
    };
    @Override
    public void createTooltip(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {
        return;
    }


}
