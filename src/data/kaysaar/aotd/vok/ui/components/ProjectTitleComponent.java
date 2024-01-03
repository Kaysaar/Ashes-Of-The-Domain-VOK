package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.models.ResearchProject;

import java.awt.*;

public class ProjectTitleComponent extends UiPanel{
    ButtonAPI buttonAPI;

    public void setProject(ResearchProject project) {
        this.project = project;
    }

    public ButtonAPI getButtonAPI() {
        return buttonAPI;
    }

    ResearchProject project;
    @Override
    public void createUI() {
        tooltip.setParaFont(Fonts.ORBITRON_24AABOLD);
        tooltip.addPara("Special Project : "+project.spec.nameOfProject,Color.ORANGE,15f);
        tooltip.setParaFontDefault();
        buttonAPI= tooltip.addButton("Start Project",project.id,200,26,15f);
        buttonAPI.setEnabled(!project.currentlyOngoing&&project.haveMetReqForProject());

        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 0;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                project.generateTooltipInfoForProject(tooltip);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW,false);
        buttonAPI.getPosition().inTL(tooltip.getWidthSoFar()-230,13);
    }

    @Override
    public void render(Color colorOfRender, float alphamult) {

    }
}
