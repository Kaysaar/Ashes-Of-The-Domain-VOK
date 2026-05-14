package data.kaysaar.aotd.vok.campaign.econ.produciton;


import com.fs.starfarer.api.impl.campaign.ids.Items;
import data.kaysaar.aotd.vok.Ids.AoTDCommodities;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;

public class SpecialItemRecipeInfluencer implements AoTDProducitonSpecListener {
    @Override
    public void specsCreated() {
        AoTDProductionSpecManager.getSpecialItemSpec(Items.FULLERENE_SPOOL).getMapOfResourcesNeeded().clear();
        AoTDProductionSpecManager.getSpecialItemSpec(Items.FULLERENE_SPOOL).getMapOfResourcesNeeded().put(AoTDCommodities.REFINED_METAL,1000);
    }
}
