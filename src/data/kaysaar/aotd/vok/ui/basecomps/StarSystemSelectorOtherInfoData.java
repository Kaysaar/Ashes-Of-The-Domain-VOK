package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface StarSystemSelectorOtherInfoData {
    public String getNameForLabel();
    public void populateLabel(TooltipMakerAPI tooltip, StarSystemAPI system,float width ,float height);
}
