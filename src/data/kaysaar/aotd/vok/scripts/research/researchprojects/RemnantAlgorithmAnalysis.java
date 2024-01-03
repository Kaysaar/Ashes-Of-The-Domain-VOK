package data.kaysaar.aotd.vok.scripts.research.researchprojects;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.models.ResearchProject;

public class RemnantAlgorithmAnalysis extends ResearchProject {
    @Override
    public boolean haveMetReqForProjectToAppear() {
        return false;
    }

    @Override
    public void generateDescriptionForCriticalMoment(TooltipMakerAPI tooltipMakerAPI) {
        tooltipMakerAPI.addSectionHeading("Field Destabilization", Alignment.MID,10f);
        tooltipMakerAPI.addPara("A critical event has occurred in the R&D department, resulting in the destabilization of the field generator.", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltipMakerAPI.addPara("The generator's erratic fluctuations are causing anomalies throughout the facility, including power surges, random teleportation, and unpredictable environmental effects.", Misc.getTooltipTitleAndLightHighlightColor(),10f);

    }
}
