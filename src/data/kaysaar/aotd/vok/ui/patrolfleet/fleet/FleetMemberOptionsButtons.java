package data.kaysaar.aotd.vok.ui.patrolfleet.fleet;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;

import java.util.ArrayList;

public class FleetMemberOptionsButtons extends PopUpUI {
    FleetMemberAPI member;
    PatrolFleetDataManager dataManager;
    CustomPanelAPI mainPanel;
    ArrayList<ButtonAPI>buttons = new ArrayList<>();
    FleetMembersShowcase showcase;
    float opad = 0f;
    public FleetMemberOptionsButtons(FleetMemberAPI member, PatrolFleetDataManager manager,FleetMembersShowcase showcaseToBlock) {
        this.member = member;
        this.dataManager = manager;
        this.borderlessMode = true;
        this.showcase = showcaseToBlock;
        for (ShipWithOfficerShowcase shipWithOfficerShowcase : this.showcase.showcases) {
            shipWithOfficerShowcase.blockButtonInstance =  true;
        }

    }

    @Override
    public void createUI(CustomPanelAPI panelAPI) {
        createUIMockup(panelAPI);
        panelAPI.addComponent(mainPanel).inTL(0, 0);
    }
    @Override
    public float createUIMockup(CustomPanelAPI panelAPI) {
        float lastY =0;
        mainPanel = panelAPI.createCustomPanel(panelAPI.getPosition().getWidth(), panelAPI.getPosition().getHeight(), null);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth()+5,mainPanel.getPosition().getHeight(),true);
        if(member.getCaptain()!=null){
            createButton("Change Officer","officer_tab",tooltip,mainPanel.getPosition().getWidth(),20);

        }
        else{
            createButton("Assign Officer","officer_tab",tooltip,mainPanel.getPosition().getWidth(),20);

        }
        createButton("De-commission vessel","de_com",tooltip,mainPanel.getPosition().getWidth(),20);
        lastY = tooltip.getHeightSoFar()+5;

        mainPanel.getPosition().setSize(mainPanel.getPosition().getWidth(),lastY);
        tooltip.getPosition().setSize(mainPanel.getPosition().getWidth(),lastY);
        mainPanel.addUIElement(tooltip).inTL(-5,0);
        return lastY;


    }
    public void createButton(String name, String data,TooltipMakerAPI tooltip,float width, float height) {
        addButton(tooltip.addAreaCheckbox(name,data, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Misc.getBrightPlayerColor(),width,height,opad));
    }
    public void addButton(ButtonAPI button){
        buttons.add(button);
        opad = 1f;
    }

    @Override
    public void onExit() {
        super.onExit();
        for (ShipWithOfficerShowcase shipWithOfficerShowcase : this.showcase.showcases) {
            shipWithOfficerShowcase.blockButtonInstance = false;
        }
    }
}
