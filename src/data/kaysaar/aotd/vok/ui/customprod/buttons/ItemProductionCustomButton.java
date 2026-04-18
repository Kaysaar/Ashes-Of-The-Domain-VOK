package data.kaysaar.aotd.vok.ui.customprod.buttons;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.campaign.SpecialItemSpecAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;
import data.kaysaar.aotd.vok.ui.customprod.ProductionBrowserSection;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;
import data.kaysaar.aotd.vok.ui.onhover.ProducitonHoverInfo;

public class ItemProductionCustomButton extends ProductionCustomButton {
    public ItemProductionCustomButton(float width, float height, AoTDProductionSpec buttonData) {
        super(width, height, buttonData);
    }

    @Override
    public void createContainerContent(CustomPanelAPI container) {
        TooltipMakerAPI inserter = container.createUIElement(1,1,false);
        if(getSpec().getProductionType()== AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM){
            SpecialItemSpecAPI specAPI = (SpecialItemSpecAPI) getSpec().getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(container.getPosition().getHeight()-4,container.getPosition().getHeight()-4,specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2,2);
            inserter.addTooltipTo(new ProducitonHoverInfo(getSpec()),viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW,false);
        }
        else{
            CommoditySpecAPI specAPI = (CommoditySpecAPI) getSpec().getUnderlyingSpec();
            ImageViewer viewer = new ImageViewer(container.getPosition().getHeight()-4,container.getPosition().getHeight()-4,specAPI.getIconName());
            container.addComponent(viewer.getComponentPanel()).inTL(2,2);
            inserter.addTooltipTo(new ProducitonHoverInfo(getSpec()),viewer.getComponentPanel(), TooltipMakerAPI.TooltipLocation.BELOW,false);

        }
        float opadText = 12;
        TooltipMakerAPI tl =  container.createUIElement(container.getPosition().getWidth()-container.getPosition().getHeight()-5,container.getPosition().getHeight(),false);
        tl.addPara(getSpec().getName(),opadText+1);
        container.addUIElement(tl).inTL(container.getPosition().getHeight(),0);

        TooltipMakerAPI tlTime = container.createUIElement(ProductionBrowserSection.getColumnWidth("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlTime.addPara(AshMisc.convertDaysToString(getSpec().getDaysToBeCreated()),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlTime).inTL(ProductionBrowserSection.getColumnStartX("time",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);


        TooltipMakerAPI tlDesign = container.createUIElement(ProductionBrowserSection.getColumnWidth("design",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlDesign.addPara(getSpec().getManufacturer(),getSpec().getManufacturerColor(),opadText).setAlignment(Alignment.MID);
        container.addUIElement(tlDesign).inTL(ProductionBrowserSection.getColumnStartX("design",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);

        TooltipMakerAPI tlCost = container.createUIElement(ProductionBrowserSection.getColumnWidth("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),container.getPosition().getHeight(),false);
        tlCost.addCustom(this.createCostSection(ProductionBrowserSection.getColumnWidth("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),height),2f).getPosition().inTL(0,1);
        container.addUIElement(tlCost).inTL(ProductionBrowserSection.getColumnStartX("totalCost",container.getPosition().getWidth(),1,getSpec().getProductionType()),0);
    }
}
