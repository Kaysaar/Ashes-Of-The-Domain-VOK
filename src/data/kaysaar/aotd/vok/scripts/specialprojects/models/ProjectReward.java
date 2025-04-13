package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;

public class ProjectReward {

    public enum ProjectRewardType {
        FIGHTER,
        WEAPON,
        SHIP,
        COMMODITY,
        ITEM,
        AICORE,
        INDUSTRY,
        ABILITY,
        CUSTOM
    }

    public ProjectRewardType type;
    public String id;
    public int amount;

    public ProjectReward(String id, ProjectRewardType type, int amount) {
        this.type = type;
        this.id = id;
        this.amount = amount;
    }

    public static ProjectRewardType translateGPTypeToRewardType(GPSpec.ProductionType type) {
        return ProjectRewardType.valueOf(type.toString());
    }
}
