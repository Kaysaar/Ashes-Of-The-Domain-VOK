package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.FactionAPI;

public interface AoTDResearchListener {
    public void finishedResearchOfTechnology(String id, FactionAPI faction);
}
