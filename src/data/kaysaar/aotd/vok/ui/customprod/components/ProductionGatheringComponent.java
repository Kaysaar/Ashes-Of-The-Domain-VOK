package data.kaysaar.aotd.vok.ui.customprod.components;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.ui.customprod.ProductionMainPanel;
import data.kaysaar.aotd.vok.ui.customprod.dialogs.GatheringPointDialog;

import java.util.List;

public class ProductionGatheringComponent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    ButtonAPI currPoint;
    ButtonAPI orderDraft, currentOrders;

    public static CustomPanelAPI getMarketEntitySprite(float width, float height, MarketAPI market) {
        CustomPanelAPI panelAPI1 = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panelAPI1.createUIElement(width, height, false);
        if (market.getPlanetEntity() != null) {
            tooltip.showPlanetInfo(market.getPlanetEntity(), width, height, false, 0f);
        } else {
            tooltip.addImage(market.getPrimaryEntity().getCustomEntitySpec().getSpriteName(), width - 30, height - 30, 15f);
        }
        panelAPI1.addUIElement(tooltip).inTL(0, 0);
        return panelAPI1;
    }

    public static CustomPanelAPI getMarketEntitySpriteWithName(float width, float height, float iconSize, MarketAPI market) {
        CustomPanelAPI panelAPI1 = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panelAPI1.createUIElement(width, height, false);
        tooltip.addCustom(getMarketEntitySprite(iconSize, iconSize, market), 0f).getPosition();
        UIComponentAPI componentAPI = tooltip.getPrev();
        LabelAPI labelAPI = tooltip.addPara(market.getName() + " %s", 1f, Misc.getBrightPlayerColor(), Misc.getGrayColor(), "(size " + market.getSize() + ")");
        componentAPI.getPosition().inTL((labelAPI.computeTextWidth(labelAPI.getText()) / 2) - (iconSize / 2), 0);
        float y = -componentAPI.getPosition().getY() - iconSize / 4;
        float respondingX = componentAPI.getPosition().getCenterX();
        labelAPI.getPosition().inTL(respondingX - labelAPI.computeTextWidth(labelAPI.getText()) / 2, y);
        panelAPI1.addUIElement(tooltip).inTL(0, 0);
        return panelAPI1;
    }

    public static Pair<CustomPanelAPI, ButtonAPI> getMarketEntitySpriteButton(float width, float height, float iconSize, MarketAPI market) {
        CustomPanelAPI panelAPI1 = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panelAPI1.createUIElement(width, height, false);
        ButtonAPI button = tooltip.addAreaCheckbox("", market, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getBrightPlayerColor(), width - 1, height, 0f);
        float y = -button.getPosition().getY() - button.getPosition().getHeight();
        button.getPosition().inTL(-2, 0);
        if (market != null) {
            tooltip.addCustom(getMarketEntitySpriteWithName(width, height, iconSize, market), 0f).getPosition().inTL(5f, y);
        } else {
            button.setEnabled(false);
        }
        panelAPI1.addUIElement(tooltip).inTL(0, 0);

        return new Pair<>(panelAPI1, button);
    }
    ProductionMainPanel parent;
    public ProductionGatheringComponent(float width, float height, ProductionMainPanel parent) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.parent = parent;
        createUI();
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if (contentPanel != null) {
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = contentPanel.createUIElement(contentPanel.getPosition().getWidth(), contentPanel.getPosition().getHeight(), false);
        tooltip.addSectionHeading("Production gathering point", Alignment.MID, 0f).getPosition().inTL(0,0);

        Pair<CustomPanelAPI, ButtonAPI> pair = getMarketEntitySpriteButton(contentPanel.getPosition().getWidth() - 25f, 75, 75, Global.getSector().getPlayerFaction().getProduction().getGatheringPoint());
        tooltip.addCustom(pair.one, 5f);
        currPoint = pair.two;
        tooltip.getPosition().setXAlignOffset(-5);
        orderDraft = tooltip.addButton("Order Draft",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,(contentPanel.getPosition().getWidth()/2-5),20,5f);
        currentOrders = tooltip.addButton("Current Orders",null,Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,(contentPanel.getPosition().getWidth()/2-5),20,5f);
        orderDraft.getPosition().inTL(0,contentPanel.getPosition().getHeight()-orderDraft.getPosition().getHeight()-3);
        currentOrders.getPosition().inTL(contentPanel.getPosition().getWidth()-currentOrders.getPosition().getWidth(),contentPanel.getPosition().getHeight()-currentOrders.getPosition().getHeight()-3);
        orderDraft.highlight();
        contentPanel.addUIElement(tooltip).inTL(0, 0);
        mainPanel.addComponent(contentPanel).inTL(0, 0);
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
        if (currPoint != null && currPoint.isChecked()) {
            currPoint.setChecked(false);
            GatheringPointDialog dialogGather = new GatheringPointDialog("Choose gathering point", this);
            AshMisc.initPopUpDialog(dialogGather, 400, 400);

        }
        if(currentOrders!=null&&currentOrders.isChecked()){
            currentOrders.setChecked(false);
            parent.swapPanels(true);

        }
        if(orderDraft!=null&&orderDraft.isChecked()){
            orderDraft.setChecked(false);
            parent.swapPanels(false);
        }
        if(parent!=null&&parent.isInOtherOrders){
            orderDraft.unhighlight();
            currentOrders.highlight();
        }
        if(parent!=null&&!parent.isInOtherOrders){
            orderDraft.highlight();
            currentOrders.unhighlight();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
