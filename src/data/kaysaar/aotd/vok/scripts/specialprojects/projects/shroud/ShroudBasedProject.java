package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import org.lazywizard.lazylib.MathUtils;

public class ShroudBasedProject extends AoTDSpecialProject {
    public int getRequiredShroudExpertLevel(){
        return 0;
    }
    @Override
    public boolean checkIfProjectShouldUnlock() {
        return ShroudProjectMisc.getLevelOfUnderstanding()>=getRequiredShroudExpertLevel()&&!ShroudProjectMisc.isCooldownBetweenProject();
    }
    @Override
    public void createIntelForUnlocking() {
        super.createIntelForUnlocking();
        ShroudProjectMisc.setCooldownBetweenProjects(MathUtils.getRandomNumberInRange(5,10));
    }

    @Override
    public void projectCompleted() {
        ShroudProjectMisc.increaseAmountOfProjectsOnLevel(getRequiredShroudExpertLevel());
    }
}
