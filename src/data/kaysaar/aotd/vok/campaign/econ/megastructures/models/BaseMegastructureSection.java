package data.kaysaar.aotd.vok.campaign.econ.megastructures.models;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.intel.MegastructureSectionCompletedIntel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContractManager;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSectionSpec;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.MegastructureSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureDialogContent;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.restoration.MegastructureSectionRestorationDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts.BaseRestorationContract;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionSpec;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry.getDeficitText;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.getMonthsFromNextMonth;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.getMonthsRemaining;

public class BaseMegastructureSection {
    public boolean isRestored;
    public String specID;
    public boolean isRestoring;
    public float progressOfRestoration = 0f;
    public BaseMegastructureScript megastructureTiedTo;
    public MutableStat upkeepMult = new MutableStat(1f);
    public LinkedHashMap<String,Integer>resourcesSpentOnRestoration = new LinkedHashMap<>();
    public LinkedHashMap<String,Integer>stockpiledResourcesForEffects  = new LinkedHashMap<>();
    public boolean isBuildable(){
        return false;
    }
    public float getDaysSpentRestoring() {
        return daysSpentRestoring;
    }
    public boolean isOwnedByPLayerFaction(){
        if(megastructureTiedTo.getEntityTiedTo()==null)return true;
        return megastructureTiedTo.getEntityTiedTo().getFaction().isPlayerFaction();
    }
    protected void applyDeficitToProduction(int index, BaseIndustry ind, Pair<String, Integer> deficit, String ... commodities) {
        for (String commodity : commodities) {
//			if (this instanceof Mining && market.getName().equals("Louise")) {
//				System.out.println("efwefwe");
//			}
            if (ind.getSupply(commodity).getQuantity().isUnmodified()) continue;
            ind.supply(index, commodity, -deficit.two, getDeficitText(deficit.one));
        }
    }
    public LinkedHashMap<String, Integer> getDemandMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        return map;
    }
    public LinkedHashMap<String, Integer> getProductionMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        return map;
    }
    public LinkedHashMap<String, Integer> getStockpiledResourcesForEffects() {
        if(stockpiledResourcesForEffects==null )stockpiledResourcesForEffects = new LinkedHashMap<>();
        return stockpiledResourcesForEffects;
    }
    public LinkedHashMap<String, Integer> getResourcesSpentOnRestoration() {
        if(resourcesSpentOnRestoration==null )resourcesSpentOnRestoration = new LinkedHashMap<>();
        return resourcesSpentOnRestoration;
    }
    public void addResourcesToBeSpentOnRestoration(String resource, int value){
        int curr = getResourcesSpentOnRestoration().getOrDefault(resource, 0);
        getResourcesSpentOnRestoration().put(resource, curr+value);
    }
    public float getAllowedProgressOnRestoration() {
        LinkedHashMap<String, Integer> required = getSpec().getResourceRestorationCost();

        if (required == null || required.isEmpty()) {
            return 1f;
        }

        float allowedProgress = 1f;

        for (String commodityId : required.keySet()) {
            int requiredAmount = required.get(commodityId);
            if (requiredAmount <= 0) {
                continue;
            }

            int spentAmount = getResourcesSpentOnRestoration().getOrDefault(commodityId, 0);
            float ratio = (float) spentAmount / (float) requiredAmount;

            allowedProgress = Math.min(allowedProgress, ratio);
        }

        return Math.max(0f, Math.min(1f, allowedProgress));
    }

    boolean isAboutToBeRemoved = false;
    public float daysSpentRestoring = 0f;

    public float getProgressOfRestoration() {
        return Math.min(1,daysSpentRestoring/getSpec().getDaysNeededForRestoration());
    }
    public boolean canRestoreSection(){
        return true;
    }

    public BaseMegastructureScript getMegastructureTiedTo() {
        return megastructureTiedTo;
    }

    public void createEffectSection(TooltipMakerAPI tooltipMakerAPI, boolean isForMainView) {
        if(isForMainView){
            tooltipMakerAPI.addPara(this.getName(), Color.ORANGE,2f);
            addToEffectSectionMain(tooltipMakerAPI);
            tooltipMakerAPI.addSpacer(5f);
        }
        else{
            createEffectExplanationSectionInSubSection(tooltipMakerAPI);
        }
    }
    public void addToEffectSectionMain(TooltipMakerAPI tl){

    }
    public void applySectionOnIndustry(BaseIndustry ind){

    }
    public void unApplySectionOnIndustry(BaseIndustry ind){

    }
    public void createEffectExplanationSectionInSubSection(TooltipMakerAPI tl){

    }
    public float getDaysLeft (){
        return  Math.max(0,(getSpec().getDaysNeededForRestoration()-daysSpentRestoring));
    }
    public String getRestorationContractID(){
        return getMegastructureTiedTo().getUniqueGenId()+"_"+this.getSpec().getId();
    }
    public void startRestoration(){
        isRestoring = true;
        if(AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID())!=null){
            BaseRestorationContract contract = (BaseRestorationContract) AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID());
            contract.setFrozen(false);
        }
        else{
            BaseRestorationContract contract = new BaseRestorationContract(this);
            AoTDTradeContractManager.getInstance().addContract(contract);
        }

    }
    public void stopRestoration(){
        isRestoring = false;
        if(AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID())!=null){
            BaseRestorationContract contract = (BaseRestorationContract) AoTDTradeContractManager.getInstance().getActiveContracts().get(getRestorationContractID());
            contract.setFrozen(true);
        }
    }
    public HashMap<String, Integer> getMonthlyResNeeded() {
        HashMap<String, Integer> commodities = new HashMap<>();

        int months = Math.max(1, getMonthsFromNextMonth(getDaysLeft())); // prevent division by 0

        LinkedHashMap<String, Integer> required = getSpec().getResourceRestorationCost();

        required.forEach((key, totalRequired) -> {
            int alreadySpent = getResourcesSpentOnRestoration().getOrDefault(key, 0);

            int remaining = Math.max(0, totalRequired - alreadySpent);

            if (remaining <= 0) return;

            // ceil division so we don't "lose" resources over time
            int perMonth = (int) Math.ceil((float) remaining / (float) months);

            AoTDMisc.putCommoditiesIntoMap(commodities, key, perMonth);
        });

        return commodities;
    }
    public boolean isRestored() {
        return isRestored;
    }
    public void setProgressOfRestoration(float progressOfRestoration) {
        this.progressOfRestoration = progressOfRestoration;
    }
    public boolean doesHaveCustomSection() {
        return false;
    }


    public void init(BaseMegastructureScript megastructureTiedTo, boolean isRestored,String id) {
        progressOfRestoration = 0f;
        this.specID = id;
        this.megastructureTiedTo = megastructureTiedTo;
        this.isRestored = isRestored;
        if (isRestored) {
            progressOfRestoration = 1f;
        }
    }


    public void createCustomSection(TooltipMakerAPI tooltipMakerAPI){

    }

    public String getImagePath(){
        return Global.getSettings().getSpriteName("sectionImage",getSpec().getIconId());
    }
    public String getName(){
        return getSpec().getName();
    }
    public void advance(float amount) {
        if (isRestoring && !isRestored) {
            float days = Global.getSector().getClock().convertToDays(amount);

            float totalDaysNeeded = getSpec().getDaysNeededForRestoration();
            if (totalDaysNeeded > 0f) {
                float allowedProgress = getAllowedProgressOnRestoration();
                float allowedDays = allowedProgress * totalDaysNeeded;

                if (daysSpentRestoring < allowedDays) {
                    daysSpentRestoring = Math.min(daysSpentRestoring + days, allowedDays);
                }

                if (daysSpentRestoring >= totalDaysNeeded || getProgressOfRestoration() >= 1f) {
                    daysSpentRestoring = totalDaysNeeded;
                    isRestoring = false;
                    setRestored(true);

                }
            }
        }

        advanceImpl(amount);
    }


    public void applyOnRestoration() {

    }

    public void advanceImpl(float amount) {

    }
    public float getUpkeepOfSection(){
        return 0f;
    }
    public MegastructureSectionSpec getSpec(){
        return MegastructureSpecManager.getSpecForSection(specID);
    }
    public void apply() {
        if (!isAboutToBeRemoved) {
            if (isRestored) {
                applyEffectOfSection();
            }
        }
//        AoTDListenerUtilis.applyUpkeepReductionCredits(this,upkeepMult);

    }
    public void printCustomEffectsSection(TooltipMakerAPI tooltip) {

    }
    public void reportButtonPressedForSection(ButtonAPI button, BaseMegastructureDialogContent dialogContent) {
        String customData = (String) button.getCustomData();
        if(customData != null){
            if(customData.equals("start")){
                BasePopUpDialog dialog = new MegastructureSectionRestorationDialog(this,dialogContent,false,"Restore Section");
                AshMisc.initPopUpDialog(dialog,800,280);
            }
            if(customData.equals("pause")){
                BasePopUpDialog dialog = new MegastructureSectionRestorationDialog(this,dialogContent,true,"Pause Restoration");
                AshMisc.initPopUpDialog(dialog,800,220);
            }


        }
        reportButtonPressedImpl(button,dialogContent);
    }
    public void reportButtonPressedImpl(ButtonAPI buttonAPI, BaseMegastructureDialogContent dialogContent) {

    }
    public ArrayList<ButtonAPI> createButtonsForSection(float width, float heightOfButtons, TooltipMakerAPI tooltip) {
        ArrayList<ButtonAPI> buttons = new ArrayList<>();
        if (!isRestored) {
            ButtonAPI bt;
            if (isRestoring) {
                bt = tooltip.addButton("Pause Restoration", "pause", Misc.getBasePlayerColor(), Misc.getNegativeHighlightColor().darker(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
            } else {
                bt = tooltip.addButton("Start Restoration", "start", Misc.getBasePlayerColor(), Misc.getStoryDarkColor(), Alignment.MID, CutStyle.TL_BR, width, heightOfButtons, 5f);
                bt.setEnabled(canRestoreSection()&& AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.MEGA_ANALYSIS));
                tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
                    @Override
                    public boolean isTooltipExpandable(Object tooltipParam) {
                        return false;
                    }

                    @Override
                    public float getTooltipWidth(Object tooltipParam) {
                        return 500;
                    }

                    @Override
                    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                        ResearchOptionSpec spec = AoTDMainResearchManager.getInstance()
                                .getSpecForSpecificResearch(AoTDTechIds.MEGA_ANALYSIS);

                        tooltip.addPara(
                                "Restoring this section of the megastructure is a monumental engineering undertaking. " +
                                        "Ancient systems must be brought back online, damaged components replaced, and vast energy flows stabilized. " +
                                        "Once completed, this section will resume its intended function within the array.",
                                0f
                        );

                        if (!AoTDMainResearchManager.getInstance().isResearchedForPlayer(AoTDTechIds.MEGA_ANALYSIS)) {
                            tooltip.addPara(
                                    "Requires %s to be researched before restoration can begin.",
                                    5f,
                                    Misc.getNegativeHighlightColor(),
                                    spec.getName()
                            );
                        }
                    }
                }, TooltipMakerAPI.TooltipLocation.LEFT, false);
            }
            buttons.add(bt);
        }

        addAdditionalButtonsForSection(buttons, width, heightOfButtons, tooltip);
        return buttons;
    }

    public void addAdditionalButtonsForSection(ArrayList<ButtonAPI> bt, float width, float heightOfButtons, TooltipMakerAPI tooltip) {

    }
    public void notifyRestorationFinished(){
        Global.getSector().getPlayerFleet().getCommanderStats().addStoryPoints(1);
        Global.getSector().getIntelManager().addIntel(new MegastructureSectionCompletedIntel(this));
    }
    public void unapply() {
        upkeepMult.unmodify();
        unapplyEffectOfSection();
    }
    public void aboutToGetRemoved() {
        this.isAboutToBeRemoved = true;
        unapply();
    }
    public void setRestored(boolean restored) {
        isRestored = restored;
        applyOnRestoration();
        notifyRestorationFinished();
    }
    public void applyEffectOfSection() {

    }
    public void unapplyEffectOfSection() {

    }
}
