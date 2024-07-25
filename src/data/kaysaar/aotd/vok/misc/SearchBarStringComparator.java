package data.kaysaar.aotd.vok.misc;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPOption;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPSpec;

import java.util.Comparator;

public class SearchBarStringComparator implements Comparator<GPOption> {
    private String searchString;
    private int threshold;

    public SearchBarStringComparator(String searchString, int threshold) {
        this.searchString = searchString.toLowerCase();
        this.threshold = threshold;
    }

    @Override
    public int compare(GPOption s1, GPOption s2) {

       String s1S="";
       String s2S ="";
       if(s1.getSpec().getType().equals(GPSpec.ProductionType.SHIP)){
            s1S = s1.getSpec().getShipHullSpecAPI().getHullName();
            s2S = s2.getSpec().getShipHullSpecAPI().getHullName();
       }
        if(s1.getSpec().getType().equals(GPSpec.ProductionType.WEAPON)){
             s1S = s1.getSpec().getWeaponSpec().getWeaponName();
             s2S =s2.getSpec().getWeaponSpec().getWeaponName();
        }
        if(s1.getSpec().getType().equals(GPSpec.ProductionType.FIGHTER)){
            s1S= s1.getSpec().getWingSpecAPI().getWingName();
            s2S =s2.getSpec().getWingSpecAPI().getWingName();
        }
        int distance1 = AoTDMisc.levenshteinDistance(searchString, s1S);
        int distance2 = AoTDMisc.levenshteinDistance(searchString, s2S);

        boolean s1Contains = s1S.contains(searchString);
        boolean s2Contains = s2S.contains(searchString);

        // Prioritize strings that contain the searchString as a substring
        if (s1Contains && !s2Contains) {
            return -1;
        } else if (!s1Contains && s2Contains) {
            return 1;
        }

        // If both contain the searchString, or both don't, compare by Levenshtein distance
        return Integer.compare(distance1, distance2);
    }

    public boolean isValid(String s) {
        return AoTDMisc.levenshteinDistance(searchString, s.toLowerCase()) <= threshold || s.toLowerCase().contains(searchString);
    }

}
