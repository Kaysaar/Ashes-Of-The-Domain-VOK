package data.kaysaar_aotd_vok.scripts.campaign.econ.conditions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import data.Ids.AoDIndustries;
import data.kaysaar_aotd_vok.scripts.research.ResearchAPI;

import java.util.HashMap;

import static data.kaysaar_aotd_vok.plugins.AoDCoreModPlugin.aodTech;

public class IndUpgradeCondition extends BaseMarketConditionPlugin {

    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    public HashMap<String,String> currUpgradesOnPlanet = new HashMap<>();
    public static String UpgradeCond = "AodIndUpgrade";
    @Override
    public void apply(String id) {
        super.apply(id);
        if(researchAPI !=null){
            applyUpgrade();

        }

    }


    @Override
    public void unapply(String id) {
        super.unapply(id);
    }
    public void applyUpgrade() {
        for (Industry ind : market.getIndustries()) {
            if(ind.getId().equals(Industries.MINING)){
                ind.getSpec().setDowngrade(AoDIndustries.EXTRACTIVE_OPERATION);
            }
            if(ind.getId().equals(Industries.REFINING)){
                ind.getSpec().setDowngrade(AoDIndustries.SMELTING);
            }
            boolean cont = false;
            for (String s : ind.getSpec().getTags()) {
                if(s.contains("starter")){
                   cont=true;
                   break;
                }
            }
            if(cont){
                continue;
            }
            if(researchAPI.getResearchOption(ind.getId())!=null&&!ind.isUpgrading()){
                ind.getSpec().setUpgrade(null);
                if(currUpgradesOnPlanet.get(ind.getId())!=null){
                    currUpgradesOnPlanet.remove(ind.getId());
                }
            }



        }
    }
    public static void applyIndustryUpgradeCondition(MarketAPI marketAPI) {
        if (marketAPI.isInEconomy() && !marketAPI.hasCondition(UpgradeCond)){
            marketAPI.addCondition(UpgradeCond);
        }
    }
    @Override
    public boolean showIcon() {
        return false;
    }

    public String getModId() {
        return condition.getId();
    }


}
