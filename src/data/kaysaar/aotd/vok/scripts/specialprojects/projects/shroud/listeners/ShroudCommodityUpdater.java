package data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.listeners;

import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.listeners.CoreUITabListener;
import data.kaysaar.aotd.vok.scripts.specialprojects.projects.shroud.ShroudProjectMisc;

public class ShroudCommodityUpdater implements CoreUITabListener {
    @Override
    public void reportAboutToOpenCoreTab(CoreUITabId tab, Object param) {
        ShroudProjectMisc.updateCommodityInfo();
    }
}
