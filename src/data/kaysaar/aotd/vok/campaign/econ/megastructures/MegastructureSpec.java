package data.kaysaar.aotd.vok.campaign.econ.megastructures;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.models.BaseMegastructureScript;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class MegastructureSpec {
    String id;
    String name;
    LinkedHashSet<String>sectionList = new LinkedHashSet<>();
    String pluginClass;
    int baseUpkeepForMega;
    String iconId;
    String entityId;
    String imageId;
    String description;
    HashSet<String>tags = new LinkedHashSet<>();

    public String getImageForMegastructure() {
        return imageId;
    }

    public void setImageForMegastructure(String imageForMegastructure) {
        this.imageId = imageForMegastructure;
    }

    public String getMegastructureID() {
        return id;
    }

    public void setMegastructureID(String megastructureID) {
        this.id = megastructureID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public BaseMegastructureScript getScript() {
        try {
            BaseMegastructureScript megastructure = (BaseMegastructureScript) Global.getSettings().getScriptClassLoader().loadClass(pluginClass).newInstance();
            return megastructure;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    public void addTag(String tag) {
        tags.add(tag);
    }
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void setScript(String script) {
        this.pluginClass = script;
    }

    public int getBaseUpkeepCredits() {
        return baseUpkeepForMega;
    }

    public void setBaseUpkeepCredits(int baseUpkeepCredits) {
        this.baseUpkeepForMega = baseUpkeepCredits;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public String getSectorEntityTokenId() {
        return entityId;
    }

    public void setSectorEntityTokenId(String sectorEntityTokenId) {
        this.entityId = sectorEntityTokenId;
    }

    public LinkedHashSet<String> getSectionIds() {
        return sectionList;
    }

    public void setSectionIds(LinkedHashSet<String> sectionIds) {
        this.sectionList = sectionIds;
    }

    public String getDescription() {
        return description;
    }

    public void setTags(HashSet<String> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
