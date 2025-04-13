package data.kaysaar.aotd.vok.scripts.specialprojects.models;

public class SpecialProjectIconData {
    public enum IconType {
        SHIP,
        WEAPON,
        ITEM,
        COMMODITY,
        CUSTOM
    }
    String iconId;
    IconType type;
    float size;

    public SpecialProjectIconData(String iconId, IconType type, float size) {
        this.iconId = iconId;
        this.type = type;
        this.size = size;
    }

    public String getIconId() {
        return iconId;
    }

    public IconType getType() {
        return type;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setType(IconType type) {
        this.type = type;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

}
