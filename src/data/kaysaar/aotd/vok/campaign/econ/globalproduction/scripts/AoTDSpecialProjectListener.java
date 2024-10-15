package data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public interface AoTDSpecialProjectListener {
    public FleetMemberAPI receiveReward(ShipHullSpecAPI specOfShip, CargoAPI cargo);
}
