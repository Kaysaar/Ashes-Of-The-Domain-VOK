package data.kaysaar.aotd.vok.campaign.econ.produciton;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.misc.ProductionReportIntel;
import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.intel.AoTDProductionReportIntel;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

public class ProductionMonthlyHistoryManager {
    public class ProductionMonthlyData{
        public String idOfProduct;
        public AoTDProductionSpec.AoTDProductionSpecType productionType;
        public int currentAmount;
        public ProductionMonthlyData(String idOfProduct, AoTDProductionSpec.AoTDProductionSpecType productionType, int currentAmount){
            this.idOfProduct = idOfProduct;
            this.productionType = productionType;
            this.currentAmount = currentAmount;
        }
        public void addToAmount(int amount){
            currentAmount += amount;
        }

    }
    LinkedHashSet<ProductionMonthlyData> productionMonthlyDataFromPrevMonth = new LinkedHashSet<ProductionMonthlyData>();
    LinkedHashSet<ProductionMonthlyData> productionMonthlyDataFromCurrent= new LinkedHashSet<ProductionMonthlyData>();

    public LinkedHashSet<ProductionMonthlyData> getProductionMonthlyDataFromCurrent() {
        return getSortedProductionMonthlyData(productionMonthlyDataFromCurrent);
    }

    public LinkedHashSet<ProductionMonthlyData> getProductionMonthlyDataFromPrevMonth() {
        return getSortedProductionMonthlyData(productionMonthlyDataFromPrevMonth);
    }

    private LinkedHashSet<ProductionMonthlyData> getSortedProductionMonthlyData(
            LinkedHashSet<ProductionMonthlyData> data
    ) {
        List<ProductionMonthlyData> sorted = new ArrayList<>(data);

        sorted.sort(Comparator.comparingInt(o -> getProductionTypeOrder(o.productionType)));

        return new LinkedHashSet<>(sorted);
    }

    private int getProductionTypeOrder(AoTDProductionSpec.AoTDProductionSpecType type) {
        if (type == null) return 999;

        return switch (type) {
            case SHIP -> 0;
            case FIGHTER -> 1;
            case WEAPON -> 2;
            case SPECIAL_ITEM -> 3;
            case COMMODITY_ITEM -> 4;
        };
    }
    public static ProductionMonthlyHistoryManager getInstance(){
        if(!Global.getSector().getPersistentData().containsKey("$aotd_prod_his_manager")){
            Global.getSector().getPersistentData().put("$aotd_prod_his_manager", new ProductionMonthlyHistoryManager());
        }
        return (ProductionMonthlyHistoryManager) Global.getSector().getPersistentData().get("$aotd_prod_his_manager");
    }
    public void addToCurrentProduction(String id, int amount , AoTDProductionSpec.AoTDProductionSpecType type){
        ProductionMonthlyData data = getDataForTypeCurrent(id,type);
        if(data==null){
            productionMonthlyDataFromCurrent.add(new ProductionMonthlyData(id,type,amount));
        }
        else{
            data.addToAmount(amount);
        }
    }
    public ProductionMonthlyData getDataForTypeCurrent(String id, AoTDProductionSpec.AoTDProductionSpecType type){
        for (ProductionMonthlyData productionMonthlyData : productionMonthlyDataFromCurrent) {
            if(productionMonthlyData.idOfProduct.equals(id)&&productionMonthlyData.productionType.equals(type)){
                return productionMonthlyData;
            }
        }
        return null;
    }
    public void executeMonthEnd(){
        productionMonthlyDataFromPrevMonth.clear();
        productionMonthlyDataFromPrevMonth.addAll(productionMonthlyDataFromCurrent);
        productionMonthlyDataFromCurrent.clear();
        if(!productionMonthlyDataFromPrevMonth.isEmpty()){
            AoTDProductionReportIntel intel = new AoTDProductionReportIntel();
            Global.getSector().getIntelManager().addIntel(intel);
        }

    }
}
