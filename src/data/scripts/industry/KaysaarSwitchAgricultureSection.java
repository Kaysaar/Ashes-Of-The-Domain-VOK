package data.scripts.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.raid.RaidIntel;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import data.Ids.AoDConditions;
import data.Ids.AoDIndustries;

public class KaysaarSwitchAgricultureSection extends BaseIndustry {
    boolean  neeedToOverwrite = false;
    @Override
    public void apply() {
      getDescriptionOverride();
      getCurrentImage();
      getCurrentName();
    }
    @Override
    public String getCurrentName() {
        if(prevIndustryId!=null){
            return Global.getSettings().getIndustrySpec(prevIndustryId).getName();
        }
    return "";
    }
    String  prevIndustryId;
    public boolean prevFood= false;
    public boolean prevBio = false;
    public boolean prevReci = false;

    void setter(){
        if(market.hasCondition(AoDConditions.SWITCH_RECITIFICATES)){
            prevReci=true;

        }
        else if(market.hasCondition(AoDConditions.SWITCH_BIOTICS)){
            prevBio= true;
        }
        else{
            prevFood = true;
        }
    }
    public boolean isAvailableToBuild() {
        return false;
    }
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public String getCurrentImage() {
        if(prevIndustryId!=null){
            return Global.getSettings().getIndustrySpec(prevIndustryId).getImageName();
        }
        return this.spec.getImageName();
    }

    @Override
    protected String getDescriptionOverride() {
        return "Currently this industry is switching production Focus";
    }
    @Override
    public boolean showShutDown() {
        return false;
    }


    public void cancelUpgrade() {
        super.cancelUpgrade();
        market.addIndustry(prevIndustryId);
        Industry ind  = market.getIndustry(prevIndustryId);
        ind.setSpecialItem(this.getSpecialItem());
        ind.setImproved(isImproved());
        ind.setAICoreId(this.getAICoreId());


        if(prevBio){
            if(!market.hasCondition(AoDConditions.SWITCH_BIOTICS)){
                market.addCondition(AoDConditions.SWITCH_BIOTICS);
                if(market.hasCondition(AoDConditions.SWITCH_RECITIFICATES)){
                    market.removeCondition(AoDConditions.SWITCH_RECITIFICATES);
                }
                if(market.hasCondition(AoDConditions.SWITCH_FOOD)){
                    market.removeCondition(AoDConditions.SWITCH_FOOD);
                }
            }

        }
        else if (prevReci){
            if(!market.hasCondition(AoDConditions.SWITCH_RECITIFICATES)){
                market.addCondition(AoDConditions.SWITCH_RECITIFICATES);
                if(market.hasCondition(AoDConditions.SWITCH_BIOTICS)){
                    market.removeCondition(AoDConditions.SWITCH_BIOTICS);
                }
            }
            if(market.hasCondition(AoDConditions.SWITCH_FOOD)){
                market.removeCondition(AoDConditions.SWITCH_FOOD);
            }
        }
        else{
            if(market.hasCondition(AoDConditions.SWITCH_RECITIFICATES)){
                market.removeCondition(AoDConditions.SWITCH_RECITIFICATES);
            }
            if(market.hasCondition(AoDConditions.SWITCH_BIOTICS)){
                market.removeCondition(AoDConditions.SWITCH_BIOTICS);
            }
            if(!market.hasCondition(AoDConditions.SWITCH_FOOD)) {
                market.addCondition(AoDConditions.SWITCH_FOOD);
            }
        }

        market.removeIndustry(this.id,null,false);
    }
    @Override
    public String getCanNotShutDownReason() {
        //return "Use \"Abandon Colony\" instead.";
        return null;
    }
    @Override
    public void startUpgrading() {
        if(market.getIndustry(Industries.FARMING)!=null&&isFunctional()){
            prevIndustryId = Industries.FARMING;
        }
        else if (market.getIndustry(AoDIndustries.SUBSIDISED_FARMING)!=null&&isFunctional()){
            prevIndustryId = AoDIndustries.SUBSIDISED_FARMING;
        }
        else if (market.getIndustry(AoDIndustries.ARTISANAL_FARMING)!=null&&isFunctional()){
            prevIndustryId = AoDIndustries.ARTISANAL_FARMING;


      }
        setter();
        super.startUpgrading();

    }
    @Override
    public void finishBuildingOrUpgrading() {
        market.addIndustry(prevIndustryId);
       Industry ind =  market.getIndustry(prevIndustryId);
        ind.setSpecialItem(this.getSpecialItem());
        ind.setImproved(isImproved());
        ind.setAICoreId(this.getAICoreId());
        market.removeIndustry(this.id,null,false);
    }
    @Override
    public String getBuildOrUpgradeProgressText() {
//		float f = buildProgress / spec.getBuildTime();
//		return "" + (int) Math.round(f * 100f) + "%";
        if (isUpgrading()) {
            //return "" + (int) Math.round(Misc.getMarketSizeProgress(market) * 100f) + "%";
            if((buildTime-buildProgress)<=1){
                return "Switching Production " + (int)(buildTime-buildProgress)+": day left";
            }
            return "Switching Production " +  (int)(buildTime-buildProgress)+": days left";
        }


        return super.getBuildOrUpgradeProgressText();
    }


    @Override
    public boolean canShutDown() {
        return false;
    }

}
