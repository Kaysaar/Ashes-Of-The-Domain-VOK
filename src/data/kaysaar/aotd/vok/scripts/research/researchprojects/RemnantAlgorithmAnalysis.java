package data.kaysaar.aotd.vok.scripts.research.researchprojects;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchProject;

public class RemnantAlgorithmAnalysis extends ResearchProject {
    @Override
    public boolean haveMetReqForProjectToAppear() {
        return false;
    }

    @Override
    public void generateDescriptionForCriticalMoment(TooltipMakerAPI tooltipMakerAPI) {
        tooltipMakerAPI.addSectionHeading("Field Destabilization", Alignment.MID,10f);
        tooltipMakerAPI.addPara("A critical malfunction has occurred in the R&D department, resulting in destabilization of the field generator.", Misc.getTooltipTitleAndLightHighlightColor(),10f);
        tooltipMakerAPI.addPara("The generator's erratic fluctuations are causing multiple anomalies throughout the facility, including power surges, unpredictable teleportation of personel, and malfunctions of life support systems.", Misc.getTooltipTitleAndLightHighlightColor(),10f);

    }
}
