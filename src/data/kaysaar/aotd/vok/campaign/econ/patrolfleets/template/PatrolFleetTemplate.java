package data.kaysaar.aotd.vok.campaign.econ.patrolfleets.template;

import data.kaysaar.aotd.vok.campaign.econ.patrolfleets.AoTDPatrolFleetData;

import java.util.ArrayList;

public class PatrolFleetTemplate {
    ArrayList<AoTDPatrolFleetData>templateData;

    public PatrolFleetTemplate() {

    }
    public void refreshTemplateAfterSave(){
        //This method will be called every boot of game, to check if mod list has changed ( if mods were disabled)

    }
}
