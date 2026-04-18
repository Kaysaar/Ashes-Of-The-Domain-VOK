package data.kaysaar.aotd.vok.ui.customprod.orders;

import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.manager.AoTDProductionManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.ui.customprod.ProductionMainPanel;
import data.kaysaar.aotd.vok.ui.customprod.dialogs.ChangeOnGoingOrderDialog;

import java.util.List;

public class IssuedOrdersList implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel,contentPanel;
    ProductionMainPanel parent;
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }
    public IssuedOrdersList(float width, float height, ProductionMainPanel parent) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.parent = parent;
        createUI();
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tlHeader,tlContent;
        tlHeader = contentPanel.createUIElement(contentPanel.getPosition().getWidth(),20,false);
        tlHeader.addSectionHeading("On-going orders", Alignment.MID,0f);
        contentPanel.addUIElement(tlHeader).inTL(0,0);
        tlContent = contentPanel.createUIElement(contentPanel.getPosition().getWidth()+9,contentPanel.getPosition().getHeight()-20,true);
        tlContent.addSpacer(0f).getPosition().inTL(0,0);
        for (AoTDProductionOrderSnapshot currentSnapshot : AoTDProductionManager.getInstance().getCurrentSnapshots()) {
            OnGoingOrderButton bt = new OnGoingOrderButton(contentPanel.getPosition().getWidth(),150,currentSnapshot);
            bt.setListener(new CustomButton.ButtonEventListener() {
                @Override
                public void onButtonClicked() {
                    new ChangeOnGoingOrderDialog(bt.getSnapshot(),parent,500);
                }
            });
            bt.createUI();
            tlContent.addCustom(bt.getMainPanel(),2f);
        }
        contentPanel.addUIElement(tlContent).inTL(-5,20);
        mainPanel.addComponent(contentPanel).inTL(0,0);
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
