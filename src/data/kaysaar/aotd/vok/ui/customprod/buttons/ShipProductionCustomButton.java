package data.kaysaar.aotd.vok.ui.customprod.buttons;

import ashlib.data.plugins.info.ShipInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.tot.ui.UIData;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionBrowserSection;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionCustomButton;
;

public class ShipProductionCustomButton extends ProductionCustomButton {

    public ShipProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData);
    }

    public ShipProductionCustomButton(
            float width,
            float height,
            AoTDProductionSpec buttonData,
            ProductionBrowserSection.ColumnLayout columnLayout
    ) {
        super(width, height, buttonData, columnLayout);
    }

    @Override
    public void createContainerContent(CustomPanelAPI container) {
        ShipHullSpecAPI specAPI = Global.getSettings().getHullSpec(getSpec().getId());

        float rowHeight = container.getPosition().getHeight();
        float iconSize = rowHeight - 4f;
        float opadText = 12f;

        CustomPanelAPI shipPanel = ShipInfoGenerator.getShipImage(specAPI, iconSize, null).one;
        container.addComponent(shipPanel).inTL(2, 2);

        FleetMemberAPI fleetMemberAPI = Global.getFactory().createFleetMember(
                FleetMemberType.SHIP,
                AshMisc.getVaraint((ShipHullSpecAPI) getSpec().getUnderlyingSpec())
        );
        fleetMemberAPI.getRepairTracker().setCR(0.7f);
        fleetMemberAPI.getCrewComposition().addCrew(fleetMemberAPI.getMinCrew());
        fleetMemberAPI.updateStats();

        UIData.createTooltipForShip(fleetMemberAPI, shipPanel);

        addNameColumn(container, rowHeight, opadText);

        addTextColumn(container, "time", AshMisc.convertDaysToString(getSpec().getDaysToBeCreated()), opadText);
        addTextColumn(container, "size", getSpec().getSize(), opadText);
        addTextColumn(container, "type", getSpec().getTypeString(), opadText);
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