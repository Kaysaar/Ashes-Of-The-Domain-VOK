package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class CoronalCollector extends BaseIndustry implements MegastructureIndAPI {
    @Override
    public void apply() {
        if(market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).getSectionById("coronal_collector").applySectionOnIndustry(this);

        }
    }

    @Override
    public void unapply() {
        super.unapply();
        if(market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).getSectionById("coronal_collector").unApplySectionOnIndustry(this);

        }
    }
    @Override
    public boolean canShutDown() {
        return false;
    }

    @Override
    public boolean canDowngrade() {
        return false;
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }

    @Override
    public boolean showShutDown() {
        return false;
    }

    @Override
    public boolean canImprove() {
        return false;
    }
    @Override
    public SectorEntityToken getEntityOfMegastructure(Industry industry) {
        if(industry.getMarket()!=null){
            return industry.getMarket().getPrimaryEntity();
        }
        return null;
    }



    @Override
    public BaseMegastructureScript getMegastructureScript(SectorEntityToken token) {
        return BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(token,"coronal_hypershunt");
    }
}
