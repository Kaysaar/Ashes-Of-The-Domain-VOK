package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.models.ResearchProject;
import data.kaysaar.aotd.vok.models.SpecialProjectStage;

import java.awt.*;
import java.util.ArrayList;

public class SpecialProjectBarComponent extends UiPanel {
    public ResearchProject projectOfIntrest;

    public CustomPanelAPI getBar() {
        return bar;
    }

    public void setProjectOfIntrest(ResearchProject projectOfIntrest) {
        this.projectOfIntrest = projectOfIntrest;
    }

    ArrayList<ButtonAPI> buttons = new ArrayList<>();
    CustomPanelAPI bar;
    float widthOfBar;

    public void setHeightOfUI(float heightOfUI) {
        this.heightOfUI = heightOfUI;
    }

    float heightOfUI;

    public void setWidthOfBar(float height) {
        this.widthOfBar = height;
    }


    @Override
    public void createUI() {
        bar = panel.createCustomPanel(widthOfBar, 25, null);
        tooltip.addSectionHeading("Progression towards  completing project : " + String.format("%.2f", 100 * projectOfIntrest.calculateProgress()) + "%", Alignment.MID, 10f).getPosition().inTL(0, 1);
        panel.addComponent(bar).inTL(0, 20);
        tooltip.addPara("Special projects are enormous undertaking in terms of scientific capabilities in the Persean Sector", 10f).getPosition().inTL(5, 65);
        tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
        tooltip.addPara("Project Specification", Color.ORANGE, 20f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        if (!projectOfIntrest.spec.isRepeatable) {
            tooltip.addPara("Warning! Due to nature and scope of project, we can only give it one shot!", Misc.getNegativeHighlightColor(), 10f);
        } else {
            tooltip.addPara("Due to abundance of resources needed for this project we can do it multiple times", Misc.getPositiveHighlightColor(), 10f);
        }
        tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
        tooltip.addPara("Project Description", Color.ORANGE, 20);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        tooltip.addPara(projectOfIntrest.spec.projectDescription, 10);
        tooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
        LabelAPI footer = tooltip.addPara("Project Log", Color.ORANGE, 20f);
        tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        float remainingHeight = heightOfUI + footer.getPosition().getY();
        TooltipMakerAPI tooltipMakerAPI = panel.createUIElement(widthOfBar - 20, remainingHeight - 6, true);
        projectOfIntrest.generateDescriptionForCurrentResults(tooltipMakerAPI);


        panel.addUIElement(tooltipMakerAPI).inTL(-5, -footer.getPosition().getY() + 5);
    }


}
