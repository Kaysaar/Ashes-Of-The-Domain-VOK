package data.kaysaar.aotd.vok.ui.customprod.components;

import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Virtualized scroll content panel.
 *
 * You create ALL items once (buttons/components) and register them in order via addItem(...).
 * This class will only attach a moving "window" of items to mainPanel depending on scroll position.
 *
 * Window policy: 2x viewport of absolutePanel (buffer = 0.5*viewport above and below).
 *
 * Coordinate assumptions:
 * - Items are laid out in mainPanel using inTL(0, i*stride).
 * - Fixed item height/spacing defined by heightOfEachItem/spacingBetweenItems.
 */
public class ProductionDynamicPanelForScroll implements ExtendedUIPanelPlugin {
    public static final Logger log = Global.getLogger(ProductionDynamicPanelForScroll.class);

    private final CustomPanelAPI mainPanel;
    private final CustomPanelAPI absolutePanel;

    // base size (we keep width fixed; height becomes dynamic)
    private final float baseWidth;
    private final float baseHeight;

    // virtualization parameters
    public int visibleAtOnce = 30; // viewport capacity (used only for optional hard-cap)
    public float heightOfEachItem = 30;
    public int spacingBetweenItems = 3;

    // ordered list of ALL items (created once, never changes)
    private final ArrayList<UIComponentAPI> allItems = new ArrayList<>();

    // currently attached window [attachedStart, attachedEnd]
    private int attachedStart = -1;
    private int attachedEnd = -1;

    public ProductionDynamicPanelForScroll(
            float width,
            float height,
            CustomPanelAPI absolutePanel,
            int visibleAtOnce,
            float heightOfEachItem,
            int spacingBetweenItems
    ) {
        this.baseWidth = width;
        this.baseHeight = height;

        this.mainPanel = Global.getSettings().createCustom(width, height, this);
        this.absolutePanel = absolutePanel;

        this.visibleAtOnce = visibleAtOnce;
        this.heightOfEachItem = heightOfEachItem;
        this.spacingBetweenItems = spacingBetweenItems;
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        // Ensure panel size matches current items when UI is first created
        resizeContentToItems(false);
        refreshWindow();
    }

    @Override
    public void clearUI() {
        clearItems();
    }

    /**
     * Register an item in strict order. Call this during construction/setup (not while scrolling).
     * The item is NOT automatically attached; attachment is controlled by refreshWindow().
     *
     * If you add items after the panel is already on screen, consider calling resizeContentToItems(true)
     * and refreshWindow().
     */
    public void addItem(UIComponentAPI comp) {
        allItems.add(comp);
    }

    /** Optional convenience if you want to reset items. */
    public void clearItems() {
        detachRange(attachedStart, attachedEnd);
        attachedStart = attachedEnd = -1;
        allItems.clear();
        resizeContentToItems(true);
    }

    /**
     * Resizes the mainPanel's height based on item count, item height and spacing.
     *
     * Important notes:
     * - We keep width fixed (baseWidth).
     * - Height becomes max(baseHeight, computedListHeight) so the content is never smaller than initial.
     * - We do NOT touch X/Y; only size.
     *
     * @param refreshAfter if true, will call refreshWindow() after resizing
     */
    public void resizeContentToItems(boolean refreshAfter) {
        float newH = computeContentHeight();
        // If Starsector internally clamps/ignores tiny diffs, you can add an epsilon here
        mainPanel.getPosition().setSize(baseWidth, newH);

        if (refreshAfter) {
            refreshWindow();
        }
    }

    /** Call this after you've added all items (or after changing height/spacing) to sync visibility. */
    public void refreshWindow() {
        int[] desired = computeDesiredWindow();
        setWindowIncremental(desired[0], desired[1]);
    }

    @Override
    public void positionChanged(PositionAPI position) {
        // called as panel moves (scroll)
        refreshWindow();
    }

    // --------------------------
    // Sizing & layout math
    // --------------------------

    private int stride() {
        return Math.round(heightOfEachItem + spacingBetweenItems);
    }

    private float computeContentHeight() {
        int n = allItems.size();
        if (n <= 0) return baseHeight;

        // total list height:
        // n items of height, plus (n-1) gaps. If you intentionally want a trailing gap, use n gaps instead.
        float total = (n * (float) heightOfEachItem) + ((n - 1) * (float) spacingBetweenItems);

        // Ensure at least initial height so the panel doesn't collapse smaller than intended.
        return Math.max(baseHeight, total);
    }

    private static int clamp(int v, int lo, int hi) {
        if (v < lo) return lo;
        if (v > hi) return hi;
        return v;
    }

    /**
     * Computes the desired attached window [start,end] based on mainPanel position and absolutePanel viewport.
     * Window is 2x viewport (buffer=0.5 viewport above and below).
     */
    private int[] computeDesiredWindow() {
        int total = allItems.size();
        if (absolutePanel == null || total == 0) return new int[]{-1, -1};

        float contentY = mainPanel.getPosition().getY();
        float contentH = mainPanel.getPosition().getHeight();

        float viewBottom = absolutePanel.getPosition().getY();
        float viewH = absolutePanel.getPosition().getHeight();
        float viewTop = viewBottom + viewH;

        float buffer = viewH * 0.5f;   // 0.5 bottom + 0.5 top => 2x viewport window
        float windowBottom = viewBottom - buffer;
        float windowTop = viewTop + buffer;

        int stride = stride();

        // Convert window screen-space to TL-space:
        // yTL = contentY + contentH - screenY
        float yTL_at_windowTop = contentY + contentH - windowTop;       // smaller yTL
        float yTL_at_windowBottom = contentY + contentH - windowBottom; // larger yTL

        // Compute indices covered by that TL range (+ padding for rounding/partial overlap)
        int start = (int) Math.floor(yTL_at_windowTop / stride) - 1;
        int end = (int) Math.ceil(yTL_at_windowBottom / stride) + 1;

        start = clamp(start, 0, total - 1);
        end = clamp(end, 0, total - 1);
        if (end < start) end = start;

        // Optional: hard cap so we never attach more than ~2*visibleAtOnce
        int maxWindow = visibleAtOnce * 2;
        int size = end - start + 1;
        if (size > maxWindow) {
            end = start + maxWindow - 1;
            end = clamp(end, 0, total - 1);
        }

        return new int[]{start, end};
    }

    // --------------------------
    // Virtualization core
    // --------------------------

    /**
     * Incrementally moves the attached window, removing/adding only the delta.
     * This minimizes work and avoids rebuilding the entire window on small scroll changes.
     */
    private void setWindowIncremental(int newStart, int newEnd) {
        if (newStart < 0 || newEnd < 0) {
            // detach everything
            detachRange(attachedStart, attachedEnd);
            attachedStart = attachedEnd = -1;
            return;
        }

        // first attach
        if (attachedStart == -1) {
            attachedStart = newStart;
            attachedEnd = newEnd;
            attachRange(attachedStart, attachedEnd);
            return;
        }

        // no change
        if (newStart == attachedStart && newEnd == attachedEnd) return;

        // If the window jumped far (e.g. scrollbar dragged), full rebuild is simpler/safer.
        int oldSize = attachedEnd - attachedStart + 1;
        if (Math.abs(newStart - attachedStart) > oldSize || Math.abs(newEnd - attachedEnd) > oldSize) {
            detachRange(attachedStart, attachedEnd);
            attachedStart = newStart;
            attachedEnd = newEnd;
            attachRange(attachedStart, attachedEnd);
            return;
        }

        // Remove ranges that fell out
        if (newStart > attachedStart) detachRange(attachedStart, newStart - 1);
        if (newEnd < attachedEnd) detachRange(newEnd + 1, attachedEnd);

        // Add ranges that entered
        if (newStart < attachedStart) attachRange(newStart, attachedStart - 1);
        if (newEnd > attachedEnd) attachRange(attachedEnd + 1, newEnd);

        attachedStart = newStart;
        attachedEnd = newEnd;
    }

    /**
     * Attach items in [start,end] to mainPanel using fixed TL layout.
     * NOTE: add order can matter for draw order; we add from top to bottom (increasing index => larger yTL).
     */
    private void attachRange(int start, int end) {
        if (start < 0 || end < 0) return;
        int stride = stride();

        for (int i = start; i <= end; i++) {
            UIComponentAPI comp = allItems.get(i);
            if(comp instanceof CustomPanelAPI panel){
                if(panel.getPlugin() instanceof CustomButton plugin){
                    if(!plugin.isCreated()){
                        plugin.createUI();
                    }
                }
            }
            float yTL = i * stride;
            mainPanel.addComponent(comp).inTL(0f, yTL);
        }
    }

    /** Detach items in [start,end] from mainPanel. Remove in reverse for safety. */
    private void detachRange(int start, int end) {
        if (start < 0 || end < 0) return;
        if (end < start) return;

        for (int i = end; i >= start; i--) {
            UIComponentAPI comp = allItems.get(i);
            mainPanel.removeComponent(comp);
        }
    }

    // --------------------------
    // Plugin no-ops
    // --------------------------

    @Override public void renderBelow(float alphaMult) {}
    @Override public void render(float alphaMult) {}
    @Override public void advance(float amount) {}
    @Override public void processInput(List<InputEventAPI> events) {}
    @Override public void buttonPressed(Object buttonId) {}
}