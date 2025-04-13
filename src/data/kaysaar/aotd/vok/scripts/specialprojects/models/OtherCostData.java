package data.kaysaar.aotd.vok.scripts.specialprojects.models;

public class OtherCostData {
    public enum ItemType {
        WEAPON,
        SHIP,
        ITEM,
        COMMODITY,
        FIGHTER
    }
    public ItemType itemType;
    public String id;
    public int amount;
    public OtherCostData(String id, String costType, int amount){
        this.itemType = ItemType.valueOf(costType);
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

    public ItemType getCostType() {
        return itemType;
    }

    public int getAmount() {
        return amount;
    }
}
