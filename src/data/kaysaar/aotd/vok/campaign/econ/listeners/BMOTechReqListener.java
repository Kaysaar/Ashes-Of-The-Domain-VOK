package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDFactionResearchManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.buildingmenu.additionalreq.BaseAdditionalReq;

import java.util.ArrayList;
import java.util.Collections;

public class BMOTechReqListener extends BaseAdditionalReq {
    String industryId,techId;
    public BMOTechReqListener(String industryId,String techId){
        this.industryId = industryId;
        this.techId = techId;
    }

    @Override
    public boolean metCriteria(MarketAPI market, String indId) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(techId,market);
    }

    @Override
    public String getReason(MarketAPI market, String indId) {
        return "Must research "+ AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(techId).getName();
    }

    @Override
    public ArrayList<String> getIndustriesAffected() {
        return new ArrayList<>(Collections.singleton(industryId));
    }
}
