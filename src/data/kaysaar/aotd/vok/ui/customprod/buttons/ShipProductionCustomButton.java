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
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.UIData;
import data.kaysaar.aotd.vok.ui.customprod.ProductionBrowserSection;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

public class ShipProductionCustomButton extends ProductionCustomButton {


    public ShipProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData);
    }

    @Override
    public void createContainerContent(CustomPanelAPI container) {
        ShipHullSpecAPI specAPI = Global.getSettings().getHullSpec(getSpec().getId());
        CustomPanelAPI shipPanel = ShipInfoGenerator.getShipImage(specAPI,container.getPosition().getHeight()-4,null).one;
        container.addComponent(shipPanel).inTL(2,2);
        FleetMemberAPI fleetMemberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP, AshMisc.getVaraint((ShipHullSpecAPI) getSpec().getUnderlyingSpec()));
        fleetMemberAPI.getRepairTracker().setCR(0.7f);
        fleetMemberAPI.getCrewComposition().addCrew(fleetMemberAPI.getMinCrew());
        fleetMemberAPI.updateStats();
        UIData.createTooltipForShip(fleetMemberAPI,shipPanel);
        float opadText = 12;
        TooltipMakerAPI tl =  container.createUIElement(container.getPosition().getWidth()-container.getPosition().getHeight()-5,container.getPosition().getHeight(),false);
        tl.addPara(getSpec().getName(),opadText+1);
        container.addUIElement(tl).inTL(container.getPosition().getHeight()-3,0);
        float startingX = ProductionBrowserSection.getColumnStartX("time",container.getPosition().getWidth(),1,getSpec().getProductionType());

        TooltipMakerAPI tlTime = container.createUIElement(ProductionBrowserSection.getColumnWidth("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlTime.addPara(AshMisc.convertDaysToString(getSpec().getDaysToBeCreated()),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlTime).inTL(ProductionBrowserSection.getColumnStartX("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);

        TooltipMakerAPI tlSize = container.createUIElement(ProductionBrowserSection.getColumnWidth("size",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlSize.addPara(getSpec().getSize(),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlSize).inTL(ProductionBrowserSection.getColumnStartX("size",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);

        TooltipMakerAPI tlType = container.createUIElement(ProductionBrowserSection.getColumnWidth("type",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlType.addPara(getSpec().getTypeString(),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlType).inTL(ProductionBrowserSection.getColumnStartX("type",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);

        TooltipMakerAPI tlDesign = container.createUIElement(ProductionBrowserSection.getColumnWidth("design",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlDesign.addPara(getSpec().getManufacturer(),getSpec().getManufacturerColor(),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlDesign).inTL(ProductionBrowserSection.getColumnStartX("design",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);

        TooltipMakerAPI tlCost = container.createUIElement(ProductionBrowserSection.getColumnWidth("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlCost.addCustom(this.createCostSection(ProductionBrowserSection.getColumnWidth("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),height),2f).getPosition().inTL(0,1);
        container.addUIElement(tlCost).inTL(ProductionBrowserSection.getColumnStartX("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);
    }

}
