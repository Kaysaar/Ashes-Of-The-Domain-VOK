package data.kaysaar.aotd.vok.ui.basecomps;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;

public interface ExtendedUIPanelPlugin extends CustomUIPanelPlugin {
    CustomPanelAPI getMainPanel();

    void createUI();
}
