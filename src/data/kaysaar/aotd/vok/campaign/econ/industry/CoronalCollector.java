package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class CoronalCollector extends BaseIndustry implements MegastructureIndAPI {
    @Override
    public void apply() {
        if(market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).getSectionById("coronal_collector").applySectionOnIndustry(this);
            if(getMegastructureScript(market.getPrimaryEntity()).getSectionById("coronal_collector").isRestored()){
                market.getPrimaryEntity().getMemoryWithoutUpdate().set("$usable",false);
            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        if(market.getPrimaryEntity()!=null){
            getMegastructureScript(market.getPrimaryEntity()).getSectionById("coronal_collector").unApplySectionOnIndustry(this);

        }
    }

    // Drives the coronal hypershunt megastructure's advance (restoration progress, timeline events).
    // This lives on the collector industry rather than the population industry (CoronalControl) because
    // the population slot can be replaced by other mods (e.g. Industrial Evolution's Switchable Population),
    // which would silently stop restoration. This industry is core to the hypershunt and cannot be removed.
    @Override
    public void advance(float amount) {
        super.advance(amount);
        if (market != null && market.getPrimaryEntity() != null) {
            BaseMegastructureScript script = getMegastructureScript(market.getPrimaryEntity());
            if (script != null) {
                script.advance(amount);
            }
        }
    }

    @Override
    public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
        super.notifyBeingRemoved(mode, forUpgrade);
        if (market != null && market.getPrimaryEntity() != null) {
            BaseMegastructureScript script = getMegastructureScript(market.getPrimaryEntity());
            if (script != null) {
                script.advance(-1f);
            }
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
