package data.kaysaar.aotd.vok.ui.customprod.components;

import java.util.LinkedHashMap;

public class RowData {
    public float row;
    public LinkedHashMap<String,Integer> stringsInRow;
    public RowData(float rowNumber,LinkedHashMap<String,Integer> stringsInRow){
        this.row = rowNumber;
        this.stringsInRow = stringsInRow;
    }
}
