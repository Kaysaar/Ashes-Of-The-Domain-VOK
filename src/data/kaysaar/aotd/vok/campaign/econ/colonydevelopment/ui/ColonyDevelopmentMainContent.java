package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;

import java.util.List;

public class ColonyDevelopmentMainContent implements ExtendedUIPanelPlugin {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    ColonyDevelopmentList list;
    ColonyDevelopmentExplainSection explainSection;
    MarketAPI market;
    public ColonyDevelopmentExplainSection getExplainSection() {
        return explainSection;
    }
    public ColonyDevelopmentList getList() {
        return list;
    }

    public ColonyDevelopmentMainContent (float width, float height,MarketAPI market) {
        mainPanel = Global.getSettings().createCustom(width,height,this);
        this.market = market;
        createUI();
    }
    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            contentPanel.removeComponent(list.getMainPanel());
            contentPanel.removeComponent(explainSection.getMainPanel());
            list = null;
            explainSection = null;
            mainPanel.removeComponent(contentPanel);
        }
        float width = mainPanel.getPosition().getWidth();
        float height = mainPanel.getPosition().getHeight();
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        list =new ColonyDevelopmentList(width*0.25f-10,height,market);
        explainSection = new ColonyDevelopmentExplainSection(width*0.75f-18,height,null,market);
        contentPanel.addComponent(list.getMainPanel()).inTL(0,0);
        contentPanel.addComponent(explainSection.getMainPanel()).inTL(width*0.25f+10,0);
        mainPanel.addComponent(contentPanel).inTL(0,0);
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
        if(list.needsToUpdateUI){
            list.setNeedsToUpdateUI(false);
            explainSection.setId((String) list.chosen.getCustomData());
            explainSection.createUI();
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
    public void clearUI(){

    }
}
