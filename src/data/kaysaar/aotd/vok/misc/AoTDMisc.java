package data.kaysaar.aotd.vok.misc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WingRole;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDSubmarkets;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDAIStance;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

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
    public static String ensureManBeingNotNull(String man){
        if(man ==null)return "Unknown";
        return man;
    }
    public String getNumberStringShort(int number){
        if(number<1000){
            return ""+number;
        }
        if(number<1000000){
            return ""+(number/1000)+"k";
        }
        return ""+(number/1000000)+"m";
    }
    public static ArrayList<String> loadEntries(String rawMap,String seperator) {
        if(!AoTDMisc.isStringValid(rawMap)){
            return  new ArrayList<>();
        }
        String[]splitted = rawMap.split(seperator);
        ArrayList<String> map = new ArrayList<>(Arrays.asList(splitted));

        return map;
    }
    public static AoTDAIStance getStanceFromString(String stance) {
        if (stance == null || stance.isEmpty()) {
            return AoTDAIStance.DEFAULT;
        }

        try {
            return AoTDAIStance.valueOf(stance.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle the case where the string does not match any enum value
            return AoTDAIStance.DEFAULT;
        }
    }
    public static boolean arrayContains(ArrayList<String> array,String key){
        for (String s : array) {
            if(s.equals(key))return true;
        }
        return false;
    }
    public static  ButtonAPI tryToGetButtonProd(String name) {
        ButtonAPI button = null;
        try {
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy((UIPanelAPI) ProductionUtil.getCurrentTab())) {
                if(componentAPI instanceof  ButtonAPI){
                    if(((ButtonAPI) componentAPI).getText().toLowerCase().contains(name)){
                        button = (ButtonAPI) componentAPI;
                        break;
                    }
                }
            }
            return button;
        }
        catch (Exception e) {

        }
        return button;

    }
    public static EngagementResultForFleetAPI getNonPlayerFleet(EngagementResultAPI resultAPI){
        if(!resultAPI.getLoserResult().isPlayer()){
            return resultAPI.getLoserResult();
        }
        return resultAPI.getWinnerResult();
    }
    public static boolean isPLayerHavingHeavyIndustry(){
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if(industry instanceof HeavyIndustry)return true;
            }
        }
        return false;
    }
    public static boolean isHavingAdvancedHeavyIndustry(){
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if(industry.getSpec().hasTag("advanced_heavy_industry"))return true;
            }
        }
        return false;
    }
    public static float retrieveAmountOfItems(String id,String submarketID) {
        float numberRemaining = 0;
        for (MarketAPI marketAPI : Misc.getPlayerMarkets(true)) {
            SubmarketAPI subMarket = marketAPI.getSubmarket(submarketID);
            if (Global.getSettings().getCommoditySpec(id) != null) {
                if (subMarket != null) {
                    numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, id);
                    continue;
                }
            }
            if (Global.getSettings().getSpecialItemSpec(id) != null) {
                if (subMarket != null) {
                    numberRemaining += subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(id, null));
                    continue;
                }
            }
            if (Global.getSettings().getHullSpec(id) != null) {
                if (subMarket != null) {
                    int sameHull = 0;
                    for (FleetMemberAPI o : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                        if(o.getHullSpec().getHullId().equals(id)){
                            sameHull++;
                        }
                    }
                    numberRemaining+=sameHull;
                }
            }

        }

        return numberRemaining;
    }
    public static void eatItems(Map.Entry<String, Integer> entry,String submarketId,List<MarketAPI>affectedMarkets) {
        float numberRemaining = entry.getValue();
        for (MarketAPI marketAPI : affectedMarkets) {

            SubmarketAPI subMarket = marketAPI.getSubmarket(submarketId);
            if (subMarket != null) {
                if (Global.getSettings().getCommoditySpec(entry.getKey()) != null) {
                    float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, entry.getKey());
                    if (numberRemaining >= onMarket) {
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, entry.getKey(), onMarket);
                    } else {
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, entry.getKey(), numberRemaining);
                    }
                    numberRemaining -= onMarket;

                }
                if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {

                    float onMarket = subMarket.getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(entry.getKey(), null));
                    if (numberRemaining >= onMarket) {
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(entry.getKey(), null), onMarket);
                    } else {
                        subMarket.getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(entry.getKey(), null), numberRemaining);
                    }
                    numberRemaining -= onMarket;

                }
                try {
                    if (Global.getSettings().getHullSpec(entry.getKey()) != null) {
                        ArrayList<FleetMemberAPI>toRemove = new ArrayList<>();
                        for (FleetMemberAPI fleetMemberAPI : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                            if(fleetMemberAPI.getHullSpec().getHullId().equals(entry.getKey())){
                                toRemove.add(fleetMemberAPI);
                            }
                        }
                        for (FleetMemberAPI fleetMemberAPI : toRemove) {
                            subMarket.getCargo().getMothballedShips().removeFleetMember(fleetMemberAPI);
                            numberRemaining--;
                            if(numberRemaining==0)break;
                        }

                    }
                }
                catch (Exception e){

                }

            }
            if (numberRemaining <= 0) {
                break;
            }
        }
    }
    public static boolean isHoveringOverButton(UIComponentAPI button){
        float x = Global.getSettings().getMouseX();
        float y = Global.getSettings().getMouseY();
        float xBut = button.getPosition().getX();
        float yBut = button.getPosition().getY();
        float width  = button.getPosition().getWidth();
        float height = button.getPosition().getHeight();

        return !(x < xBut) && !(x > xBut + width) && !(y < yBut) && !(y > yBut + height);
    }
    public static boolean isHoveringOverButton(UIComponentAPI button,float tooltipCorrection){
        float x = Global.getSettings().getMouseX();
        float y = Global.getSettings().getMouseY();
        float xBut = button.getPosition().getX();
        float yBut = button.getPosition().getY()+tooltipCorrection;
        float width  = button.getPosition().getWidth();
        float height = button.getPosition().getHeight();

        return !(x < xBut) && !(x > xBut + width) && !(y < yBut) && !(y > yBut + height);
    }
    public static boolean isStringValid(String str){
        return str!=null&&!str.isEmpty();
    }
    public static void startStencil(CustomPanelAPI panel,float scale) {
        GL11.glClearStencil(0);
        GL11.glStencilMask(0xff);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glColorMask(false, false, false, false);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
        GL11.glStencilMask(0xff);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        GL11.glBegin(GL11.GL_POLYGON);
        PositionAPI position = panel.getPosition();
        float x = position.getX();
        float y = position.getY();
        float width = position.getWidth();
        float height = position.getHeight();

        // Define the rectangle
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height*scale);
        GL11.glVertex2f(x, y + height*scale);
        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }
    public static void startStencil(float x ,float y , float width , float height) {
        GL11.glClearStencil(0);
        GL11.glStencilMask(0xff);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        GL11.glColorMask(false, false, false, false);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff);
        GL11.glStencilMask(0xff);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

        GL11.glBegin(GL11.GL_POLYGON);

        // Define the rectangle
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }
    public static void endStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
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
    public static boolean knowsItem(String id, FactionAPI faction){
        return faction.getMemory().is("$aotd"+id,true)||AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).haveResearched(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
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
