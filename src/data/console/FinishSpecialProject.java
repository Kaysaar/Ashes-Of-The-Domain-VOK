package data.console;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectCompletionListener;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProjectStage;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class FinishSpecialProject implements BaseCommand {
    @Override
    public org.lazywizard.console.BaseCommand.CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        if(args.isEmpty()){
            return CommandResult.BAD_SYNTAX;
        }
        AoTDSpecialProject option;
        try {
            option = SpecialProjectManager.getInstance().getProject(args);
        }
        catch (NullPointerException exception){
            return CommandResult.ERROR;
        }
        option.wasCompleted = true;
        option.countOfCompletion++;
        for (AoTDSpecialProjectStage stage : option.getStages()) {
            stage.setProgress(stage.getSpec().getDays());
        }
        Object reward = option.grantReward();
        Global.getSector().getListenerManager().getListeners(SpecialProjectCompletionListener.class).forEach(x->x.completedProject(option.specID,reward));
        option.sentFinishNotification();
        SpecialProjectManager.getInstance().setCurrentlyOnGoingProject(null);;
        Console.showMessage(" Finished  Project - " + option.getNameOverride());

        return CommandResult.SUCCESS;
    }

}
