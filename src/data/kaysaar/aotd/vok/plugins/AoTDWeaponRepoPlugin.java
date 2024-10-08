package data.kaysaar.aotd.vok.plugins;

import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfoGenerator;
import data.kaysaar.aotd.vok.misc.fighterinfo.FighterInfoRepo;
import data.kaysaar.aotd.vok.misc.weaponinfo.WeaponMissileInfoRepo;

public class AoTDWeaponRepoPlugin extends BaseEveryFrameCombatPlugin {
    @Override
    public void init(CombatEngineAPI engine) {

        if(Global.getCurrentState()== GameState.TITLE) {
            if (WeaponMissileInfoRepo.weapontoMissleMap.isEmpty()) {
                WeaponMissileInfoRepo.initMap();
            }
        if(FighterInfoRepo.fighterRepo.isEmpty()){
            FighterInfoRepo.initializeRepo();
        }
        }


    }
}
