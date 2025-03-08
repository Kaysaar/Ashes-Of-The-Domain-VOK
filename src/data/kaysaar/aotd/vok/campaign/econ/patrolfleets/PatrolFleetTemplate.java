package data.kaysaar.aotd.vok.campaign.econ.patrolfleets;

import com.fs.starfarer.api.combat.ShipHullSpecAPI;

import java.util.ArrayList;

public class PatrolFleetTemplate {
    ArrayList<PatrolFleetMemberData> data =new ArrayList<>();

    public PatrolFleetTemplate() {

    }
    public void addShip(ShipHullSpecAPI spec){
        data.add(new PatrolFleetMemberData(spec));
    }
    public void removeShip(ShipHullSpecAPI spec){
        for (PatrolFleetMemberData datum : data) {
            if(datum.getShipId().equals(spec.getHullId())){
                data.remove(datum);
                break;
            }
        }
    }
    public void refreshTemplateAfterSave(){
        //This method will be called every boot of game, to check if mod list has changed ( if mods were dissabled)

    }
}
