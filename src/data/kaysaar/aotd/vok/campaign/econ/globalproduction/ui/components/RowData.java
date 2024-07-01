package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class RowData {
    public float row;
    public LinkedHashMap<String,Integer> stringsInRow;
    public RowData(float rowNumber,LinkedHashMap<String,Integer> stringsInRow){
        this.row = rowNumber;
        this.stringsInRow = stringsInRow;
    }
}
