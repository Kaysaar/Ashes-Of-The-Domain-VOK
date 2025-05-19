package data.kaysaar.aotd.vok.timeline.unique;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;
import data.scripts.models.TimelineEventType;
import data.scripts.timelineevents.templates.FirstItemInstalled;
public class HyperdimensionalProcessorEvent extends FirstItemInstalled{
    public HyperdimensionalProcessorEvent() {
        super("omega_processor");
    }

    @Override
    public String getTitleOfEvent() {
        return "Mastery of The Veil";
    }

    @Override
    public TimelineEventType getEventType() {
        return TimelineEventType.UNIQUE;
    }

    @Override
    public String getImagePath() {
        return Global.getSettings().getIndustrySpec(AoTDIndustries.RESEARCH_CENTER).getImageName();
    }

    @Override
    public void createDetailedTooltipOnHover(TooltipMakerAPI tooltip) {
        tooltip.addPara(
                "Installation of the Hyperdimensional Processor has exponentially increased research throughput, unlocking pathways once thought theoretical. " +
                        "With it, your faction can now pursue the most advanced and speculative technologies in the Sector.",
                10f
        );
    }


    @Override
    public int getPointsForGoal() {
        return 70;
    }
}
