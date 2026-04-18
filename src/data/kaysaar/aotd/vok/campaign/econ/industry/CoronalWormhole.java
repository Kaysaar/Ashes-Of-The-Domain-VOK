package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalCenter;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoronalWormhole extends BaseIndustry implements MegastructureIndAPI{
    public boolean allSectionsRestored = false;
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
