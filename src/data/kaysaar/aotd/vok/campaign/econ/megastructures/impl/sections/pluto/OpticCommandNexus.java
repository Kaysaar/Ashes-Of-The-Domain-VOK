package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.pluto;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.plugins.AoTDCommodityEconSpecManager;
import data.kaysaar.aotd.tot.ui.commoditypanel.AoTDCommodityShortPanelCombined;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.slider.LaserStengthDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.CoronalHypershuntMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OpticCommandNexus extends BaseMegastructureSection {
    public int currentMagnitude = 0;
    public int expectedMagnitude = 0;

    public int getExpectedMagnitude() {
        return currentMagnitude;
    }

    @Override
    public float getUpkeepOfSection() {
        return currentMagnitude*upkeepMulter;
    }

    public void setExpectedMagnitude(int expectedMagnitude) {
        this.expectedMagnitude = expectedMagnitude;
    }

    public static int maxMagnitude = 10;
    public static int upkeepMulter = 5000;
    public boolean connectedToHypershunt = false;
    public boolean wasConnectedToHypershunt = false;
    public static int minMagnitude = 0; //0  means turned off;

    public void setCurrentMagnitude(int currentMagnitude) {
        if(currentMagnitude<this.currentMagnitude){
            for (BaseMegastructureSection megaStructureSection : getMegastructureTiedTo().getMegaStructureSections()) {
                if(megaStructureSection instanceof PlutoForgeSection section){
                    section.getRes().clear();
                }
            }
            getMegastructureTiedTo().getIndustryTiedToMegastructureIfPresent().apply();
        }

        this.currentMagnitude = currentMagnitude;
    }

    int supplyUnitsPerMagnitude = 5;

    public int getMaxMagnitude() {
        if (CoronalHypershuntMegastructure.isWithinReceiverSystem(this.getMegastructureTiedTo().getEntityTiedTo())) {
            return maxMagnitude;
        }
        return maxMagnitude - 3;
    }

    public boolean isFiringLaser() {
        return currentMagnitude > 0;
    }

    public int getCurrentMagnitude() {
        return currentMagnitude;
    }


    @Override
    public void init(BaseMegastructureScript megastructureTiedTo, boolean isRestored, String id) {
        super.init(megastructureTiedTo, isRestored, id);

    }

    @Override
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put(Commodities.ORE, 10);
        map.put(Commodities.RARE_ORE, 12);
        return map;
    }


    @Override
    public void reportButtonPressedImpl(ButtonAPI buttonAPI, BaseMegastructureDialogContent dialogContent) {
        String customData = (String) buttonAPI.getCustomData();
        if (customData != null && (customData.equals("adjust_laser"))) {
            BasePopUpDialog dialog = new LaserStengthDialog(dialogContent.getSection(), "Laser Calibration", 10, getMaxMagnitude(), getCurrentMagnitude(), 0, this);
            AshMisc.initPopUpDialog(dialog, 600, 330);
        }
    }

    public int getTakenSupplyBySections(String commodityId) {
        int taken = 0;
        for (BaseMegastructureSection restoredSection : getMegastructureTiedTo().getRestoredSections()) {
            if (restoredSection instanceof PlutoForgeSection section) {
                taken += section.getDemandMap().getOrDefault(commodityId, 0);
            }
        }
        return taken;
    }
    public int getMaxAllowedToBeTaken(String commodityId){
        int total = getProductionMap().getOrDefault(commodityId,0);
        total*=getCurrentMagnitude();
        total-=getTakenSupplyBySections(commodityId);
        return total;
    }

    @Override
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl) {
        tl.addSectionHeading("Mining Laser",Alignment.MID,5f);
        if(!isRestored){
            tl.addPara("Once restored, allows usage of Pluto's powerful mining laser, to mine %s and %s.",5f, Color.ORANGE,"Ore","Transplutonic Ore");
            tl.addPara("Allows restoration of all other sections",Misc.getTooltipTitleAndLightHighlightColor(),5f);
        }
        else{
            if(isFiringLaser()){
                tl.addPara("Mining array active. Extraction operations in progress.", 5f);
                tl.addPara("Laser firing at %s capacity",5f,Color.ORANGE,(getCurrentMagnitude()*10)+"%");
                LinkedHashMap<String,Integer>map = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
                    int total = entry.getValue() * getExpectedMagnitude();
                    total -= getTakenSupplyBySections(entry.getKey());
                    if (total >=0) {;
                        map.put(entry.getKey(), AoTDCommodityEconSpecManager.getCargoAmountFromSupplyOrDemand(total,true,entry.getKey()));
                    }

                }
                tl.addSectionHeading("Production from mining operations",Alignment.MID,5f);
                tl.addCustom(new AoTDCommodityShortPanelCombined(tl.getWidthSoFar(),3,map).getMainPanel(),5f);
                tl.addPara("Current section upkeep : %s",5f,Color.ORANGE,Misc.getDGSCredits(getUpkeepOfSection()));

            }
            else{
                tl.addPara("Currently laser is dormant!",Misc.getTooltipTitleAndLightHighlightColor(),5f);
            }
        }
    }

    @Override
    public void applySectionOnIndustry(BaseIndustry ind) {

        for (Map.Entry<String, Integer> entry : getProductionMap().entrySet()) {
            int total = entry.getValue() * getExpectedMagnitude();
            total -= getTakenSupplyBySections(entry.getKey());
            if (total >=0) {
                ind.supply(entry.getKey(), total);
            }

        }
        ind.getUpkeep().modifyFlat(getSpec().getId(),getUpkeepOfSection(),getName());
    }

    @Override
    public void addAdditionalButtonsForSection(ArrayList<ButtonAPI> bt, float width, float heightOfButtons, TooltipMakerAPI tooltip) {
        ButtonAPI button = tooltip.addButton("Adjust Laser Strength", "adjust_laser", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
        bt.add(button);
        button.setEnabled(isRestored);
    }
}
