package data.kaysaar.aotd.vok.ui.patrolfleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;

import java.awt.*;
import java.util.List;

public class FleetMarketInfo implements CustomUIPanelPlugin {

    CustomPanelAPI mainPanel;

    public FleetMarketInfo(final MarketAPI market) {
        mainPanel = Global.getSettings().createCustom(80, 55, this);
        TooltipMakerAPI tooltip2 = mainPanel.createUIElement(80, 55, false);

        UILinesRenderer renderer = new UILinesRenderer(0f);
        renderer.setBoxColor(market.getFaction().getDarkUIColor());
        CustomPanelAPI holder = Global.getSettings().createCustom(80, 55, null);
        TooltipMakerAPI tooltip = holder.createUIElement(80, 54, false);
        CustomPanelAPI box = Global.getSettings().createCustom(80, 35, renderer);
        renderer.setPanel(box);
        tooltip.addCustom(box, 1f);
        ImageViewer viewer = new ImageViewer(34, 25, Global.getSettings().getSpriteName("systemMap", "icon_fleet3"));
        viewer.setColorOverlay(market.getFaction().getBaseUIColor());
        tooltip.addCustom(viewer.getComponentPanel(), 3f).getPosition().inTL(10, 5);
        tooltip.setParaFont("graphics/fonts/orbitron12condensed.fnt");
        LabelAPI labelAPI = tooltip.addPara("Fleet", market.getFaction().getBrightUIColor(), 0f);
        labelAPI.getPosition().inTL(45 - (labelAPI.computeTextWidth(labelAPI.getText()) / 2), 34);
        tooltip.setParaFontDefault();
        market.reapplyConditions();
        market.reapplyIndustries();
        labelAPI = tooltip.addPara(""+(int)(market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).computeEffective(0f)*100),market.getFaction().getBrightUIColor(),0f);
        labelAPI.getPosition().inTL(44,10);
        holder.addUIElement(tooltip).inTL(0, 0);
        tooltip2.addCustom(holder, 0f);
        tooltip2.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 400f;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addTitle("Fleet Armada Points");
                tooltip.addSectionHeading("AoTD - Vaults of Knowledge",Alignment.MID,5f);
                tooltip.addPara("This market generates around %s armada points",5f, Color.ORANGE,""+(int)(market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).computeEffective(0f)*100));
                tooltip.addPara("Armada points are used to deploy fleets. More points faction generates, more fleets can be deployed at one time.",5f);
                tooltip.addPara("For market's armada points to count, market needs to have military installations first!",Color.ORANGE,5f);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        mainPanel.addUIElement(tooltip2);
    }

    public CustomPanelAPI getMainPanel() {
        return mainPanel;
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
