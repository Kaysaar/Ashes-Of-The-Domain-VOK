package data.kaysaar.aotd.vok.ui.customprod.buttons;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionBrowserSection;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionCustomButton;

import data.kaysaar.aotd.vok.ui.onhover.ProducitonHoverInfo;

public class ItemProductionCustomButton extends ProductionCustomButton {

    public ItemProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData);
    }

    public ItemProductionCustomButton(
            float width,
            float height,
            AoTDProductionSpec buttonData,
            ProductionBrowserSection.ColumnLayout columnLayout
    ) {
        super(width, height, buttonData, columnLayout);
    }

    @Override
    public void createContainerContent(CustomPanelAPI container) {
        TooltipMakerAPI inserter = container.createUIElement(1, 1, false);

        float rowHeight = container.getPosition().getHeight();
        float iconSize = rowHeight - 4f;
        float opadText = 12f;

        if (getSpec().getProductionType() == AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            SpecialItemSpecAPI specAPI = (SpecialItemSpecAPI) getSpec().getUnderlyingSpec();

            ImageViewer viewer = new ImageViewer(iconSize, iconSize, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2, 2);

            inserter.addTooltipTo(
                    new ProducitonHoverInfo(getSpec()),
                    viewer.getComponentPanel(),
                    TooltipMakerAPI.TooltipLocation.BELOW,
                    false
            );
        } else {
            CommoditySpecAPI specAPI = (CommoditySpecAPI) getSpec().getUnderlyingSpec();

            ImageViewer viewer = new ImageViewer(iconSize, iconSize, specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2, 2);

            inserter.addTooltipTo(
                    new ProducitonHoverInfo(getSpec()),
                    viewer.getComponentPanel(),
                    TooltipMakerAPI.TooltipLocation.BELOW,
                    false
            );
        }

        addNameColumn(container, rowHeight, opadText);

        addTextColumn(container, "time", AshMisc.convertDaysToString(getSpec().getDaysToBeCreated()), opadText);
        addTextColumn(container, "design", getSpec().getManufacturer(), opadText, true);
        addCostColumn(container, rowHeight);
    }

    protected void addNameColumn(CustomPanelAPI container, float rowHeight, float opadText) {
        float iconReserved = rowHeight + 2f;
        float nameWidth = hasColumn("name")
                ? Math.max(0f, getColumnWidth("name") - iconReserved)
                : Math.max(0f, container.getPosition().getWidth() - iconReserved);

        TooltipMakerAPI tl = container.createUIElement(nameWidth, rowHeight, false);
        tl.addPara(getSpec().getName(), opadText + 1f);
        container.addUIElement(tl).inTL(iconReserved, 0);
    }

    protected void addTextColumn(CustomPanelAPI container, String columnId, String text, float opadText) {
        addTextColumn(container, columnId, text, opadText, false);
    }

    protected void addTextColumn(CustomPanelAPI container, String columnId, String text, float opadText, boolean useManufacturerColor) {
        if (!hasColumn(columnId)) return;

        TooltipMakerAPI tl = container.createUIElement(
                getColumnWidth(columnId),
                container.getPosition().getHeight(),
                false
        );

        if (useManufacturerColor) {
            tl.addPara(text, getSpec().getManufacturerColor(), opadText).setAlignment(Alignment.MID);
        } else {
            tl.addPara(text, opadText).setAlignment(Alignment.MID);
        }

        container.addUIElement(tl).inTL(getColumnStartX(columnId), 0);
    }

    protected void addCostColumn(CustomPanelAPI container, float rowHeight) {
        if (!hasColumn("totalCost")) return;

        TooltipMakerAPI tlCost = container.createUIElement(
                getColumnWidth("totalCost"),
                rowHeight,
                false
        );

        tlCost.addCustom(
                createCostSection(getColumnWidth("totalCost"), height),
                2f
        ).getPosition().inTL(0, 1);

        container.addUIElement(tlCost).inTL(getColumnStartX("totalCost"), 0);
    }
}