package data.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

public class ResearchTechnology implements BaseCommand {
    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        if(args.isEmpty()){
            return CommandResult.BAD_SYNTAX;
        }
        AoTDFactionResearchManager manager = AoTDMainResearchManager.getInstance().getManagerForPlayer();
        ResearchOption option;
        try {
            option = manager.getResearchOptionFromRepo(args);
        }
        catch (NullPointerException exception){
            return CommandResult.ERROR;
        }
        option.setResearched(true);
        if(manager.getCurrentFocus()!=null){
            if(manager.getCurrentFocus().getSpec().getId().equals(option.Id)){
                manager.setCurrentFocus(null);
            }
        }

        MessageIntel intel = new MessageIntel("Faction "+Global.getSector().getPlayerFaction().getDisplayName()+" Researched Technology - " + option.Name, Misc.getBasePlayerColor());
        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO);
        Console.showMessage(" Researched Technology - " + option.Name);

        return CommandResult.SUCCESS;
    }
}
