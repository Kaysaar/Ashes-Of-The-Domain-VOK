package data.kaysaar.aotd.vok.campaign.econ.produciton.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.campaign.econ.produciton.ProductionMonthlyHistoryManager;


import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Set;

import static data.kaysaar.aotd.vok.ui.customprod.orders.OnGoingOrderButton.createProductionIconPanel;

public class AoTDProductionReportIntel extends FleetLogIntel {
    protected static final float ICON_SIZE = 40f;
    protected static final float GROUP_GAP_MIN = 8f;
    protected static final float ROW_GAP = 4f;
    protected static final float TOP_PAD = 2f;
    protected static final float BOTTOM_PAD = 2f;
    protected static final float LEFT_PAD = 2f;
    protected static final float RIGHT_PAD = 15f;
    protected static final int MAX_ICONS_PER_GROUP = 5;

    protected MarketAPI gatheringPoint;
    protected int totalCost;

    public AoTDProductionReportIntel() {
        this.gatheringPoint = Global.getSector().getPlayerFaction().getProduction().getGatheringPoint();

        for (ProductionMonthlyHistoryManager.ProductionMonthlyData data :
                ProductionMonthlyHistoryManager.getInstance().getProductionMonthlyDataFromPrevMonth()) {

            if (data.currentAmount <= 0) continue;

            AoTDProductionSpec spec = AoTDProductionSpecManager.getSpec(data.idOfProduct, data.productionType);
            if (spec == null) continue;

            totalCost += spec.getProductionCost() * data.currentAmount;
        }

        setDuration(10f);
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {
        LinkedHashSet<ProductionMonthlyHistoryManager.ProductionMonthlyData> data =
                ProductionMonthlyHistoryManager.getInstance().getProductionMonthlyDataFromPrevMonth();

        Color h = Misc.getHighlightColor();

        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) {
            initPad = opad;
        }

        Color tc = getBulletColorForMode(mode);

        bullet(info);

        if (!data.isEmpty()) {
            float days = getDaysSincePlayerVisible();
            String destination = gatheringPoint != null ? gatheringPoint.getName() : "the gathering point";

            if (days < 1f) {
                info.addPara(
                        "Items delivered to %s",
                        initPad,
                        tc,
                        getFactionForUIColors().getBaseUIColor(),
                        destination
                );
                initPad = 0f;
            } else {
                LabelAPI label = info.addPara(
                        "Items delivered to %s %s " + getDaysString(days) + " ago",
                        initPad,
                        tc,
                        getFactionForUIColors().getBaseUIColor(),
                        destination,
                        getDays(days)
                );

                label.setHighlightColors(getFactionForUIColors().getBaseUIColor(), h);
                initPad = 0f;
            }
        }

        if (totalCost > 0) {
            info.addPara(
                    "Production cost: %s",
                    initPad,
                    tc,
                    h,
                    Misc.getDGSCredits(totalCost)
            );
        }

        unindent(info);
    }

    @Override
    public boolean hasSmallDescription() {
        return true;
    }

    @Override
    public boolean hasLargeDescription() {
        return false;
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        LinkedHashSet<ProductionMonthlyHistoryManager.ProductionMonthlyData> data =
                ProductionMonthlyHistoryManager.getInstance().getProductionMonthlyDataFromPrevMonth();

        Color base = getFactionForUIColors().getBaseUIColor();
        Color dark = getFactionForUIColors().getDarkUIColor();
        Color h = Misc.getHighlightColor();
        Color positive = Misc.getPositiveHighlightColor();
        Color tc = Misc.getTextColor();

        float pad = 3f;
        float opad = 10f;

        info.addSectionHeading("Produced this month", base, dark, Alignment.MID, 0f);

        if (data.isEmpty()) {
            info.addPara("No items were produced this month.", opad);
            return;
        }

        float iconPanelWidth = width - 10f;
        float iconPanelHeight = getProductionGroupsPanelHeight(iconPanelWidth, data);

        CustomPanelAPI producedPanel = createProductionGroupsPanel(iconPanelWidth, iconPanelHeight, data);
        info.addCustom(producedPanel, opad);

        info.addSectionHeading("Production summary", base, dark, Alignment.MID, opad);

        String destination = gatheringPoint != null ? gatheringPoint.getName() : "the gathering point";

        int calculatedTotal = 0;

        for (ProductionMonthlyHistoryManager.ProductionMonthlyData productionDatum : data) {
            int amount = productionDatum.currentAmount;
            if (amount <= 0) continue;

            AoTDProductionSpec spec = AoTDProductionSpecManager.getSpec(
                    productionDatum.idOfProduct,
                    productionDatum.productionType
            );

            if (spec == null) continue;

            int cost = spec.getProductionCost() * amount;
            calculatedTotal += cost;

            String name = spec.getName();

            LabelAPI label = info.addPara(
                    "• %s x%s delivered to %s. Manufacturing cost: %s.",
                    pad,
                    tc,
                    h,
                    name,
                    Misc.getWithDGS(amount),
                    destination,
                    Misc.getDGSCredits(cost)
            );

            label.setHighlightColors(h, positive, base, h);
        }

        int finalTotal = totalCost > 0 ? totalCost : calculatedTotal;

        if (finalTotal > 0) {
            info.addPara(
                    "Total production cost this month: %s",
                    opad,
                    tc,
                    h,
                    Misc.getDGSCredits(finalTotal)
            );
        }
    }

    private CustomPanelAPI createProductionGroupsPanel(
            float width,
            float height,
            LinkedHashSet<ProductionMonthlyHistoryManager.ProductionMonthlyData> data
    ) {
        CustomPanelAPI panel = Global.getSettings().createCustom(width, height, null);

        float overlapGap = Math.max(4f, ICON_SIZE * 0.25f);
        float contentWidth = width - LEFT_PAD - RIGHT_PAD;

        class GroupEntry {
            AoTDProductionSpec spec;
            int iconsToShow;
            float width;

            GroupEntry(AoTDProductionSpec spec, int iconsToShow, float width) {
                this.spec = spec;
                this.iconsToShow = iconsToShow;
                this.width = width;
            }
        }

        class Row {
            java.util.List<GroupEntry> groups = new java.util.ArrayList<>();
            float width = 0f;

            boolean isEmpty() {
                return groups.isEmpty();
            }

            void add(GroupEntry group) {
                if (!groups.isEmpty()) {
                    width += GROUP_GAP_MIN;
                }

                groups.add(group);
                width += group.width;
            }

            float getRenderGap(float contentWidth) {
                if (groups.size() <= 1) {
                    return GROUP_GAP_MIN;
                }

                float freeSpace = Math.max(0f, contentWidth - width);
                return GROUP_GAP_MIN + freeSpace / Math.max(1, groups.size() - 1);
            }
        }

        java.util.List<Row> rows = new java.util.ArrayList<>();
        Row currentRow = new Row();
        rows.add(currentRow);

        for (ProductionMonthlyHistoryManager.ProductionMonthlyData productionDatum : data) {
            int count = productionDatum.currentAmount;
            if (count <= 0) continue;

            AoTDProductionSpec spec = AoTDProductionSpecManager.getSpec(
                    productionDatum.idOfProduct,
                    productionDatum.productionType
            );

            if (spec == null) continue;

            int iconsToShow = Math.min(count, MAX_ICONS_PER_GROUP);
            float groupWidth = getGroupWidth(iconsToShow, overlapGap);

            GroupEntry group = new GroupEntry(spec, iconsToShow, groupWidth);

            float widthWithGap = currentRow.isEmpty()
                    ? group.width
                    : currentRow.width + GROUP_GAP_MIN + group.width;

            if (!currentRow.isEmpty() && widthWithGap > contentWidth) {
                currentRow = new Row();
                rows.add(currentRow);
            }

            currentRow.add(group);
        }

        float y = TOP_PAD;

        for (Row row : rows) {
            if (row.isEmpty()) continue;

            float x = LEFT_PAD;
            float renderGap = row.getRenderGap(contentWidth);

            for (GroupEntry group : row.groups) {
                for (int i = 0; i < group.iconsToShow; i++) {
                    float iconX = x + i * overlapGap;

                    CustomPanelAPI icon = createProductionIconPanel(ICON_SIZE, group.spec);
                    panel.addComponent(icon).inTL(iconX, y);
                }

                x += group.width + renderGap;
            }

            y += ICON_SIZE + ROW_GAP;
        }

        return panel;
    }

    private float getProductionGroupsPanelHeight(
            float width,
            LinkedHashSet<ProductionMonthlyHistoryManager.ProductionMonthlyData> data
    ) {
        float overlapGap = Math.max(4f, ICON_SIZE * 0.25f);
        float contentWidth = width - LEFT_PAD - RIGHT_PAD;

        float currentRowWidth = 0f;
        int rows = 0;

        for (ProductionMonthlyHistoryManager.ProductionMonthlyData productionDatum : data) {
            int count = productionDatum.currentAmount;
            if (count <= 0) continue;

            AoTDProductionSpec spec = AoTDProductionSpecManager.getSpec(
                    productionDatum.idOfProduct,
                    productionDatum.productionType
            );

            if (spec == null) continue;

            int iconsToShow = Math.min(count, MAX_ICONS_PER_GROUP);
            float groupWidth = getGroupWidth(iconsToShow, overlapGap);

            if (rows == 0) {
                rows = 1;
                currentRowWidth = groupWidth;
                continue;
            }

            float widthWithGap = currentRowWidth + GROUP_GAP_MIN + groupWidth;

            if (widthWithGap > contentWidth) {
                rows++;
                currentRowWidth = groupWidth;
            } else {
                currentRowWidth = widthWithGap;
            }
        }

        if (rows <= 0) {
            return TOP_PAD + BOTTOM_PAD;
        }

        return TOP_PAD + rows * ICON_SIZE + Math.max(0, rows - 1) * ROW_GAP + BOTTOM_PAD;
    }

    private float getGroupWidth(int iconsToShow, float overlapGap) {
        if (iconsToShow <= 0) {
            return 0f;
        }

        return ICON_SIZE + Math.max(0, iconsToShow - 1) * overlapGap;
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "production_report");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_PRODUCTION);
        return tags;
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return Global.getSector().getPlayerFaction();
    }

    @Override
    public String getSmallDescriptionTitle() {
        return getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (gatheringPoint == null) return null;
        return gatheringPoint.getPrimaryEntity();
    }

    @Override
    public boolean shouldRemoveIntel() {
        if (isImportant()) return false;
        if (getDaysSincePlayerVisible() < 30) return false;
        return super.shouldRemoveIntel();
    }

    @Override
    public String getSortString() {
        return super.getSortString();
    }

    @Override
    public String getName() {
        return "Production Report";
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);
        addBulletPoints(info, mode);
    }
}