package data.kaysaar.aotd.vok.scripts.research.contracts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.impl.campaign.ids.Abilities;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.EventFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.ht.HyperspaceTopographyEventIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.ResearchContract;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;

public class BaseResearchContract extends BaseEventIntel {
    BaseResearchContractData data;
    public static Color BAR_COLOR = Global.getSettings().getColor("progressBarFleetPointsColor");
    public static String RESEARCH_KEY = "aotd_contract_ref"; //we assume you can only have one contract  at the time;
    public static void addFactorCreateIfNecessary(EventFactor factor, InteractionDialogAPI dialog) {
        if (get() == null) {
            return;

        }
        if (get() != null) {
            get().addFactor(factor, dialog);
        }
    }

    public static BaseResearchContract get() {
        return (BaseResearchContract) Global.getSector().getMemoryWithoutUpdate().get(RESEARCH_KEY);
    }
    public BaseResearchContract(BaseResearchContractData contract, TextPanelAPI text,boolean withIntelNotification){
        super();

        this.data = contract;
        setup();
        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }
    @Override
    protected void notifyEnded() {
        super.notifyEnded();
        Global.getSector().getIntelManager().removeIntel(this);
        Global.getSector().getMemoryWithoutUpdate().unset(RESEARCH_KEY);
    }
    protected void setup() {
        factors.clear();
        stages.clear();

        setMaxProgress(data.maxProgress);
        for (Map.Entry<String, String> entry : data.stageNames.entrySet()) {
            addStage(entry.getKey(),data.stageValues.get(entry.getKey()),StageIconSize.MEDIUM);
            getDataFor(entry.getKey()).keepIconBrightWhenLaterStageReached = true;

        }
        stages.get(stages.size()-1).isOneOffEvent = true;


    }

    public float getImageSizeForStageDesc(Object stageId) {
        return 48f;
    }
    public float getImageIndentForStageDesc(Object stageId) {

        return 16f;
    }

    @Override
    protected String getName() {
        return "Research contract "+data.getFaction().getDisplayName();
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("events", "hyperspace_topography");
    }
    protected String getStageIconImpl(Object stageId) {
        String stage = (String) stageId;
        if (data.iconMap.get(stage) != null) {
            return Global.getSettings().getSpriteName("events", data.iconMap.get(stageId));
        }
        // should not happen - the above cases should handle all possibilities - but just in case
        return Global.getSettings().getSpriteName("events", "hyperspace_topography");
    }
    @Override
    public Color getBarColor() {
        Color color = BAR_COLOR;
        //color = Misc.getBasePlayerColor();
        color = Misc.interpolateColor(color, Color.black, 0.25f);
        return color;
    }

    @Override
    public boolean withMonthlyFactors() {
        return false;
    }
}
