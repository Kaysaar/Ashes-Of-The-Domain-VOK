package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.procgen.MarkovNames;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

import java.util.List;

public class ColonyDevelopmentNameComponent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI componentPanel;
    TextFieldAPI field;
    ButtonAPI buttonToGenerateName;

    public ColonyDevelopmentNameComponent(float width,float height){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        createUI();
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(componentPanel!=null){
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(),componentPanel.getPosition().getHeight(),false);
        String font = "graphics/fonts/insignia21LTaa.fnt";
        tooltip.setParaFont(font);
        LabelAPI label  = tooltip.addPara("Colony name :", Misc.getTooltipTitleAndLightHighlightColor(),2f);
        field = tooltip.addTextField(250,25f,font,0);
        field.getPosition().inTL(label.computeTextWidth(label.getText())+10,0);
        tooltip.setButtonFontOrbitron20();
        generateAndApplyName(field);
        buttonToGenerateName = tooltip.addButton("Generate",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.BL_TR,componentPanel.getPosition().getWidth()-label.computeTextWidth(label.getText())-20-250,field.getPosition().getHeight(),0f);
        buttonToGenerateName.getPosition().inTL(field.getPosition().getX()+260,0);
        componentPanel.addUIElement(tooltip).inTL(0,0);

        mainPanel.addComponent(componentPanel);

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if(buttonToGenerateName!=null&&buttonToGenerateName.isChecked()){
            buttonToGenerateName.setChecked(false);
            MarkovNames.loadIfNeeded();
            for (int attempt = 0; attempt < 20; attempt++) {
                MarkovNames.MarkovNameResult result = MarkovNames.generate(null);
                if (result == null || result.name == null) continue;

                String name = result.name;
                if (!name.regionMatches(true, 0, "the ", 0, 4)) {
                    field.setText(name);
                    break;
                }
            }

        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }


    public static String generateAndApplyName(TextFieldAPI field) {
        if (field == null) throw new IllegalArgumentException("field is null");

        // Make sure the Markov tables are ready
        MarkovNames.loadIfNeeded();

        for (int attempt = 0; attempt < 20; attempt++) {
            MarkovNames.MarkovNameResult result = MarkovNames.generate(null);
            if (result == null || result.name == null) continue;

            String name = result.name;
            // skip names that start with "The "
            if (name.regionMatches(true, 0, "the ", 0, 4)) continue;

            field.setText(name);
            return name;
        }
        return null;
    }


}
