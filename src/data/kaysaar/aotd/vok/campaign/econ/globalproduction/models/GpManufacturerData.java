package data.kaysaar.aotd.vok.campaign.econ.globalproduction.models;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class GpManufacturerData {
    String manufacturerId;
    int ac_ship_max;

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setAc_ship_max(int ac_ship_max) {
        this.ac_ship_max = ac_ship_max;
    }

    public void setAc_wp_small(int ac_wp_small) {
        this.ac_wp_small = ac_wp_small;
    }

    public void setAc_wp_medium(int ac_wp_medium) {
        this.ac_wp_medium = ac_wp_medium;
    }

    public void setAc_wp_large(int ac_wp_large) {
        this.ac_wp_large = ac_wp_large;
    }

    public void setAc_fighter_max(int ac_fighter_max) {
        this.ac_fighter_max = ac_fighter_max;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    int ac_wp_small;
    int ac_wp_medium;
    int ac_wp_large;
    int ac_fighter_max;

    public static ArrayList<GpManufacturerData>getManufacturerDataFromCSV(){
        ArrayList<GpManufacturerData>data = new ArrayList<>();
        try {
            JSONArray csvFile = Global.getSettings().loadCSV("data/campaign/aotd_production_manufacturer_data.csv");
            for (int i = 0; i < csvFile.length(); i++) {
                JSONObject entry = csvFile.getJSONObject(i);
                String manId = entry.getString("man_id");
                int ac_ship_max = entry.getInt("ac_ship_max");
                int ac_wp_small = entry.getInt("ac_wp_small");
                int ac_wp_medium = entry.getInt("ac_wp_medium");
                int ac_wp_large = entry.getInt("ac_wp_large");
                int ac_fighter_max = entry.getInt("ac_fighter_max");
                GpManufacturerData daten = new GpManufacturerData();
                daten.setManufacturerId(manId);
                daten.setAc_fighter_max(ac_fighter_max);
                daten.setAc_ship_max(ac_ship_max);
                daten.setAc_wp_small(ac_wp_small);
                daten.setAc_wp_medium(ac_wp_medium);
                daten.setAc_wp_large(ac_wp_large);
                data.add(daten);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
    public  int getMaxACCostForShip(){
        return ac_ship_max;
    }
    public int getMaxAcCostForWeapon(WeaponSpecAPI specAPI){
        if(specAPI.getSize().equals(WeaponAPI.WeaponSize.SMALL)){
            return ac_wp_small;

        } else if (specAPI.getSize().equals(WeaponAPI.WeaponSize.MEDIUM)) {
            return ac_wp_medium;
        }
        else{
            return ac_wp_large;
        }
    }
    public  int getMaxACCostForFighter(){
        return ac_fighter_max;
    }
}
