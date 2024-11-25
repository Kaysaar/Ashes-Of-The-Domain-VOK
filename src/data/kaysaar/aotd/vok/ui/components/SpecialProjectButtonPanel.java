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
        LabelAPI labelAPI= tooltip.addPara("Technology Tree", Color.ORANGE,10f);
        labelAPI.getPosition().inTL(150-labelAPI.computeTextWidth(labelAPI.getText())/2,10);
        tooltip.setParaFontDefault();

    }
}
