package data.kaysaar.aotd.vok.timeline.military;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class TierFourStationLowTechEvent extends FirstIndustryEvent {
    public TierFourStationLowTechEvent( String entityId) {
        super(AoTDIndustries.STAR_CITADEL_LOW, entityId);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.MILITARY;
    }

    @Override
    public String getTitleOfEvent() {
        return "Chicomoztoc's Shield";
    }

    @Override
    public int getPointsForGoal() {
        return 90;
    }
}
