package data.kaysaar.aotd.vok.campaign.econ.megastructures;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableStat;

public class MegastructureStatManager {
    public MutableStat megastructureResourceCostMult = new MutableStat(1f);
    public static String memKey = "$aotd_mega_0stat_manager";
    public static MegastructureStatManager getInstance(){
        if(Global.getSector().getPersistentData().get(memKey)==null){
            Global.getSector().getPersistentData().put(memKey, new MegastructureStatManager());
        }
        return (MegastructureStatManager)Global.getSector().getPersistentData().get(memKey);
    }

    public MutableStat getMegastructureResourceCostMult() {
        return megastructureResourceCostMult;
    }
}
