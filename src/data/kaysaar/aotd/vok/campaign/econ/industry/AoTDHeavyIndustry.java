package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;

public class AoTDHeavyIndustry extends HeavyIndustry {
    public boolean ignorePolutionStat = false;

    public void setIgnorePolutionStat(boolean ignorePolutionStat) {
        this.ignorePolutionStat = ignorePolutionStat;
    }

    @Override
    protected void updatePollutionStatus() {
        if(ignorePolutionStat)return;
        if(special!=null&& Global.getSettings().getSpecialItemSpec(special.getId()).hasTag("no_pollution")){
            return;
        }
        super.updatePollutionStatus();
    }

    @Override
    public void setSpecialItem(SpecialItemData special) {
        super.setSpecialItem(special);
    }
}
