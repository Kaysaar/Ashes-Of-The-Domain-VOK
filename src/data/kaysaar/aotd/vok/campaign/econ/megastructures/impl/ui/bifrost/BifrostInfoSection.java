package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureTestDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.components.MegastructureSectionButton;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.BifrostMegastructureManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.gatebuilding.BifrostStarSystemSelectorDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

import java.awt.*;
import java.util.List;

public class BifrostInfoSection implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel,componentPanel;
    UILinesRenderer renderer;
    BifrostMainUI mainUI;
    ButtonAPI addGate;

    public BifrostInfoSection(BifrostMainUI mainUI){
        renderer = new UILinesRenderer(0f);
        this.mainUI = mainUI;
        mainPanel = Global.getSettings().createCustom(410, BaseMegastructureTestDialog.height-45, this);
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
        TooltipMakerAPI tooltip = componentPanel.createUIElement(componentPanel.getPosition().getWidth(), componentPanel.getPosition().getHeight(), false);
        MegastructureSectionButton viewer = new MegastructureSectionButton(190,95, BifrostMegastructureManager.getInstance().getMegastructure());
        BaseMegastructureScript megastructureScript = BifrostMegastructureManager.getInstance().getMegastructure();
        CustomPanelAPI container = Global.getSettings().createCustom(componentPanel.getPosition().getWidth(), viewer.getComponentPanel().getPosition().getHeight(), null);
        container.addComponent(viewer.getComponentPanel()).inTL((container.getPosition().getWidth() / 2) - (viewer.getComponentPanel().getPosition().getWidth() / 2) - 5, 0);
        tooltip.addSpacer(0f).getPosition().inTL(0, 0);
        tooltip.setTitleFont(Fonts.ORBITRON_20AA);
        tooltip.addTitle("Megastructure Overview").setAlignment(Alignment.MID);
        tooltip.addCustom(container, 2f);
        tooltip.addPara(megastructureScript.getSpec().getDescription(), 3f);
        tooltip.addSectionHeading("Empire Wide Effects",Alignment.MID,5f);
        BifrostMegastructure mega = (BifrostMegastructure) megastructureScript;
        if(mega.getSections().isEmpty()){
            tooltip.addPara("Currently there are no gates under our control, build gates in star systems under your control, and connect your colonies via Bifrost Network.",3f);
        }
        else{
            String gates = "gate";
            if(mega.getActiveSections().size()!=1){
                gates = "gates";
            }
            tooltip.addPara("Currently there are %s "+gates+" active!",3f, Color.ORANGE,""+mega.getActiveSections().size());
            float bonus = mega.getTotalAccessibility()*100;
            String per = Misc.getRoundedValueMaxOneAfterDecimal(bonus)+"%";
            tooltip.addPara("Total accessibility bonus across entire empire : %s",3f,Color.ORANGE,per);
        }
        addGate = tooltip.addButton("Order construction of new gate",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TL_BR,componentPanel.getPosition().getWidth()-15,25,10f);


        componentPanel.addUIElement(tooltip).inTL(0,0);
        mainPanel.addComponent(componentPanel).inTL(0,0);
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
        if(addGate!=null&& addGate.isChecked()){
            addGate.setChecked(false);
            BifrostStarSystemSelectorDialog dialog = new BifrostStarSystemSelectorDialog("Choose system to build Bifrost Gate", mainUI);
            AshMisc.initPopUpDialog(dialog,800,420);
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
