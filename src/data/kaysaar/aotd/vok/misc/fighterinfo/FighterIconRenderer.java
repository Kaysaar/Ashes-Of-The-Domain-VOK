package data.kaysaar.aotd.vok.misc.fighterinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderInfo;
import data.kaysaar.aotd.vok.misc.shipinfo.ShipRenderInfoRepo;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FighterIconRenderer implements CustomUIPanelPlugin {
    transient ArrayList<CustomPanelAPI>panels = new ArrayList<>();
    public CustomPanelAPI tester;
    public SpriteAPI wingSprite;
    public void setTester(CustomPanelAPI tester) {
        this.tester = tester;
    }
    public void addPanels(ArrayList<CustomPanelAPI>panels){
        this.panels.addAll(panels);
    }

    public ShipRenderInfo info;
    float width;
    float hegiht;
    float scale =1f;
    public void setDimensions(float scale){
      this.scale = scale;
      this.width = (float) (info.width*scale);
      this.hegiht = (float) (info.height*scale);
      wingSprite.setSize(this.width,this.hegiht);
    }

    public FighterIconRenderer(FighterWingSpecAPI specAPI){
        info = ShipRenderInfoRepo.renderInfoRepo.get(specAPI.getVariant().getHullSpec().getHullId());
        wingSprite = Global.getSettings().getSprite(specAPI.getVariant().getHullSpec().getSpriteName());
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

//        if(tester!=null){
//            testSprite.setColor(Color.CYAN);
//            testSprite.setSize(tester.getPosition().getWidth(),tester.getPosition().getHeight());
//            testSprite.renderAtCenter(tester.getPosition().getCenterX(),tester.getPosition().getCenterY());
//        }
//;
//        for (CustomPanelAPI panel : panels) {
//            testSprite.setColor(Color.ORANGE);
//            testSprite.setSize(panel.getPosition().getWidth(),panel.getPosition().getHeight());
//            testSprite.renderAtCenter(panel.getPosition().getCenterX(),panel.getPosition().getCenterY());
//        }
        for (CustomPanelAPI panel : panels) {
            wingSprite.renderAtCenter(panel.getPosition().getCenterX(),panel.getPosition().getCenterY());
            for (ShipRenderInfo.Slot builtInSlot : info.built_in_slots) {
                WeaponSpecAPI weapon = Global.getSettings().getWeaponSpec(builtInSlot.id);
                String base = weapon.getTurretSpriteName();
                String under = weapon.getTurretUnderSpriteName();
                SpriteAPI baseSprite = Global.getSettings().getSprite(base);
                SpriteAPI underSprite = Global.getSettings().getSprite(under);
                setSizeOfSpriteToScale(baseSprite);
                setSizeOfSpriteToScale(underSprite);
                baseSprite.setAngle(builtInSlot.angle);
                underSprite.setAngle(builtInSlot.angle);
                float x = panel.getPosition().getCenterX() ;
                float y = panel.getPosition().getCenterY();

                renderSlotMoved(baseSprite, x + builtInSlot.locationOnShip.x * scale, y + builtInSlot.locationOnShip.y * scale);

            }

        }
    }
    private static void renderSlotMoved(SpriteAPI underSprite, float x, float y) {
        underSprite.renderAtCenter(x, y);
    }

    private void setSizeOfSpriteToScale(@NotNull SpriteAPI baseSprite) {
        baseSprite.setSize((float) baseSprite.getWidth() * scale, (float) baseSprite.getHeight() * scale);
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
