package data.kaysaar.aotd.vok.campaign.econ.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.Ids.AoTDIndustries;

import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalSegment;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.AoTDResearchUIDP;
import data.kaysaar.aotd.vok.ui.UpgradeListUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AoTDIndButtonsListener implements IndustryOptionProvider {
    public HashMap<String, ArrayList<String>>upgradeForIndustryRepo = new HashMap<>();
    public Object AOTD_VOK_UPGRADE = new Object();
    public Object AOTD_VOK_CANCEL_UPGRADE = new Object();
    public static Object STELLA = new Object();
    public static Object CORONAL = new Object();
    public static Object RESEARCH_CENTER = new Object();
    public static Object PROGRAMMING = new Object();
    ReflectionUtilis reflectionUtilis = new ReflectionUtilis();
    static ArrayList<Pair<String,String>> industries = new ArrayList<>();
    static {
        industries.add(new Pair<>(Industries.AQUACULTURE,"aotd_tech_aquatic_agriculture"));
        industries.add(new Pair<>(Industries.FARMING,"aotd_tech_agriculture"));
        industries.add(new Pair<>(Industries.MINING,"aotd_tech_exosceletons"));
        industries.add(new Pair<>(Industries.REFINING,"aotd_tech_nanometal"));
        industries.add(new Pair<>(Industries.HEAVYINDUSTRY,"aotd_tech_hull_manufacture"));
        industries.add(new Pair<>(Industries.ORBITALWORKS,"aotd_tech_orbital_assembly"));
        industries.add(new Pair<>(Industries.LIGHTINDUSTRY,"aotd_tech_nanometal"));


    }
    @Override
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        ArrayList<IndustryOptionData> data = new ArrayList<>();
       if(!ind.isUpgrading()){
            IndustryOptionData opt = validateUpgradeOption(ind);
            if(opt!=null){
                data.add(opt);
            }
       }
       else{
           IndustryOptionData opt = validateCancelUpgradeOption(ind);
           if(opt!=null){
               data.add(opt);
           }
       }
       if(ind instanceof CoronalSegment && !ind.isBuilding()){
           if(!((CoronalSegment) ind).haveCompletedRestoration){
               List<IndustryOptionData> result = new ArrayList<>();
               IndustryOptionData opt;
               opt = new IndustryOptionData("Repair section", CORONAL, ind, this);
               opt.color = new Color(246, 94, 0, 255);
               data.add(opt);
           }
       }

        if(ind.getId().equals(AoTDIndustries.RESEARCH_CENTER)&&ind.getMarket().getFaction().isPlayerFaction()){
            List<IndustryOptionData> result = new ArrayList<>();
            IndustryOptionData opt;
            opt = new IndustryOptionData("Research Center", RESEARCH_CENTER, ind, this);
            opt.color = new Color(0, 217, 246, 255);
            data.add(opt);
        }
       if(data.isEmpty()){
           return null;
       }
       return data;
    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        if (opt.id == AOTD_VOK_UPGRADE) {
            tooltip.addPara("Select Upgrade for "+ opt.ind.getCurrentName(),0f);
        }
        if (opt.id == RESEARCH_CENTER) {
            tooltip.addPara("Access the interface of research center",0f);
        }
        if (opt.id == AOTD_VOK_CANCEL_UPGRADE) {
            String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId", opt.ind);
            IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);
            String costStr = Misc.getDGSCredits(upgrdInd.getCost());
            if (opt.ind.getBuildOrUpgradeProgress() != 0.0f) {
                costStr = Misc.getDGSCredits(upgrdInd.getCost() * 0.4f);
            }
            tooltip.addPara("Currently upgrading to : " + upgrdInd.getName() + ". If you cancel the upgrade you will receive %s as refund\n", 10f, Color.ORANGE, "" + costStr);
        }
        if(opt.id == STELLA ){
            tooltip.addPara("Access the Stellar Forge, where special equipment for industries is being made.",0f);

        }
        if(opt.id == CORONAL ){
            CoronalSegment segment = (CoronalSegment) opt.ind;
            segment.createTooltipInfoForOption(tooltip);
        }
    }

    @Override
    public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui) {
        if (opt.id == AOTD_VOK_UPGRADE) {
            CustomDialogDelegate delegate = new UpgradeListUI(opt.ind,upgradeForIndustryRepo.get(opt.ind.getId()));
            ui.showDialog(UpgradeListUI.WIDTH, UpgradeListUI.HEIGHT, delegate);

        }
        if (opt.id == RESEARCH_CENTER) {
            ui.showDialog(null, (new AoTDResearchUIDP()));
        }

        if (opt.id == AOTD_VOK_CANCEL_UPGRADE) {
            final BaseIndustry industry= (BaseIndustry) opt.ind;
            CustomDialogDelegate delegate = new BaseCustomDialogDelegate() {
                @Override
                public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
                    float opad = 10f;
                    Color highlight = Misc.getHighlightColor();
                    TooltipMakerAPI info = panel.createUIElement(600, 100, false);
                    info.setParaInsigniaLarge();
                    info.addSpacer(2f);
                    String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
                    IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);


                    if (industry.getBuildOrUpgradeProgress() == 0.0f) {
                        int cost = (int) upgrdInd.getCost();
                        String costStr = Misc.getDGSCredits(cost);
                        info.addPara("Cancelling the upgrade of " + industry.getCurrentName() + " will refund you the full upgrade cost of %s and it will take effect immediately", opad,
                                highlight, "" + costStr);
                    } else {
                        int cost = (int) (upgrdInd.getCost() * 0.4f);
                        String costStr = Misc.getDGSCredits(cost);
                        info.addPara("Cancelling the in-progress upgrade of " + industry.getCurrentName() + " will refund you %s of the upgrade cost, or %s and it will take effect immediately", opad,
                                highlight, "40%", costStr);
                    }


                    panel.addUIElement(info).inTL(0, 0);
                }

                @Override
                public boolean hasCancelButton() {
                    return true;
                }

                @Override
                public void customDialogConfirm() {

                    String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
                    IndustrySpecAPI upgrdInd = Global.getSettings().getIndustrySpec(upgradeId);
                    boolean fullRefund = industry.getBuildOrUpgradeProgress() == 0.0f;
                    if (fullRefund) {
                        Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost());
                    } else {
                        Global.getSector().getPlayerFleet().getCargo().getCredits().add(upgrdInd.getCost() * 0.4f);

                    }
                    industry.cancelUpgrade();

                }

                @Override
                public void customDialogCancel() {

                }
            };
            ui.showDialog(600, 125, delegate);
        }
        if(opt.id.equals(CORONAL)){
            opt.ind.getSpec().setUpgrade(opt.ind.getId());
            opt.ind.startUpgrading();
            opt.ind.getSpec().setUpgrade(null);
            Global.getSoundPlayer().playUISound("ui_upgrade_industry",1,1);
        }
    }

    @Override
    public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {

    }
    public void updateIndustryRepo(){
        for (IndustrySpecAPI indsSpec : Global.getSettings().getAllIndustrySpecs()) {
            if(indsSpec.hasTag("starter"))continue;
            ArrayList<String>upgrades = new ArrayList<>();
            if(indsSpec.getUpgrade()!=null)continue;
            for (IndustrySpecAPI subIndSpec : Global.getSettings().getAllIndustrySpecs()) {
                String downgradeID= subIndSpec.getDowngrade();
                if(downgradeID!=null&&downgradeID.equals(indsSpec.getId())){
                    upgrades.add(subIndSpec.getId());
                }
            }
            upgradeForIndustryRepo.put(indsSpec.getId(),upgrades);
        }
    }
    public IndustryOptionData validateUpgradeOption(Industry ind){

        if(upgradeForIndustryRepo.get(ind.getId())!=null&&!upgradeForIndustryRepo.get(ind.getId()).isEmpty()){
            boolean canShow = false;
            for (String s : upgradeForIndustryRepo.get(ind.getId())) {

                MarketAPI marketAPI = ind.getMarket();
                MarketAPI copy = marketAPI.clone();
                marketAPI = copy;
                marketAPI.addIndustry(s);
                Industry upgrdInd = marketAPI.getIndustry(s);
                if(upgrdInd.isAvailableToBuild()||(upgrdInd.showWhenUnavailable()&&!upgrdInd.isAvailableToBuild())){
                    canShow = true;
                    for (Pair<String, String> industry : industries) {
                        if(s.equals(industry.one)&& !AoTDMainResearchManager.getInstance().isResearchedForPlayer(industry.two)){
                            canShow = false;
                        }
                    }
                }
                marketAPI.removeIndustry(upgrdInd.getId(), null, false);
                marketAPI.reapplyIndustries();
            }
            if(canShow){
                IndustryOptionData opt = new IndustryOptionData("Choose Upgrade", AOTD_VOK_UPGRADE, ind, this);
                opt.color = new Color(203, 127, 3, 255);
                return opt;
            }

        }
        return null;
    }
    public IndustryOptionData validateCancelUpgradeOption(Industry ind){
        if(ind.getSpec().getUpgrade()!=null)return null;
        if(!ind.isUpgrading())return  null;
        BaseIndustry industry = (BaseIndustry) ind;
        String upgradeId = (String) reflectionUtilis.getPrivateVariableFromSuperClass("upgradeId",industry);
        if(upgradeId!=null){
            IndustryOptionData opt = new IndustryOptionData("Cancel Upgrade", AOTD_VOK_CANCEL_UPGRADE, ind, this);
            return opt;
        }
        return null;
    }
}
