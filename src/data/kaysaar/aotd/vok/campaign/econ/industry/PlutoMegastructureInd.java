package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.pluto.PlutoMegastructure;

public class PlutoMegastructureInd extends BaseMegastructureIndustry{
    @Override
    public String getCurrentImage() {
        if(megastructure instanceof PlutoMegastructure mega){
            if(mega.getLaserSection().isFiringLaser()){
               return Global.getSettings().getSpriteName("industry","pluto_active");
            }
        }
        return super.getCurrentImage();
    }
}
