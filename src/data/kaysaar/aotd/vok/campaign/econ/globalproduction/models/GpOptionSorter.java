package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.ui.customprod.components.SortingState;
import data.kaysaar.aotd.vok.misc.AoTDMisc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class GpOptionSorter {
    public static ArrayList<GPOption> getShipPackagesBasedOnTags(ArrayList<String> manufacturues, ArrayList<String> sizes, ArrayList<String> types) {
        boolean allMan = AoTDMisc.arrayContains(manufacturues, "All designs") || manufacturues.isEmpty();
        boolean allSizes = AoTDMisc.arrayContains(sizes, "All sizes") || sizes.isEmpty();
        boolean allTypes = AoTDMisc.arrayContains(types, "All types") || types.isEmpty();
        ArrayList<GPOption> options = new ArrayList<>();
        if (allMan && allSizes && allTypes)
            return getShipPackagesBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedShipPackages());
        for (GPOption learnedShipPackage : getShipPackagesBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedShipPackages())) {
            boolean valid = true;
            if (!allMan) {
                valid = false;
                for (String manufacturue : manufacturues) {
                    if (learnedShipPackage.getSpec().getShipHullSpecAPI().getManufacturer().equals(manufacturue)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) continue;
            if (!allSizes) {
                valid = false;
                for (String s : sizes) {
                    if (Misc.getHullSizeStr(learnedShipPackage.getSpec().getShipHullSpecAPI().getHullSize()).equals(s)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) continue;
            if (!allTypes) {
                valid = false;
                for (String s : types) {
                    if (AoTDMisc.getType(learnedShipPackage.getSpec().getShipHullSpecAPI()).equals(s)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                options.add(learnedShipPackage);
            }

        }
        return options;
    }

    public static ArrayList<GPOption> getItemsBasedOnTag(ArrayList<String> manufacturues) {
        boolean allMan = AoTDMisc.arrayContains(manufacturues, "All designs") || manufacturues.isEmpty();
        ArrayList<GPOption> options = new ArrayList<>();
        if (allMan) return getItemSortedBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedItems());
        for (GPOption learnedShipPackage : getItemSortedBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedItems())) {
            if(learnedShipPackage.getSpec().getItemSpecAPI()==null)continue;
            boolean valid = true;
            if (!allMan) {
                valid = false;
                for (String manufacturue : manufacturues) {
                    if (learnedShipPackage.getSpec().getItemSpecAPI().getManufacturer().equals(manufacturue)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                options.add(learnedShipPackage);
            }

        }
        if(AoTDMisc.arrayContains(manufacturues, "AI Cores")){
            options.addAll(GPManager.getInstance().getAICores());
        }
        return options;
    }

    public static ArrayList<GPOption> getWeaponPackagesBasedOnTags(ArrayList<String> manufacturues, ArrayList<String> sizes, ArrayList<String> types) {
        boolean allMan = AoTDMisc.arrayContains(manufacturues, "All designs") || manufacturues.isEmpty();
        boolean allSizes = AoTDMisc.arrayContains(sizes, "All sizes") || sizes.isEmpty();
        boolean allTypes = AoTDMisc.arrayContains(types, "All types") || types.isEmpty();
        ArrayList<GPOption> options = new ArrayList<>();
        if (allMan && allSizes && allTypes)
            return getWeaponPackagesBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedWeapons());
        for (GPOption option : getWeaponPackagesBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedWeapons())) {
            boolean valid = true;
            if (!allMan) {
                valid = false;
                for (String manufacturue : manufacturues) {
                    if (option.getSpec().getWeaponSpec().getManufacturer().equals(manufacturue)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) continue;
            if (!allSizes) {
                valid = false;
                for (String s : sizes) {
                    if (option.getSpec().getWeaponSpec().getSize().getDisplayName().equals(s)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) continue;
            if (!allTypes) {
                valid = false;
                for (String s : types) {
                    if (option.getSpec().getWeaponSpec().getType().getDisplayName().equals(s)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                options.add(option);
            }

        }
        return options;
    }

    public static ArrayList<GPOption> getFighterPackagesBasedOnTags(ArrayList<String> manufacturues, ArrayList<String> sizes, ArrayList<String> types) {
        boolean allMan = AoTDMisc.arrayContains(manufacturues, "All designs") || manufacturues.isEmpty();
        boolean allSizes = AoTDMisc.arrayContains(sizes, "All sizes") || sizes.isEmpty();
        boolean allTypes = AoTDMisc.arrayContains(types, "All types") || types.isEmpty();
        ArrayList<GPOption> options = new ArrayList<>();
        if (allMan && allSizes && allTypes)
            return getFighterBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedFighters());
        for (GPOption option : getFighterBasedOnData("Cost", SortingState.ASCENDING, GPManager.getInstance().getLearnedFighters())) {
            boolean valid = true;
            if (!allMan) {
                valid = false;
                for (String manufacturue : manufacturues) {
                    if (option.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer().equals(manufacturue)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (!valid) continue;
            if (!allTypes) {
                valid = false;
                for (String s : types) {
                    if (AoTDMisc.getType(option.getSpec().getWingSpecAPI()).equals(s)) {
                        valid = true;
                        break;
                    }
                }
            }
            if (valid) {
                options.add(option);
            }

        }
        return options;
    }

    public static ArrayList<GPOption> getShipPackagesBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getShipHullSpecAPI().getHullName();
                    String s2 = o2.getSpec().getShipHullSpecAPI().getHullName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Size")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = Misc.getHullSizeStr(o1.getSpec().getShipHullSpecAPI().getHullSize());
                    String s2 = Misc.getHullSizeStr(o2.getSpec().getShipHullSpecAPI().getHullSize());
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = AoTDMisc.getType(o1.getSpec().getShipHullSpecAPI());
                    String s2 = AoTDMisc.getType(o2.getSpec().getShipHullSpecAPI());
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getShipHullSpecAPI().getManufacturer();
                    String s2 = o2.getSpec().getShipHullSpecAPI().getManufacturer();
                    s1 = AoTDMisc.ensureManBeingNotNull(s1);
                    s2 = AoTDMisc.ensureManBeingNotNull(s2);
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }

    public static ArrayList<GPOption> getItemSortedBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1,s2;
                    if(o1.getSpec().getItemSpecAPI()!=null){
                        s1=o1.getSpec().getItemSpecAPI().getName();
                    }
                    else {
                        s1= o1.getSpec().getAiCoreSpecAPI().getName();
                    }
                    if(o2.getSpec().getItemSpecAPI()!=null){
                        s2=o2.getSpec().getItemSpecAPI().getName();
                    }
                    else{
                        s2= o2.getSpec().getAiCoreSpecAPI().getName();
                    }


                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1,s2;
                    if(o1.getSpec().getItemSpecAPI()!=null){
                        s1=o1.getSpec().getItemSpecAPI().getManufacturer();
                    }
                    else {
                        s1= "Ai cores";
                    }
                    if(o2.getSpec().getItemSpecAPI()!=null){
                        s2=o2.getSpec().getItemSpecAPI().getManufacturer();
                    }
                    else{
                        s2= "Ai cores";
                    }
                    s1 = AoTDMisc.ensureManBeingNotNull(s1);
                    s2 = AoTDMisc.ensureManBeingNotNull(s2);
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }

    public static ArrayList<GPOption> getFighterBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWingSpecAPI().getWingName();
                    String s2 = o2.getSpec().getWingSpecAPI().getWingName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWingSpecAPI().getRoleDesc();
                    String s2 = o2.getSpec().getWingSpecAPI().getRoleDesc();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer();
                    String s2 = o2.getSpec().getWingSpecAPI().getVariant().getHullSpec().getManufacturer();
                    s1 = AoTDMisc.ensureManBeingNotNull(s1);
                    s2 = AoTDMisc.ensureManBeingNotNull(s2);
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }

    public static ArrayList<GPOption> getWeaponPackagesBasedOnData(String nameOfSort, SortingState sortingState, ArrayList<GPOption> temp) {
        ArrayList<GPOption> packages = new ArrayList<>(temp);
        Comparator<GPOption> comparator = null;
        if (nameOfSort.equals("Name")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getWeaponName();
                    String s2 = o2.getSpec().getWeaponSpec().getWeaponName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Build time")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().days;
                    float price2 = o2.getSpec().days;
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Size")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getSize().getDisplayName();
                    String s2 = o2.getSpec().getWeaponSpec().getSize().getDisplayName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getType().getDisplayName();
                    String s2 = o2.getSpec().getWeaponSpec().getType().getDisplayName();
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Design Type")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    String s1 = o1.getSpec().getWeaponSpec().getManufacturer();
                    String s2 = o2.getSpec().getWeaponSpec().getManufacturer();
                    s1 = AoTDMisc.ensureManBeingNotNull(s1);
                    s2 = AoTDMisc.ensureManBeingNotNull(s2);
                    return s1.compareTo(s2);
                }
            };
        }
        if (nameOfSort.equals("Cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float price1 = o1.getSpec().getCredistCost();
                    float price2 = o2.getSpec().getCredistCost();
                    return Float.compare(price1, price2);
                }
            };
        }
        if (nameOfSort.equals("Gp cost")) {
            comparator = new Comparator<GPOption>() {
                @Override
                public int compare(GPOption o1, GPOption o2) {
                    float wage1 = 0;
                    float wage2 = 0;
                    // What we do is 1 advanced = 10 normal ones BUT not yet ;
                    for (Map.Entry<String, Integer> option : o1.getSpec().getSupplyCost().entrySet()) {
                        wage1 += option.getValue();
                    }
                    for (Map.Entry<String, Integer> option : o2.getSpec().getSupplyCost().entrySet()) {
                        wage2 += option.getValue();
                    }

                    return Float.compare(wage1, wage2);
                }
            };
        }
        if (sortingState == SortingState.DESCENDING) {
            Collections.sort(packages, comparator);
        }
        if (sortingState == SortingState.ASCENDING) {
            Collections.sort(packages, Collections.reverseOrder(comparator));
        }
        return packages;
    }
}
