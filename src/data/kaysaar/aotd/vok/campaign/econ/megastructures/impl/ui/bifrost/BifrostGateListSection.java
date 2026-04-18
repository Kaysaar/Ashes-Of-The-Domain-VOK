package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureTestDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.table.BifrostGateUITableList;

import java.util.List;

public class BifrostGateListSection implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel,componentPanel;
    UILinesRenderer renderer;
    BifrostGateUITableList list;
    public BifrostGateListSection() {
        mainPanel = Global.getSettings().createCustom(800, BaseMegastructureTestDialog.height,this);

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
        if (componentPanel != null) {
            mainPanel.removeComponent(componentPanel);
        }
        componentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tlHeader = componentPanel.createUIElement(componentPanel.getPosition().getWidth(),20,false);
        tlHeader.addSectionHeading("List of gates", Alignment.MID,0f);
        if(list!=null) {
            list.clearUI();
        }
        list = new BifrostGateUITableList(componentPanel.getPosition().getWidth(),componentPanel.getPosition().getHeight()-42,true,0,0);
        list.createSections();
        list.createTable();
        componentPanel.addUIElement(tlHeader).inTL(0,0);
        componentPanel.addComponent(list.mainPanel).inTL(0,22);
        mainPanel.addComponent(componentPanel).inTL(0,0);
    }

    @Override
    public void clearUI() {
        if(list!=null){
            list.clearUI();
        }
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
