package data.kaysaar.aotd.vok.timeline.research;

import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class ResearchFacilityEvent extends FirstIndustryEvent {
    public ResearchFacilityEvent( String entityId) {
        super(AoTDIndustries.RESEARCH_CENTER, entityId);
    }

    @Override
    public String getTitleOfEvent() {
        return "Research and Development";
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.RESEARCH_AND_EXPLORATION;
    }

    @Override
    public int getPointsForGoal() {
        return 70;
    }


}
