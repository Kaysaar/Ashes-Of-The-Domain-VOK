package data.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.util.Misc;

import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.awt.*;

@SuppressWarnings("unused")
public class TestRing implements BaseCommand {

    @Override
    public CommandResult runCommand(String args, CommandContext context) {

        //Checks if used in campaign
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        LocationAPI currentLoc = playerFleet.getContainingLocation();

        //Checks if used in Hyperspace
        if (currentLoc.isHyperspace() || !(currentLoc instanceof StarSystemAPI)) {
            Console.showMessage("Not in Star System.");
            return CommandResult.WRONG_CONTEXT;
        }

        StarSystemAPI system = playerFleet.getStarSystem();
        PlanetAPI planet = (PlanetAPI) Misc.findNearestPlanetTo(playerFleet, false, false);

        //Does the thing
        if (planet != null) {
            system.addRingBand(planet, "misc", "AotD_rings", 256f, 3, Color.white, 256f, planet.getRadius() + 75, 40f);
            /**
             * Quick explanation about ring sprites:
             * Each image has four rings we can choose from (0 to 3, in our case only 3 has textures).
             * The length of each ring in the image is defined by bandWidthInTexture (always 256f).
             * Which ring we choose is defined by bandIndex.
             *
             * @param focus -The planet you want the ring on.
             * @param category -The category in settings.json.
             * @param key -The key in the selected category.
             * @param bandWidthInTexture -DON'T TOUCH. Always 256f.
             * @param bandIndex -DON'T TOUCH. Always 3.
             * @param color -The color of the ring. Leaving this to white is ideal.
             * @param bandWidthInEngine -The bandwidth in the engine. Can be used to increase or decrease ring size. Ideally this should be equal to bandWidthInTexture for our purpose.
             *                          If you want to change this I recommend picking a number that is a sub-multiple of bandWidthInTexture.
             * @param middleRadius -The radius of the ring starting from the mid-point of the band. Ideally use planet.getRadius() and add 75 (based on how the original mod did it).
             * @param orbitDays -The time it takes the ring to orbit around its planet. Current value is what the original mod used.
             **/
        }
        return CommandResult.SUCCESS;
    }
}
