package data.kaysaar.aotd.vok.ui.scientist;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistPerson;
import data.kaysaar.aotd.vok.ui.basecomps.ExtendedUIPanelPlugin;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.awt.*;
import java.util.List;

public class ScientistButton implements ExtendedUIPanelPlugin {
    public CustomPanelAPI mainPanel;
    public CustomPanelAPI contentPanel;
    ButtonAPI button;
    ScientistPerson person;

    public ScientistButton(ScientistPerson person,float width) {
        this.person = person;
        mainPanel = Global.getSettings().createCustom(width,1,this);
        createUI();


    }
    public ButtonAPI getButton() {
        return button;
    }


    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(contentPanel!=null){
            contentPanel.removeComponent(mainPanel);
        }
        TooltipMakerAPI testerOfHeight  = mainPanel.createUIElement(mainPanel.getPosition().getWidth()-130,1,true);
        createTextSection(testerOfHeight);
        float height = testerOfHeight.getHeightSoFar()+4;
        float heightReq = 128;
        if(height>heightReq){
            heightReq = height;
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),heightReq+2,null);
        CustomPanelAPI textPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth()-130,heightReq,null);
        TooltipMakerAPI textTooltip = textPanel.createUIElement(textPanel.getPosition().getWidth()-2,textPanel.getPosition().getHeight(),false);
        createTextSection(textTooltip);
        ImageViewer viewer = new ImageViewer(125,125,person.getScientistPerson().getPortraitSprite());
        TooltipMakerAPI buttonTooltip = contentPanel.createUIElement(contentPanel.getPosition().getWidth(),contentPanel.getPosition().getHeight(),false);
        button = buttonTooltip.addAreaCheckbox("",person, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Misc.getBrightPlayerColor(),contentPanel.getPosition().getWidth(),contentPanel.getPosition().getHeight(),0f);
        button.getPosition().inTL(0,0);
        buttonTooltip.addCustom(viewer.getComponentPanel(),0f).getPosition().inTL(2,((heightReq-125)/2)+1);
        textPanel.addUIElement(textTooltip).inTL(0,0);
        buttonTooltip.addCustom(textPanel,0f).getPosition().inTL(130,1);
        contentPanel.addUIElement(buttonTooltip).inTL(0,0);
        mainPanel.addComponent(contentPanel).inTL(0,0);
        mainPanel.getPosition().setSize(mainPanel.getPosition().getWidth(),contentPanel.getPosition().getHeight());



    }

    private void createTextSection(TooltipMakerAPI testerOfHeight) {
        String title = person.getScientistPerson().getNameString();
        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().isHeadOfResearch(person)){
            title+=" (Current Head of R&D)";
        }
        testerOfHeight.addTitle(title);
        testerOfHeight.addPara("Passive - "+person.getPassiveSkillName(),Color.ORANGE,5f);
        testerOfHeight.setBulletedListMode(BaseIntelPlugin.INDENT);

        person.createPassiveSkillDescription(testerOfHeight);
        testerOfHeight.setBulletedListMode(null);
        testerOfHeight.addPara("Active - "+person.getActiveSkillName(),Color.cyan,5f);
        testerOfHeight.setBulletedListMode(BaseIntelPlugin.INDENT);
        person.createActiveSkillDescription(testerOfHeight);
        testerOfHeight.setBulletedListMode(null);
        testerOfHeight.addPara("Scientist Salary : %s",20f, Color.ORANGE,Misc.getDGSCredits(person.getMonthlySalary()));
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
