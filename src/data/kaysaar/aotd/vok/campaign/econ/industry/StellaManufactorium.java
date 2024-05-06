package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.SMSpecialItem;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;


import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StellaManufactorium extends BaseIndustry  {

    boolean canProduce = false;
    public HashMap<SMSpecialItem, Float> demandForProduction = new HashMap<>();
    public boolean applyDemand(){
        boolean metCriteria = true;
        if(!demandForProduction.isEmpty()){
            for (Map.Entry<SMSpecialItem, Float> entry : demandForProduction.entrySet()) {
                for (Map.Entry<String, Integer> costEntry : entry.getKey().cost.entrySet()) {
                    int value = costEntry.getValue();
                    if(this.aiCoreId!=null){
                        value-=1;
                    }
                    if(value!=0){
                        this.getDemand(costEntry.getKey()).getQuantity().modifyFlat("stella"+entry.getKey().id+costEntry.getKey(),value,"Stella Manufactorium");
                    }

                    Pair<String, Integer> deficit = getMaxDeficit(costEntry.getKey());
                    if(deficit.two!=0){
                        metCriteria = false;
                    }

                }
            }
        }
        else{
            metCriteria = false;
        }
        return  metCriteria;
    }
    public void unapplyDemand(){
        if(!demandForProduction.isEmpty()){
            for (Map.Entry<SMSpecialItem, Float> entry : demandForProduction.entrySet()) {
                for (Map.Entry<String, Integer> costEntry : entry.getKey().cost.entrySet()) {
                    this.getDemand(costEntry.getKey()).getQuantity().unmodifyFlat("stella"+entry.getKey().id+costEntry.getKey());
                }
            }
        }
        demandForProduction.clear();
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        float days = Global.getSector().getClock().convertToDays(amount);

        if(this.aiCoreId!=null&&this.aiCoreId.equals(Commodities.ALPHA_CORE)){
            days+= Global.getSector().getClock().convertToDays(amount);
        }
        if(canProduce){
            for (Map.Entry<SMSpecialItem, Float> smSpecialItemIntegerEntry : demandForProduction.entrySet()) {
                smSpecialItemIntegerEntry.setValue(smSpecialItemIntegerEntry.getValue()-days);
                if(smSpecialItemIntegerEntry.getValue()<=0){
                    smSpecialItemIntegerEntry.setValue(smSpecialItemIntegerEntry.getKey().costInDays);
                    SpecialItemData data = new SpecialItemData(smSpecialItemIntegerEntry.getKey().id,null);
                    this.market.getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo().addSpecial(data,1);
                }
            }
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STELLA_MANUFACTORIUM,market) &&super.isAvailableToBuild();
    }

    @Override
    public boolean showWhenUnavailable() {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(AoTDTechIds.STELLA_MANUFACTORIUM,market);
    }

    @Override
    public void apply() {
        super.apply(true);
       canProduce = applyDemand();
    }

    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        super.addPostDemandSection(tooltip, hasDemand, mode);
        if(mode.equals(IndustryTooltipMode.NORMAL)){
            if(canProduce){
                tooltip.addSectionHeading("Manufactorium Forges", Alignment.MID,10f);
                for (Map.Entry<SMSpecialItem, Float> smSpecialItemFloatEntry : demandForProduction.entrySet()) {
                    String days= " days ";
                    if(smSpecialItemFloatEntry.getValue()<=1){
                        days=" day ";
                    }

                    String insert = Math.round(smSpecialItemFloatEntry.getValue()) + days+"to create";
                    if(this.aiCoreId!=null&&this.aiCoreId.equals(Commodities.ALPHA_CORE)){
                         insert = Math.round(smSpecialItemFloatEntry.getValue()/2) + days+"to create";
                    }
                    float dim = 40;
                    tooltip.addImage(Global.getSettings().getSpecialItemSpec(smSpecialItemFloatEntry.getKey().id).getIconName(),dim,dim,10f);
                    tooltip.addPara(Global.getSettings().getSpecialItemSpec(smSpecialItemFloatEntry.getKey().id).getName()+" "+insert,Misc.getStoryBrightColor(),10f);

                }
            }
            else if (demandForProduction.isEmpty()){
                tooltip.addPara("Currently the Forges are dormant, waiting for new orders to create new equipment.", Misc.getNegativeHighlightColor(),10f);
            }
            else{
                tooltip.addPara("Forges have not been supplied with enough resources to continue production.", Misc.getNegativeHighlightColor(),10f);
            }
        }
        else{
            tooltip.addPara("This Industry specializes in manufacturing of colony items by using advanced resources.",Color.ORANGE,10f);
        }


    }

// TODO : IMPLEMENT AI CORE EFFECTS




    @Override
    protected void applyNoAICoreModifiers() {
       this.getUpkeep().unmodifyFlat("beta_core");
       this.getUpkeep().unmodifyFlat("gamma_core");
       this.getUpkeep().unmodifyFlat("alpha_core");

    }

    @Override
    protected void addGammaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Reduces cost of forging equipment by 1 unit.",10f);
    }
    @Override
    protected void addBetaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Reduces cost of forging equipment by 1 unit and lowers upkeep costs by 25%.",10f);
    }
    @Override
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        tooltip.addPara("Reduces cost of forging equipment by 1 unit, lowers upkeep costs by 25% and reduces time to forge equipment by 50%.",10f);
    }

    @Override
    public boolean canInstallAICores() {
        return true;
    }



}
