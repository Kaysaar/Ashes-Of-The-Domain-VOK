package data.kaysaar.aotd.vok.ui.components;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.scripts.research.models.ResearchOption;

import java.util.ArrayList;
import java.util.List;

public class TechTreeEraSection extends  UiPanel {
    public List<ResearchOption>sortedResearchOptions;
    public ArrayList<TechTreeResearchOptionPanel> researchOptionPanels = new ArrayList<>();
    public int biggestIndexOfColumn = 0;
    public  int biggestIndexOfRows = 0;
    public TechTreeEraSection(List<ResearchOption> optionsFromEra){
        sortedResearchOptions = optionsFromEra;
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
    @Override
    public void createUI(float x, float y) {
        float xPanelPos;
        float yPanelPos;
        for (ResearchOption option : sortedResearchOptions) {
            TechTreeResearchOptionPanel panel = new TechTreeResearchOptionPanel(option);
            panel.init(mainPanel,this.panel,tooltip);
            xPanelPos=(AoTDUiComp.WIDTH_OF_TECH_PANEL+AoTDUiComp.SEPERATOR_OF_COLUMNS)*option.UiInfo.ColumnNumber;
            yPanelPos=(AoTDUiComp.HEIGHT_OF_TECH_PANEL+AoTDUiComp.SEPERATOR_OF_PANELS)*option.UiInfo.RowNumber;
            panel.createUI(xPanelPos, yPanelPos);
            researchOptionPanels.add(panel);
        }
        placeTooltip(0,0);

    }
    public void placeOnTooltipOfCore(TooltipMakerAPI tooltipMakerAPI,float x, float y){
        tooltipMakerAPI.addComponent(panel).inTL(x,y);
    }
    public ArrayList<TechTreeResearchOptionPanel> getResearchOptionPanels() {
        return researchOptionPanels;
    }

    public List<ResearchOption> getSortedResearchOptions() {
        return sortedResearchOptions;
    }
}
