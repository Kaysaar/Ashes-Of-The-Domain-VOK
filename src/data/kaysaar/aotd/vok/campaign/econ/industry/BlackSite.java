package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;

import java.awt.*;

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
        if (IndustryTooltipMode.ADD_INDUSTRY.equals(mode)) {
            tooltip.addPara("Building that structure will enable your faction to research new technologies.", Misc.getHighlightColor(), 10f);
        }
        tooltip.addSectionHeading("Research costs", Alignment.MID, 10f);
        tooltip.addPara("Upkeep costs of the research facility are dependent on what is being currently researched.", 3f);

        if (this.market.hasCondition("pre_collapse_facility")) {
            tooltip.addPara("By building this facility here, our scientists will be able to analyze local pre-collapse ruins.", Misc.getPositiveHighlightColor(), 10f);
        }
        if (IndustryTooltipMode.NORMAL.equals(mode)) {
            tooltip.addSectionHeading("Currently ongoing research",Alignment.MID,10f);

            if (AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus() != null) {
                tooltip.addPara("Researching : %s",10, Color.ORANGE,AoTDMainResearchManager.getInstance().getManagerForPlayerFaction().getCurrentFocus().getSpec().getName());
            }
            else{
                tooltip.addPara("%s",10, Color.ORANGE,"Nothing is being researched.");
            }
        }
        tooltip.addSectionHeading("Black Site costs",Misc.getTextColor(), Global.getSector().getFaction(Factions.PIRATES).getBaseUIColor(),Alignment.MID,10f);
        tooltip.addPara("Due to this facility being black site, it requires additional funds for covering up operations, based on stability of colony",3f);


    }
    @Override
    public boolean isAvailableToBuild() {
        return SpecialProjectManager.getInstance().canEngageInBlackSite();
    }
}
