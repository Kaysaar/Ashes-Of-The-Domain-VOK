package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.coreui.CoreUITracker;
import sidebarLib.buttons.sidebarButton;

public class SideBarImpl extends sidebarButton {
    public static  boolean disabled = false;

    @Override
    public void executePrimary() {
        CoreUITracker.setMemFlag(CoreUITracker.getStringForCoreTabResearch());
        CoreUITracker.setMemFlagForTechTab("research");
        Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String getButtonImage() {
        return Global.getSettings().getSpriteName("ui","sidebar_icon_research");
    }

    @Override
    public TooltipMakerAPI.TooltipCreator getButtonTooltip() {
        final SideBarImpl impl = this;
        LabelAPI l1 = Global.getSettings().createLabel(this.getName(), "graphics/fonts/orbitron12condensed.fnt");
        return new TooltipMakerAPI.TooltipCreator() {
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            public float getTooltipWidth(Object tooltipParam) {
                return 300f;
            }

            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                impl.createTooltip(tooltip, expanded);
            }

        };
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
        tooltip.setParaFont("graphics/fonts/orbitron12condensed.fnt");
        super.createTooltip(tooltip, expanded);
    }
}
