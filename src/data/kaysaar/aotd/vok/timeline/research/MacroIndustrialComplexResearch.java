package data.kaysaar.aotd.vok.timeline.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.timeline.templates.ResearchedTechnologyEvent;

public class MacroIndustrialComplexResearch extends ResearchedTechnologyEvent {
    public MacroIndustrialComplexResearch() {
        super(AoTDTechIds.MACRO_INDUSTRIAL_COMPLEX);
    }

    @Override
    public String getTitleOfEvent() {
        return "From the Ashes";
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec(AoTDIndustries.MACRO_INDUSTRIAL_COMPLEX).getImageName();
    }


}
