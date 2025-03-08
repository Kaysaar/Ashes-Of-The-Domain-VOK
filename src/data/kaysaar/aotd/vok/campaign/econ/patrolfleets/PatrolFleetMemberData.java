package data.kaysaar.aotd.vok.campaign.econ.patrolfleets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;

public class PatrolFleetMemberData {
    public String shipId;
    public int dpPoints;
    public ShipAPI.HullSize hullSize;
    public String shipRoleType;

    public int getDpPoints() {
        return dpPoints;
    }
    public ShipAPI.HullSize getHullSize() {
        return hullSize;
    }
    public String getShipRoleType() {
        return shipRoleType;
    }
    public String getShipId() {
        if(Global.getSettings().getShipSystemSpec(shipId)==null){
            findMatchingShip();
        }
        return shipId;
    }
    public PatrolFleetMemberData (ShipHullSpecAPI spec){
        this.shipId = spec.getHullId();
        this.dpPoints = spec.getFleetPoints();
        this.hullSize = spec.getHullSize();
        this.shipRoleType = "Carrier";
    }
    public void findMatchingShip(){
        // Remember to do here that first looks for role tpye then looks for closest matching size and DP

    }

}
