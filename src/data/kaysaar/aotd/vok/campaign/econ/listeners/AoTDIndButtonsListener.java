package data.kaysaar.aotd.vok.campaign.econ.listeners;


import ashlib.data.plugins.coreui.CommandTabMemoryManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomDialogDelegate;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.DialogCreatorUI;
import com.fs.starfarer.api.campaign.listeners.IndustryOptionProvider;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.loading.IndustrySpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.campaign.econ.industry.BaseMegastructureIndustry;
import data.kaysaar.aotd.vok.campaign.econ.industry.coronaltap.CoronalSegment;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergiesManager;
import data.kaysaar.aotd.vok.campaign.econ.synergies.models.IndustrySynergySourceAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.UpgradeListUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AoTDIndButtonsListener implements IndustryOptionProvider {
    public HashMap<String, ArrayList<String>> upgradeForIndustryRepo = new HashMap<>();
    public Object AOTD_VOK_UPGRADE = new Object();
    public Object AOTD_VOK_CANCEL_UPGRADE = new Object();
    public static Object STELLA = new Object();
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
        if (ind instanceof BaseMegastructureIndustry industry) {
            ArrayList<IndustryOptionData> data = new ArrayList<>();
            IndustryOptionData data1 = new IndustryOptionData("Manage Megastructure", MEGA, ind, this);
            data1.color = Color.magenta;
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
            CommandTabMemoryManager.getInstance().setLastCheckedTab("research & production");
            CommandTabMemoryManager.getInstance().getTabStates().put("research & production","megastructures");
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.OUTPOSTS);
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
