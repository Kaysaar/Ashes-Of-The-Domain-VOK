package data.kaysaar.aotd.vok.plugins.bmo;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import kaysaar.bmo.buildingmenu.additionalreq.BaseAdditionalReq;

public class VanillaTechReq extends BaseAdditionalReq {
    String techId;
    public VanillaTechReq(String techId){
        this.techId = techId;
    }

    @Override
    public boolean metCriteria(MarketAPI market, String ind) {
        return AoTDMainResearchManager.getInstance().isAvailableForThisMarket(techId,market);
    }

    @Override
    public String getReason(MarketAPI market, String indId) {
       return AoTDMainResearchManager.getInstance().getNameForResearchBd(techId);
    }
}
