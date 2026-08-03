package data.kaysaar.aotd.vok.ui.customprod.dialogs;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderSnapshot;
import data.kaysaar.aotd.vok.ui.customprod.ProductionMainPanel;
import data.kaysaar.aotd.vok.ui.customprod.dialogs.components.ChangeOrderProductList;
import data.kaysaar.aotd.vok.ui.customprod.orders.CurrentOrderList;

public class ChangeOnGoingOrderDialog extends BasePopUpDialog {
    AoTDProductionOrderSnapshot snapshot;
    ChangeOrderProductList listToChange;
    ProductionMainPanel mainPanel;
    public ChangeOnGoingOrderDialog(AoTDProductionOrderSnapshot snapshot, ProductionMainPanel mainPanel,float height) {
        super("Change Order");
        this.snapshot = snapshot;
        this.mainPanel = mainPanel;
        AshMisc.initPopUpDialog(this,ChangeOrderProductList.getWidth()+40,height);
    }

    float heightRecorded ;  @Override
    public void createUI(CustomPanelAPI panelAPI) {
        heightRecorded = panelAPI.getPosition().getHeight()-this.y-20;
        super.createUI(panelAPI);

    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        listToChange = new ChangeOrderProductList(heightRecorded-50, snapshot);
        tooltip.addCustom(listToChange.getMainPanel(),2f);


    }

    @Override
    public void applyConfirmScript() {
        listToChange.onConfirm();
        listToChange.clearUI();
        mainPanel.getIssuedOrdersList().createUI();
    }
}
