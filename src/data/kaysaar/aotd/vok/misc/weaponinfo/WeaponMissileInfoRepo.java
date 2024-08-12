package data.kaysaar.aotd.vok.misc.weaponinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.json.JSONObject;

import java.util.HashMap;

public class WeaponMissileInfoRepo {
    public static HashMap<String,String>weapontoMissleMap = new HashMap<>();

    public static void initMap(){
        ShipHullSpecAPI specShip = Global.getSettings().getHullSpec("dem_drone");
        ShipVariantAPI v = Global.getSettings().createEmptyVariant("dem_drone", specShip);
        ShipAPI shipAPI = Global.getCombatEngine().createFXDrone(v);
        for (WeaponSpecAPI s : Global.getSettings().getAllWeaponSpecs()) {
            WeaponAPI weapon = Global.getCombatEngine().createFakeWeapon(shipAPI, s.getWeaponId());
            if (weapon != null && weapon.getMissileRenderData() != null&&!weapon.getMissileRenderData().isEmpty()) {
                try {
                    String id = weapon.getMissileRenderData().get(0).getMissileSpecId();
                    JSONObject obj = Global.getSettings().loadJSON("data/weapons/proj/"+id+".proj");
                    weapontoMissleMap.put(s.getWeaponId(),obj.getString("sprite"));
                } catch (Exception e) {

                }
            }
        }
    }
}
