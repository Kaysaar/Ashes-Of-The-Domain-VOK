package data.kaysaar.aotd.vok.timeline.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.timeline.templates.ResearchedTechnologyEvent;

public class StellaManufactoriumResearch extends ResearchedTechnologyEvent {
    public StellaManufactoriumResearch() {
        super(AoTDTechIds.STELLA_MANUFACTORIUM);
    }

    @Override
    public String getTitleOfEvent() {
        return "From the Ashes";
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec("stella_manufactorium").getImageName();
    }


}
