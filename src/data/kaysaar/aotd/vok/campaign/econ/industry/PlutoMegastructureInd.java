package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts.PlutoMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class PlutoMegastructureInd extends BaseIndustry implements MegastructureIndAPI {
    SectorEntityToken megastructureStation;

    @Override
    public void init(String id, MarketAPI market) {
        super.init(id, market);
        if(getEntityOfMegastructure(this)==null){

        }
        else{
            megastructureStation = getEntityOfMegastructure(this);
        }
    }

    @Override
    public boolean canInstallAICores() {
        return false;
    }
    @Override
    public void apply() {
        super.apply(true);
        if(market.getPrimaryEntity()!=null){
            BaseMegastructureScript script = getMegastructureScript(market.getPrimaryEntity());
            script.getRestoredSections().forEach(x->x.applySectionOnIndustry(this));
        }

    }

    @Override
    public void unapply() {
        super.unapply();
        if(market.getPrimaryEntity()!=null){
            BaseMegastructureScript script = getMegastructureScript(market.getPrimaryEntity());
            script.getRestoredSections().forEach(x->x.unApplySectionOnIndustry(this));
        }
    }



    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        super.notifyBeingRemoved(mode, forUpgrade);
        if(market!=null&&market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).advance(-1f);

        }
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(market!=null&&market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).advance(amount);

        }
    }



    @Override
    public String getCurrentImage() {
        if(getMegastructureScript(market.getPrimaryEntity()) instanceof PlutoMegastructure mega){
            if(mega.getLaserSection().isFiringLaser()){
               return Global.getSettings().getSpriteName("industry","pluto_active");
            }
        }
        return super.getCurrentImage();
    }


    @Override
    public SectorEntityToken getEntityOfMegastructure(Industry industry) {
        if(industry.getMarket()==null)return null;
        return industry.getMarket().getConnectedEntities().stream().filter(x->{
            if (x.getCustomEntitySpec() != null) {
                x.getCustomEntitySpec().getId();
                return x.getCustomEntitySpec().getId().equals("aotd_pluto_station");
            }
            return false;
        }).findAny().orElse(null);
    }

    @Override
    public BaseMegastructureScript getMegastructureScript(SectorEntityToken token) {
        return BaseMegastructureScript.getInstanceOfScriptFromEntityIfPresent(token,"aotd_pluto_station");
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
}
