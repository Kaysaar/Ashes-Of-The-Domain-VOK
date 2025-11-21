package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class BifrostLocationSelector extends BasePopUpDialog {
    BifrostStarSystemSelectorDialog selector;
    ChooseOrbitForBifrost map;
    CustomPanelAPI panelOfInfoMain;
    CustomPanelAPI contentForInfo;
    boolean shouldUpdate = false;
    SectorEntityToken orbitFocus = null;
    public BifrostLocationSelector(BifrostStarSystemSelectorDialog selector) {
        super("Choose a location for a Bifrost Gate");
        this.selector = selector;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        map = new ChooseOrbitForBifrost(width-10,500,selector.getSelector().getCurrentlyChosenStarSystem());
        panelOfInfoMain = Global.getSettings().createCustom(width-10,100,null);
        contentForInfo = Global.getSettings().createCustom(width-10,100,null);
        panelOfInfoMain.addComponent(contentForInfo).inTL(0,0);
        tooltip.addCustom(map.getMainPanel(),5f);
        tooltip.addCustom(panelOfInfoMain,10f);
        tooltip.setHeightSoFar(0f);

        super.createContentForDialog(tooltip,width);
    }
    public void updateInfo(){
        panelOfInfoMain.removeComponent(contentForInfo);
        contentForInfo = Global.getSettings().createCustom(panelOfInfoMain.getPosition().getWidth(),panelOfInfoMain.getPosition().getHeight(),null);
        TooltipMakerAPI tooltip = contentForInfo.createUIElement(contentForInfo.getPosition().getWidth(),contentForInfo.getPosition().getHeight(),false);
        String font = "graphics/fonts/insignia21LTaa.fnt";
        tooltip.setParaFont( font);
        Float rad = map.getOrbitComponent().getSavedRadiusWorld();
        int r = 0;
        if(rad!=null){
            r = Math.round(rad);
        }
        tooltip.addPara("Orbit Focus : %s",0f, Color.ORANGE,orbitFocus.getName());
        contentForInfo.addUIElement(tooltip);
        panelOfInfoMain.addComponent(contentForInfo);

    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(map!=null&&map.getOrbitComponent().isLocked()&&map.getOrbitComponent().getCurrentlyCenteredAround()!=null){
            if(!map.getOrbitComponent().getCurrentlyCenteredAround().getToken().equals(orbitFocus)){
                orbitFocus = map.getOrbitComponent().getCurrentlyCenteredAround().getToken();
                updateInfo();
            }
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if(map.getOrbitComponent().isLocked()){
            BifrostLocationData data = new BifrostLocationData(map.getOrbitComponent().getCurrentlyCenteredAround().getToken(),map.getOrbitComponent().getSavedRadiusWorld(),map.translateCoordinatesFromUIToWorld(map.getOrbitComponent().getSavedCordsOfGate()), (float) Math.toDegrees(map.getOrbitComponent().getSavedGateAngleRad()));
            selector.data = data;
            map.clearUI();
        }
        else{
            selector.getSelector().currentlyChosenStarSystem=null;
        }
    }
}
