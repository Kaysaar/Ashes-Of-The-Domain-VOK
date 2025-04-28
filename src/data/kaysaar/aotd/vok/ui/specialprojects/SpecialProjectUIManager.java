package data.kaysaar.aotd.vok.ui.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.SpecialProjectManager;
import data.kaysaar.aotd.vok.ui.SoundUIManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;
import data.kaysaar.aotd.vok.ui.customprod.components.UIData;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import data.kaysaar.aotd.vok.ui.customprod.components.onhover.CommodityInfo;
import data.kaysaar.aotd.vok.ui.research.HeadOfResearchShowcase;
import data.kaysaar.aotd.vok.ui.research.ScientistButtonComponent;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

public class SpecialProjectUIManager implements CustomUIPanelPlugin, SoundUIManager {
    UILinesRenderer renderer;
    CustomPanelAPI mainPanel;
    SpecialProjectListManager listManager;
    SpecialProjectShowcase currProjectShowcase;
    CurrentSpecialProjectShowcase showcaseProj;

    public SpecialProjectListManager getListManager() {
        return listManager;
    }

    CustomPanelAPI panelOfMarketData;

    public CustomPanelAPI getMainPanel() {
        return mainPanel;

    }
    public void clear(){
        listManager.buttons.clear();
        currProjectShowcase.windows.clear();
    }

    public SpecialProjectShowcase getCurrProjectShowcase() {
        return currProjectShowcase;
    }

    public CurrentSpecialProjectShowcase getShowcaseProj() {
        return showcaseProj;
    }

    public SpecialProjectUIManager(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        listManager = new SpecialProjectListManager(400, height - 210, this);
        currProjectShowcase = new SpecialProjectShowcase(width - 400 - 15, height - 210, SpecialProjectManager.getInstance().getCurrentlyOnGoingProject(), this);
        ScientistAPI scientistAPI = AoTDMainResearchManager.getInstance().getManagerForPlayer().currentHeadOfCouncil;
        createMarketResourcesPanel();
        HeadOfResearchShowcase showcase = new HeadOfResearchShowcase(450, 130, scientistAPI);
        showcaseProj = new CurrentSpecialProjectShowcase(450, 130, this);
        mainPanel.addComponent(new ScientistButtonComponent(130, 130, scientistAPI).getPanelOfButton()).inTL(((width - 10) / 2) - 65, height - 130);
        mainPanel.addComponent(showcaseProj.getMainPanel()).inTL(((width - 10) / 2) - 65 - 455, height - 130);
        mainPanel.addComponent(showcase.getMainPanel()).inTL(((width - 10) / 2) + 70, height - 130);
        mainPanel.addComponent(listManager.mainPanel).inTL(0, 50);
        mainPanel.addComponent(currProjectShowcase.mainPanel).inTL(listManager.mainPanel.getPosition().getWidth() + 10f, 50);
        mainPanel.addComponent(panelOfMarketData).inTL(5 + (width / 4), 5);
        renderer = new UILinesRenderer(0f);

    }
    public void refreshMarketPanel(){
        mainPanel.removeComponent(panelOfMarketData);
        createMarketResourcesPanel();
        mainPanel.addComponent(panelOfMarketData).inTL(5 + (mainPanel.getPosition().getWidth() / 4), 5);
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        renderer.render(alphaMult);
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

    @Override
    public void playSound() {
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_special", true);
    }

    @Override
    public void pauseSound() {

    }

    public void createMarketResourcesPanel() {
        float width = UIData.WIDTH / 2;
        panelOfMarketData = Global.getSettings().createCustom(width, 50, null);
        TooltipMakerAPI tooltip = panelOfMarketData.createUIElement(width, 50, false);
        GPManager manager = GPManager.getInstance();
        float totalSize = width;
        float sections = totalSize / commodities.size();
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = 35;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        float x = positions;
        for (Map.Entry<String, Integer> entry : GPManager.getInstance().getTotalResources().entrySet()) {
            tooltip.addImage(Global.getSettings().getCommoditySpec(entry.getKey()).getIconName(), iconsize, iconsize, 0f);
            tooltip.addTooltipToPrevious(new CommodityInfo(entry.getKey(), 700, true, false, manager.getProductionOrders()), TooltipMakerAPI.TooltipLocation.BELOW);
            UIComponentAPI image = tooltip.getPrev();
            image.getPosition().inTL(x, topYImage);
            String text = "" + entry.getValue();
            String text2 = text + "(" + GPManager.getInstance().getReqResources(GPManager.getInstance().getProductionOrders()).get(entry.getKey()) + ")";
            tooltip.addPara("" + entry.getValue() + " %s", 0f, Misc.getTooltipTitleAndLightHighlightColor(), Color.ORANGE, "(" + manager.getExpectedCostsFromManager().get(entry.getKey()) + ")").getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            x += sections;
        }
        panelOfMarketData.addUIElement(tooltip).inTL(0, 0);
    }
}
