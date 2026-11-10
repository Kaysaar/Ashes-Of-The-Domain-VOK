package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionEra;
import data.kaysaar.aotd.vok.ui.basecomps.ZoomPanelComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResearchZoomPanel extends ZoomPanelComponent {
    public String currentModID;
    public ArrayList<TechTreeEra>eras  = new ArrayList<>();
    private final Map<TechTreeEraArea, float[]> eraHeaderOverlays = new LinkedHashMap<>();
    public AoTDResearchNewPlugin plugin;

    public ArrayList<TechTreeEra> getEras() {
        return eras;
    }

    public ResearchZoomPanel(float width, float height, float trueWidth, float trueHeight, float currZoom, AoTDResearchNewPlugin plugin) {
        super(width, height, trueWidth, trueHeight,currZoom);

        this.plugin = plugin;
    }
    public void refresh(){
        for (TechTreeEra era : eras) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.refresh();
            }
        }
    }
    public String getCurrentModID() {
        return currentModID;
    }

    public void createTechTree(String modId){
        this.currentModID = modId;
        clearPanel();


        Map<ResearchPanelComponent, float[]> nodePositions = new LinkedHashMap<>();
        float xForEra = 20f;
        float estimatedTreeWidth = 50f;
        int maximumRow = 0;
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            ArrayList<ResearchOption> options = getResearchOptionsFromCertainEraAndMod(value, currentModID);
            if (options.isEmpty()) continue;
            int maximumColumn = 0;
            for (ResearchOption option : options) {
                maximumRow = Math.max(maximumRow, option.UiInfo.RowNumber);
                maximumColumn = Math.max(maximumColumn, option.UiInfo.ColumnNumber);
            }
            estimatedTreeWidth += maximumColumn
                    * (AoTDUiComp.WIDTH_OF_TECH_PANEL + AoTDUiComp.SEPERATOR_OF_COLUMNS)
                    + AoTDUiComp.SEPERATOR_OF_COLUMNS * 3f;
        }
        float minExpectedZoom = Math.min(maxScale,
                this.width / Math.max(this.width, estimatedTreeWidth));
        float areaTop = 20f;
        float yForEra = Math.max(240f, areaTop
                + (TechTreeEraArea.HEADER_HEIGHT_PX + 14f)
                / Math.max(0.01f, minExpectedZoom));
        float headerSpace = yForEra - areaTop;
        float areaHeight = headerSpace + maximumRow
                * (AoTDUiComp.HEIGHT_OF_TECH_PANEL + AoTDUiComp.SEPERATOR_OF_PANELS)
                + AoTDUiComp.HEIGHT_OF_TECH_PANEL + 100f;
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            ArrayList<ResearchOption>researchOptionsOfEra = getResearchOptionsFromCertainEraAndMod(value,currentModID);
            if(researchOptionsOfEra.isEmpty())continue;
            TechTreeEra eraSection = new TechTreeEra(researchOptionsOfEra,this);
            eraSection.checkForBiggestColumnAndRowIndex();
            float width = eraSection.calculateWidthOfEraPanel();

            float eraStride = width + AoTDUiComp.SEPERATOR_OF_COLUMNS * 3f;
            TechTreeEraArea area = new TechTreeEraArea(value, eraStride, areaHeight, headerSpace);
            addComponent(area, xForEra - 20f, areaTop);

            eraHeaderOverlays.put(area, new float[]{xForEra - 20f, yForEra, eraStride});
            this.addComponent(eraSection,xForEra,yForEra);
            xForEra += eraStride;
            for (ResearchPanelComponent researchOptionPanel : eraSection.researchOptionPanels) {
                researchOptionPanel.setAbsolutePanel(this.mainPanel);

                float wx = xForEra - eraStride +
                        (AoTDUiComp.WIDTH_OF_TECH_PANEL + AoTDUiComp.SEPERATOR_OF_COLUMNS)
                                * researchOptionPanel.spec.UiInfo.ColumnNumber;
                float wy = yForEra +
                        (AoTDUiComp.HEIGHT_OF_TECH_PANEL + AoTDUiComp.SEPERATOR_OF_PANELS)
                                * researchOptionPanel.spec.UiInfo.RowNumber;
                nodePositions.put(researchOptionPanel, new float[]{wx, wy});
            }
            eras.add(eraSection);
        }

        if (!nodePositions.isEmpty()) {
            ResearchTreeConnector connections = new ResearchTreeConnector(this, nodePositions);
            addComponent(connections, 0f, 0f);
        }
        if((calculateWidthAndHeight().two+5)*minScale<=this.height){
//            this.mainPanel.getPosition().setSize(this.mainPanel.getPosition().getWidth(),(calculateWidthAndHeight().two+5+yForEra)*minScale);
        }

        xForEra+=30;

        sentToBottomComponentsOfClass(ResearchTreeConnector.class);
        sentToBottomComponentsOfClass(TechTreeEraArea.class);
        this.setMaxOffsets((int) xForEra, (int) (calculateWidthAndHeight().two+5+yForEra),1f);

        for (TechTreeEraArea eraHeader : eraHeaderOverlays.keySet()) {
            eraHeader.setZoomRange(minScale, maxScale);
        }
        this.setCurrScale(minScale);
        this.data.setCurrentOffsetX(0);
        this.data.setCurrentOffsetY(0);
    }
    public Pair<Float,Float> calculateWidthAndHeight() {
        float totalX = 0;
        float totalY = 25;
        for (String s : AoTDMainResearchManager.getInstance().getModIDsRepo()) {
            for (ResearchOptionEra value : ResearchOptionEra.values()) {
                ArrayList<ResearchOption>researchOptionsOfEra = getResearchOptionsFromCertainEraAndMod(value,s);
                if(researchOptionsOfEra.isEmpty())continue;
                TechTreeEra eraSection = new TechTreeEra(researchOptionsOfEra,this);
                eraSection.checkForBiggestColumnAndRowIndex();
                float width = eraSection.calculateWidthOfEraPanel();
                float height = eraSection.calculateHeightOfEraPanel();
                if(height+AoTDUiComp.HEIGHT_OF_TECH_PANEL*2>=totalY){
                    totalY = height+AoTDUiComp.HEIGHT_OF_TECH_PANEL*2;
                }
                totalX+=width+AoTDUiComp.SEPERATOR_OF_COLUMNS*2.5f;
            }
        }

        return new Pair<>(totalX,totalY);


    }

    public void setCurrentModID(String currentModID) {
        this.currentModID = currentModID;
    }


    public void clearPanel(){
        eraHeaderOverlays.clear();
        removeComponentOfClass(TechTreeEraArea.class);
        removeComponentOfClass(ResearchTreeConnector.class);
        removeComponentOfClass(TechTreeEra.class);
        eras.clear();
    }
    public void clearUI(){
        eraHeaderOverlays.clear();
        removeComponentOfClass(TechTreeEraArea.class);
        for (TechTreeEra era : eras) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.clearUI();
            }
            era.clearUI();
        }
        eras.clear();

    }
    public void blockButtonsFromHover() {
        for (TechTreeEra era : getEras()) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.setClickable(false);
            }
        }
    }
    public void unlockButtonsFromHover() {
        for (TechTreeEra era : getEras()) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                researchOptionPanel.setClickable(true);
            }
        }
    }

    public ArrayList<ResearchOption> getResearchOptionsFromCertainEraAndMod(ResearchOptionEra era, String modID){
        ArrayList<ResearchOption> eraOptions = new ArrayList<>();
        for (ResearchOption allResearchOption : AoTDMainResearchManager.getInstance().getManagerForPlayer().getResearchRepoOfFaction()) {
            if(allResearchOption.Tier == era&&allResearchOption.modID.equals(modID)){
                eraOptions.add(allResearchOption);
            }
        }
        return eraOptions;
    }
    public ArrayList<ResearchPanelComponent> getParentComponents(ResearchPanelComponent component){
        ArrayList<ResearchPanelComponent> parents = new ArrayList<>();
        for (String s : component.spec.getReqTechsToResearchFirst()) {
            for (TechTreeEra era : eras) {
                for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                    if(researchOptionPanel.spec.getId().equals(s)){
                        parents.add(researchOptionPanel);
                    }
                }
            }
        }
        parents.sort(Comparator.comparing(
                x -> !AoTDMainResearchManager.getInstance().isResearchedForPlayer(x.spec.getId())
        ));
        return parents;
    }

    @Override
    public void renderBelow(float alphaMult) {
        // Era background panels render underneath the technology boxes.
    }

    public void render(float alphaMult) {
        if (mainPanel == null || data == null || alphaMult <= 0f) return;
        com.fs.starfarer.api.ui.PositionAPI pos = mainPanel.getPosition();
        float rootX = pos.getX();
        float rootTop = pos.getY() + pos.getHeight();
        float zoom = currScale;
        float offsetX = data.getCurrentOffsetX();
        float offsetY = data.getCurrentOffsetY();
        for (Map.Entry<TechTreeEraArea, float[]> header : eraHeaderOverlays.entrySet()) {
            TechTreeEraArea area = header.getKey();
            float[] world = header.getValue();
            float x = rootX + (world[0] + offsetX) * zoom;
            float techRowTop = rootTop - (world[1] - offsetY) * zoom;
            float headerTop = techRowTop + TechTreeEraArea.HEADER_TO_TECH_GAP_PX
                    + TechTreeEraArea.HEADER_HEIGHT_PX;
            float w = world[2] * zoom;
            area.drawViewportHeader(x, headerTop, w, zoom, alphaMult);
        }
    }
}

