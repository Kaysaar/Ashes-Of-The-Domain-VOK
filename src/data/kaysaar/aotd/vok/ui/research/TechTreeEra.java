package data.kaysaar.aotd.vok.ui.research;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;
import data.kaysaar.aotd.vok.ui.basecomps.ResizableComponent;

import java.util.ArrayList;
import java.util.List;

import static data.kaysaar.aotd.vok.ui.research.AoTDUiComp.*;

public class TechTreeEra extends ResizableComponent {

    public List<ResearchOption> sortedResearchOptions;
    public ArrayList<ResearchPanelComponent> researchOptionPanels = new ArrayList<>();
    public int biggestIndexOfColumn = 0;
    public  int biggestIndexOfRows = 0;
    public ResearchZoomPanel manP;
    public TechTreeEra(List<ResearchOption> optionsFromEra,ResearchZoomPanel manP){
        sortedResearchOptions = optionsFromEra;
        checkForBiggestColumnAndRowIndex();
        this.manP = manP;
        componentPanel = Global.getSettings().createCustom(calculateWidthOfEraPanel(),calculateHeightOfEraPanel(),this);
        createUI();
    }
    public void checkForBiggestColumnAndRowIndex(){
        for (ResearchOption option : sortedResearchOptions) {
            int tester = option.UiInfo.ColumnNumber;
            int testerButRows = option.UiInfo.RowNumber;
            if(tester>=biggestIndexOfColumn) biggestIndexOfColumn=tester;
            if(testerButRows>=biggestIndexOfRows) biggestIndexOfRows=testerButRows;
        }
    }
    public float calculateWidthOfEraPanel(){
        return biggestIndexOfColumn*(WIDTH_OF_TECH_PANEL+SEPERATOR_OF_COLUMNS);
    }
    public float calculateHeightOfEraPanel(){
        return biggestIndexOfRows*(HEIGHT_OF_TECH_PANEL+SEPERATOR_OF_PANELS);
    }
    public void createUI() {
        float xPanelPos;
        float yPanelPos;
        for (ResearchOption option : sortedResearchOptions) {
            xPanelPos=(WIDTH_OF_TECH_PANEL+ SEPERATOR_OF_COLUMNS)*option.UiInfo.ColumnNumber;
            yPanelPos=(AoTDUiComp.HEIGHT_OF_TECH_PANEL+ SEPERATOR_OF_PANELS)*option.UiInfo.RowNumber;
            ResearchPanelComponent component = new ResearchPanelComponent(WIDTH_OF_TECH_PANEL,HEIGHT_OF_TECH_PANEL,option.getSpec(),manP);
            addComponent(component,xPanelPos,yPanelPos);
            researchOptionPanels.add(component);
        }


    }

    @Override
    public void clearUI() {
        super.clearUI();
        researchOptionPanels.clear();
        sortedResearchOptions.clear();
    }

    public ArrayList<ResearchPanelComponent> getResearchOptionPanels() {
        return researchOptionPanels;
    }

    public List<ResearchOption> getSortedResearchOptions() {
        return sortedResearchOptions;
    }
}
