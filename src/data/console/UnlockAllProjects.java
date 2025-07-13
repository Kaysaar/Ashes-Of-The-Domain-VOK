package data.console;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import static data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager.memflagBlacksite;

public class UnlockAllProjects implements BaseCommand {
    @Override
    public org.lazywizard.console.BaseCommand.CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return org.lazywizard.console.BaseCommand.CommandResult.WRONG_CONTEXT;
        }
        Global.getSector().getPlayerMemoryWithoutUpdate().set(memflagBlacksite,true);
        BlackSiteProjectManager manager = BlackSiteProjectManager.getInstance();
        for (AoTDSpecialProject option : manager.getProjects().values()) {
            option.setWasEverDiscovered(true);
            option.createIntelForUnlocking();
        }

        Console.showMessage("Unlocked All Black Site Projects");

        return org.lazywizard.console.BaseCommand.CommandResult.SUCCESS;

    }
}
