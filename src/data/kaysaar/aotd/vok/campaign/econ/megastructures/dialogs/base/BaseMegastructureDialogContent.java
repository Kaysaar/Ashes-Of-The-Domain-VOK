package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.MegastructureSubSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.MegastructureViewSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.SectionShowcaseSection;

import java.util.List;

public class BaseMegastructureDialogContent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    MegastructureViewSection megastructureViewSection;
    SectionShowcaseSection section;
    BaseMegastructureScript script;

    public SectionShowcaseSection getSection() {
        return section;
    }

    public MegastructureViewSection getMegastructureViewSection() {
        return megastructureViewSection;
    }

    public BaseMegastructureDialogContent(float width, float height, BaseMegastructureScript script){
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.script = script;
        megastructureViewSection = new MegastructureViewSection(script);
        section = new SectionShowcaseSection(script,this);
        mainPanel.addComponent(megastructureViewSection.getMainPanel()).inTL(0,0);
        mainPanel.addComponent(section.getMainPanel()).inTL(mainPanel.getPosition().getWidth()-section.getMainPanel().getPosition().getWidth(),0);
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {

    }

    @Override
    public void clearUI() {

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
        if(megastructureViewSection!=null&&section!=null){
            if(megastructureViewSection.shouldUpdateUI()){
                if(section.getCurrSection()==null){
                    section.setCurrSection(megastructureViewSection.getCurrentlyChosenSection());

                }
                else{
                    if(!section.getCurrSection().equals(megastructureViewSection.getCurrentlyChosenSection())){
                        section.setCurrSection(megastructureViewSection.getCurrentlyChosenSection());
                    }
                }
                if(section.getCurrSection()!=null){
                    for (MegastructureSubSection subSection : megastructureViewSection.getSubSections()) {
                        subSection.setCurrentlyChosen(subSection.section.equals(section.getCurrSection()));
                    }
                }
                else{
                    for (MegastructureSubSection subSection : megastructureViewSection.getSubSections()) {
                        subSection.setCurrentlyChosen(false);
                    }
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
}
