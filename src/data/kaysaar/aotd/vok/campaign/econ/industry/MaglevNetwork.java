package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;

public class MaglevNetwork extends BaseIndustry {
    @Override
    public void apply() {
        super.apply(true);

    }

    @Override
    public void unapply() {
        super.unapply();
    }



    @Override
    public boolean isTooltipExpandable() {
        return true;
    }


    @Override
    public boolean canImprove() {
        return true;
    }

   @Override
    public String getImproveMenuText() {
        return "Improve railway lines...";
    }

    @Override
    public void addImprovedSection(IndustryTooltipMode mode, TooltipMakerAPI tooltip, boolean expanded) {

    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return false;
    }

    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        if(!mode.equals(ImprovementDescriptionMode.INDUSTRY_TOOLTIP)){
            info.addPara("Each improvement made at a colony doubles the number of " +
                            "" + Misc.STORY + " points required to make an additional improvement.", 0f,
                    Misc.getStoryOptionColor(), Misc.STORY + " points");
            info.addPara("Increase Maglev's synergy efficiency by %s",3f, Color.ORANGE,"30%");
            info.addSpacer(-5f);
        }
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.MAGLEV,market);
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.MAGLEV,market);
    }

    @Override
    protected void applyAICoreModifiers() {
        super.applyAICoreModifiers();
    }
}
