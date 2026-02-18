package data.kaysaar.aotd.vok.campaign.econ.synergies.ui;

import ashlib.data.plugins.ui.models.InstantPopUpUI;
import ashlib.data.plugins.ui.models.PopUpUI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class SynergyInfoPopUp extends PopUpUI {
    CustomPanelAPI mainPanel;
    SynergyInfoDisplay history;
    MarketAPI market;
    public SynergyInfoPopUp(MarketAPI market) {
        this.market = market;
    }
    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }

    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(panelAPI.getPosition().getWidth()+5,panelAPI.getPosition().getHeight(),true);
        if(history==null){
            history = new SynergyInfoDisplay(market);
        }
        tooltip.addCustom(history.getMainPanel(),1f);
        mainPanel.getPosition().setSize(panelAPI.getPosition().getWidth(),tooltip.getHeightSoFar());
        mainPanel.addUIElement(tooltip).inTL(-3,0);
        addTooltip(tooltip);
        return tooltip.getHeightSoFar();


    }

    @Override
    public void onExit() {
        history.buttons.clear();
        super.onExit();
    }
}
