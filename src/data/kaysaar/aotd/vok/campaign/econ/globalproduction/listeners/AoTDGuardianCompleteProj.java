package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class  AoTDGuardianCompleteProj extends AoTDSpecialProjBaseListener{
    @Override
    public FleetMemberAPI receiveReward(ShipHullSpecAPI specOfShip, CargoAPI cargo) {
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(specOfShip));
        if(specOfShip.getHullId().equals("guardian")){
            fleet.getVariant().getHullMods().remove(HullMods.AUTOMATED);
        }
        fleet.getVariant().clear();
        return fleet;
    }
}
