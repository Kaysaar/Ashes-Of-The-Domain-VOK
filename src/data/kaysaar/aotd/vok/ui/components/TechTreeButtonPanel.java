package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;

import java.awt.*;

public class TechTreeButtonPanel extends UiPanel{
    //TODO - implement special project mechanics in next patch ITS TIME

    public ButtonAPI getTechTreeButton() {
        return techTreeButton;
    }
    ButtonAPI techTreeButton;
    @Override
    public void createUI() {
        tooltip.setTitleOrbitronLarge();
        LabelAPI labelAPI= tooltip.addPara("Special Projects of  "+ AoTDMainResearchManager.getInstance().getManagerForPlayer().getFaction().getDisplayName(), Color.ORANGE,10f);
        labelAPI.getPosition().inTL(150-labelAPI.computeTextWidth(labelAPI.getText())/2,10);
        tooltip.setParaFontDefault();
        techTreeButton =tooltip.addButton("Tech Tree","UI_TECH_TREE",290,30,10f);
        techTreeButton.getPosition().inTL(5,52);

    }
}
