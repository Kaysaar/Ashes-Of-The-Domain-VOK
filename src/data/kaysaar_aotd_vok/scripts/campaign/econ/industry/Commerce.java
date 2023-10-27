package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.TradeCenter;
import data.Ids.AoDIndustries;

public class Commerce extends TradeCenter {

    @Override
    public boolean isAvailableToBuild() {
        boolean canBuild = false;
        if (market.getPlanetEntity() != null) {
            if (market.hasIndustry(AoDIndustries.FORBIDDEN_CITY) || market.hasIndustry(AoDIndustries.UNDERWORLD)) {
                return false;
            }
            return true;
        }
        return false;


    }

    @Override
    public String getUnavailableReason() {
        if (market.hasIndustry(AoDIndustries.UNDERWORLD)) {
            return "There is already a Underworld in your colony";
        }
        if (market.hasIndustry(AoDIndustries.FORBIDDEN_CITY)) {
            return "There is already a Forbidden City in your colony";
        }
        return super.getUnavailableReason();
    }
}
