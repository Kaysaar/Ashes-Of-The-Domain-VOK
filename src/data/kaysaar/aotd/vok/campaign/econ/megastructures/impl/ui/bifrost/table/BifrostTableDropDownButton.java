package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.table;

import ashlib.data.plugins.ui.models.DropDownButton;
import ashlib.data.plugins.ui.plugins.UITableImpl;
import data.kaysaar.aotd.tot.ui.commodityDetailedInfo.AoTDDetailedInfoButton;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityInfoButton;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;

import java.util.ArrayList;

public class BifrostTableDropDownButton extends DropDownButton {
    public BifrostSection section;
    public BifrostTableDropDownButton(UITableImpl tableOfReference, float width, float height, float maxWidth, float maxHeight,BifrostSection section) {
        super(tableOfReference, width, height, maxWidth, maxHeight, false);
        this.section = section;
    }

    @Override
    public void createUIContent() {
        buttons = new ArrayList<>();
        mainButton = new BifrostInfoButton(width,height,section);

        mainButton.createUI();
        tooltipOfImpl.addCustom(mainButton.getPanel(), 5f).getPosition().inTL(0, 0);
    }
}
