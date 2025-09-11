package data.kaysaar.aotd.vok.campaign.econ.industry;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDItems;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.ui.basecomps.ImageViewer;

import java.awt.*;
import java.util.AbstractMap;

public class ExomatterProcessing extends BaseIndustry {
    public static String subMarketId = "aotd_exomatter_processing";
    public float getDaysForConversion(){
        if(AshMisc.isStringValid(getAICoreId())){
            if(getAICoreId().equals(Commodities.BETA_CORE)||getAICoreId().equals(Commodities.ALPHA_CORE)){
                return 15;
            }
        }
        this.getSpec().setUpkeep(10);
        return 35;
    }
    boolean isCurrentlyConverting = false;
    float daysConverting = 0;
    public int getConversionAmount(){
        if(Commodities.ALPHA_CORE.equals(getAICoreId())){
            return 2;
        }
        return 1;
    }
    public void setCurrentlyConverting(boolean currentlyConverting) {
        isCurrentlyConverting = currentlyConverting;
    }


    @Override
    public void apply() {
        super.apply(true);
        if (!market.hasSubmarket(subMarketId)) {
            market.addSubmarket(subMarketId);
        }

        demand(Commodities.MARINES,6);

    }
    public int getShroudedSubstrateReq(){
        if(this.isImproved()){
            return 2;
        }
        return 4;
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        String str =Global.getSettings().getSpecialItemSpec(AoTDItems.SHROUDED_SUBSTRATE).getName();
        tooltip.addSectionHeading(str+" Conversion", Alignment.MID,5f);
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(tooltip.getWidthSoFar(),28,null);
        TooltipMakerAPI helper = panelAPI.createUIElement(panelAPI.getPosition().getWidth(),panelAPI.getPosition().getHeight(),false);
        float spacer =15;
        for (int i = 0; i < getShroudedSubstrateReq(); i++) {
            ImageViewer viewer = new ImageViewer(30,30,Global.getSettings().getSpecialItemSpec(AoTDItems.SHROUDED_SUBSTRATE).getIconName());
            helper.addCustom(viewer.getComponentPanel(),0f).getPosition().inTL(i*spacer,0);
        }
        float x = helper.getPrev().getPosition().getX()+35;
        helper.setParaInsigniaLarge();
        LabelAPI labelAPI = helper.addPara(">>>", Misc.getPositiveHighlightColor(),0f);
        labelAPI.getPosition().inTL(x+5,2);

        x=labelAPI.getPosition().getX()+labelAPI.computeTextWidth(labelAPI.getText())+8;
        for (int i = 0; i < getConversionAmount(); i++) {
            ImageViewer viewer = new ImageViewer(30,30,Global.getSettings().getSpecialItemSpec(AoTDItems.TENEBRIUM_CELL).getIconName());
            helper.addCustom(viewer.getComponentPanel(),0f).getPosition().inTL(x+(i*spacer),0);
        }
        float width = helper.getPrev().getPosition().getX()+30;
        float centerX = panelAPI.getPosition().getWidth()/2;

        panelAPI.addUIElement(helper).inTL(centerX-(width/2),0);
        tooltip.addCustom(panelAPI,10f);
        tooltip.addPara("With current rate we are able to convert %s into %s every %s",5f, Color.ORANGE,getShroudedSubstrateReq()+" "+str,getConversionAmount()+" "+Global.getSettings().getSpecialItemSpec(AoTDItems.TENEBRIUM_CELL).getName(),AoTDMisc.convertDaysToString((int) getDaysForConversion()));
        if(isCurrentlyConverting){
            tooltip.addPara("We will convert this %s into %s within %s",10f,Color.ORANGE,str,Global.getSettings().getSpecialItemSpec(AoTDItems.TENEBRIUM_CELL).getName(), AoTDMisc.convertDaysToString((int) (getDaysForConversion()-daysConverting)));
        }
    }

    @Override
    protected void addBetaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Beta-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Beta-level AI core. ";
        }
        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                            "Lowers conversion time to %s days", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION
                    ,"15");
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                        "Lowers conversion time to %s days", 0f, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION
                ,"15");
    }
    protected void addStabilityPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        float opad = 10f;
        Color h = Misc.getNegativeHighlightColor();
        tooltip.addPara("Stability penalty: %s", opad, h, "" + -6);
    }
    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP || mode == AICoreDescriptionMode.MANAGE_CORE_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                            "Lowers conversion time to %s days. Increase yield of conversion by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION
                    ,"15","1");
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                        "Lowers conversion time to %s days. Increase yield of conversion by %s.", 0f, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION
                ,"15","1");
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {

    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        market.removeSubmarket(subMarketId);
    }

    @Override
    public boolean isAvailableToBuild() {
        boolean player =  market.isPlayerOwned()|| Factions.PLAYER.equals(market.getFactionId());

        return player && BlackSiteProjectManager.getInstance().getProject("aotd_tenebrium_proj").checkIfProjectWasCompleted();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(isFunctional()){
            if(isCurrentlyConverting){
                daysConverting+=Global.getSector().getClock().convertToDays(amount);
                if(daysConverting>=getDaysForConversion()){
                    daysConverting=0;
                    isCurrentlyConverting = false;
                    MarketAPI market = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();
                    if(market==null)market = this.market;
                    if(market.hasSubmarket(Submarkets.SUBMARKET_STORAGE)){
                        market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(new SpecialItemData(AoTDItems.TENEBRIUM_CELL,null),getConversionAmount());
                    }
                }
            }
            else{
                if(!market.hasSubmarket(subMarketId)){
                    market.addSubmarket(subMarketId);
                }
                if(AoTDMisc.retrieveAmountOfItems(AoTDItems.SHROUDED_SUBSTRATE,Submarkets.SUBMARKET_STORAGE)>=getShroudedSubstrateReq()){
                    AoTDMisc.eatItems(new AbstractMap.SimpleEntry<>(AoTDItems.SHROUDED_SUBSTRATE,getShroudedSubstrateReq()),Submarkets.SUBMARKET_STORAGE,AoTDMisc.getPlayerFactionMarkets());
                    isCurrentlyConverting = true;
                }
            }
        }
    }

    @Override
    public boolean canImprove() {
        return true;
    }

    @Override
    public void addImproveDesc(TooltipMakerAPI info, ImprovementDescriptionMode mode) {
        if(!mode.equals(ImprovementDescriptionMode.INDUSTRY_TOOLTIP)){
            info.addPara("Each improvement made at a colony doubles the number of " +
                            "" + Misc.STORY + " points required to make an additional improvement.", 0f,
                    Misc.getStoryOptionColor(), Misc.STORY + " points");
            info.addPara("Decrease amount of %s required by %s",3f, Color.ORANGE,Global.getSettings().getSpecialItemSpec(AoTDItems.SHROUDED_SUBSTRATE).getName(),"2");
            info.addSpacer(-5f);
        }
    }

    @Override
    public void finishBuildingOrUpgrading() {
        super.finishBuildingOrUpgrading();

    }
}
