package data.kaysaar.aotd.vok.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WingRole;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class AoTDMisc {
    @Nullable
    public static String getVaraint(ShipHullSpecAPI allShipHullSpec) {
        String variantId = null;
        for (String allVariantId : Global.getSettings().getAllVariantIds()) {
            ShipVariantAPI variant = Global.getSettings().getVariant(allVariantId);
            if (variant.getVariantFilePath() == null) continue;
            if (variant.getHullSpec().getHullId().equals(allShipHullSpec.getHullId())) {
                variantId = allVariantId;
                break;
            }
        }
        if (variantId == null) {
            final String withHull = allShipHullSpec.getHullId() + "_Hull";
            for (String id : Global.getSettings().getAllVariantIds()) {
                if (withHull.equalsIgnoreCase(id)) {
                    variantId = id;
                    break;
                }
            }
        }

        return variantId;
    }

    public static String[] splitAndClean(String input) {
        // Convert to lowercase to make the search case-insensitive
        String lowerCaseInput = input.toLowerCase();

        // Find the index of the separator "class"
        int separatorIndex = lowerCaseInput.indexOf("class");

        if (separatorIndex == -1) {
            // If "class" is not found, return the original string and an empty string
            return new String[]{input.trim(), ""};
        }

        // Split the string into two parts based on the index of "class"
        String part1 = input.substring(0, separatorIndex).trim();
        String part2 = input.substring(separatorIndex).trim();

        // Remove any extraneous characters from part1
        part1 = part1.replace("-", "").trim();

        // Capitalize "Class" in part2 to match the desired output
        if (part2.length() > 0) {
            part2 = Character.toUpperCase(part2.charAt(0)) + part2.substring(1);
        }

        return new String[]{part1, part2};
    }

    public static LinkedHashMap<String, Integer> sortByValueDescending(LinkedHashMap<String, Integer> map) {
        // Convert the map's entries to a list
        List<Map.Entry<String, Integer>> list = new LinkedList<>(map.entrySet());

        // Sort the list by value in descending order
        Collections.sort((java.util.List<Map.Entry<String, Integer>>) list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Create a new LinkedHashMap to store the sorted entries
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static String cleanPath(String fullPath) {
        // Define the keyword that marks the beginning of the subpath
        String subPathKeyword = "data\\";

        // Find the index of the subpath keyword
        int startIndex = fullPath.indexOf(subPathKeyword);

        // If the keyword is found, extract the subpath
        if (startIndex != -1) {
            return fullPath.substring(startIndex);
        } else {
            // If the keyword is not found, return the original path or handle accordingly
            return fullPath;
        }
    }

    public static String convertDaysToString(int days) {
        if (days <= 1) {
            return days + " day";
        }
        return days + " days";
    }

    public static String getType(FleetMemberAPI ship) {
        if (ship.isPhaseShip()) {
            return "Phase";
        }
        if (ship.isCivilian()) {
            return "Civilian";
        }
        if (ship.isCarrier()) {
            return "Carrier";
        }
        return "Warship";
    }

    public static int levenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }

        return dp[a.length()][b.length()];
    }

    public static String getType(ShipHullSpecAPI ship) {
        if (ship.isPhase()) {
            return "Phase";
        }
        ShipVariantAPI spec = Global.getSettings().getVariant(getVaraint(ship));
        if (spec.isCivilian()) {
            return "Civilian";
        }
        if (spec.isCarrier()) {
            return "Carrier";
        }
        return "Warship";
    }

    public static String getType(FighterWingSpecAPI wing) {
        WingRole role = wing.getRole();
        if (role == WingRole.BOMBER) {
            return "Bomber";
        }
        if (role == WingRole.FIGHTER) {
            return "Fighter";
        }
        if (role == WingRole.INTERCEPTOR) {
            return "Interceptor";
        }
        return "Other";


    }

    public static String getStringFromStatFloat(float base, float modified) {
        float diff = base - modified;
        if (diff == 0) {
            return null;
        }
        if (diff < 0) {
            return "(+" + String.format("%.1f", Math.abs(diff)) + ")";
        } else {
            return "(-" + String.format("%.1f", Math.abs(diff)) + ")";
        }
    }

    public static Color getColorForSecondText(float base, float modified, boolean isIncreaseBad) {
        int diff = (int) base - (int) modified;
        if (diff == 0) {
            return Misc.getTooltipTitleAndLightHighlightColor();
        }
        if (!isIncreaseBad) {
            if (diff < 0) {
                return Misc.getPositiveHighlightColor();
            } else {
                return Misc.getNegativeHighlightColor();
            }
        } else {
            if (diff < 0) {
                return Misc.getNegativeHighlightColor();
            } else {
                return Misc.getPositiveHighlightColor();
            }
        }

    }

    public static String getWeaponTurretBarrelSpriteName(String id) {
        String spriteName = "";
        try {
            JSONObject jsonData = Global.getSettings().getMergedJSON("data/weapons/" + id + ".wpn");
            spriteName = jsonData.optString("turretGunSprite", "");
        } catch (IOException | JSONException ex) {

        }
        return spriteName;
    }

    public static String getNumberString(float number) {
        int numberButInt = (int) number;
        if ((float) numberButInt == number) {
            return "" + numberButInt;
        }
        return String.format("%.1f", number);
    }
}
