package data.kaysaar.aotd.vok.timeline.templates;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.scripts.models.BaseFactionTimelineEvent;

public class ResearchedTechnologyEvent extends BaseFactionTimelineEvent {
    String technologyId;
    public ResearchedTechnologyEvent(String technologyId) {
        this.technologyId = technologyId;
    }

    @Override
    public String getID() {
        return super.getID()+technologyId;
    }

    @Override
    public String getTitleOfEvent() {
        return AoTDMainResearchManager.getInstance().getNameForResearchBd(technologyId);
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec(AoTDIndustries.RESEARCH_CENTER).getImageName();
    }

    @Override
    public boolean checkForCondition() {
        return AoTDMainResearchManager.getInstance().haveFactionResearchedCertainTech(Global.getSector().getPlayerFaction(), technologyId);
    }
    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        super.createDetailedTooltipOnHover(tooltip);
        tooltip.addPara(
                "%s has completed a major technological breakthrough: \"%s.\"",
                10f,
                Misc.getHighlightColor(),
                Global.getSector().getPlayerFaction().getDisplayNameLong(),
                AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(technologyId).getName()
        );
        tooltip.addPara(
                "This advancement opens new paths for development, production, and strategic superiority across multiple domains. " +
                        "The implications of this research will ripple across the Sector—whether through more efficient industries, advanced weaponry, or entirely new capabilities.",
                5f
        );
    }
    @Override
    public void createSmallNoteForEvent(TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "%s has completed research on \"%s\"",
                0f,
                Misc.getTextColor(),
                Global.getSector().getPlayerFaction().getDisplayNameLong(),
                AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(technologyId).getName()

        ).setAlignment(Alignment.MID);
    }

    @Override
    public void updateDataUponEntryOfUI() {
        lastSavedName = AoTDMainResearchManager.getInstance().getNameForResearchBd(technologyId);
    }

    @Override
    public int getPointsForGoal() {
        return 60;
    }
}
