package data.kaysaar.aotd.vok.timeline.prosperity;

import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstIndustryEvent;

public class MiningMegaplexEvent extends FirstIndustryEvent {
    public MiningMegaplexEvent( String entityId) {
        super("aotd_mining_megaplex", entityId);
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.PROSPERITY;
    }

    @Override
    public String getTitleOfEvent() {
        return "Rock and Stone";
    }

    @Override
    public int getPointsForGoal() {
        return 40;
    }
}
