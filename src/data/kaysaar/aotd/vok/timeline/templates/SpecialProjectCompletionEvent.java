package data.kaysaar.aotd.vok.timeline.templates;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.scripts.models.BaseFactionTimelineEvent;
import data.scripts.models.TimelineEventType;

import java.awt.*;

public class SpecialProjectCompletionEvent extends BaseFactionTimelineEvent {
    String projectID;
    public SpecialProjectCompletionEvent(String projectID) {
        this.projectID = projectID;
    }

    @Override
    public String getTitleOfEvent() {
        return BlackSiteProjectManager.getInstance().getProject(projectID).getProjectSpec().getName();
    }

    @Override
    public String getID() {
        return super.getID()+projectID;
    }

    @Override
    public boolean checkForCondition() {
        return BlackSiteProjectManager.getInstance().getProject(projectID) != null&& BlackSiteProjectManager.getInstance().getProject(projectID).checkIfProjectWasCompleted();
    }

    @Override
    public void createSmallNoteForEvent(TooltipMakerAPI tooltip) {
        tooltip.addPara("%s was completed",0f, Color.ORANGE, BlackSiteProjectManager.getInstance().getProject(projectID).getProjectSpec().getName()).setAlignment(Alignment.MID);
    }

    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        tooltip.addPara("%s was completed, marking it a significant milestone.",5f, Color.ORANGE, BlackSiteProjectManager.getInstance().getProject(projectID).getProjectSpec().getName());

    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getSpriteName("illustrations","galatia_academy");
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.UNIQUE;
    }

    @Override
    public int getPointsForGoal() {
        return 15;
    }
}
