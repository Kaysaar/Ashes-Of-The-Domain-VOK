package data.kaysaar.aotd.vok.campaign.econ.listeners;


import ashlib.data.plugins.misc.AshMisc;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;

import data.kaysaar.aotd.vok.campaign.econ.industry.MegastructureIndAPI;
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
    public static Object CORONAL = new Object();
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
        return null;
    }

    @Override
    public void createTooltip(IndustryOptionData opt, TooltipMakerAPI tooltip, float width) {
        if(opt.id.equals(MEGA)){
            tooltip.addPara("Access megastructure menu",3f);
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
