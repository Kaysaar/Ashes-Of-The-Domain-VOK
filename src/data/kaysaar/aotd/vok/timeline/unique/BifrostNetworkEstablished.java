package data.kaysaar.aotd.vok.timeline.unique;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.timeline.templates.MegastructureClaimEvent;
import data.scripts.models.TimelineEventType;

public class BifrostNetworkEstablished extends MegastructureClaimEvent {
    public BifrostNetworkEstablished() {
        super(
                "aotd_bifrost", BifrostMegastructureManager.getInstance().getMegastructure().getName(), BifrostMegastructureManager.getInstance().getMegastructure().getCurrentImage()
        );

    }

    @Override
    public String getTitleOfEvent() {
        return "The New Bridge";
    }

    @Override
    public void createSmallNoteForEvent(TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "The Bifrost Network is operational — a first step toward bridging the stars, even if it falls short of Domain-era marvels.",
                0f,
                Misc.getHighlightColor()
        ).setAlignment(Alignment.MID);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.UNIQUE;
    }

    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "Rather than salvage the remains of the Domain’s shattered gate network, your scientists took another path: invention. The Bifrost Network is the first homegrown faster-than-light transit system since the Collapse.",
                10f,
                Misc.getHighlightColor()
        );

        tooltip.addPara(
                "Unlike the ancient gates, Bifrost nodes do not form a seamless interstellar web — not yet. Their range is heavly limited compared to the Gates and their activation volatile. But they work. And most importantly, they are yours.",
                5f
        );

        tooltip.addPara(
                "While no match for the Domain’s forgotten masterpieces, Bifrost is proof that the Sector can stand on its own — and perhaps, one day, surpass the old empires that once ruled it.",
                5f
        );
    }

    @Override
    public boolean checkForCondition() {
        if(AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.BIFROST_GATE)){
            return BifrostMegastructureManager.getInstance().getMegastructure().getBuiltSections().size() >= 2;
        }
        return false;
    }
}
