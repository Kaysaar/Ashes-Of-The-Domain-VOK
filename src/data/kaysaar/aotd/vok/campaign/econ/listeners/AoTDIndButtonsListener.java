package data.kaysaar.aotd.vok.campaign.econ.listeners;


import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;

import data.kaysaar.aotd.tot.grandwonders.GrandWonderManager;
import data.kaysaar.aotd.vok.campaign.econ.industry.MegastructureIndAPI;
import data.kaysaar.aotd.vok.campaign.econ.industry.grandwonders.GardensOfElysium;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.dialogs.base.BaseMegastructureTestDialog;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.ui.bifrost.BifrostManagmentDialog;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AoTDIndButtonsListener implements IndustryOptionProvider {
    public HashMap<String, ArrayList<String>> upgradeForIndustryRepo = new HashMap<>();
    public static Object BIFROST = new Object();
    public static Object ELYSIUM = new Object();
    public static Object RESEARCH_CENTER = new Object();
    public static Object MEGA = new Object();
    public static ArrayList<Pair<String,String>> industries = new ArrayList<>();
    static {
        industries.add(new Pair<>(Industries.AQUACULTURE,"aotd_tech_rudimentary_eq"));
        industries.add(new Pair<>(Industries.FARMING,"aotd_tech_agriculture"));
        industries.add(new Pair<>(Industries.MINING,"aotd_tech_exoskeletons"));
        industries.add(new Pair<>(Industries.REFINING,"aotd_tech_nanometal"));
        industries.add(new Pair<>(Industries.HEAVYINDUSTRY,"aotd_tech_hull_manufacture"));
        industries.add(new Pair<>(Industries.ORBITALWORKS,"aotd_tech_orbital_assembly"));
        industries.add(new Pair<>(Industries.LIGHTINDUSTRY,"aotd_tech_nanometal"));
        industries.add(new Pair<>(Industries.FUELPROD,"aotd_tech_antimatter_production"));
        industries.add(new Pair<>(Industries.COMMERCE,"aotd_tech_dual_trade_system"));
        industries.add(new Pair<>(Industries.MEGAPORT,"aotd_tech_spaceport_expansion"));
    }
    @Override
    public List<IndustryOptionData> getIndustryOptions(Industry ind) {
        if (ind instanceof MegastructureIndAPI industry) {
            ArrayList<IndustryOptionData> data = new ArrayList<>();
            IndustryOptionData data1 = new IndustryOptionData(industry.getMegastructureButtonText(ind), MEGA, ind, this);
            data1.color = Color.magenta;
            data.add(data1);
            return data;
        }
        if(ind.getSpec().getId().equals("aotd_bfc_ind")){
            ArrayList<IndustryOptionData> data = new ArrayList<>();
            IndustryOptionData data1 = new IndustryOptionData("Manage Bifrost Network", BIFROST, ind, this);
            data1.color = Color.cyan;
            data.add(data1);
            return data;
        }
        if(ind.getSpec().getId().equals("aotd_garden_of_elysium")){
            ArrayList<IndustryOptionData> data = new ArrayList<>();
            IndustryOptionData data1 = new IndustryOptionData("Take your crew to the Gardens of Elysium", ELYSIUM, ind, this);
            data1.color = Misc.getStoryOptionColor();
            if( Global.getSector().getCampaignUI().getCurrentInteractionDialog()!=null&&Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget()!=null&&Global.getSector().getCampaignUI().getCurrentInteractionDialog().getInteractionTarget().equals(ind.getMarket().getPrimaryEntity())){
                data1.enabled = true;
            }
            else{
                data1.enabled = false;
            }
            data.add(data1);
            return data;
        }
        return null;
    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        if(opt.id.equals(MEGA)){
            tooltip.addPara("Access megastructure menu",3f);
        }
        if (opt.id.equals(ELYSIUM)) {
            tooltip.addPara(
                    "Take your crew to the %s, where the %s and serene man-made landscapes offer true rest for even the most battle-worn souls.",
                    3f,
                    Misc.getHighlightColor(),
                    "Gardens of Elysium",
                    "finest resorts"
            );

            tooltip.addPara(
                    "Time spent on Elysium grants a %s to Combat Readiness, with the effects lasting up to %s. (excluding automated ships)",
                    3f,
                    Misc.getPositiveHighlightColor(),
                    "long-lasting boost",
                    "365 days"
            );

            tooltip.addPara(
                    "Human officers who have not previously benefited from the Gardens of Elysium may also gain a %s.",
                    3f,
                    Misc.getPositiveHighlightColor(),
                    "one-time level increase"
            );
            if (!opt.enabled) {
                tooltip.addPara(
                        "You need to be docked at the market before you can interact with it.",
                        Misc.getNegativeHighlightColor(),5f
                );
            }
        }
    }

    @Override
    public void optionSelected(IndustryOptionData opt, DialogCreatorUI ui) {
        if (opt.id.equals(MEGA)) {
            MegastructureIndAPI api = (MegastructureIndAPI) opt.ind;
            AshMisc.initPopUpDialog(new BaseMegastructureTestDialog("Manage Megastructure - "+api.getMegastructureScript(opt.ind.getMarket().getPrimaryEntity()).getName(),api.getMegastructureScript(opt.ind.getMarket().getPrimaryEntity()),ui,opt),BaseMegastructureTestDialog.width,BaseMegastructureTestDialog.height+55);
            Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_mega", true);
        }
        if(opt.id.equals(BIFROST)){
            AshMisc.initPopUpDialog(new BifrostManagmentDialog(ui),BaseMegastructureTestDialog.width,BaseMegastructureTestDialog.height+55);
            Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_mega", true);
        }
        if(opt.id.equals(ELYSIUM)){
            final GardensOfElysium industry= (GardensOfElysium) opt.ind;
            CustomDialogDelegate delegate = new BaseCustomDialogDelegate() {
                @Override
                public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
                    float opad = 10f;
                    Color highlight = Misc.getHighlightColor();
                    TooltipMakerAPI info = panel.createUIElement(1000, 180, false);
                    info.setParaInsigniaLarge();
                    info.addSpacer(2f);

                    info.addPara(
                            "Your crew will spend time resting in the %s, granting your fleet a %s to Combat Readiness for up to %s.",
                            0f,
                            Misc.getPositiveHighlightColor(),
                            "Gardens of Elysium",
                            "long-lasting boost",
                            "365 days"
                    );

                    info.addPara(
                            "Human officers who have not previously benefited from Elysium may also gain a %s. If they had reached their max level",
                            4f,
                            Misc.getPositiveHighlightColor(),
                            "one-time level increase"
                    );

                    info.addPara(
                            "This bonus %s. Visiting Elysium again will refresh or apply the effect where valid, but it will not multiply existing bonuses.",
                            4f,
                            Misc.getHighlightColor(),
                            "does not stack"
                    );

                    info.addPara(
                            "Automated ships are excluded from the Combat Readiness bonus.",
                            4f,
                            Misc.getNegativeHighlightColor(),
                            "Automated ships"
                    );
                    panel.addUIElement(info).inTL(0, 0);
                }

                @Override
                public boolean hasCancelButton() {
                    return true;
                }

                @Override
                public void customDialogConfirm() {
                    industry.setDaysLeftForBuff(365);
                    for (OfficerDataAPI s : Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
                        if(s.getPerson().isAICore())continue;
                        if(s.getPerson().getMemoryWithoutUpdate().is("$aotd_gardens",true))continue;
                        OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");
                        s.getPerson().getMemoryWithoutUpdate().set("$aotd_gardens",true);
                        List<String> list = plugin.pickLevelupSkills(s.getPerson(),Misc.random);
                        if( !list.isEmpty()){
                            s.levelUp(list.get(0));
                        }


                    }

                    Global.getSoundPlayer().playUISound("ui_char_gained_story_point",0.9f,1);
                }


                @Override
                public void customDialogCancel() {

                }
            };
            ui.showDialog(1000, 175, delegate);
        }
    }

    @Override
    public void addToIndustryTooltip(Industry ind, Industry.IndustryTooltipMode mode, TooltipMakerAPI tooltip, float width, boolean expanded) {
        for (Map.Entry<String, IndustrySynergySourceAPI> entry : IndustrySynergiesManager.getInstance().getSourcesOfSynergy().entrySet()) {
            if (entry.getValue().getId().equals(ind.getId())) {
                entry.getValue().addToTooltip(ind, mode, tooltip, width, expanded);
            }
        }
    }

}
