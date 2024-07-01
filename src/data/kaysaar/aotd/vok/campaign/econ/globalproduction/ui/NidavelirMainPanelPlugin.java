package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UIData;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels.BasePanelInterface;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels.FighterPanelInterface;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels.ShipPanelInterface;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.panels.WeaponPanelInterface;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfoGenerator;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NidavelirMainPanelPlugin implements CustomUIPanelPlugin {
    PositionAPI p;
    InteractionDialogAPI dialog;
    CustomVisualDialogDelegate.DialogCallbacks callbacks;
    CustomPanelAPI panel;
    public static int maxItemsPerPage = 50;


    Color base = Global.getSector().getPlayerFaction().getBaseUIColor();
    Color bg = Global.getSector().getPlayerFaction().getDarkUIColor();
    Color bright = Global.getSector().getPlayerFaction().getBrightUIColor();
    float spacerX = 7f; //Used for left panels
    ShipPanelInterface shipPanelManager;
    WeaponPanelInterface weaponPanelManager;
    FighterPanelInterface fighterPanelInterface;
    BasePanelInterface currentManager;
    CustomPanelAPI panelOfMarketData;
    CustomPanelAPI panelOfOrders;
    CustomPanelAPI topPanel;
    ArrayList<ButtonAPI> switchingButtons = new ArrayList<>();
  // 30 for top buttons , 60 for bottom ones rest is padding
    float leftHeight = UIData.HEIGHT-30-145-20;
    public void init(CustomPanelAPI panel, CustomVisualDialogDelegate.DialogCallbacks callbacks, InteractionDialogAPI dialog) {

        this.panel = panel;
        this.callbacks = callbacks;
        shipPanelManager = new ShipPanelInterface(this.panel);
        weaponPanelManager = new WeaponPanelInterface(this.panel);
        fighterPanelInterface = new FighterPanelInterface(this.panel);
        currentManager =shipPanelManager;
        this.dialog = dialog;
        shipPanelManager.init();
        createTopBar();
        createMarketResourcesPanel();
        createOrders();
    }
    public void createTopBar() {
        topPanel = panel.createCustomPanel(UIData.WIDTH_OF_OPTIONS, 20, null);
        TooltipMakerAPI tooltip = topPanel.createUIElement(UIData.WIDTH_OF_OPTIONS, 20, false);
        ArrayList<ButtonAPI> butt = new ArrayList<>();
        butt.add(tooltip.addButton("Ships", "ship", base, bg, Alignment.MID, CutStyle.TOP, 120, 20, 0f));
        butt.add(tooltip.addButton("Weapons", "weapon", base, bg, Alignment.MID, CutStyle.TOP, 120, 20, 0f));
        butt.add(tooltip.addButton("Fighters", "fighter", base, bg, Alignment.MID, CutStyle.TOP, 120, 20, 0f));
        butt.add(tooltip.addButton("Special Project", null, base, bg, Alignment.MID, CutStyle.TOP, 150, 20, 0f));
        float currX = 0;
        float paddingX = 5f;
        for (ButtonAPI buttonAPI : butt) {
            buttonAPI.getPosition().inTL(currX, 0);
            currX += buttonAPI.getPosition().getWidth() + paddingX;
        }
        switchingButtons.addAll(butt);
        topPanel.addUIElement(tooltip).inTL(-5, 0);
        panel.addComponent(topPanel).inTL(UIData.WIDTH - UIData.WIDTH_OF_OPTIONS - 10, 29);

    }
    public void createOrders(){
        UILinesRenderer renderer = new UILinesRenderer(0f);
        panelOfOrders = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, leftHeight, renderer);
        TooltipMakerAPI tooltip = panelOfOrders.createUIElement(UIData.WIDTH_OF_ORDERS,leftHeight,false);
        tooltip.addSectionHeading("On-going special project",Alignment.MID,0f);
        tooltip.addSectionHeading("Production Orders",Alignment.MID,70f);
        panelOfOrders.addUIElement(tooltip).inTL(0,0);
        renderer.setPanel(panelOfOrders);
        panel.addComponent(panelOfOrders).inTL(spacerX,175);
    }
    public void createMarketResourcesPanel() {
        UILinesRenderer renderer = new UILinesRenderer(0f);
        panelOfMarketData = panel.createCustomPanel(UIData.WIDTH_OF_ORDERS, 145, renderer);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(UIData.WIDTH_OF_ORDERS, 155, false);
        tooltip.addSectionHeading("Produced resources", Alignment.MID, 0f);
        tooltip.addSectionHeading("Consumed resources", Alignment.MID, 55f);

        float totalSize = UIData.WIDTH_OF_ORDERS;
        float sections = totalSize/3;
        float positions = totalSize/12;
        float iconsize = 40;
        float topYImage = 25;
        LabelAPI test  = Global.getSettings().createLabel("",Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(),iconsize,iconsize,0f);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x,topYImage);
            tooltip.addPara("x"+entry.getValue(),Color.ORANGE,0f).getPosition().inTL(x+iconsize+5,(topYImage+(iconsize/2))-(test.computeTextHeight("x"+entry.getValue())/3));
            x+=sections;

        }
        x= positions;
        topYImage+=73;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getReqResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(),iconsize,iconsize,0f);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x,topYImage);
            tooltip.addPara("x"+entry.getValue(),Color.ORANGE,0f).getPosition().inTL(x+iconsize+5,(topYImage+(iconsize/2))-(test.computeTextHeight("x"+entry.getValue())/3));
            x+=sections;

        }

        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
        renderer.setPanel(panelOfMarketData);
        panel.addComponent(panelOfMarketData).inTL(spacerX, 30);
    }

    public void reset() {

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
        if(currentManager!=null){
            currentManager.advance(amount);
        }
        for (ButtonAPI switchingButton : switchingButtons) {
            if(switchingButton.isChecked()){
                switchingButton.setChecked(false);
                if(switchingButton.getCustomData() instanceof  String){
                    String match = (String) switchingButton.getCustomData();
                    if(match.equals("ship")&&!currentManager.getClass().isInstance(shipPanelManager)){
                        currentManager.clear();
                        currentManager = shipPanelManager;
                        shipPanelManager.reInit();
                        break;
                    }
                    if(match.equals("weapon")&&!currentManager.getClass().isInstance(weaponPanelManager)){
                        currentManager.clear();
                        currentManager = weaponPanelManager;
                        weaponPanelManager.reInit();
                        break;
                    }
                    if(match.equals("fighter")&&!currentManager.getClass().isInstance(fighterPanelInterface)){
                        currentManager.clear();
                        currentManager = fighterPanelInterface;
                        fighterPanelInterface.reInit();
                        break;
                    }
                }
            }
        }



    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.getEventValue() == Keyboard.KEY_ESCAPE) {

                dialog.dismiss();

            }
        }
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
