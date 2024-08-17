package data.kaysaar.aotd.vok.misc.fighterinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;

import java.util.ArrayList;
import java.util.HashMap;

public class FighterInfoRepo {

    public static ArrayList<FighterInfo> fighterRepo = new ArrayList<>();
    public static FighterInfo getFromRepo(String id){
        for (FighterInfo fighterInfo : fighterRepo) {
            if(fighterInfo.getFighterWingID().equals(id)){
                return fighterInfo;
            }
        }
        return null;
    }
    public static void initalizeRepo(){
        for (FighterWingSpecAPI allFighterWingSpec : Global.getSettings().getAllFighterWingSpecs()) {
            ShipAPI ship = Global.getCombatEngine().createFXDrone(allFighterWingSpec.getVariant());
            FleetMemberAPI ship2 = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING,allFighterWingSpec.getId());
            FighterInfo info = new FighterInfo(ship2.getHullSpec().getHullId(),new HashMap<String, Integer>());
            for (WeaponAPI allWeapon : ship.getAllWeapons()) {
                if(allWeapon.isDecorative())continue;
                if (info.getWeaponMap().containsKey(allWeapon.getSpec().getWeaponId())) {
                    info.getWeaponMap().put(allWeapon.getSpec().getWeaponId(), info.getWeaponMap().get((allWeapon.getSpec().getWeaponId()))+1);
                } else {
                    info.getWeaponMap().put(allWeapon.getSpec().getWeaponId(), 1);
                }
            }
            fighterRepo.add(info);
        }

    }
}
