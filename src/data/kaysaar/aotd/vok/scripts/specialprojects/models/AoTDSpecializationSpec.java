package data.kaysaar.aotd.vok.scripts.specialprojects.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

public class AoTDSpecializationSpec {
    public String name;
    public String id;
    public Color colorOfString;
    public  int order;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Color getColorOfString() {
        return colorOfString;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColorOfString(Color colorOfString) {
        this.colorOfString = colorOfString;
    }

    public static AoTDSpecializationSpec getSpecFromJson(JSONObject obj)throws JSONException {
        String id = obj.getString("id");
        if (id == null || id.isEmpty()) return null;
        String name = obj.getString("name");
        Color colorOfString = new Color(obj.getInt("red"), obj.getInt("green"), obj.getInt("blue"));
        AoTDSpecializationSpec spec = new AoTDSpecializationSpec();
        spec.name = name;
        spec.id = id;
        spec.colorOfString = colorOfString;
        spec.order = obj.getInt("order");
        return spec;

    }
}
