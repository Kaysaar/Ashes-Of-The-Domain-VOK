package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

public class BlackSite extends ResearchFacility{

    @Override
    public void apply() {
        super.apply();
        int stab = market.getStability().getModifiedInt();
        float reductionMult = 1;
        if(getAICoreId()!=null) {
            if (getAICoreId().equals(Commodities.ALPHA_CORE)) {
                reductionMult = 0.5f;
            }
            if (getAICoreId().equals(Commodities.BETA_CORE)) {
                reductionMult = 0.75f;
            }
            if (getAICoreId().equals(Commodities.GAMMA_CORE)) {
                reductionMult = 0.9f;
            }
        }
        if(stab>10){
            stab =10;
        }
        int negatedStab = stab-10;

        this.getUpkeep().modifyFlat("aotd_research_3",20000*Math.abs(negatedStab)*reductionMult,"Black Site Cover-Up Cost");

    }

    @Override
    public void unapply() {
        super.unapply();
        this.getUpkeep().unmodifyFlat("aotd_research_3");

    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
    @Override
    protected void addRightAfterDescriptionSection(TooltipMakerAPI tooltip, IndustryTooltipMode mode) {
        super.addRightAfterDescriptionSection(tooltip, mode);
        tooltip.addSectionHeading("Black Site costs",Misc.getTextColor(), Global.getSector().getFaction(Factions.PIRATES).getDarkUIColor(),Alignment.MID,10f);
        tooltip.addPara("Due to this facility being black site, it requires additional funds for covering up operations, based on stability of colony",3f);
        tooltip.addPara("Each additional black site (up to 4) provides 5% special project speed bonus",Misc.getTooltipTitleAndLightHighlightColor(),3f );


    }
    @Override
    public boolean isAvailableToBuild() {
        return BlackSiteProjectManager.getInstance().canEngageInBlackSite();
    }
}
