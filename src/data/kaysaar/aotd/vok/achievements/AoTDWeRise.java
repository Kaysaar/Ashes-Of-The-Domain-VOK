package data.kaysaar.aotd.vok.achievements;

import data.kaysaar.aotd.vok.Ids.AoTDTechIds;

public class AoTDWeRise extends AoTDBaseResearchAchievement{
    public AoTDWeRise(){
        technologies.add(AoTDTechIds.ORBITAL_FLEETWORK_FACILITIES);
        technologies.add(AoTDTechIds.ORBITAL_SKUNKWORK_FACILITIES);
    }

}
