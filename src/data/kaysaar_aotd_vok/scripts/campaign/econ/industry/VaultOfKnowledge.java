package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class VaultOfKnowledge extends BaseIndustry {
    @Override
    public void apply() {
        this.setHidden(true);
        if(market.getFaction().isPlayerFaction()&&market.getMemoryWithoutUpdate().contains("$aotd_vok_databank")){
            this.special = new SpecialItemData("aotd_vok_databank_pristine",(String)market.getMemoryWithoutUpdate().get("$aotd_vok_databank"));
            market.getMemory().unset("$aotd_vok_databank");
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
