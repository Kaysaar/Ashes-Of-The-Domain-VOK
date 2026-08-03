package data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureTestDialog;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;

import java.util.ArrayList;
import java.util.List;

public class MegastructureViewSection implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel,contentPanel;
    BaseMegastructureScript megastructureAPI;
    UILinesRenderer renderer;
    ArrayList<MegastructureSubSection>subSections = new ArrayList<>();
    public  boolean needsUpdate = false;
    public BaseMegastructureSection currentlyChosenSection  =null;

    public BaseMegastructureSection getCurrentlyChosenSection() {
        return currentlyChosenSection;
    }
    public boolean shouldUpdateUI(){
        if(needsUpdate){
            needsUpdate = false;
            return true;
        }
        return false;
    }

    public void setCurrentlyChosenSection(BaseMegastructureSection currentlyChosenSection) {
        needsUpdate = true;
        if(currentlyChosenSection==null||this.currentlyChosenSection==null){
            this.currentlyChosenSection=currentlyChosenSection;
        }
        if(currentlyChosenSection!=null&&this.currentlyChosenSection!=currentlyChosenSection){
            this.currentlyChosenSection=currentlyChosenSection;
        }

    }

    public MegastructureViewSection(BaseMegastructureScript megastructureAPI){
        mainPanel = Global.getSettings().createCustom(800, BaseMegastructureTestDialog.height,this);
        this.megastructureAPI = megastructureAPI;
        renderer = new UILinesRenderer(0f);
        renderer.setPanel(mainPanel);
        createUI();
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            mainPanel.removeComponent(contentPanel);
        }
        subSections.clear();
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),this);
        HologramViewer viewer = BlackSiteProjectManager.createEntityViewHologram(megastructureAPI.getEntityTiedTo(),300,contentPanel.getPosition().getHeight());

        contentPanel.addComponent(viewer.getComponentPanel()).inTL((contentPanel.getPosition().getWidth()/2)-(viewer.getComponentPanel().getPosition().getWidth()/2),0);
        for (BaseMegastructureSection megaStructureSection : megastructureAPI.getMegaStructureSections()) {
            MegastructureSubSection section = new MegastructureSubSection(megastructureAPI,megaStructureSection,contentPanel,megaStructureSection.getSpec().getMode(),megaStructureSection.getSpec().getOriginMode(),megaStructureSection.getSpec().getUiCordsOfBox(),megaStructureSection.getSpec().getUiCordsOnHologram(),this);
            subSections.add(section);
            section.createUI();
        }
        viewer.setRenderLine(false);


        mainPanel.addComponent(contentPanel);
    }

    public ArrayList<MegastructureSubSection> getSubSections() {
        return subSections;
    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
//        renderer.render(alphaMult);
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
