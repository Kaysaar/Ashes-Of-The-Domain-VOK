package data.kaysaar.aotd.vok.campaign.econ.produciton.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class AoTDProductionListenerUtils {
    public static void onShipProductionFinished(FleetMemberAPI member){
        for (AoTDProductionListenerAPI listener : Global.getSector().getListenerManager().getListeners(AoTDProductionListenerAPI.class)) {
            listener.onShipProductionFinished(member);
        }
    }
}
