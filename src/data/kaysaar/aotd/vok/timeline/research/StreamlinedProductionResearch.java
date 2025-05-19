package data.kaysaar.aotd.vok.timeline.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.timeline.templates.ResearchedTechnologyEvent;

public class StreamlinedProductionResearch extends ResearchedTechnologyEvent {
    public StreamlinedProductionResearch() {
        super(AoTDTechIds.STREAMLINED_PRODUCTION);
    }

    @Override
    public String getTitleOfEvent() {
        return "Catalyst Protocol";
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec(AoTDIndustries.CRYSTALIZATOR).getImageName();
    }



}
