package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOptionEra;
import data.kaysaar.aotd.vok.ui.AoTDResearchNewPlugin;
import data.kaysaar.aotd.vok.ui.basecomps.ZoomPanelComponent;

import java.awt.*;
import java.util.ArrayList;

public class ResearchZoomPanel extends ZoomPanelComponent {
    public String currentModID;
    public ArrayList<TechTreeEra>eras  = new ArrayList<>();
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
        this.renderer.alphaMultiplier = 0f;


        float xForEra = 20;
        float yForEra = 240;
        float lastX = 0;
        float highets = 0;
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            ArrayList<ResearchOption>researchOptionsOfEra = getResearchOptionsFromCertainEraAndMod(value,currentModID);
            if(researchOptionsOfEra.isEmpty())continue;
            TechTreeEra eraSection = new TechTreeEra(researchOptionsOfEra,this);
            eraSection.checkForBiggestColumnAndRowIndex();
            float width = eraSection.calculateWidthOfEraPanel();
            if(highets<=height){
                highets = height;
            }
            this.addComponent(eraSection,xForEra,yForEra);
            lastX = width+ AoTDUiComp.SEPERATOR_OF_COLUMNS*3;
            xForEra+=width+ AoTDUiComp.SEPERATOR_OF_COLUMNS*3;
            for (ResearchPanelComponent researchOptionPanel : eraSection.researchOptionPanels) {
                researchOptionPanel.setAbsolutePanel(this.mainPanel);
            }
            eras.add(eraSection);
        }
        for (TechTreeEra era : eras) {
            for (ResearchPanelComponent researchOptionPanel : era.getResearchOptionPanels()) {
                if(!researchOptionPanel.spec.getReqTechsToResearchFirst().isEmpty()){
                    for (ResearchPanelComponent parentComponent : getParentComponents(researchOptionPanel)) {
                        Color color = Color.WHITE;
                        if(AoTDMainResearchManager.getInstance().getManagerForPlayer().haveResearched(parentComponent.spec.getId())){
                            color = Color.ORANGE;
                        }
                        ResearchTreeConnector drawer = new ResearchTreeConnector(3,parentComponent,researchOptionPanel,color);
                        addComponent(drawer,0,0);
                    }
                }
            }
        }
        if((calculateWidthAndHeight().two+5)*minScale<=this.height){
//            this.mainPanel.getPosition().setSize(this.mainPanel.getPosition().getWidth(),(calculateWidthAndHeight().two+5+yForEra)*minScale);
        }

        xForEra+=30;

        sentToBottomComponentsOfClass(ResearchTreeConnector.class);
        this.setMaxOffsets((int) xForEra, (int) (calculateWidthAndHeight().two+5+yForEra),1f);
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
        removeComponentOfClass(ResearchTreeConnector.class);
        removeComponentOfClass(TechTreeEra.class);
        eras.clear();
    }
    public void clearUI(){
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
        return parents;
    }
}
