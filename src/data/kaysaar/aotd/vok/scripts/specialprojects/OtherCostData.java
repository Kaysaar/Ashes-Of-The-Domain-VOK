package data.kaysaar.aotd.vok.scripts.specialprojects;

public class OtherCostData {
    public enum CostType{
        WEAPON,
        SHIP,
        ITEM,
        COMMODITY,
        FIGHTER
    }
    public CostType costType;
    public String id;
    public int amount;
    public OtherCostData(String id, String costType, int amount){
        this.costType = CostType.valueOf(costType);
        this.id = id;
        this.amount = amount;
    }
    public void remove(int amount){
        this.amount -= amount;
    }
    public void addAmount(int amount){
        this.amount += amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public CostType getCostType() {
        return costType;
    }

    public int getAmount() {
        return amount;
    }
}
