package data.kaysaar_aotd_vok.scripts.campaign.econ;


import java.util.HashMap;


public class SMSpecialItem {
    public HashMap<String,Integer> cost;
    public String id;
    public float costInDays;
    public  SMSpecialItem(HashMap<String,Integer>costFromCsv, String specId, float cost){
        this.cost = costFromCsv;
        this.id = specId;
        this.costInDays=cost;
    }

}
