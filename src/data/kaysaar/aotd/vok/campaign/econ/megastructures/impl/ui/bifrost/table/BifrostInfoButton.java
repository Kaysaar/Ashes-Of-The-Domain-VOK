package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.table;

import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.resizable.ImageViewer;
import ashlib.data.plugins.ui.plugins.UILinesRenderer;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.misc.AoTDToolboxMisc;
import data.kaysaar.aotd.tot.scripts.commoditydata.AoTDCommodityOnMarket;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityUITable;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;

public class BifrostInfoButton extends CustomButton {
    public BifrostInfoButton(float width, float height, Object buttonData) {
        super(width, height, buttonData, 0f, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Misc.getBrightPlayerColor());
    }
    @Override
    public void createButtonContent(TooltipMakerAPI tooltip) {
        CustomPanelAPI container = Global.getSettings().createCustom(this.width,this.height,null);
        createContainerContent(container);
        tooltip.addCustom(container,0f).getPosition().inTL(5,0);
        float centerY = height/2;
        if(isWithArrow){
            panelIndicator = Global.getSettings().createCustom(15,15,null);
//            tooltip.addCustom(panelIndicator,0f).getPosition().inTL((float) StarSystemHoldingTable.widthMap.get("name")*0.75f,centerY-7);

        }
    }
    public void createContainerContent(CustomPanelAPI container) {
        if(buttonData instanceof BifrostSection section){
            float iconSize = 36;
            TooltipMakerAPI tlName = container.createUIElement(BifrostGateUITableList.widthMap.get("name"),40,false);
            ImageViewer viewer = new ImageViewer(iconSize*2,iconSize,section.getImagePath());
            tlName.addCustom(viewer.getComponentPanel(),0f).getPosition().inTL(2,2);
            tlName.addPara(section.getStarSystemAPI().getBaseName(),3f).getPosition().rightOfMid(viewer.getComponentPanel(),5);
            LabelAPI prev = (LabelAPI) tlName.getPrev();
            float y = Math.abs(prev.getPosition().getY() )- prev.computeTextHeight(prev.getText());

            TooltipMakerAPI status,demand,accBonus;
            status = container.createUIElement(BifrostGateUITableList.widthMap.get("status"),40,false);
            section.addStatusToGate(BifrostGateUITableList.widthMap.get("status"),40,y,status);

            demand = container.createUIElement(BifrostGateUITableList.widthMap.get("demand"),40,false);

            CustomPanelAPI combined = AoTDMisc.createCostSection(BifrostGateUITableList.widthMap.get("demand"),40,30,section.getCurrentDemand(),false);
            demand.addCustom(combined,0f).getPosition().inTL(0,5);
            accBonus = container.createUIElement(BifrostGateUITableList.widthMap.get("bonus"),40,false);
            if(section.getRawBonus()==0){
                accBonus.addPara("---",y).setAlignment(Alignment.MID);
            }
            else{
                String per = (Math.round(section.getRawBonus()*100f))+"%";
                accBonus.addPara(per,y).setAlignment(Alignment.MID);
            }


            container.addUIElement(tlName).inTL(0,0);
            container.addUIElement(status).inTL(BifrostGateUITableList.getStartingX("status")-1,0);
            container.addUIElement(demand).inTL(BifrostGateUITableList.getStartingX("demand"),0);
            container.addUIElement(accBonus).inTL(BifrostGateUITableList.getStartingX("bonus"),0);

        }
    }
}
