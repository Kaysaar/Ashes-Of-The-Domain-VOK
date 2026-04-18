package data.kaysaar.aotd.vok.campaign.econ.industry;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import data.kaysaar.aotd.tot.industries.AoTDToolboxPopAndInfra;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

public class CoronalControl extends AoTDToolboxPopAndInfra implements MegastructureIndAPI {
    @Override
    protected Object readResolve() {
        id = Industries.POPULATION;
        Object toReturn = super.readResolve();
        spec = Global.getSettings().getIndustrySpec("aotd_coronal_control");
        return toReturn;
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
    protected String getDescriptionOverride() {
        int size = market.getSize();
        String cid = null;
        if (size >= 1 && size <= 9) {
            cid = "population_" + size;
            MarketConditionSpecAPI mcs = Global.getSettings().getMarketConditionSpec(cid);
            if (mcs != null) {
                return spec.getDesc() + "\n\n" + mcs.getDesc().replaceAll("\\$MarketName", market.getName());
            }
        }
        return super.getDescriptionOverride();
    }

    @Override
    public String getId() {
        return Industries.POPULATION;
    }

    @Override
    public IndustrySpecAPI getSpec() {
        if (spec == null) spec = Global.getSettings().getIndustrySpec("aotd_coronal_control");
        if(!spec.getId().equals("aotd_coronal_control")){
            spec = Global.getSettings().getIndustrySpec("aotd_coronal_control");
        }
        return spec;
    }@Override
    public String getCurrentImage() {
        return spec.getImageName();
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
