package data.kaysaar.aotd.vok.scripts.research.contracts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
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
        this.progress=10;
        setup();

        Global.getSector().getIntelManager().addIntel(this, !withIntelNotification, text);
    }

    @Override
    public void addStageDescriptionText(TooltipMakerAPI info, float width, Object stageId) {;

        if (getLastActiveStage(true).id.equals(stageId)) {
            FactionAPI faction = data.getFaction();
            if(getStageId(stageId).equals("stage1")){
                info.addPara("With our contract with %s now we are obliged to help them improve, in return for allowing us to use their databanks and gaining permission to undertake research programs.",0f,Color.ORANGE,faction.getDisplayName());
                info.addPara("We can gain contract points by: ",5f);
                info.addPara("Selling databanks to "+faction.getDisplayName(),Color.ORANGE,5f);
                info.addPara("Researching technologies that faction is interested in (Gives points monthly)",Color.ORANGE,5f);
                info.addPara("Effects till contract is finished:",10f);
                info.addPara("Reduction of research cost (in items) by %s for technologies researched by %s",5f,Color.ORANGE,"100%",faction.getDisplayName());
                info.addPara("Increase of research cost (in items) by %s for technologies not researched by %s",5,Color.ORANGE,"100%",faction.getDisplayName());
            }
            if(getStageId(stageId).equals("stage2")){
                info.addPara("test",0f);
            }
        }

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
    public String  getStageId(Object id){
        return (String)id;
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
    public TooltipMakerAPI.TooltipCreator getStageTooltip(Object stageId) {
        if(getStageId(stageId).equals("stage2")){
            return  new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return true;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 400;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addTitle("End of contract");
                    tooltip.addPara("Contract with faction you have signed has ended, from now on you shall pursue your own goals",10f);
                    tooltip.addPara("Allows for ending contract without penalties and researching under faction's banner without any negative modifiers",Color.ORANGE,5f);
                }
            };
        }
        if(getStageId(stageId).equals("stage1")){
            return  new TooltipMakerAPI.TooltipCreator() {
                @Override
                public boolean isTooltipExpandable(Object tooltipParam) {
                    return true;
                }

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 400;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addTitle("Begin of contract");
                    tooltip.addPara("Contract with faction you have signed has ended, from now on you shall pursue your own goals",10f);
                    tooltip.addPara("Allows for ending contract without penalties and researching under faction's banner without any negative modifiers",Color.ORANGE,5f);
                }
            };
        }
        return super.getStageTooltip(stageId);

    }

    @Override
    public TooltipMakerAPI.TooltipCreator getBarTooltip() {
        return super.getBarTooltip();
    }

    @Override
    public boolean withMonthlyFactors() {
        return true;
    }
}
