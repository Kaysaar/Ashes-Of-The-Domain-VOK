package data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.listeners.models.AoTDSpecialProjectListener;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

public class AoTDSpecialProjBaseListener implements AoTDSpecialProjectListener {
    @Override
    public FleetMemberAPI receiveReward(ShipHullSpecAPI specOfShip, CargoAPI cargo) {
        FleetMemberAPI fleet = cargo.getMothballedShips().addFleetMember(AoTDMisc.getVaraint(specOfShip));
        fleet.getVariant().clear();
        return fleet;
    }
}
