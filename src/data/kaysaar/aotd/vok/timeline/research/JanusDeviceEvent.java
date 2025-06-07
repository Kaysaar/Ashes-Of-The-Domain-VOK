package data.kaysaar.aotd.vok.timeline.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.timeline.templates.ResearchedTechnologyEvent;

public class JanusDeviceEvent extends ResearchedTechnologyEvent {
    public JanusDeviceEvent() {
        super(AoTDTechIds.JANUS_DEVICE_ANALYSIS);
    }

    @Override
    public String getTitleOfEvent() {
        return "Janus Analysis";
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec(AoTDIndustries.RESEARCH_CENTER).getImageName();
    }


}
