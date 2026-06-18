package data.kaysaar.aotd.vok.timeline.prosperity;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class GardensOfElysiumEvent extends FirstIndustryEvent {
    public GardensOfElysiumEvent( String entityId) {
        super(AoTDIndustries.GARDEN_OF_ELYSIUM, entityId);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.PROSPERITY;
    }

    @Override
    public String getTitleOfEvent() {
        return "Daughter Of Elysium";
    }

    @Override
    public int getPointsForGoal() {
        return 70;
    }
}
