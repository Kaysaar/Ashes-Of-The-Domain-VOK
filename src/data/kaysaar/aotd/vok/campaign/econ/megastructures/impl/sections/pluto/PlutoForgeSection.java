package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider.LaserStengthDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider.ResourceAllocationDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class PlutoForgeSection extends BaseMegastructureSection {
    public int assginedOre,assignedTransplutonicOre;
    LinkedHashMap<String,Integer>res = new LinkedHashMap<>();
    public static LinkedHashMap<String,Integer>resUpkeepMap = new LinkedHashMap<>();
    static {
        resUpkeepMap.put(Commodities.ORE,500);
        resUpkeepMap.put(Commodities.RARE_ORE,1000);
    }

    public LinkedHashMap<String, Integer> getRes() {
        if(res==null )res = new LinkedHashMap<>();
        return res;
    }
    @Override
    public boolean canRestoreSection() {
        return getMegastructureTiedTo().getSectionById("pluto_ocn").isRestored();
    }
    public void updateResourceDesignated(String commodity,Integer newAmount){
        getRes().put(commodity,newAmount);
    }
    @Override
    public LinkedHashMap<String, Integer> getDemandMap() {
        return getRes();
    }

    @Override
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String,Integer>res = new LinkedHashMap<>();
        res.put(Commodities.METALS,4);
        res.put(Commodities.RARE_METALS,2);
        return res;
    }
    public LinkedHashMap<String,String>getProdMapForOre(){
        LinkedHashMap<String,String>res = new LinkedHashMap<>();
        res.put(Commodities.METALS,Commodities.ORE);
        res.put(Commodities.RARE_METALS,Commodities.RARE_ORE);
        return res;
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {
        LinkedHashMap<String,String>relationMap = getProdMapForOre();
        for (Map.Entry<String, String> entry : relationMap.entrySet()) {
            String id = entry.getKey();
            String resourceDemandId = entry.getValue();
            ind.supply(id,getRes().getOrDefault(resourceDemandId,0)*getProductionMap().getOrDefault(id,0));
        }
        ind.getUpkeep().modifyFlat(getSpec().getId(),getUpkeepOfSection(),getName());
    }

    @Override
    public void addAdditionalButtonsForSection(ArrayList<ButtonAPI> bt, float width, float heightOfButtons, TooltipMakerAPI tooltip) {
        ButtonAPI button = tooltip.addButton("Assign ore", "assignO", Misc.getBasePlayerColor(), new Color(58, 57, 57), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored);
        button = tooltip.addButton("Assign transplutonic ore", "assignTO", Misc.getBasePlayerColor(), new Color(152, 152, 152), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored);
    }

    @Override
    public void reportButtonPressedImpl(ButtonAPI buttonAPI, BaseMegastructureDialogContent dialogContent) {
        String customData = (String) buttonAPI.getCustomData();
        if (customData != null && (customData.equals("assignO"))) {
            OpticCommandNexus nexus = getMegastructureTiedTo().getSectionById("pluto_ocn",OpticCommandNexus.class);
            int total = getDemandMap().getOrDefault(Commodities.ORE,0)+nexus.getMaxAllowedToBeTaken(Commodities.ORE);
            BasePopUpDialog dialog = new ResourceAllocationDialog(dialogContent.getSection(), "Resource Allocation", 1, total, getDemandMap().getOrDefault(Commodities.ORE,0), 0, this,Commodities.ORE);
            AshMisc.initPopUpDialog(dialog, 600, 270);
        }
        if (customData != null && (customData.equals("assignTO"))) {
            OpticCommandNexus nexus = getMegastructureTiedTo().getSectionById("pluto_ocn",OpticCommandNexus.class);
            int total = getDemandMap().getOrDefault(Commodities.RARE_ORE,0)+nexus.getMaxAllowedToBeTaken(Commodities.RARE_ORE);
            BasePopUpDialog dialog = new ResourceAllocationDialog(dialogContent.getSection(), "Resource Allocation", 1, total, getDemandMap().getOrDefault(Commodities.RARE_ORE,0), 0, this,Commodities.RARE_ORE);
            AshMisc.initPopUpDialog(dialog, 600, 270);
        }
    }

    @Override
    public float getUpkeepOfSection() {
        float curr = 0;
        for (Map.Entry<String, Integer> entry : getRes().entrySet()) {
            curr+=resUpkeepMap.getOrDefault(entry.getKey(),500)*entry.getValue();
        }
        return curr;
    }

    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        LinkedHashSet<String>commodityNames = new LinkedHashSet<>();
        getProdMapForOre().keySet().forEach(x->commodityNames.add(Global.getSettings().getCommoditySpec(x).getName()));
        tl.addSectionHeading("Massive Refinery Plant",Alignment.MID,5f);
        if(!isRestored){

            tl.addPara("Once restored, allows usage of ores, excavated by Pluto to be smelted into %s and %s.",5f, Color.ORANGE,commodityNames.toArray(new String[0]));

        }
        else{
            tl.addPara("Due to integral structure of %s, we are only able to assign ores, that are directly mined by this station!",5f,Color.ORANGE,"Pluto Mining Station");
            tl.addSectionHeading("Production",Alignment.MID,5f);
            LinkedHashMap<String,String>relationMap = getProdMapForOre();
            LinkedHashMap<String,Integer>mapOfProd = new LinkedHashMap<>();
            LinkedHashMap<String,Integer>mapOfDem = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : relationMap.entrySet()) {
                String id = entry.getKey();
                String resourceDemandId = entry.getValue();
                int val=getRes().getOrDefault(resourceDemandId,0)*getProductionMap().getOrDefault(id,0);
                mapOfProd.put(id,AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(val,false,id));
            }
            tl.addCustom(new AoTDCommodityShortPanelCombined(tl.getWidthSoFar(),3, mapOfProd).getMainPanel(),5f);

            tl.addSectionHeading("Demand",Alignment.MID,5f);
            for (Map.Entry<String, Integer> entry : getDemandMap().entrySet()) {
                mapOfDem.put(entry.getKey(),AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(entry.getValue(),true,entry.getKey()));
            }
            tl.addCustom(new AoTDCommodityShortPanelCombined(tl.getWidthSoFar(),3, mapOfDem).getMainPanel(),5f);
            tl.addPara("Current section upkeep : %s",5f,Color.ORANGE,Misc.getDGSCredits(getUpkeepOfSection()));
        }
    }

}
