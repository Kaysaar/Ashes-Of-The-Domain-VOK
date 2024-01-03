package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Pair;
import data.kaysaar.aotd.vok.models.ResearchOption;
import data.kaysaar.aotd.vok.models.ResearchOptionEra;

import java.util.ArrayList;
import java.util.List;

public class TechTreeCore extends UiPanel{
    // Tech Tree Core -> y amount Tech Tree Era Section - > x amount of tech tree column section
    public List<TechTreeEraSection> Eras = new ArrayList<>();
    //Idea : This whole list is already sorted Era->Column->Row
    public List<ResearchOption> allResearchOptions;
    public String currentModToShow = null;


    public  void setCurrentModToShow(String modID){
        currentModToShow = modID;
    }
    public TechTreeCore(List<ResearchOption>sortedResearchOptions){
        allResearchOptions = sortedResearchOptions;
    }


    public String modID;

    @Override
    public void createUI(float x, float y) {
        float xForEra = 0;
        float yForEra = 25;
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            ArrayList<ResearchOption>researchOptionsOfEra = getResearchOptionsFromCertainEraAndMod(value,currentModToShow);
            if(researchOptionsOfEra.isEmpty())continue;
            TechTreeEraSection eraSection = new TechTreeEraSection(researchOptionsOfEra);
            eraSection.checkForBiggestColumnAndRowIndex();
            float width = eraSection.calculateWidthOfEraPanel();
            float height = eraSection.calculateHeightOfEraPanel();
            CustomPanelAPI panelForEra = panel.createCustomPanel(width,height,null);
            TooltipMakerAPI tooltipForEra = panelForEra.createUIElement(width,height,false);
            eraSection.init(panel,panelForEra,tooltipForEra);
            eraSection.createUI(xForEra,yForEra);
            eraSection.placeOnTooltipOfCore(tooltip,xForEra,yForEra);
            xForEra+=width+AoTDUiComp.SEPERATOR_OF_COLUMNS*3;
            Eras.add(eraSection);
        }
        placeTooltip(-5,0);
        placeSubPanel(x, y);

    }

    public Pair<Float,Float> calculateWidthAndHeight() {
        float totalX = 0;
        float totalY = 25;
        for (ResearchOptionEra value : ResearchOptionEra.values()) {
            ArrayList<ResearchOption>researchOptionsOfEra = getResearchOptionsFromCertainEraAndMod(value,currentModToShow);
            if(researchOptionsOfEra.isEmpty())continue;
            TechTreeEraSection eraSection = new TechTreeEraSection(researchOptionsOfEra);
            eraSection.checkForBiggestColumnAndRowIndex();
            float width = eraSection.calculateWidthOfEraPanel();
            float height = eraSection.calculateHeightOfEraPanel();
            if(height+AoTDUiComp.HEIGHT_OF_TECH_PANEL*2>=totalY){
                totalY = height+AoTDUiComp.HEIGHT_OF_TECH_PANEL*2;
            }
            totalX+=width+AoTDUiComp.SEPERATOR_OF_COLUMNS*2.5f;
        }
        return new Pair<>(totalX,totalY);


    }

    public ArrayList<ResearchOption> getResearchOptionsFromCertainEraAndMod(ResearchOptionEra era, String modID){
        ArrayList<ResearchOption> eraOptions = new ArrayList<>();
        for (ResearchOption allResearchOption : allResearchOptions) {
            if(allResearchOption.Tier == era&&allResearchOption.modID.equals(modID)){
                eraOptions.add(allResearchOption);
            }
        }
        return eraOptions;
    }

    public ArrayList<ButtonAPI> getAllButtons(){
        ArrayList<ButtonAPI> buttons = new ArrayList<>();
        for (TechTreeEraSection era : Eras) {
            for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                buttons.add(researchOptionPanel.getCurrentButton());
            }
        }
        return buttons;
    }
    public PositionAPI retrieveCoordinatesOfSpecificPanel(String id){
        for (TechTreeEraSection era : Eras) {
            for (TechTreeResearchOptionPanel researchOptionPanel : era.getResearchOptionPanels()) {
                if(researchOptionPanel.TechToResearch.Id.equals(id)){
                    return researchOptionPanel.coordinates;
                }
            }
        }
        return null;
    }
}
