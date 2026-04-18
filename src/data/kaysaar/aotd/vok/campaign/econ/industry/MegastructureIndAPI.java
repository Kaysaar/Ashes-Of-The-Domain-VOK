package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public interface MegastructureIndAPI {
     default String getMegastructureButtonText(Industry industry){
          return "Manage megastructure";
     };
     SectorEntityToken getEntityOfMegastructure(Industry industry);
     BaseMegastructureScript getMegastructureScript(SectorEntityToken token);


}
