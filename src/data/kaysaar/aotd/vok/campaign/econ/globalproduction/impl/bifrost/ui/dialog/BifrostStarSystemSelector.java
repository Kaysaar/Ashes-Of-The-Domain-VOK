package data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.bifrost.ui.dialog;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.StarSystemSelector;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.StarSystemSelectorOtherInfoData;

import java.util.ArrayList;

public class BifrostStarSystemSelector extends StarSystemSelector {
    BifrostStarSystemSelectorDialog dialog;

    public BifrostStarSystemSelectorDialog getDialog() {
        return dialog;
    }

    public BifrostStarSystemSelector(ArrayList<StarSystemAPI> systems, CustomPanelAPI panel, StarSystemSelectorOtherInfoData data,BifrostStarSystemSelectorDialog dialog) {
        super(systems, panel, data);
        this.dialog = dialog;
    }

    @Override
    public void onChangeOfStarSystem(StarSystemAPI starSystem) {
        BifrostLocationSelector bifrost = new BifrostLocationSelector(dialog);
        AshMisc.initPopUpDialog(bifrost,1000,600);

    }
}
