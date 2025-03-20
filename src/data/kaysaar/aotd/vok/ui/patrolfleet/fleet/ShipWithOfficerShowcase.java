package data.kaysaar.aotd.vok.ui.patrolfleet.fleet;

import ashlib.data.plugins.info.ShipInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.PopUpUI;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.patrolfleet.CreateNewFleetTab;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;
import data.kaysaar.aotd.vok.ui.research.ResearchInfoUI;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.placePopUpUI;

public class ShipWithOfficerShowcase extends ButtonComponent {
    boolean patrolFleetCreation = false;
    AoTDPatrolFleetData data;
    String shipIdl;
    CreateNewFleetTab tab;
    FleetMemberAPI memberOfFleet;
    FleetMembersShowcase parent;
    public ShipWithOfficerShowcase(float boxSize, boolean shouldRenderBorders, String shipId, boolean patrolFleetCreation, AoTDPatrolFleetData data, CreateNewFleetTab tab,FleetMembersShowcase parent) {
        super(boxSize, boxSize);
        this.data = data;
        this.shipIdl = shipId;
        this.parent = parent;
        this.tab = tab;
        this.patrolFleetCreation = patrolFleetCreation;
        this.setEnableRightClick(patrolFleetCreation);
        alphaBG = 0f;//uaf_supercap_slv_core
        this.shouldRenderBorders = shouldRenderBorders;
        if(shipId!=null) {
            CustomPanelAPI panel =  ShipInfoGenerator.getShipImage(Global.getSettings().getHullSpec(shipId),boxSize-10,null).one;
            componentPanel.addComponent(panel).inTL(originalWidth/2-(panel.getPosition().getWidth()/2)+1,5);
        }

        String imageName = Global.getSettings().getSpriteName("misc","default_portrait");
        ImageViewer viewer = new ImageViewer(boxSize/4,boxSize/4,imageName);
        addComponent(viewer,originalWidth-boxSize/4,0);
    }

    public void setMemberOfFleet(FleetMemberAPI memberOfFleet) {
        this.memberOfFleet = memberOfFleet;
    }

    @Override
    public void performActionOnClick(boolean isRightClick) {
        if(isRightClick&&patrolFleetCreation){
            data.removeExpectedVessel(shipIdl);
            if(tab!=null){
                tab.resetFleet();
                tab.resetInfoUI();
            }
        }
        if(!isRightClick&&memberOfFleet!=null){
            PopUpUI ui = new FleetMemberOptionsButtons(memberOfFleet,parent.manager,parent);
            placePopUpUI(ui, componentPanel,300,100);
        }
    }

}
