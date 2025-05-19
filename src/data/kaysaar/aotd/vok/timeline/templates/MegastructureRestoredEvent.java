package data.kaysaar.aotd.vok.timeline.templates;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.scripts.models.BaseFactionTimelineEvent;
import data.scripts.models.TimelineEventType;

import java.awt.*;

public class MegastructureRestoredEvent extends BaseFactionTimelineEvent {
    String megastrucutreID;
    String title;
    String imageName;

    @Override
    public String getID() {
        return super.getID();
    }

    public MegastructureRestoredEvent(String megastructureID, String title, String imageName){
        this.megastrucutreID = megastructureID;
        this.title = title;
        this.imageName = imageName;
    }

    @Override
    public String getImagePath() {
        return imageName;
    }

    @Override
    public String getTitleOfEvent() {
        return title+" Restored";
    }

    @Override
    public boolean checkForCondition() {
        return GPManager.getInstance().getMegastructures().stream().anyMatch(x->x.getSpec().getMegastructureID().equals(megastrucutreID)&&x.isFullyRestored());
    }

    @Override
    public void createSmallNoteForEvent(TooltipMakerAPI tooltip) {
        tooltip.addPara("%s has been fully restored by "+ Global.getSector().getPlayerFaction().getDisplayNameLong(),0f, Color.ORANGE,GPManager.getInstance().getMegaSpecFromList(megastrucutreID).getName()).setAlignment(Alignment.MID);;
    }

    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        super.createDetailedTooltipOnHover(tooltip);
        String megaName = GPManager.getInstance().getMegaSpecFromList(megastrucutreID).getName();
        String factionName = Global.getSector().getPlayerFaction().getDisplayNameLong();

        tooltip.addPara(
                "%s has completed the full restoration of the ancient megastructure \"%s.\"",
                10f,
                Misc.getHighlightColor(),
                factionName,
                megaName
        );

        tooltip.addPara(
                "Once a marvel of pre-Collapse engineering, \"%s\" stood as a silent witness to a forgotten age—scarred by time, stellar decay, and abandonment. "
                        + "Its systems were long dormant, sustained only by ghost routines and resilient construction.",
                5f,
                Misc.getHighlightColor(),
                megaName
        );

        tooltip.addPara(
                "Through a monumental restoration effort, it now hums with life once more. Fully reactivated, it stands as a symbol of your faction's technical mastery and ambition. "
                        + "Its capabilities promise to reshape the balance of power in the Sector.",
                5f
        );
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.UNIQUE;
    }

    @Override
    public int getPointsForGoal() {
        return 250;
    }
}
