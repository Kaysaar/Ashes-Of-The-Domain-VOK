package data.kaysaar.aotd.vok.ui.customprod.buttons;

import ashlib.data.plugins.info.FighterInfoGenerator;
import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.FormationType;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.customprod.ProductionBrowserSection;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

import static data.kaysaar.aotd.vok.ui.UIData.createFighterTooltip;

public class FighterProductionCustomButton extends ProductionCustomButton {
    public FighterProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData);
    }

    @Override
    public void createContainerContent(CustomPanelAPI container) {
        FighterWingSpecAPI specAPI = (FighterWingSpecAPI) getSpec().getUnderlyingSpec();
        CustomPanelAPI shipPanel = FighterInfoGenerator.createFormationPanel(specAPI, FormationType.BOX, (int) (container.getPosition().getHeight()-4f),specAPI.getNumFighters()).one;
        container.addComponent(shipPanel).inTL(2,2);
        FleetMemberAPI fleetMember = Global.getFactory().createFleetMember(FleetMemberType.FIGHTER_WING,getSpec().getId());
        createFighterTooltip(fleetMember, (FighterWingSpecAPI) getSpec().getUnderlyingSpec(),shipPanel);

        float opadText = 12;
        TooltipMakerAPI tl =  container.createUIElement(container.getPosition().getWidth()-container.getPosition().getHeight()-5,container.getPosition().getHeight(),false);
        tl.addPara(getSpec().getName(),opadText+1);
        container.addUIElement(tl).inTL(container.getPosition().getHeight(),0);
        float startingX = ProductionBrowserSection.getColumnStartX("time",container.getPosition().getWidth(),1,getSpec().getProductionType());

        TooltipMakerAPI tlTime = container.createUIElement(ProductionBrowserSection.getColumnWidth("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlTime.addPara(AshMisc.convertDaysToString(getSpec().getDaysToBeCreated()),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlTime).inTL(ProductionBrowserSection.getColumnStartX("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);
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
