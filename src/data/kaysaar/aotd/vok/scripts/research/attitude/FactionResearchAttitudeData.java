package data.kaysaar.aotd.vok.scripts.research.attitude;

import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.scripts.research.AoTDAIStance;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FactionResearchAttitudeData implements Cloneable {
    public AoTDAIStance stance;
    public String factionID;
    public float databankRepMultiplier;
    public float databankCashMultiplier;
    public String convInit;
    public String initResponse;
    public String dialogOptionSelect;
    public String dialogOptionDissmay;
    public String responseAfter;
    public ArrayList<String>forbiddenTech;

    public String getInitResponse() {
        if(!AoTDMisc.isStringValid(initResponse)){
            return "test init response";
        }
        return initResponse;
    }

    public String getResponseAfter() {
        if(!AoTDMisc.isStringValid(initResponse)){
            return "test init after core";
        }
        return responseAfter;
    }

    public String getDialogOptionDissmay() {
        if(!AoTDMisc.isStringValid(dialogOptionDissmay)){
            return "Never mind, I don't actually have any.";
        }
        return dialogOptionDissmay;
    }

    public String getDialogOptionSelect() {
        if(!AoTDMisc.isStringValid(dialogOptionSelect)){
            return "Select databanks to turn in";
        }
        return dialogOptionSelect;
    }
    public void  setDialogData(String initResponse,String dialogOptionDissmay, String dialogOptionSelect,String responseAfter){
        this.initResponse = initResponse;
        this.dialogOptionSelect = dialogOptionSelect;
        this.dialogOptionDissmay = dialogOptionDissmay;
        this.responseAfter = responseAfter;
    }
    public AoTDAIStance getStance() {
        return stance;
    }

    public float getDatabankCashMultiplier() {
        return databankCashMultiplier;
    }

    public float getDatabankRepMultiplier() {
        return databankRepMultiplier;
    }

    public String getAttitudeFlavourText() {
        return convInit;
    }

    public String getFactionID() {
        return factionID;
    }
    public FactionResearchAttitudeData(String factionId,AoTDAIStance stance, float databankCashMultiplier,float databankRepMultiplier,String attitudeFlavourText,ArrayList<String>forbiddenTech){
        this.factionID = factionId;
        this.stance = stance;
        this.databankCashMultiplier = databankCashMultiplier;
        this.databankRepMultiplier = databankRepMultiplier;
        this.convInit = attitudeFlavourText;
        this.forbiddenTech = forbiddenTech;
    }
    public static ArrayList<FactionResearchAttitudeData>getDataFromCSV() throws JSONException, IOException {
        ArrayList<FactionResearchAttitudeData>attitudeData = new ArrayList<>();
        JSONArray mergedArray = Global.getSettings().getMergedSpreadsheetData("factionId","data/campaign/aotd_faction_behaviour.csv");
        for (int i = 0; i < mergedArray.length(); i++) {
            JSONObject entry = mergedArray.getJSONObject(i);
            String manId = entry.getString("factionId");
            String stance = entry.getString("researchStance");
            String initResponse = entry.getString("initResponse");
            String dialogSelectOptionName = entry.getString("dialogSelectOptionName");
            String dialogDismayOptionName = entry.getString("dialogDismayOptionName");
            String responseAfter = entry.getString("responseAfter");

            ArrayList<String> forbidden_techs = AoTDMisc.loadEntries(entry.getString("forbidden_tech"),",");
            float db_cash_mult = (float) entry.getDouble("db_cash_mult");
            float db_rep_mult = (float) entry.getDouble("db_rep_mult");
            FactionResearchAttitudeData data = new FactionResearchAttitudeData(manId,AoTDMisc.getStanceFromString(stance),db_cash_mult,db_rep_mult,null,forbidden_techs);
            data.setDialogData(initResponse,dialogDismayOptionName,dialogSelectOptionName,responseAfter);
            attitudeData.add(data);
        }
        return attitudeData;
    }

}
