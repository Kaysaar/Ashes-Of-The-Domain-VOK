package data.kaysaar.aotd.vok.campaign.econ.produciton.listeners;

import com.fs.starfarer.api.fleet.FleetMemberAPI;

public interface AoTDProductionListenerAPI {
    public void onShipProductionFinished(FleetMemberAPI member);
}
