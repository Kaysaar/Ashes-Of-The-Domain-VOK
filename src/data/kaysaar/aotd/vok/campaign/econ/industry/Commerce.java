package data.kaysaar.aotd.vok.campaign.econ.industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.TradeCenter;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;


public class Commerce extends TradeCenter {

    @Override
    public boolean isAvailableToBuild() {
        boolean canBuild = false;
        if (market.getPlanetEntity() != null) {
            if (market.hasIndustry(AoTDIndustries.FORBIDDEN_CITY) || market.hasIndustry(AoTDIndustries.UNDERWORLD)) {
                return false;
            }
            return true;
        }
        return false;


    }

    @Override
    public String getUnavailableReason() {
        if (market.hasIndustry(AoTDIndustries.UNDERWORLD)) {
            return "There is already a Underworld present in your colony.";
        }
        if (market.hasIndustry(AoTDIndustries.FORBIDDEN_CITY)) {
            return "There is already a Forbidden City present in your colony.";
        }
        return super.getUnavailableReason();
    }
}
