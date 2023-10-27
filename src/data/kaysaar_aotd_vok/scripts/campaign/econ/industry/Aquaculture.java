package data.kaysaar_aotd_vok.scripts.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.Farming;
import com.fs.starfarer.api.impl.campaign.ids.Planets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Aquaculture extends Farming {
    public static Set<String> AQUA_PLANETS = new HashSet<String>();

    static {
        AQUA_PLANETS.add(Planets.PLANET_WATER);

    }

    @Override
    public boolean isAvailableToBuild() {
        if(market.getFactionId().equals(Global.getSector().getPlayerFaction().getId())){
            if(Global.getSettings().getModManager().isModEnabled("aod_core")){
                HashMap<String,Boolean>researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
                if(researchSaved!=null){
                    return  researchSaved.get(this.id);
                }

            }
        }
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());

        return canAquaculture;

    }



    @Override
    public boolean showWhenUnavailable() {
        if(market.getFactionId().equals(Global.getSector().getPlayerFaction().getId())){
            if(Global.getSettings().getModManager().isModEnabled("aod_core")){
                HashMap<String,Boolean>researchSaved = (HashMap<String, Boolean>) Global.getSector().getPersistentData().get("researchsaved");
                if(researchSaved!=null){
                    return  researchSaved.get(this.id);
                }

            }
        }
        boolean canAquaculture = market.getPlanetEntity() != null &&
                AQUA_PLANETS.contains(market.getPlanetEntity().getTypeId());

        return canAquaculture;


    }


    @Override
    public String getUnavailableReason() {
        return "Requires water surfaced world";
    }






    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
