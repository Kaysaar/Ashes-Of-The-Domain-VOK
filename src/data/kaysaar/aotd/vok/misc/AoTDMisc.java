package data.kaysaar.aotd.vok.misc;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.BasePopUpDialog;
import ashlib.data.plugins.ui.models.InstantPopUpUI;
import ashlib.data.plugins.ui.models.PopUpUI;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.econ.impl.HeavyIndustry;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.loading.WingRole;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;

import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.scripts.research.AoTDAIStance;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchRewardType;
import kaysaar.aotd_question_of_loyalty.data.misc.QoLMisc;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.GPManager.commodities;

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
    public static String getPercentageString(float percentage){
        return Misc.getRoundedValueMaxOneAfterDecimal(percentage*100f)+"%";
    }
    public static List<MarketAPI>getPlayerFactionMarkets(){
        return Misc.getPlayerMarkets(true).stream().filter(x -> !x.hasTag("nex_playerOutpost")).toList();
    }
    public static NidavelirComplexMegastructure getNidavelirIfOwned(){
        return (NidavelirComplexMegastructure) GPManager.getInstance().getMegastructure("aotd_nidavelir");
    }
    public static NidavelirComplexMegastructure getNidavelir(){
       StarSystemAPI system = Global.getSector().getStarSystem(Global.getSector().getPlayerMemoryWithoutUpdate().getString("$aotd_mega_system_id_aotd_nidavelir"));
       PlanetAPI planet  = system.getPlanets().stream().filter(x->x.hasCondition("aotd_nidavelir_complex")).findFirst().orElse(null);

       if(planet == null) return null;
       return (NidavelirComplexMegastructure) planet.getMemoryWithoutUpdate().get(GPBaseMegastructure.memKey);
    }
    public static List<MarketAPI> retrieveFactionMarkets(FactionAPI faction) {
        ArrayList<MarketAPI> marketsToReturn = new ArrayList<>();
        if (faction.isPlayerFaction()) {
            return Misc.getPlayerMarkets(checkForQolEnabled());
        }
        for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
            if (marketAPI.getFactionId().equals(faction.getId())) {
                marketsToReturn.add(marketAPI);
            }

        }

        return marketsToReturn;
    }

    public static boolean doesMarketBelongToFaction(FactionAPI faction, MarketAPI marketAPI) {
        if (marketAPI.getFaction() == null) return false;
        if (checkForQolEnabled() && faction.isPlayerFaction()) {
            return marketAPI.getFaction().getId().equals(faction.getId()) || marketAPI.isPlayerOwned();
        }
        return marketAPI.getFaction().getId().equals(faction.getId());
    }

    public static boolean checkForQolEnabled() {
        if (Global.getSettings().getModManager().isModEnabled("aotd_qol")) {
            if (QoLMisc.isCommissioned()) {
                return true;
            }
        }
        return false;
    }

    public static CustomPanelAPI createTooltipOfResourcesForDialog(float width, float height, float iconSize, HashMap<String, Integer> costs, boolean isForSalvage) {
        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = customPanel.createUIElement(width, height, false);
        float totalSize = width;
        float positions = totalSize / (commodities.size() * 4);
        float iconsize = iconSize;
        float topYImage = 0;
        LabelAPI test = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);


        float x = positions;
        ArrayList<CustomPanelAPI> panelsWithImage = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : costs.entrySet()) {
            float widthTempPanel = iconsize;
            int number = entry.getValue();
            int owned = (int) AoTDMisc.retrieveAmountOfItemsFromPlayer(entry.getKey());
            String icon = null;
            if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {
                icon = Global.getSettings().getSpecialItemSpec(entry.getKey()).getIconName();
            } else {
                icon = Global.getSettings().getCommoditySpec(entry.getKey()).getIconName();
            }

            String text = "" + number;
            String text2 = "(" + owned + ")";
            if (isForSalvage) {
                text2 = "";
            }
            widthTempPanel += test.computeTextWidth(text + text2);
            CustomPanelAPI panelTemp = Global.getSettings().createCustom(widthTempPanel + iconSize + 5, iconSize, null);
            TooltipMakerAPI tooltipMakerAPI = panelTemp.createUIElement(widthTempPanel + iconSize + 5, iconSize, false);
            tooltipMakerAPI.addImage(icon, iconsize, iconsize, 0f);
            UIComponentAPI image = tooltipMakerAPI.getPrev();
            image.getPosition().inTL(x, topYImage);

            Color col = Misc.getTooltipTitleAndLightHighlightColor();
            if (number > owned && !isForSalvage) {
                col = Misc.getNegativeHighlightColor();
            }

            tooltipMakerAPI.addPara("%s %s", 0f, col, col, text, text2).getPosition().inTL(x + iconsize + 5, (topYImage + (iconsize / 2)) - (test.computeTextHeight(text2) / 3));
            panelTemp.addUIElement(tooltipMakerAPI).inTL(0, 0);
            panelsWithImage.add(panelTemp);
        }


        float totalWidth = 0f;
        float secondRowWidth = 0f;
        float left;
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            totalWidth += panelAPI.getPosition().getWidth() + 15;
        }
        left = totalWidth;
        ArrayList<CustomPanelAPI> panelsSecondRow = new ArrayList<>();
        if (totalWidth >= width) {
            for (int i = panelsWithImage.size() - 1; i >= 0; i--) {
                left -= panelsWithImage.get(i).getPosition().getWidth() - 15;
                panelsSecondRow.add(panelsWithImage.get(i));
                if (left < width) {
                    break;
                }
                panelsWithImage.remove(i);
            }
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            secondRowWidth += panelAPI.getPosition().getWidth() + 15;
        }
        float startingXFirstRow = 0;
        float startingXSecondRow = 0;
        if (!panelsSecondRow.isEmpty()) {
            tooltip.getPosition().setSize(width, height * 2 + 5);
            customPanel.getPosition().setSize(width, height * 2 + 5);
        }
        for (CustomPanelAPI panelAPI : panelsWithImage) {
            tooltip.addCustom(panelAPI, 0f).getPosition().inTL(startingXFirstRow, 0);
            startingXFirstRow += panelAPI.getPosition().getWidth() + 5;
        }
        for (CustomPanelAPI panelAPI : panelsSecondRow) {
            tooltip.addCustom(panelAPI, 0f).getPosition().inTL(startingXSecondRow, iconSize + 5);
            startingXSecondRow += panelAPI.getPosition().getWidth() + 5;
        }

        customPanel.addUIElement(tooltip).inTL(0, 0);
        return customPanel;
    }

    public static Object getOrDefault(Map map, Object key, Object defaultValue) {
        if (map.get(key) == null) {
            return defaultValue;
        }
        return map.get(key);
    }

    public static void putCommoditiesIntoMap(HashMap<String, Integer> map, String commodity, int val) {
        if (map.get(commodity) == null) {
            map.put(commodity, val);
        } else {
            map.compute(commodity, (k, prev) -> val + prev);
        }
    }

    public static boolean hasRewardOfType(ResearchRewardType type, ResearchOption option) {
        for (Map.Entry<String, ResearchRewardType> o : option.Rewards.entrySet()) {
            if (o.getValue().equals(type)) return true;
        }
        return false;
    }
    public static void initPopUpDialog(BasePopUpDialog dialog, float width, float height){
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width, height, dialog);
        UIPanelAPI panelAPI1 = ProductionUtil.getCoreUI();
        dialog.init(panelAPI, panelAPI1.getPosition().getCenterX() - (panelAPI.getPosition().getWidth() / 2), panelAPI1.getPosition().getCenterY() - (panelAPI.getPosition().getHeight() / 2), true);
    }
    public static boolean doesPlayerHaveTuringEngine() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if (industry.getSpecialItem() != null) {
                    if (industry.getSpecialItem().getId().equals("turing_engine")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String ensureManBeingNotNull(String man) {
        if (man == null) return "Unknown";
        return man;
    }

    public String getNumberStringShort(int number) {
        if (number < 1000) {
            return "" + number;
        }
        if (number < 1000000) {
            return "" + (number / 1000) + "k";
        }
        return "" + (number / 1000000) + "m";
    }

    public static ArrayList<String> loadEntries(String rawMap, String seperator) {
        if (!AoTDMisc.isStringValid(rawMap)) {
            return new ArrayList<>();
        }
        String[] splitted = rawMap.split(seperator);
        ArrayList<String> map = new ArrayList<>(Arrays.asList(splitted));

        return map;
    }

    public static LinkedHashMap<String, Integer> loadCostMap(String rawMap) {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        for (String s : loadEntries(rawMap, ",")) {
            String[] extracted = s.split(":");
            map.put(extracted[0], Integer.valueOf(extracted[1]));
        }
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

    public static boolean arrayContains(ArrayList<String> array, String key) {
        for (String s : array) {
            if (s.equals(key)) return true;
        }
        return false;
    }

    public static void placePopUpUI(PopUpUI ui, ButtonAPI button, float initWidth, float initHeight) {

        float width1 = initWidth;
        float height1 = ui.createUIMockup(Global.getSettings().createCustom(initWidth, initHeight, null));
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, ui);

        float x = button.getPosition().getX() + button.getPosition().getWidth();
        float y = button.getPosition().getY() + button.getPosition().getHeight();
        if (x + width1 >= Global.getSettings().getScreenWidth()) {
            float diff = x + width1 - Global.getSettings().getScreenWidth();
            x = x - diff - 5;

        }
        if (y - height1 <= 0) {
            y = height1;
        }
        if (y > Global.getSettings().getScreenHeight()) {
            y = Global.getSettings().getScreenHeight() - 10;
        }

        ui.init(panelAPI, x, y, false);
    }

    public static void placePopUpUI(PopUpUI ui, UIComponentAPI button, float initWidth, float initHeight) {

        float width1 = initWidth;
        float height1 = ui.createUIMockup(Global.getSettings().createCustom(initWidth, initHeight, null));
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, ui);

        float x = button.getPosition().getX() + button.getPosition().getWidth();
        float y = button.getPosition().getY() + button.getPosition().getHeight();
        if (x + width1 >= Global.getSettings().getScreenWidth()) {
            float diff = x + width1 - Global.getSettings().getScreenWidth();
            x = x - diff - 5;

        }
        if (y - height1 <= 0) {
            y = height1;
        }
        if (y > Global.getSettings().getScreenHeight()) {
            y = Global.getSettings().getScreenHeight() - 10;
        }

        ui.init(panelAPI, x, y, false);
    }
    public static void placePopUpUIUnder(PopUpUI ui, UIComponentAPI button, float initWidth, float initHeight) {

        float width1 = initWidth;
        float height1 = ui.createUIMockup(Global.getSettings().createCustom(initWidth, initHeight, null));
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, ui);

        float x = button.getPosition().getX();
        float y = Global.getSettings().getScreenHeight()-button.getPosition().getY();
        if (x + width1 >= Global.getSettings().getScreenWidth()) {
            float diff = x + width1 - Global.getSettings().getScreenWidth();
            x = x - diff - 5;

        }
        if (y - height1 <= 0) {
            y = height1;
        }
        if (y > Global.getSettings().getScreenHeight()) {
            y = Global.getSettings().getScreenHeight() - 10;
        }

        ui.init(panelAPI, x, y, false);
    }
    public static void placePopUpUIUnder(InstantPopUpUI ui, UIComponentAPI button, float initWidth, float initHeight) {

        float width1 = initWidth;
        float height1 = ui.createUIMockup(Global.getSettings().createCustom(initWidth, initHeight, null));
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, ui);

        float x = button.getPosition().getX();
        float y = button.getPosition().getY();
        if (x + width1 >= Global.getSettings().getScreenWidth()) {
            float diff = x + width1 - Global.getSettings().getScreenWidth();
            x = x - diff - 5;

        }
        if (y - height1 <= 0) {
            y = height1;
        }
        if (y > Global.getSettings().getScreenHeight()) {
            y = Global.getSettings().getScreenHeight() - 10;
        }

        ui.init(panelAPI, x, y, false);
    }
    public static void placePopUpUIUnder(InstantPopUpUI ui, UIComponentAPI button, float initWidth, float initHeight) {

        float width1 = initWidth;
        float height1 = ui.createUIMockup(Global.getSettings().createCustom(initWidth, initHeight, null));
        CustomPanelAPI panelAPI = Global.getSettings().createCustom(width1, height1, ui);

        float x = button.getPosition().getX();
        float y = button.getPosition().getY();
        if (x + width1 >= Global.getSettings().getScreenWidth()) {
            float diff = x + width1 - Global.getSettings().getScreenWidth();
            x = x - diff - 5;

        }
        if (y - height1 <= 0) {
            y = height1;
        }
        if (y > Global.getSettings().getScreenHeight()) {
            y = Global.getSettings().getScreenHeight() - 10;
        }

        ui.init(panelAPI, x, y, false);
    }

    public static ButtonAPI tryToGetButtonProd(String name) {
        ButtonAPI button = null;
        try {
            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy((UIPanelAPI) ProductionUtil.getCurrentTab())) {
                if (componentAPI instanceof ButtonAPI) {
                    if (((ButtonAPI) componentAPI).getText().toLowerCase().contains(name)) {
                        button = (ButtonAPI) componentAPI;
                        break;
                    }
                }
            }
            return button;
        } catch (Exception e) {

        }
        return button;

    }

    public static EngagementResultForFleetAPI getNonPlayerFleet(EngagementResultAPI resultAPI) {
        if (!resultAPI.getLoserResult().isPlayer()) {
            return resultAPI.getLoserResult();
        }
        return resultAPI.getWinnerResult();
    }

    public static boolean isPLayerHavingHeavyIndustry() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if (industry instanceof HeavyIndustry || industry.getSpec().hasTag("heavyindustry")) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isPLayerHavingIndustry(String id) {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if(industry.getSpec().getId().equals(id)){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isHavingAdvancedHeavyIndustry() {
        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            for (Industry industry : playerMarket.getIndustries()) {
                if (industry.getSpec().hasTag("advanced_heavy_industry")) return true;
            }
        }
        return false;
    }

    public static float retrieveAmountOfItems(String id, String submarketID) {
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
            try {
                if (Global.getSettings().getHullSpec(id) != null) {
                    if (subMarket != null) {
                        int sameHull = 0;
                        for (FleetMemberAPI o : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                            if (o.getHullSpec().getHullId().equals(id)) {
                                sameHull++;
                            }
                        }
                        numberRemaining += sameHull;
                    }
                }
            } catch (Exception e) {
                //ignore the fuck out
            }


        }

        return numberRemaining;
    }

    public static float retrieveAmountOfItemsFromPlayer(String id) {
        float numberRemaining = 0;
        if (Global.getSettings().getCommoditySpec(id) != null) {
            numberRemaining += Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.RESOURCES, id);
        }
        if (Global.getSettings().getSpecialItemSpec(id) != null) {
            numberRemaining += Global.getSector().getPlayerFleet().getCargo().getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(id, null));
        }


        return numberRemaining;
    }

    public static float eatPlayerItems(String id, int amount) {
        float numberRemaining = 0;
        if (Global.getSettings().getCommoditySpec(id) != null) {
            Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.RESOURCES, id, amount);
        }
        if (Global.getSettings().getSpecialItemSpec(id) != null) {
            Global.getSector().getPlayerFleet().getCargo().removeItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(id, null), amount);
        }


        return numberRemaining;
    }
    public static void processItemDifferences(HashMap<String, Integer> itemDifferences, String submarketId, MarketAPI fallbackMarket, List<MarketAPI> affectedMarkets) {
        for (Map.Entry<String, Integer> entry : itemDifferences.entrySet()) {
            int amount = entry.getValue();
            if (amount > 0) {
                // Add to fallback market (e.g., player gathering point)
                eatItems(Map.entry(entry.getKey(), amount), submarketId, affectedMarkets);
            } else if (amount < 0) {
                // Remove from the list of affected markets
                addItems(Map.entry(entry.getKey(), -amount), submarketId, fallbackMarket);
            }
        }
    }

    public static void eatItems(Map.Entry<String, Integer> entry, String submarketId, List<MarketAPI> affectedMarkets) {
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
                        ArrayList<FleetMemberAPI> toRemove = new ArrayList<>();
                        for (FleetMemberAPI fleetMemberAPI : subMarket.getCargo().getMothballedShips().getMembersListCopy()) {
                            if (fleetMemberAPI.getHullSpec().getHullId().equals(entry.getKey())) {
                                toRemove.add(fleetMemberAPI);
                            }
                        }
                        for (FleetMemberAPI fleetMemberAPI : toRemove) {
                            subMarket.getCargo().getMothballedShips().removeFleetMember(fleetMemberAPI);
                            numberRemaining--;
                            if (numberRemaining == 0) break;
                        }

                    }
                } catch (Exception e) {

                }

            }
            if (numberRemaining <= 0) {
                break;
            }
        }
    }
    public static void addItems(Map.Entry<String, Integer> entry, String submarketId, MarketAPI marketAPI) {

            SubmarketAPI subMarket = marketAPI.getSubmarket(submarketId);
            if (subMarket != null) {
                if (Global.getSettings().getCommoditySpec(entry.getKey()) != null) {
                    subMarket.getCargo().addItems(CargoAPI.CargoItemType.RESOURCES, entry.getKey(), entry.getValue());


                }
                if (Global.getSettings().getSpecialItemSpec(entry.getKey()) != null) {
                    subMarket.getCargo().addItems(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(entry.getKey(), null), entry.getValue());

                }
                try {
                    if (Global.getSettings().getHullSpec(entry.getKey()) != null) {
                        for (int i = 0; i < entry.getValue(); i++) {
                            FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP,getVaraint(Global.getSettings().getHullSpec(entry.getKey())));
                            member.getVariant().setSource(VariantSource.REFIT);
                            member.getVariant().clear();
                            subMarket.getCargo().getMothballedShips().addFleetMember(member);
                        }

                    }
                } catch (Exception e) {

                }

            }


    }

    public static boolean isHoveringOverButton(UIComponentAPI button) {
        float x = Global.getSettings().getMouseX();
        float y = Global.getSettings().getMouseY();
        float xBut = button.getPosition().getX();
        float yBut = button.getPosition().getY();
        float width = button.getPosition().getWidth();
        float height = button.getPosition().getHeight();

        return !(x < xBut) && !(x > xBut + width) && !(y < yBut) && !(y > yBut + height);
    }

    public static boolean isHoveringOverButton(UIComponentAPI button, float tooltipCorrection) {
        float x = Global.getSettings().getMouseX();
        float y = Global.getSettings().getMouseY();
        float xBut = button.getPosition().getX();
        float yBut = button.getPosition().getY() + tooltipCorrection;
        float width = button.getPosition().getWidth();
        float height = button.getPosition().getHeight();

        return !(x < xBut) && !(x > xBut + width) && !(y < yBut) && !(y > yBut + height);
    }

    public static boolean isStringValid(String str) {
        return str != null && !str.isEmpty();
    }

    public static void startStencil(CustomPanelAPI panel, float scale) {
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
        GL11.glVertex2f(x + 1, y + 1);
        GL11.glVertex2f(x + width - 1, y + 1);
        GL11.glVertex2f(x + width - 1, y + height * scale - 1);
        GL11.glVertex2f(x + 1, y + height * scale - 1);
        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }

    public static void startStencilWithYPad(CustomPanelAPI panel, float yPad) {
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
        float x = position.getX() - 5;
        float y = position.getY();
        float width = position.getWidth() + 10;
        float height = position.getHeight();

        // Define the rectangle
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height - yPad);
        GL11.glVertex2f(x, y + height - yPad);
        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }

    public static void startStencilWithXPad(CustomPanelAPI panel, float xPad) {
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
        float x = position.getX() - 5;
        float y = position.getY() - 10;
        float width = position.getWidth() + 10;
        float height = position.getHeight() + 20;

        // Define the rectangle
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width - xPad, y);
        GL11.glVertex2f(x + width - xPad, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glColorMask(true, true, true, true);

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF);
    }

    public static void startStencil(float x, float y, float width, float height) {
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

    public static boolean knowsItem(String id, FactionAPI faction) {
        if(Global.getSettings().getSpecialItemSpec(id)!=null){
            SpecialItemSpecAPI spec = Global.getSettings().getSpecialItemSpec(id);
            if(spec.hasTag("aotd_ignore_standarization")){
                return faction.getMemory().is("$aotd" + id, true);
            }
        }
        return faction.getMemory().is("$aotd" + id, true) || AoTDMainResearchManager.getInstance().getSpecificFactionManager(faction).haveResearched(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION);
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

    public static ArrayList<JSONObject> getObjectListFromArray(JSONArray array) throws JSONException {
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getJSONObject(i));
        }
        return list;
    }

    public static ArrayList<StarSystemAPI> getStarSystemWithMegastructure(String id) {
        ArrayList<StarSystemAPI>starSystemAPIS = new ArrayList<>();
        for (StarSystemAPI starSystem : Global.getSector().getStarSystems()) {
            if(starSystem.getAllEntities().stream().filter(x-> AshMisc.isStringValid(x.getCustomEntityType())).anyMatch(x->x.getCustomEntityType().equals(id))){
                starSystemAPIS.add(starSystem);
            }
        }
        return starSystemAPIS;
    }

    public static boolean isSubImageExisting(String imageName) {
        try {
            Global.getSettings().getSpriteName("ui_icons_tech_tree", imageName + "_sub");
        } catch (RuntimeException exception) {
            return false;
        }
        return true;

    }

    public static String getTechImage(String imageName) {
        try {

            return Global.getSettings().getSpriteName("ui_icons_tech_tree", imageName);
        } catch (RuntimeException exception) {
            Global.getSettings().getSpriteName("ui_icons_tech_tree", "generic");

        }
        return Global.getSettings().getSpriteName("ui_icons_tech_tree", "generic");
    }

    public static String getImagePathForTechIcon(String id) {
        String imagename;
        if (isSubImageExisting(id)) {
            imagename = Global.getSettings().getSpriteName("ui_icons_tech_tree", id + "_sub");
        } else {
            imagename = Global.getSettings().getSpriteName("ui_icons_tech_tree", "special");
        }
        return imagename;
    }

}
