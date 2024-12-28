package data.kaysaar.aotd.vok.ui.buildingmenu;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;

import java.util.*;

public class BuildingMenuMisc {
    public static ArrayList<IndustrySpecAPI> getSpecsOfParent(String parentTag) {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        if(parentTag==null)return specs;
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.hasTag(parentTag) && allIndustrySpec.hasTag("sub_item")) {
                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }

    public static LinkedHashMap<IndustrySpecAPI, ArrayList<IndustrySpecAPI>> getSpecMapParentChild() {
        LinkedHashMap<IndustrySpecAPI, ArrayList<IndustrySpecAPI>> map = new LinkedHashMap<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.hasTag("parent_item")) {
                map.put(allIndustrySpec, getSpecsOfParent(allIndustrySpec.getData()));
            }
        }
        return map;
    }

    public static Set<IndustrySpecAPI> getIndustryTree(String progenitor) {
        Set<IndustrySpecAPI> specs = new LinkedHashSet<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.getDowngrade() == null) continue;
            IndustrySpecAPI currentOne = allIndustrySpec;
            Set<IndustrySpecAPI> specsToProgenitor = new LinkedHashSet<>();
            while (currentOne.getDowngrade() != null) {
                specsToProgenitor.add(currentOne);
                currentOne = Global.getSettings().getIndustrySpec(currentOne.getDowngrade());
            }
            if (currentOne.getId().equals(progenitor)) {
                specs.addAll(specsToProgenitor);
            }
        }
        return specs;
    }

    public static ArrayList<IndustrySpecAPI> getAllSpecsWithoutDowngrade() {
        ArrayList<IndustrySpecAPI> specs = new ArrayList<>();
        for (IndustrySpecAPI allIndustrySpec : Global.getSettings().getAllIndustrySpecs()) {
            if (allIndustrySpec.getDowngrade() == null  && !allIndustrySpec.hasTag("sub_item")) {
                specs.add(allIndustrySpec);
            }
        }
        return specs;
    }

    public static boolean isIndustryFromTreePresent(IndustrySpecAPI spec, MarketAPI marketToValidate) {
        if (spec.hasTag("parent_item")) {
            for (IndustrySpecAPI industrySpecAPI : getSpecsOfParent(spec.getData())) {
                IndustrySpecAPI current = industrySpecAPI;
                if (marketToValidate.hasIndustry(current.getId())) return true;
                while (current.getUpgrade() != null) {
                    current = Global.getSettings().getIndustrySpec(current.getUpgrade());
                    if (marketToValidate.hasIndustry(current.getId())) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            for (IndustrySpecAPI industrySpecAPI : getIndustryTree(spec.getId())) {
                if (marketToValidate.hasIndustry(industrySpecAPI.getId())) {
                    return true;
                }
            }
            return false;
        }
    }

    // New method to sort IndustrySpecAPI by name alphabetically
    public static void sortIndustrySpecsByName(ArrayList<IndustrySpecAPI> industrySpecs) {
        Collections.sort(industrySpecs, new Comparator<IndustrySpecAPI>() {
            @Override
            public int compare(IndustrySpecAPI spec1, IndustrySpecAPI spec2) {
                return spec1.getName().compareToIgnoreCase(spec2.getName());
            }
        });
    }
}
