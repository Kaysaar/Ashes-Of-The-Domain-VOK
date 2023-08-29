package data.scripts.research;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.Industry.IndustryTooltipMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.*;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.ui.*;
import data.Ids.AoDIndustries;
import data.plugins.AoDUtilis;
import data.ui.HeadOfResearchCenterUI;
import data.ui.ResearchUIDP;
import data.ui.StellaManufactoriumUI;
import data.ui.UpgradeListUI;

import static data.plugins.AoDCoreModPlugin.aodTech;

public class UpgradeOptionsListener extends BaseIndustryOptionProvider{

    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    public static Object CUSTOM_PLUGIN = new Object();
    public static Object IMMEDIATE_ACTION = new Object();
    public static Object CUSTOM_PLUGIN_RESEARCHER = new Object();
    public static Object STELLA = new Object();
    int handleFarming(MarketAPI market){

        int quantity = market.getSize();
        if(market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)){
            quantity+=2;
        }
        if(market.hasCondition(Conditions.FARMLAND_RICH)){
            quantity+=1;
        }
        if(market.hasCondition(Conditions.FARMLAND_POOR)){
            quantity-=1;
        }
        if(market.hasCondition(Conditions.SOLAR_ARRAY)){
            quantity+=2;
        }
        return quantity;
    }
    @Override
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        if(ind.getId().equals(AoDIndustries.RESEARCH_CENTER)){
            List<IndustryOptionData> result = new ArrayList<IndustryOptionData>();
            IndustryOptionData opt = new IndustryOptionData("Research Interface", IMMEDIATE_ACTION, ind, this);
            opt.color = new Color(8, 219, 239, 255);
            result.add(opt);
            if(AoDUtilis.getResearchAPI().getResearchersInPossetion().size()>1){
                opt = new IndustryOptionData("Change Main Researcher", CUSTOM_PLUGIN_RESEARCHER, ind, this);
                opt.color = new Color(241, 189, 23, 255);
                result.add(opt);
            }
            return result;
        }
        if(ind.getId().equals("stella_manufactorium")){
            List<IndustryOptionData> result = new ArrayList<IndustryOptionData>();
            IndustryOptionData opt;
            opt = new IndustryOptionData("Access Stellar Forge", STELLA, ind, this);
            opt.color = new Color(246, 94, 0, 255);
            result.add(opt);
            return result;
        }
        boolean hasUprade = false;
        for (String tag : ind.getSpec().getTags()) {
            if(tag.contains("starter")){
                return null;
            }
        }
        for (ResearchOption researchOption : researchAPI.getAllResearchedOptions()) {
            if(!researchOption.isResearched) continue;
            if(!researchOption.hasDowngrade) continue;
            if(!researchOption.downgradeId.equals(ind.getId())) continue;
            hasUprade = true;
        }
        if(!hasUprade) return null;
        if(ind.isUpgrading()) return null;
        List<IndustryOptionData> result = new ArrayList<IndustryOptionData>();

        IndustryOptionData opt = new IndustryOptionData("Choose Upgrade", CUSTOM_PLUGIN, ind, this);
        opt.color = new Color(203, 127, 3, 255);
        result.add(opt);
      return  result;
    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        if (opt.id == CUSTOM_PLUGIN) {
            tooltip.addPara("Select Upgrade for "+ opt.ind.getCurrentName(),0f);
        }
        if(opt.id == IMMEDIATE_ACTION && opt.ind.getMarket().getFaction().isPlayerFaction()){
            tooltip.addPara("Show Research Interface",0f);

        }
        if(opt.id == CUSTOM_PLUGIN_RESEARCHER && opt.ind.getMarket().getFaction().isPlayerFaction()){
            tooltip.addPara("Change current Head of Research Center",0f);

        }
        if(opt.id == STELLA ){
            tooltip.addPara("Access Stellar Forge where special equipment for industries is being forged",0f);

        }

    }

    @Override
    public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui) {
        if (opt.id == CUSTOM_PLUGIN) {
            CustomDialogDelegate delegate = new UpgradeListUI(opt.ind);
            ui.showDialog(UpgradeListUI.WIDTH, UpgradeListUI.HEIGHT, delegate);
        }
        if( opt.id == IMMEDIATE_ACTION){
            ui.showDialog(null, (new ResearchUIDP()));
        }
        if( opt.id == CUSTOM_PLUGIN_RESEARCHER){
            CustomDialogDelegate delegate = new HeadOfResearchCenterUI(AoDUtilis.getResearchAPI().getCurrentResearcher());
            ui.showDialog(HeadOfResearchCenterUI.WIDTH, HeadOfResearchCenterUI.HEIGHT, delegate);
        }
        if( opt.id == STELLA){
            CustomDialogDelegate delegate = new StellaManufactoriumUI(opt.ind);
            ui.showDialog(StellaManufactoriumUI.WIDTH, StellaManufactoriumUI.HEIGHT, delegate);
        }
    }




    @Override
    public void addToIndustryTooltip(Industry ind, IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {
    }

}




