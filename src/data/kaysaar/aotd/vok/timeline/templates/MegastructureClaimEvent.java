package data.kaysaar.aotd.vok.timeline.templates;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.scripts.models.BaseFactionTimelineEvent;
import data.scripts.models.TimelineEventType;
import org.lazywizard.console.Console;

import java.awt.*;

public class MegastructureClaimEvent extends BaseFactionTimelineEvent {
    String megastructureID;
    String title;
    String imageName;
    
    @Override
    public String getID() {
        return super.getID()+ megastructureID;
    }

    public MegastructureClaimEvent(String megastructureID, String title, String imageName){
        this.megastructureID = megastructureID;
//        Console.showMessage("MegastructureClaimEvent - Megastructure ID = "+this.megastructureID);
        this.title = title;
        this.imageName = imageName;
    }

    @Override
    public String getImagePath() {
        return imageName;
    }

    @Override
    public String getTitleOfEvent() {
        return title;
    }

    @Override
    public boolean checkForCondition() {
        return GPManager.getInstance().getMegastructures().stream().anyMatch(x->x.getSpec().getMegastructureID().equals(megastructureID));
    }

    @Override
    public void createSmallNoteForEvent(TooltipMakerAPI tooltip) {
        tooltip.addPara("%s has been claimed by "+Global.getSector().getPlayerFaction().getDisplayNameLong(),0f, Color.ORANGE,GPManager.getInstance().getMegaSpecFromList(megastructureID).getName()).setAlignment(Alignment.MID);;
    }

    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        super.createDetailedTooltipOnHover(tooltip);
        String megaName = GPManager.getInstance().getMegaSpecFromList(megastructureID).getName();
        String factionName = Global.getSector().getPlayerFaction().getDisplayNameLong();

        tooltip.addPara(
                "%s has successfully asserted control over the ancient megastructure \"%s.\"",
                10f,
                Misc.getHighlightColor(),
                factionName,
                megaName
        );

        tooltip.addPara(
                "Once a marvel of pre-Collapse engineering, \"%s\" now lies dormant—weathered by time, neglect, and stellar decay. "
                        + "Its systems are barely operational, held together by residual automation and stubborn architecture.",
                5f,
                Misc.getHighlightColor(),
                megaName
        );

        tooltip.addPara(
                "Claiming it is only the beginning. Full functionality will demand massive resource investment, technical expertise, and patient restoration efforts. "
                        + "But once awakened, few assets in the Sector could rival its potential.",
                5f
        );
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.RESEARCH_AND_EXPLORATION;
    }

    @Override
    public int getPointsForGoal() {
        return 50;
    }
}
