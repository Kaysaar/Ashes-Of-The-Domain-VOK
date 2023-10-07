package data.scripts.research;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.Misc;
import data.plugins.AoDUtilis;
import lunalib.lunaSettings.LunaSettings;

import static data.plugins.AoDCoreModPlugin.aodTech;

public class ResearchProgressScript implements EveryFrameScript {

    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    public boolean firstTick = true;
    public int lastDayChecked = 0;
    public static int interval = 180;
    public static int staticInteral = 180;
    public static boolean shouldStartInterval = false;
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }
    public boolean hasResearchedin30Days = true;
    public boolean sentmessage = false;
    int counter =0;

    public boolean canResearchAnything(){
        for (ResearchOption researchOption : AoDUtilis.getResearchAPI().getAlreadyResearchedTechs()) {
            if(researchOption.isDisabled)continue;
            if(researchOption.isResearched)continue;
            if(AoDUtilis.getResearchAPI().canResearch(researchOption.industryId,false)){
                return true;
            }
        }
        return false;
    }
    public void advance(float amount) {
        researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
        if(researchAPI.alreadyResearchedAmount()>=12){
            if (Global.getSettings().getModManager().isModEnabled("lunalib"))
            {
                boolean enabled = Boolean.TRUE.equals(LunaSettings.getBoolean("aod_core", "aoTDVOK_SOPHIA_ENABLED"));
                if(enabled){
                    Global.getSector().getMemory().set("$aotd_can_op_scientist",true);
                }
            }
            Global.getSector().getMemory().set("$aotd_can_scientist",true);
        }
       if(researchAPI.getResearchFacilitiesQuantity()!=0){
           if(Global.getSector().getMemory().is("$has_built_first_facility",false)){
               if(Global.getSector().getMemory().is("$aotd_got_core ",false)||!Global.getSector().getMemory().contains("$aotd_got_core")){
                   MessageIntel intel = new MessageIntel("Galatia Council wants to see you personally (Go to Galatia)", Misc.getHighlightColor());
                   intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                   intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                   Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                   Global.getSector().getMemory().set("$has_built_first_facility",true);
               }

           }

       }
        if(researchAPI.alreadyResearchedAmount()>=17&&researchAPI.alreadyResearchedAmountCertainTier(3)>=1){
            if (Global.getSettings().getModManager().isModEnabled("lunalib"))
            {
                 boolean enabled = Boolean.TRUE.equals(LunaSettings.getBoolean("aod_core", "aoTDVOK_OP_SCIENTIST_ENABLED"));
                 if(enabled){
                     Global.getSector().getMemory().set("$aotd_can_op_scientist",true);
                 }
            }
            Global.getSector().getMemory().set("$aotd_can_op_scientist",true);
        }

        sentmessage=false;
        if (newDay()) {
            if(shouldStartInterval){
                interval-=1;
                if(interval<=0){
                    shouldStartInterval = false;
                    interval=staticInteral;
                }
            }
            if (researchAPI.isResearching()) {
                hasResearchedin30Days=true;
                counter=0;
                boolean stopedResearch = false;
                ResearchOption currResearch = researchAPI.getCurrentResearching();
                if(Global.getSettings().getIndustrySpec(currResearch.industryId).hasTag("experimental")&&!AoDUtilis.canExperimental()){
                    MessageIntel intel = new MessageIntel("Halted Research - " + currResearch.industryName, Misc.getBasePlayerColor());
                    intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                    intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                    Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                    researchAPI.stopResearch();
                    stopedResearch = true;
                }
                currResearch.currentResearchDays -= AoDUtilis.researchBonusCurrent();
                if (currResearch.currentResearchDays <= 0) {
                    researchAPI.finishResearch();
                    stopedResearch = true;
                }
                if(stopedResearch){
                    if(!researchAPI.getResearchQueue().isEmpty()){
                        researchAPI.startResearch(researchAPI.getResearchQueue().get(0).industryId);
                        MessageIntel intel = new MessageIntel("Start Queued Research - " + researchAPI.getCurrentResearching().industryName, Misc.getBasePlayerColor());
                        intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                        Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                        researchAPI.getResearchQueue().remove(0);
                    }
                }
            }
            else{
                hasResearchedin30Days=false;
                counter++;
            }
            if(researchAPI.firstMarketThatHaveResearchFacility()!=null&&canResearchAnything()&&counter!=0&&counter%30==0&&counter<=90){
                MessageIntel intel = new MessageIntel("It has been "+counter+" days since you done your last research!", Misc.getHighlightColor());
                intel.setIcon(Global.getSector().getPlayerFaction().getCrest());
                intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
                Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.NOTHING);
                sentmessage=true;
            }
        }


    }

    private boolean newDay() { //New day check, stolen from VIC mod
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstTick) {
            lastDayChecked = clock.getDay();
            firstTick = false;
            return false;
        } else if (clock.getDay() != lastDayChecked) {
            lastDayChecked = clock.getDay();
            return true;
        }
        return false;
    }
}
