package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GpHistory {
    public static class GPFullHistoryData {
        public  int cycle,month;
        public LinkedHashMap<String, GPOrderData>data = new LinkedHashMap<>();

        public GPFullHistoryData(int cycle, int month){
            this.cycle = cycle;
            this.month  = month;
        }

        public int getCycle() {
            return cycle;
        }

        public int getMonth() {
            return month;
        }
        public void addToData(GPOrderData data){
            this.data.put(data.getId(),data);
        }

    }

    public ArrayList<GPFullHistoryData> getData() {
        this.data.sort((a, b) -> {
            if (b.getCycle() != a.getCycle()) {
                return Integer.compare(b.getCycle(), a.getCycle());
            }
            return Integer.compare(b.getMonth(), a.getMonth());
        });
        return data;
    }

    public static class GPOrderData {
        public String id;
        public GPSpec.ProductionType productionType;
        public int amount;
        public GPOrderData(String id, GPSpec.ProductionType productionType) {
            this.id = id;
            this.productionType = productionType;
        }

        public String getId() {
            return id;
        }

        public GPSpec.ProductionType getProductionType() {
            return productionType;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }

    public LinkedHashMap<GPOrderData, Integer> currMonthProd = new LinkedHashMap<>();
    public ArrayList<GPFullHistoryData> data = new ArrayList<>();

    public void addProduction(GPSpec spec, int amount) {
        if (spec == null || amount <= 0) return;

        GPOrderData data = new GPOrderData(spec.projectId, spec.type);

        // Update current month production
        currMonthProd.put(data, currMonthProd.getOrDefault(data, 0) + amount);

    }

    public static void reportPlayerProducedStuff(GPSpec spec, Object param, int amount) {
        if (spec == null || amount <= 0) return;

        // Notify listeners
        Global.getSector().getListenerManager().getListeners(GPProductionListener.class).forEach(x -> {
            x.reportPlayerProducedStuff(spec, param, amount);
        });

        // Register production
        GpHistory history = GPManager.getInstance().getProductionHistory();
        if (history != null) {
            history.addProduction(spec, amount);
        }
    }

    public void endOfMonth() {
        currMonthProd.forEach(GPOrderData::setAmount);
        if(!currMonthProd.isEmpty()){
            GPFullHistoryData data = new GPFullHistoryData(Global.getSector().getClock().getCycle(),Global.getSector().getClock().getMonth());
            currMonthProd.keySet().forEach(data::addToData);
            this.data.add(data);
            this.data.sort((a, b) -> {
                if (b.getCycle() != a.getCycle()) {
                    return Integer.compare(b.getCycle(), a.getCycle());
                }
                return Integer.compare(b.getMonth(), a.getMonth());
            });
        }

        // Clear current month data
        currMonthProd.clear();
    }


}
