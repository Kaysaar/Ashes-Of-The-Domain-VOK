package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;

public class SpecialProjectButtonPanel extends UiPanel{
    //TODO - implement special project mechanics in next patch ITS TIME

    public ButtonAPI getSpecialProjectButton() {
        return specialProjectButton;
    }

    ButtonAPI specialProjectButton;
    @Override
    public void createUI() {

        tooltip.setTitleOrbitronLarge();
        LabelAPI labelAPI= tooltip.addPara("Technology Tree of "+ AoTDMainResearchManager.getInstance().getManagerForPlayer().getFaction().getDisplayName(), Color.ORANGE,10f);
        labelAPI.getPosition().inTL(150-labelAPI.computeTextWidth(labelAPI.getText())/2,10);
        tooltip.setParaFontDefault();
        specialProjectButton=tooltip.addButton("Special Projects","UI_SPECIAL_PROJECTS",290,30,10f);
        specialProjectButton.getPosition().inTL(5,52);
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 500;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara("Special projects are special type of research options, that appear once certain criteria are met.\nThese projects unlike research options in the tech tree are not guaranteed to be successful." +
                        "\nCurrently special projects are inaccessible due to being WIP",10f);
            }
        }, TooltipMakerAPI.TooltipLocation.RIGHT,false);

    }
}
