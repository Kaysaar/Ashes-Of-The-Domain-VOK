package data.kaysaar.aotd.vok.campaign.econ.megastructures.tradecontracts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContract;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureSection;
import data.kaysaar.aotd.vok.campaign.econ.produciton.order.AoTDProductionOrderData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.LinkedHashMap;
import java.util.Map;

public class BaseRestorationContract extends AoTDTradeContract {
    public static float MARGIN_OF_COMMODITY_PRICE = 0.1f;
    BaseMegastructureSection section;
    public BaseRestorationContract(BaseMegastructureSection section) {
        super(section.getRestorationContractID(), null, Factions.PLAYER, 9999);
        this.section = section;
        for (Map.Entry<String, Integer> entry : AoTDMisc.getOrderedResourceMap(section.getMonthlyResNeeded()).entrySet()) {
            addContractData(entry.getKey(),entry.getValue(),MARGIN_OF_COMMODITY_PRICE);
        }
    }


    @Override
    public String getIconName() {
        return section.getMegastructureTiedTo().getIcon();
    }

    public static int getCreditsWorthOfResources(LinkedHashMap<String,Integer>resources){
        int price = 0;
        for (Map.Entry<String, Integer> entry : resources.entrySet()) {
            CommoditySpecAPI spec  = Global.getSettings().getCommoditySpec(entry.getKey());
            price+= (int) (spec.getBasePrice()*entry.getValue()*MARGIN_OF_COMMODITY_PRICE);
        }
        return price;
    }

    @Override
    public void executeMonthEndForCommodity(int delivered, String commodityId) {
        section.addResourcesToBeSpentOnRestoration(commodityId,delivered);
    }

    @Override
    public void executeMonthEnd(float percentageOfEntireContractMet) {
        contractData.clear();
        for (Map.Entry<String, Integer> entry : AoTDMisc.getOrderedResourceMap(section.getMonthlyResNeeded()).entrySet()) {
            if(entry.getValue()>0){
                addContractData(entry.getKey(),entry.getValue(),MARGIN_OF_COMMODITY_PRICE);

            }
        }
        this.runCleanUp();
    }

    @Override
    public String getContractType() {
        return "Megastructure Restoration";
    }

    @Override
    public String getContractTypeId() {
        return "megastructure_restoration";
    }
    @Override
    public String getSubTypeOfContractString() {
        return section.getName() ;
    }
    @Override
    public void printCustomSection(TooltipMakerAPI tooltip, float width) {
        tooltip.addPara(
                "Every month this contract attempts to consume the required resources to complete restoration of this section.",
                3f
        );

    }
    @Override
    public boolean canEditContract() {
        return false;
    }

    @Override
    public boolean canTerminateContract() {
        return false;
    }

    @Override
    public boolean isExpired() {
        return section.isRestored()||!section.isOwnedByPLayerFaction()||contractData.isEmpty();
    }
    @Override
    public boolean canFreezeContract() {
        return false;
    }
}
