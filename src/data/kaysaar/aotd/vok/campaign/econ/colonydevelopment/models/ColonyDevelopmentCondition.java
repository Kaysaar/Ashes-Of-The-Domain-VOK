package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models;

import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketImmigrationModifier;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin2;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class ColonyDevelopmentCondition extends BaseMarketConditionPlugin2 implements MarketImmigrationModifier {
    String idOfDevelopment;

    public void setIdOfDevelopment(String idOfDevelopment) {
        this.idOfDevelopment = idOfDevelopment;
    }

    public String getIdOfDevelopment() {
        if(!AshMisc.isStringValid(idOfDevelopment)) {
            setIdOfDevelopment("standard");
        }
        return idOfDevelopment;
    }

    @Override
    protected void createTooltipAfterDescription(TooltipMakerAPI tooltip, boolean expanded) {
        ColonyDevelopmentManager.getInstance().getColonyDevelopment(getIdOfDevelopment()).generateTooltipForMarketCond(market,tooltip,expanded);


    }

    @Override
    public void apply(String id) {
        super.apply(id);
        if(idOfDevelopment!=null){
            BaseColonyDevelopment development = ColonyDevelopmentManager.getInstance().getColonyDevelopment(idOfDevelopment);
            development.apply(market);
        }
    }

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        if(idOfDevelopment!=null){
            BaseColonyDevelopment development = ColonyDevelopmentManager.getInstance().getColonyDevelopment(idOfDevelopment);
            development.modifyIncoming(market,incoming);
        }
    }

    @Override
    public void unapply(String id) {
        super.unapply(id);
        if(idOfDevelopment!=null){
            BaseColonyDevelopment development = ColonyDevelopmentManager.getInstance().getColonyDevelopment(idOfDevelopment);
            development.unapply(market);
        }
    }
}
