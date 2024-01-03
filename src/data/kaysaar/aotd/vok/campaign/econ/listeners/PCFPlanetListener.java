package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.listeners.SurveyPlanetListener;
import com.fs.starfarer.api.impl.campaign.intel.PCFPlanetIntel;

public class PCFPlanetListener implements SurveyPlanetListener {

    @Override
    public void reportPlayerSurveyedPlanet(PlanetAPI planet) {
        // Condition to test for goes here
        if (planet.hasCondition("pre_collapse_facility")) {
            makePlanetIntelReport(planet);
        }
    }

    // If you want to create an intel manually simply call this function
    public static void makePlanetIntelReport(PlanetAPI planet) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();

        // Checks if this intel exists already
        for (IntelInfoPlugin intel : intelManager.getIntel(PCFPlanetIntel.class)) {
            PCFPlanetIntel pl = (PCFPlanetIntel) intel;
            if (pl.getPlanet() == planet) {
                return;
            }
        }

        PCFPlanetIntel report = new PCFPlanetIntel(planet);
        // Should the report appear in the "New" tab?
        report.setNew(true);
        // Adds the intel, can force no notification
        intelManager.addIntel(report, false);
    }
}
