package data.kaysaar.aotd.vok.campaign.econ.produciton.order;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.scripts.trade.contracts.AoTDTradeContractManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.manager.AoTDProductionManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.trade.ProductionTradeContract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AoTDProductionOrderSnapshot {
    public ArrayList<AoTDProductionOrderData>productionData = new ArrayList<>();
    String id;

    public String getId() {
        return id;
    }
    public LinkedHashMap<String, Integer> getReqResources() {
        LinkedHashMap<String, Integer> resources = new LinkedHashMap<>();

        for (AoTDProductionOrderData productionDatum : productionData) {
            for (Map.Entry<String, Integer> entry : productionDatum.getReqResources().entrySet()) {
                String commodityId = entry.getKey();
                int amount = entry.getValue();

                resources.put(commodityId, resources.getOrDefault(commodityId, 0) + amount);
            }
        }

        return resources;
    }
    public int getMoneyThatWillBeConsumed(){
        int am = 0;
        for (AoTDProductionOrderData productionDatum : productionData) {
            am+=productionDatum.getRemainingUnits()*productionDatum.getSpec().getProductionCost();
        }
        return am;
    }
    public AoTDProductionOrderSnapshot(){
        this.id = Misc.genUID();
    }
    public void addProductionData(AoTDProductionOrderData productionData){
        this.productionData.add(productionData);
    }
    public void initSnapshotConsumption(){
        ProductionTradeContract contract = new ProductionTradeContract(this);

        AoTDTradeContractManager.getInstance().addContract(contract);
        AoTDProductionManager.getInstance().addSnapshot(this);
    }

    public void advance(float amount){
        productionData.forEach(x-> x.advanceProduction(amount));
        productionData.removeIf(AoTDProductionOrderData::isCompleted);
    }
    public static boolean isItemSpecial(String itemId){
        return Global.getSettings().getCommoditySpec(itemId)==null&&Global.getSettings().getSpecialItemSpec(itemId)!=null;
    }
}
