package data.kaysaar.aotd.vok.misc.weaponinfo;

import com.fs.graphics.Sprite;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.BeamWeaponSpecAPI;
import com.fs.starfarer.api.loading.MissileSpecAPI;
import com.fs.starfarer.api.loading.ProjectileWeaponSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.loading.specs.Object;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONObject;
import org.lwjgl.util.glu.Project;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class WeaponSpriteRenderer implements CustomUIPanelPlugin {

    ArrayList<SpriteAPI> spritesToRedner;
    CustomPanelAPI anchor;
    WeaponSpecAPI specWeapon;
    String idOfMissileSprite = null;
    float scale = 1f;
    Vector2f originalCenterOfMissle;
    public void setAnchor(CustomPanelAPI anchor) {
        this.anchor = anchor;
    }

    public WeaponSpriteRenderer(WeaponSpecAPI spec, float iconSize, float angle) {
        this.specWeapon =spec;
        ShipHullSpecAPI specShip = Global.getSettings().getHullSpec("dem_drone");
        ShipVariantAPI v = Global.getSettings().createEmptyVariant("dem_drone", specShip);
        ShipAPI shipAPI = Global.getCombatEngine().createFXDrone(v);
        WeaponAPI weapon = Global.getCombatEngine().createFakeWeapon(shipAPI, spec.getWeaponId());
        spritesToRedner = new ArrayList<>();
        spritesToRedner.add(Global.getSettings().getSprite(spec.getTurretUnderSpriteName()));
        spritesToRedner.add(Global.getSettings().getSprite(spec.getTurretSpriteName()));
        SpriteAPI baseSprite = weapon.getSprite();
        if(spec instanceof ProjectileWeaponSpecAPI){
            spritesToRedner.add(Global.getSettings().getSprite(((ProjectileWeaponSpecAPI) spec).getTurretGunSpriteName()));
        }
        for (SpriteAPI spriteAPI : spritesToRedner) {
            spriteAPI.setAngle(angle);
            float originalWidth = spriteAPI.getWidth();
            float originalHeight = spriteAPI.getHeight();
            float newWidth, newHeight;
            float aspectRatio = originalWidth / originalHeight;
            newHeight = iconSize;
            newWidth = iconSize * aspectRatio;

            spriteAPI.setSize(newWidth, newHeight);
        }

        scale = getScale(baseSprite,iconSize);
        if (weapon != null && weapon.getMissileRenderData() != null&&!weapon.getMissileRenderData().isEmpty()) {
            try {
                String id = weapon.getMissileRenderData().get(0).getMissileSpecId();
                JSONObject obj = Global.getSettings().loadJSON("data/weapons/proj/"+id+".proj");
                idOfMissileSprite = obj.getString("sprite");
                float x;
                float y;
                float width;
                float height;

            } catch (Exception e) {

            }
        }
    }
    public float getScale(SpriteAPI sprite,float iconSize){
        float originalWidth = sprite.getWidth();
        float originalHeight = sprite.getHeight();
        float newWidth, newHeight;
        float aspectRatio = originalWidth / originalHeight;
        newHeight = iconSize;
        newWidth = iconSize * aspectRatio;
        return newWidth/originalWidth;
    }
    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        if (anchor != null) {
            for (SpriteAPI spriteAPI : spritesToRedner) {
                spriteAPI.renderAtCenter(anchor.getPosition().getCenterX(), anchor.getPosition().getCenterY());
            }
        }
        if(idOfMissileSprite!=null){
            SpriteAPI sprite = Global.getSettings().getSprite(idOfMissileSprite);
            sprite.setSize(sprite.getWidth()*scale,sprite.getHeight()*scale);
            for (Vector2f turretFireOffset : specWeapon.getTurretFireOffsets()) {
                sprite.renderAtCenter((anchor.getPosition().getCenterX()+(turretFireOffset.getY()*scale)),anchor.getPosition().getCenterY()+(turretFireOffset.x*scale));
            }
        }



    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(java.lang.Object buttonId) {

    }

    public void buttonPressed(Object buttonId) {

    }
}
