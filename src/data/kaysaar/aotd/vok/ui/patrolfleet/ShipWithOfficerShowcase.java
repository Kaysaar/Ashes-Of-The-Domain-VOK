package data.kaysaar.aotd.vok.ui.patrolfleet;

import ashlib.data.plugins.info.ShipInfoGenerator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.ui.basecomps.ButtonComponent;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.util.List;

public class ShipWithOfficerShowcase extends ButtonComponent {
    public ShipWithOfficerShowcase(float boxSize,boolean shouldRenderBorders,String shipId) {
        super(boxSize, boxSize);
        alphaBG = 0f;//uaf_supercap_slv_core
       CustomPanelAPI panel =  ShipInfoGenerator.getShipImage(Global.getSettings().getHullSpec(shipId),boxSize-10,null).one;
       this.shouldRenderBorders = shouldRenderBorders;
       componentPanel.addComponent(panel).inTL(originalWidth/2-(panel.getPosition().getWidth()/2)+1,5);
        String imageName = Global.getSettings().getSpriteName("misc","default_portrait");
        ImageViewer viewer = new ImageViewer(boxSize/4,boxSize/4,imageName);
        addComponent(viewer,originalWidth-boxSize/4,0);
    }

}
