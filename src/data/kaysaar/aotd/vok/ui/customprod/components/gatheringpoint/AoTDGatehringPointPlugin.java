package data.kaysaar.aotd.vok.ui.customprod.components.gatheringpoint;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomVisualDialogDelegate;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.ui.customprod.NidavelirMainPanelPlugin;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class AoTDGatehringPointPlugin implements CustomUIPanelPlugin {
    CustomPanelAPI panelAPI;
    ArrayList<ButtonAPI> buttons;
    float iconSize = 40;
    CustomVisualDialogDelegate.DialogCallbacks callbacks;

    public void init(CustomPanelAPI panelAPI, CustomVisualDialogDelegate.DialogCallbacks callbacks) {
        buttons = new ArrayList<>();
        this.panelAPI = panelAPI;
        this.callbacks = callbacks;
        TooltipMakerAPI tooltipMakerAPI = panelAPI.createUIElement(30, 30, false);
        tooltipMakerAPI.addPara("test", 10f);
        panelAPI.addUIElement(tooltipMakerAPI).inTL(0, 0);

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
        for (InputEventAPI event : events) {
            if (event.isConsumed()) continue;
            if (event.getEventValue() == Keyboard.KEY_ESCAPE) {
                callbacks.dismissDialog();
                event.consume();
                return;
            }
        }
    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

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
    public static Pair<CustomPanelAPI,ButtonAPI>getMarketEntitySpriteButton(float width, float height, float iconSize,MarketAPI market) {
        CustomPanelAPI panelAPI1 = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = panelAPI1.createUIElement(width, height, false);
        ButtonAPI button = tooltip.addAreaCheckbox("", market, NidavelirMainPanelPlugin.base, NidavelirMainPanelPlugin.bg, NidavelirMainPanelPlugin.bright, width-1, height, 0f);
        float y = -button.getPosition().getY() -button.getPosition().getHeight();
        button.getPosition().inTL(-2,0);
        tooltip.addCustom(getMarketEntitySpriteWithName(width, height, iconSize, market), 0f).getPosition().inTL(5f,y);
        panelAPI1.addUIElement(tooltip).inTL(0, 0);

        return new Pair<>(panelAPI1, button);
    }
}
