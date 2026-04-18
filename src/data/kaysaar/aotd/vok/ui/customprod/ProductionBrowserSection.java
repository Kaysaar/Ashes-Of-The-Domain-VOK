package data.kaysaar.aotd.vok.ui.customprod;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import ashlib.data.plugins.ui.plugins.UITableImpl;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.AoTDProductionUIData;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.ui.customprod.common.*;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductionBrowserSection implements ExtendedUIPanelPlugin {
    TextFieldAPI textField;
    ProductionOptionList list;
    ChooseManufacturerPanel chooseManufacturerPanel;
    ChooseSizePanel chooseSizePanel;
    ChooseTypeInfo chooseTypeInfo;
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    AoTDProductionSpec.AoTDProductionSpecType prodType;
    ButtonAPI name, time, size, type, design, totalCost;
    String prevText = "";
    ProductionMainPanel parent;
    public static final LinkedHashMap<String, Float> FULL_WIDTHS = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Float> FIGHTER_WIDTHS = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Float> ITEM_WIDTHS = new LinkedHashMap<>();

    static {
        // Ships / Weapons
        FULL_WIDTHS.put("name", 0.28f);
        FULL_WIDTHS.put("time", 0.10f);
        FULL_WIDTHS.put("size", 0.10f);
        FULL_WIDTHS.put("type", 0.10f);
        FULL_WIDTHS.put("design", 0.25f);
        FULL_WIDTHS.put("totalCost", 0.17f);

        // Fighters
        FIGHTER_WIDTHS.put("name", 0.34f);
        FIGHTER_WIDTHS.put("time", 0.10f);
        FIGHTER_WIDTHS.put("type", 0.14f);
        FIGHTER_WIDTHS.put("design", 0.24f);
        FIGHTER_WIDTHS.put("totalCost", 0.18f);

        // Items
        ITEM_WIDTHS.put("name", 0.42f);
        ITEM_WIDTHS.put("time", 0.12f);
        ITEM_WIDTHS.put("design", 0.26f);
        ITEM_WIDTHS.put("totalCost", 0.20f);
    }

    public ProductionBrowserSection(float width, float height, AoTDProductionSpec.AoTDProductionSpecType prodType,ProductionMainPanel parent) {
        this.prodType = prodType;
        mainPanel = Global.getSettings().createCustom(width, height, this);
        this.parent = parent;
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    private static LinkedHashMap<String, Float> getWidthsForType(AoTDProductionSpec.AoTDProductionSpecType prodType) {
        switch (prodType) {
            case SHIP:
            case WEAPON:
                return FULL_WIDTHS;
            case FIGHTER:
                return FIGHTER_WIDTHS;
            case SPECIAL_ITEM:
            case COMMODITY_ITEM:
                return ITEM_WIDTHS;
            default:
                return FULL_WIDTHS;
        }
    }

    private List<String> getColumnsForType() {
        ArrayList<String> cols = new ArrayList<>();

        switch (prodType) {
            case SHIP:
            case WEAPON:
                cols.add("name");
                cols.add("time");
                cols.add("size");
                cols.add("type");
                cols.add("design");
                cols.add("totalCost");
                break;

            case FIGHTER:
                cols.add("name");
                cols.add("time");
                cols.add("type");
                cols.add("design");
                cols.add("totalCost");
                break;

            case SPECIAL_ITEM:
            case COMMODITY_ITEM:
                cols.add("name");
                cols.add("time");
                cols.add("design");
                cols.add("totalCost");
                break;

            default:
                cols.add("name");
                cols.add("time");
                cols.add("design");
                cols.add("totalCost");
                break;
        }

        return cols;
    }

    private String getButtonLabel(String key) {
        switch (key) {
            case "name":
                return "Name";
            case "time":
                return "Time";
            case "size":
                return "Size";
            case "type":
                return "Type";
            case "design":
                return "Design type";
            case "totalCost":
                return "Cost";
            default:
                return key;
        }
    }

    /**
     * Distributes integer widths so that:
     * sum(all widths) == effective width exactly.
     */
    private static LinkedHashMap<String, Integer> getDistributedColumnWidths(
            float totalPanelWidth,
            int gap,
            AoTDProductionSpec.AoTDProductionSpecType prodType
    ) {
        LinkedHashMap<String, Float> ratios = getWidthsForType(prodType);
        LinkedHashMap<String, Integer> result = new LinkedHashMap<>();
        LinkedHashMap<String, Float> fractionalParts = new LinkedHashMap<>();

        int gaps = Math.max(0, ratios.size() - 1);
        int effectiveWidth = Math.round(totalPanelWidth - (gaps * gap));

        int used = 0;
        for (Map.Entry<String, Float> entry : ratios.entrySet()) {
            String key = entry.getKey();
            float exact = entry.getValue() * effectiveWidth;

            int base = (int) Math.floor(exact);
            result.put(key, base);
            fractionalParts.put(key, exact - base);
            used += base;
        }

        int remainder = effectiveWidth - used;

        while (remainder > 0) {
            String bestKey = null;
            float bestFraction = -1f;

            for (Map.Entry<String, Float> entry : fractionalParts.entrySet()) {
                if (entry.getValue() > bestFraction) {
                    bestFraction = entry.getValue();
                    bestKey = entry.getKey();
                }
            }

            if (bestKey == null) break;

            result.put(bestKey, result.get(bestKey) + 1);
            fractionalParts.put(bestKey, 0f);
            remainder--;
        }

        return result;
    }

    public static int getColumnWidth(String id, float totalPanelWidth, int gap, AoTDProductionSpec.AoTDProductionSpecType prodType) {
        return getDistributedColumnWidths(totalPanelWidth, gap, prodType).getOrDefault(id, 0);
    }

    public static int getColumnStartX(String id, float totalPanelWidth, int gap, AoTDProductionSpec.AoTDProductionSpecType prodType) {
        LinkedHashMap<String, Integer> widths = getDistributedColumnWidths(totalPanelWidth, gap, prodType);

        int x = 0;
        for (String key : widths.keySet()) {
            if (id.equals(key)) {
                break;
            }
            x += widths.get(key) + gap;
        }
        return x;
    }

    private int getColumnEndX(String id, float totalPanelWidth, int gap, AoTDProductionSpec.AoTDProductionSpecType prodType) {
        return getColumnStartX(id, totalPanelWidth, gap, prodType)
                + getColumnWidth(id, totalPanelWidth, gap, prodType);
    }

    @Override
    public void createUI() {
        if (contentPanel != null) {
            mainPanel.removeComponent(contentPanel);
        }

        name = null;
        time = null;
        size = null;
        type = null;
        design = null;
        totalCost = null;

        contentPanel = Global.getSettings().createCustom(
                mainPanel.getPosition().getWidth(),
                mainPanel.getPosition().getHeight(),
                null
        );

        TooltipMakerAPI tl = contentPanel.createUIElement(250, 20, false);
        TooltipMakerAPI buttonTl = contentPanel.createUIElement(contentPanel.getPosition().getWidth(), 20, false);

        List<String> columns = getColumnsForType();

        int gap = 1;
        float usablePanelWidth = contentPanel.getPosition().getWidth() - 10f;
        LinkedHashMap<String, Integer> distributedWidths = getDistributedColumnWidths(usablePanelWidth, gap, prodType);

        ButtonAPI previous = null;

        for (String col : columns) {
            int buttonWidth = distributedWidths.getOrDefault(col, 0);

            ButtonAPI btn = buttonTl.addAreaCheckbox(
                    getButtonLabel(col),
                    UITableImpl.SortingState.NON_INITIALIZED,
                    Misc.getBasePlayerColor(),
                    Misc.getDarkPlayerColor(),
                    Misc.getBrightPlayerColor(),
                    buttonWidth,
                    20,
                    0f
            );

            if (previous == null) {
                btn.getPosition().inTL(0, 0);
            } else {
                btn.getPosition().rightOfMid(previous, gap);
            }

            if ("name".equals(col)) name = btn;
            else if ("time".equals(col)) time = btn;
            else if ("size".equals(col)) size = btn;
            else if ("type".equals(col)) type = btn;
            else if ("design".equals(col)) design = btn;
            else if ("totalCost".equals(col)) totalCost = btn;

            previous = btn;
        }

        if (totalCost != null) {
            totalCost.setCustomData(UITableImpl.SortingState.ASCENDING);
            totalCost.setChecked(true);
        }

        contentPanel.addUIElement(buttonTl).inTL(0, 26);

        textField = tl.addTextField(250, 20, Fonts.DEFAULT_SMALL, 0f);
        contentPanel.addUIElement(tl).inTL(contentPanel.getPosition().getWidth() - 265, 0);

        AoTDProductionUIData.populateByType(prodType);

        if (prodType != AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM) {
            chooseTypeInfo = new ChooseTypeInfo(
                    contentPanel.getPosition().getWidth(),
                    AoTDProductionUIData.getTypeInfoBasedOnType(prodType)
            );
            contentPanel.addComponent(chooseTypeInfo.getMainPanel())
                    .inTL(0, contentPanel.getPosition().getHeight() - 30 - 4 - chooseTypeInfo.getMainPanel().getPosition().getHeight());
        }

        if (prodType == AoTDProductionSpec.AoTDProductionSpecType.WEAPON ||
                prodType == AoTDProductionSpec.AoTDProductionSpecType.SHIP) {
            chooseSizePanel = new ChooseSizePanel(
                    contentPanel.getPosition().getWidth(),
                    AoTDProductionUIData.getSizeInfoBasedOnType(prodType)
            );
            contentPanel.addComponent(chooseSizePanel.getMainPanel())
                    .inTL(0, contentPanel.getPosition().getHeight() - chooseSizePanel.getMainPanel().getPosition().getHeight());
        }

        float remHeight = contentPanel.getPosition().getHeight() - 64 - tl.getPosition().getHeight() - 30 - 20;
        float heightOfManus = 180;
        float sectionsHeight = remHeight - heightOfManus;

        if (list == null) {
            list = new ProductionOptionList(
                    AoTDProductionSpecManager.getLearnedSpecsForFaction(prodType, Global.getSector().getPlayerFaction()),
                    contentPanel.getPosition().getWidth(),
                    sectionsHeight,
                    prodType
            );

            list.setFilter(new ProductionMenuFilterAPI() {
                @Override
                public void pruneList(ArrayList<ProductionCustomButton> buttons) {
                    String rawSearch = textField.getText();

                    if (AshMisc.isStringValid(rawSearch)) {
                        final String searchString = rawSearch.toLowerCase().trim();
                        final int threshold = 2;

                        buttons.removeIf(button -> {
                            String candidate = getSearchString(button);
                            if (candidate == null) return true;

                            String normalized = candidate.toLowerCase();
                            int distance = AoTDMisc.levenshteinDistance(searchString, normalized);

                            return distance > threshold && !normalized.contains(searchString);
                        });

                        buttons.sort((b1, b2) -> {
                            String s1 = getSearchString(b1);
                            String s2 = getSearchString(b2);

                            if (s1 == null) s1 = "";
                            if (s2 == null) s2 = "";

                            String s1S = s1.toLowerCase();
                            String s2S = s2.toLowerCase();

                            boolean s1Contains = s1S.contains(searchString);
                            boolean s2Contains = s2S.contains(searchString);

                            if (s1Contains && !s2Contains) return -1;
                            if (!s1Contains && s2Contains) return 1;

                            int distance1 = AoTDMisc.levenshteinDistance(searchString, s1S);
                            int distance2 = AoTDMisc.levenshteinDistance(searchString, s2S);

                            int cmp = Integer.compare(distance1, distance2);
                            if (cmp != 0) return cmp;

                            return s1S.compareTo(s2S);
                        });
                    } else {
                        buttons.removeIf(x -> {
                            AoTDProductionSpec spec = x.getSpec();

                            if (chooseManufacturerPanel != null && !chooseManufacturerPanel.isManufacturerChosen(spec.getManufacturer())) {
                                return true;
                            }
                            if (chooseTypeInfo != null && !chooseTypeInfo.isTypeChosen(spec.getTypeString())) {
                                return true;
                            }
                            if (chooseSizePanel != null && !chooseSizePanel.isSizeChosen(spec.getManufacturer())) {
                                return true;
                            }
                            return false;
                        });
                    }
                }

                private String getSearchString(ProductionCustomButton button) {
                    return button.getSpec().getName();
                }
            });

            if (totalCost != null) {
                handleSortButton(totalCost, Comparator.comparing(o -> o.getSpec().getMoneyPrice()));
            }
        }
        list.getButtonsStorage().forEach(x->x.setListener(new CustomButton.ButtonEventListener() {
            @Override
            public void onButtonClicked() {
                parent.orderList.addOrder(x.getSpec().getId(),x.getSpec());
                parent.swapPanels(false);
            }
        }));

        list.createUI();
        contentPanel.addComponent(list.getMainPanel()).inTL(-5, 50);

        chooseManufacturerPanel = new ChooseManufacturerPanel(
                contentPanel.getPosition().getWidth(),
                heightOfManus,
                AoTDProductionUIData.getManInfoBasedOnType(prodType)
        );
        contentPanel.addComponent(chooseManufacturerPanel.getMainPanel()).inTL(0, 55 + sectionsHeight + 5);

        mainPanel.addComponent(contentPanel).inTL(0, 0);
    }

    public void setProdType(AoTDProductionSpec.AoTDProductionSpecType prodType) {
        this.prodType = prodType;
        clearUI();
        createUI();
    }

    @Override
    public void clearUI() {
        if (chooseManufacturerPanel != null) {
            chooseManufacturerPanel.clearUI();
            chooseManufacturerPanel = null;
        }
        if (chooseTypeInfo != null) {
            chooseTypeInfo.clearUI();
            chooseTypeInfo = null;
        }
        if (chooseSizePanel != null) {
            chooseSizePanel.clearUI();
            chooseSizePanel = null;
        }
        if (list != null) {
            list.getButtonsStorage().forEach(x->x.setListener(null));
            list.clearUI();
            list = null;
        }
    }

    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
    }

    @Override
    public void render(float alphaMult) {
    }

    @Override
    public void advance(float amount) {
        if (chooseManufacturerPanel != null && chooseManufacturerPanel.isNeedsUpdate()) {
            chooseManufacturerPanel.setNeedsUpdate(false);
            list.createUI();
        }
        if (chooseSizePanel != null && chooseSizePanel.isNeedsUpdate()) {
            chooseSizePanel.setNeedsUpdate(false);
            list.createUI();
        }
        if (chooseTypeInfo != null && chooseTypeInfo.isNeedsUpdate()) {
            chooseTypeInfo.setNeedsUpdate(false);
            list.createUI();
        }
        if (textField != null && !prevText.equals(textField.getText())) {
            prevText = textField.getText();
            list.createUI();
        }

        handleSortButton(name, Comparator.comparing(o -> o.getSpec().getName()));
        handleSortButton(time, Comparator.comparing(o -> o.getSpec().getDaysToBeCreated()));
        handleSortButton(size, Comparator.comparingInt(o -> {
            AoTDProductionSpec spec = o.getSpec();
            if (spec.getUnderlyingSpec() instanceof ShipHullSpecAPI ship) {
                return hullSizeRank(ship.getHullSize());
            }
            if (spec.getUnderlyingSpec() instanceof WeaponSpecAPI weapon) {
                return weaponSizeRank(weapon.getSize());
            }
            return Integer.MAX_VALUE;
        }));
        handleSortButton(type, Comparator.comparing(o -> o.getSpec().getTypeString()));
        handleSortButton(design, Comparator.comparing(o -> o.getSpec().getManufacturer()));
        handleSortButton(totalCost, Comparator.comparing(o -> o.getSpec().getMoneyPrice()));
    }

    private static int hullSizeRank(ShipAPI.HullSize size) {
        if (size == null) return Integer.MAX_VALUE;

        switch (size) {
            case FIGHTER:
                return 0;
            case FRIGATE:
                return 1;
            case DESTROYER:
                return 2;
            case CRUISER:
                return 3;
            case CAPITAL_SHIP:
                return 4;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private static int weaponSizeRank(WeaponAPI.WeaponSize size) {
        if (size == null) return Integer.MAX_VALUE;

        switch (size) {
            case SMALL:
                return 0;
            case MEDIUM:
                return 1;
            case LARGE:
                return 2;
            default:
                return Integer.MAX_VALUE;
        }
    }

    private void handleSortButton(ButtonAPI button, Comparator<ProductionCustomButton> comparator) {
        if (button == null || comparator == null) return;
        if (!button.isChecked()) return;

        button.setChecked(false);

        UITableImpl.SortingState current = (UITableImpl.SortingState) button.getCustomData();
        UITableImpl.SortingState newState = AshMisc.switchState(current);

        AshMisc.sortByState(list.getButtonsStorage(), newState, comparator);

        button.setCustomData(newState);
        list.createUI();
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
    }

    @Override
    public void buttonPressed(Object buttonId) {
    }
}