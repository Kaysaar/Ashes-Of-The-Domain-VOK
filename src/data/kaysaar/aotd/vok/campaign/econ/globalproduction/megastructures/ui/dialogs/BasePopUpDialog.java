package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.dialogs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class BasePopUpDialog extends PopUpUI {
    TooltipMakerAPI headerTooltip;
    String title;
    public float x,y;
    public BasePopUpDialog(String headerTitle) {
        this.title = headerTitle;
        x=15;
        y=45;
    }

    @Override
    public void renderBelow(float alphaMult) {
        super.renderBelow(alphaMult);
        if(headerTooltip != null) {
            MegastructureUIMisc.drawRectangleFilledForTooltip(headerTooltip,1f,  Global.getSector().getPlayerFaction().getDarkUIColor().darker());
        }
    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createHeaader(panelAPI);

        TooltipMakerAPI tooltip = panelAPI.createUIElement(panelAPI.getPosition().getWidth()-30,panelAPI.getPosition().getHeight()-y,true);
        createContentForDialog(tooltip,panelAPI.getPosition().getWidth()-30);
        panelAPI.addUIElement(tooltip).inTL(x,y);
        createConfirmAndCancelSection(panelAPI);


    }

    public void createHeaader(CustomPanelAPI panelAPI) {
        if(AoTDMisc.isStringValid(title)) {
            headerTooltip = panelAPI.createUIElement(panelAPI.getPosition().getWidth()-30,20,false);
            headerTooltip.setParaFont(Fonts.ORBITRON_20AABOLD);
            LabelAPI label = headerTooltip.addPara(title,Misc.getTooltipTitleAndLightHighlightColor(),5f);
            panelAPI.addUIElement(headerTooltip).inTL(15,10);
            float width = label.computeTextWidth(label.getText());
            label.getPosition().setLocation(0,0).inTL((panelAPI.getPosition().getWidth()/2)-(width/2),3);
        }
        else {
            y = 10;
        }
    }

    public void createContentForDialog(TooltipMakerAPI tooltip,float width){

    }

    @Override
    public void applyConfirmScript() {

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }

}
