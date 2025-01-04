package data.kaysaar.aotd.vok.ui.buildingmenu;

import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.misc.ui.ImagePanel;

import java.util.List;

public class IndustryShowcaseUI implements CustomUIPanelPlugin {

    CustomPanelAPI mainPanel;
    CustomPanelAPI holderPanel;
    TooltipMakerAPI mainTooltip;
    IndustrySpecAPI currentSpec;
    MarketAPI market;
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public IndustryShowcaseUI(float width, float height,MarketAPI market) {
        this.market = market;
        mainPanel = Global.getSettings().createCustom(width,height,this);

        holderPanel = Global.getSettings().createCustom(width,height,null);
        mainTooltip = holderPanel.createUIElement(width,height,true);

    }

    public void setCurrentSpec(IndustrySpecAPI currentSpec) {
        this.currentSpec = currentSpec;
    }

    public void createIndustryPanel(){
        mainTooltip.setTitleOrbitronLarge();
        mainTooltip.addTitle(currentSpec.getName());
        UILinesRenderer renderer = new UILinesRenderer(0f);
        ImagePanel panel = new ImagePanel();
        CustomPanelAPI panelHolder = holderPanel.createCustomPanel(mainPanel.getPosition().getWidth(),95,renderer);
        CustomPanelAPI panelImage  = panelHolder.createCustomPanel(190,95,panel);
        renderer.setPanel(panelImage);
        panel.init(panelImage,Global.getSettings().getSprite(currentSpec.getImageName()));
        panelHolder.addComponent(panelImage).inTL(mainPanel.getPosition().getWidth()/2-95,0);
        mainTooltip.addCustom(panelHolder,10f);
        BuildingMenuMisc.createTooltipForIndustry((BaseIndustry) currentSpec.getNewPluginInstance(market), Industry.IndustryTooltipMode.ADD_INDUSTRY,mainTooltip,true,false,mainPanel.getPosition().getWidth(),true);
        holderPanel.addUIElement(mainTooltip).inTL(0,0);
        mainPanel.addComponent(holderPanel).inTL(0,0);

    }
    public void recreateIndustryPanel(){
        mainPanel.removeComponent(holderPanel);
        holderPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        mainTooltip = holderPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),true);
        createIndustryPanel();
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
