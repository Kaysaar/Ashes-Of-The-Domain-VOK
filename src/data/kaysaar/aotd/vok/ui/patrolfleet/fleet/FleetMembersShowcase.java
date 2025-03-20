package data.kaysaar.aotd.vok.ui.patrolfleet.fleet;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;
import data.kaysaar.aotd.vok.ui.patrolfleet.CreateNewFleetTab;
import data.kaysaar.aotd.vok.ui.patrolfleet.PatrolFleetDataManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FleetMembersShowcase implements CustomUIPanelPlugin {
    public CustomPanelAPI panel;
    TextFieldAPI textPanelOfName;
    AoTDPatrolFleetData data;
    public CreateNewFleetTab tab;
   public PatrolFleetDataManager manager;
    ArrayList<ShipWithOfficerShowcase>showcases = new ArrayList<>();
    public void addFleetMembers(TooltipMakerAPI tooltip, float width) {
        float currX = 0;
        float currY = 0;
        float seperatorX = 5;
        float seperatorY = 5;;
        LinkedHashMap<String,Integer>registeredShips = new LinkedHashMap<>();
        if(data.getFleet()!=null){
            tooltip.addSpacer(60);
            registeredShips.put(data.getFleet().getFlagship().getVariant().getHullSpec().getHullId(),1);
            for (FleetMemberAPI str : data.getFleet().getFleetData().getMembersInPriorityOrder()) {
                if(str.isFlagship())continue;
                ShipWithOfficerShowcase showcase = new ShipWithOfficerShowcase(50, false, str.getVariant().getHullSpec().getHullId(),creatorMode,data,tab,this);
                showcase.setMemberOfFleet(str);
                if (currX + showcase.originalWidth > width) {
                    currY += showcase.originalHeight + seperatorY;
                    tooltip.addSpacer(showcase.originalHeight + seperatorY);
                    currX = 0;
                }
                if(!registeredShips.containsKey(str.getVariant().getHullSpec().getHullId())){
                    registeredShips.put(str.getVariant().getHullSpec().getHullId(), 1);
                }
                else{
                    int curr = registeredShips.get(str.getVariant().getHullSpec().getHullId());
                    registeredShips.put(str.getVariant().getHullSpec().getHullId(), curr+1);
                }
                tooltip.addCustomDoNotSetPosition(showcase.getComponentPanel()).getPosition().inTL(currX, currY);
                currX += showcase.originalWidth + seperatorX;
                showcases.add(showcase);

            }
        }

        currX = 0;
        LinkedHashMap<String,Integer> missingOnes = new LinkedHashMap();
        boolean mustCont = true;
        for (Map.Entry<String, Integer> entry : data.getExpectedVesselsInFleet().entrySet()) {

            if(!registeredShips.containsKey(entry.getKey())){
                missingOnes.put(entry.getKey(),entry.getValue());
            }
            else{
                if(entry.getValue()-registeredShips.get(entry.getKey())>0){
                    missingOnes.put(entry.getKey(),entry.getValue()-registeredShips.get(entry.getKey()));

                }

            }
        }
        if(!missingOnes.isEmpty()&&data.getFleet()!=null){
            tooltip.addSectionHeading("Missing ones",Alignment.MID,5f);
            tooltip.addSpacer(60);
            currY +=90;
        }
        for (Map.Entry<String, Integer> entry : missingOnes.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                if(data.getFleet()==null&&mustCont){
                    mustCont = false;
                    continue;
                }
                ShipWithOfficerShowcase showcase = new ShipWithOfficerShowcase(50, false,entry.getKey(),creatorMode,data,tab,this);
                if (currX + showcase.originalWidth > width) {
                    currY += showcase.originalHeight + seperatorY;
                    tooltip.addSpacer(showcase.originalHeight + seperatorY);
                    currX = 0;
                }
                tooltip.addCustomDoNotSetPosition(showcase.getComponentPanel()).getPosition().inTL(currX, currY);
                showcases.add(showcase);
                currX += showcase.originalWidth + seperatorX;
            }


        }


    }

    public String getFirstInTop(){
        for (String s : data.getExpectedVesselsInFleet().keySet()) {
            return s;
        }
        return null;
    }
    public boolean creatorMode = false;
    public FleetMembersShowcase(float width, float height, AoTDPatrolFleetData data,boolean creatorMode,CreateNewFleetTab tab){
        this.data = data;
        this.tab = tab;
        createPanel(width, height, data, creatorMode, tab);
    }
    public FleetMembersShowcase(float width, float height, AoTDPatrolFleetData data,boolean creatorMode,PatrolFleetDataManager tab){
        this.data = data;
        this.manager = tab;
        createPanel(width, height, data, creatorMode,null );
    }
    private void createPanel(float width, float height, AoTDPatrolFleetData data, boolean creatorMode, CreateNewFleetTab tab) {
        panel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tool = panel.createUIElement(width -115, height -25, true);
        TooltipMakerAPI tooltip = panel.createUIElement(width,20,false);
        this.creatorMode = creatorMode;
        textPanelOfName = tooltip.addTextField(panel.getPosition().getWidth(), Fonts.ORBITRON_20AABOLD,0f);
        textPanelOfName.setText(data.name);
        textPanelOfName.setLimitByStringWidth(true);
        addFleetMembers(tool, panel.getPosition().getWidth()-115);
        ShipWithOfficerShowcase showcase;
        if(data.getFleet()!=null){
            showcase = new ShipWithOfficerShowcase(110, true, data.getFleet().getFlagship().getVariant().getHullSpec().getHullId(), creatorMode, data, tab,this);
            showcase.setMemberOfFleet(data.getFleet().getFlagship());
        }
        else{
            showcase = new ShipWithOfficerShowcase(110, true,getFirstInTop(), creatorMode, data, tab,this);

        }
        showcases.add(showcase);
        panel.addUIElement(tooltip).inTL(-5,0);
        panel.addComponent(showcase.getPanelOfButton()).inTL(0,35);
        panel.addUIElement(tool).inTL(115,35);
    }

    public CustomPanelAPI getPanel() {
        return panel;
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
        if(data!=null){
            this.data.setName(textPanelOfName.getText());}
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
