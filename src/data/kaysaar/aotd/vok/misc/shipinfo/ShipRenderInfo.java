package data.kaysaar.aotd.vok.misc.shipinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class ShipRenderInfo {
    //Remember locations here are from BOTTOM LEFT
    static String static_filepath = "data/hulls/";
    static String format= ".ship";
    static String filepath2 = "data/hulls/skins/";
    static String format2= ".skin";
    public class Slot{
        public Vector2f locationOnShip;
        public String id;
        public float angle;
        public boolean isWeapon;
        public Slot(Vector2f locationOnShip, String moduleHullID, float angle, boolean isWeapon){
            this.locationOnShip = locationOnShip;
            this.id = moduleHullID;
            this.angle = angle;
            this.isWeapon = isWeapon;

        }

    }
    public Module centralModule;

    public  Module createModule(Vector2f center, double width, double height, Vector2f locationOnShip, String moduleID,int renderingOrder){
        Module mod = new Module(center,width,height,new Slot(locationOnShip,moduleID,0,false),renderingOrder);

        return mod;
    }

    public void setCentralModule(Module centralModule) {
        this.centralModule = centralModule;
    }

    public Module getCentralModule() {
        return centralModule;
    }

    public class Module{
        public Slot slotOnOriginal;
        public double  width;
        public double  height;
        public Vector2f center;
        public ArrayList<Slot>built_in_slots;
        public int renderingOrder;
        public Module (Vector2f center, double width, double height,Slot slotOnOriginal, int renderingOrder){
            this.center = center;
            this.width = width;
            this.height = height;
            this.slotOnOriginal = slotOnOriginal;
            this.renderingOrder = renderingOrder;
            this.built_in_slots = populateBuiltInList(slotOnOriginal.id,false);
        }


    }
    boolean isFighter;
    public ShipRenderInfo(String hullID,boolean isFighter) throws JSONException, IOException {
        this.hullId = hullID;
        JSONObject obj = null;
        if(hullID.equals("atlas2_default_D")){
            int a =2;
        }
        obj = getShipJson(hullID);
        this.width = obj.getDouble("width");
        this.height = obj.getDouble("height");
        JSONArray locationArray = obj.getJSONArray("center");
        float x = (float) locationArray.getDouble(0);
        float y = (float) locationArray.getDouble(1);
        this.center = new Vector2f(x,y);
        this.isFighter = isFighter;
        this.built_in_slots = populateBuiltInList(hullID,isFighter);

    }

    private static JSONObject getShipJson(String hullID) throws IOException, JSONException {
        JSONObject obj;
        try {
            String Filepath = Global.getSettings().getHullSpec(hullID).getShipFilePath();
            Filepath = AoTDMisc.cleanPath(Filepath);
            Filepath =Filepath.replace("\\", "/");
            obj = Global.getSettings().loadJSON(Filepath);
            obj.getDouble("width");


        }
        catch (Exception e){
            String Filepath = Global.getSettings().getHullSpec(hullID).getShipFilePath();
            Filepath = AoTDMisc.cleanPath(Filepath);
            Filepath =Filepath.replace("\\", "/");
            JSONObject obj2 =  Global.getSettings().loadJSON(Filepath);
            String hullIdProper = obj2.getString("baseHullId");
            Filepath = Global.getSettings().getHullSpec(hullIdProper).getShipFilePath();
            Filepath = AoTDMisc.cleanPath(Filepath);
            Filepath =Filepath.replace("\\", "/");
            obj = Global.getSettings().loadJSON(Filepath);


        }
        return obj;
    }

    public double  width;
    public double  height;
    public String hullId;
    public Vector2f center;

    HashMap<String,String>moduleSlotsMapVariants;
    HashMap<String,String>moduleSlotsShipHulls;
    public ArrayList<Module>moduleSlotsOnOriginalShip;
    public ArrayList<Slot>built_in_slots;
   public void getModuleSlotsFromVariantFile(String filepath) throws JSONException, IOException {
        String betterFilePath =  filepath.replace("\\", "/");
       JSONObject object = Global.getSettings().loadJSON(betterFilePath);

       // Create a HashMap to store the module slots
       HashMap<String, String> moduleSlots = new HashMap<String, String>();

       // Check if the JSON object contains the "modules" key
       if (object.has("modules")) {
           // Get the modules array from the JSON object
           try {
               JSONArray modulesArray = object.getJSONArray("modules");

               // Iterate through the modules array
               for (int i = 0; i < modulesArray.length(); i++) {
                   // Get each module JSONObject
                   JSONObject moduleObject = modulesArray.getJSONObject(i);

                   // Iterate through the keys of the moduleObject
                   Iterator<String> keys = moduleObject.keys();
                   while (keys.hasNext()) {
                       String key = keys.next();
                       // Get the value for each key
                       String value = moduleObject.getString(key);

                       // Put the key-value pair into the HashMap
                       moduleSlots.put(key, value);
                   }
               }
           } catch (Exception e) {
               JSONObject modulesArray = object.getJSONObject("modules");
               Iterator<String> keys = modulesArray.keys();
               while (keys.hasNext()) {
                   String key = keys.next();
                   // Get the value for each key
                   String value = modulesArray.getString(key);

                   // Put the key-value pair into the HashMap
                   moduleSlots.put(key, value);
               }
           }
       }



       this.moduleSlotsMapVariants = moduleSlots;

   }
   public void populateSlotShipHullsMap()  throws JSONException, IOException{
       moduleSlotsShipHulls = new HashMap<>();
       for (Map.Entry<String, String> entry : moduleSlotsMapVariants.entrySet()) {

           String filepath  = Global.getSettings().getVariant(entry.getValue()).getVariantFilePath();
           filepath = AoTDMisc.cleanPath(filepath);
           filepath = filepath.replace("\\", "/");
           JSONObject obj = Global.getSettings().loadJSON(filepath);
           String shipHullId = obj.getString("hullId");
           moduleSlotsShipHulls.put(entry.getKey(),shipHullId);

       }
   }
   public ArrayList<Slot> populateBuiltInList(String hullID,boolean IsFighter){
       ArrayList<Slot>slots = new ArrayList<>();
       try {
           JSONObject obj =getShipJson(hullID);
           int current =0;
           LinkedHashMap<String,String> builtIn = new LinkedHashMap<>();
           try {
               JSONObject builtInWeapons = obj.getJSONObject("builtInWeapons");
               Iterator<String> keys = builtInWeapons.keys();
               while (keys.hasNext()) {
                   String key = keys.next();
                   String value = builtInWeapons.getString(key);
                   builtIn.put(key, value);
               }

               JSONArray weaponSlots = obj.getJSONArray("weaponSlots");
               for (int i = 0; i < weaponSlots.length(); i++) {
                   JSONObject slot = weaponSlots.getJSONObject(i);
                   String id = slot.getString("id");
                   if(builtIn.get(id)!=null){
                       JSONArray locationArray = slot.getJSONArray("locations");
                       float x = (float) locationArray.getDouble(1);
                       float y = (float) locationArray.getDouble(0);
                       Vector2f locationOnShip = new Vector2f(x,y);
                       float angle = slot.getInt("angle");
                       if(angle<0){
                           angle = 360 + angle;
                       }
                       if(slot.get("type").equals("DECORATIVE")){
                           Slot weaponSlot = new Slot(locationOnShip,builtIn.get(id),angle,true);
                           slots.add(weaponSlot);
                       }
                       if(IsFighter){
                           Slot weaponSlot = new Slot(locationOnShip,builtIn.get(id),angle,true);
                           slots.add(weaponSlot);
                       }

                   }
               }
           }
           catch (Exception e){
               return slots;
           }


       } catch (Exception e) {
           throw new RuntimeException(e);
       }
       return slots;
   }
   public void populateModuleList(String hullID) throws JSONException, IOException{
        moduleSlotsOnOriginalShip = new ArrayList<>();
        if(hullID.contains("slv")){
            int a = 1;
        }
       JSONObject obj = getShipJson(hullID);
       int current =0;
       JSONArray weaponSlots = obj.getJSONArray("weaponSlots");
       for (int i = 0; i < weaponSlots.length(); i++) {
           JSONObject slot = weaponSlots.getJSONObject(i);
           String id = slot.getString("id");
           if(moduleSlotsShipHulls.get(id)!=null){
               JSONArray locationArray = slot.getJSONArray("locations");
               float x = (float) locationArray.getDouble(1);
               float y = (float) locationArray.getDouble(0);
               Vector2f locationOnShip = new Vector2f(x,y);
               float angle = slot.getInt("angle");
               if(angle<0){
                   angle = 360 + angle;
               }
               Slot shipSlot = new Slot(locationOnShip,moduleSlotsShipHulls.get(id),angle,false);
               JSONObject moduleHull =getShipJson(moduleSlotsShipHulls.get(id));
               double width = moduleHull.getDouble("width");
               double height = moduleHull.getDouble("height");
               JSONArray locationArrayModule = moduleHull.getJSONArray("center");
               float xMod = (float) locationArrayModule.getDouble(0);
               float yMod = (float) locationArrayModule.getDouble(1);
               Vector2f center = new Vector2f(xMod,yMod);
               int prev = current;
               if(Global.getSettings().getHullSpec(moduleSlotsShipHulls.get(id)).getHints().contains(ShipHullSpecAPI.ShipTypeHints.UNDER_PARENT)){
                   current=-10000+prev;
               }
               Module module = new Module(center,width,height,shipSlot,current);
               moduleSlotsOnOriginalShip.add(module);
               current=prev;
               current++;
           }
       }
   }
   

}
