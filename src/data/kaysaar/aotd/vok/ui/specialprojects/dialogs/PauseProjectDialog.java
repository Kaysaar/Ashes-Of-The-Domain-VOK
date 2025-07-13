package data.kaysaar.aotd.vok.ui.specialprojects.dialogs;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs.BasePopUpDialog;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.AoTDSpecialProject;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectUIManager;

public class PauseProjectDialog extends BasePopUpDialog {
    SpecialProjectUIManager manager;
    AoTDSpecialProject project;

    public PauseProjectDialog(String headerTitle,SpecialProjectUIManager manager, AoTDSpecialProject project) {
        super(headerTitle);
        this.manager = manager;
        this.project = project;
    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        super.createUI(panelAPI);
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        tooltip.setParaInsigniaLarge();
        tooltip.addPara("By pausing this project we can allocate resources somewhere else.",5f);
        tooltip.addPara("Warning : this will reset progress of all stages that are not fully completed!", Misc.getNegativeHighlightColor(),5f);

    }

    @Override
    public void applyConfirmScript() {
        for (String currentlyAttemptedStage : project.getCurrentlyAttemptedStages()) {
            if(project.getStage(currentlyAttemptedStage).getProgressComputed()!=1){
                project.getStage(currentlyAttemptedStage).setProgress(0);
            }
        }
        BlackSiteProjectManager.getInstance().setCurrentlyOnGoingProject(null);
        manager.getCurrProjectShowcase().setProject(null);
        manager.getCurrProjectShowcase().createUI();
        manager.getListManager().createListUI();
        manager.getShowcaseProj().createUI();
        project.getCurrentlyAttemptedStages().clear();
    }
}
