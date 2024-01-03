package data.kaysaar.aotd.vok.scripts.research.scientist;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import data.kaysaar.aotd.vok.scripts.research.scientist.models.ScientistAPI;

public class IcetoKafkanari extends ScientistAPI {
    public IcetoKafkanari(PersonAPI person, FactionAPI tiedToFaction) {
        super(person, tiedToFaction);
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
    }


}
