package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud;

import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;

public class BindingShadowProject extends ShroudBasedProject{
    @Override
    public int getRequiredShroudExpertLevel() {
        return 3;

    }

    @Override
    public boolean checkIfProjectShouldUnlock() {
        return super.checkIfProjectShouldUnlock()&& BlackSiteProjectManager.getInstance().getProject("aotd_tenebrium_weapons").checkIfProjectWasCompleted();
    }
}
