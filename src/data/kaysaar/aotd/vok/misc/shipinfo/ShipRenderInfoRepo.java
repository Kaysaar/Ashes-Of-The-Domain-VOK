package data.kaysaar.aotd.vok.misc.shipinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import static data.kaysaar.aotd.vok.misc.AoTDMisc.cleanPath;
import static data.kaysaar.aotd.vok.misc.AoTDMisc.getVaraint;

public class ShipRenderInfoRepo {
    public static final Logger log = Global.getLogger(ShipRenderInfoRepo.class);
    public static HashMap<String,ShipRenderInfo>renderInfoRepo  =new HashMap<>();
    public static void populateRenderInfoRepo(){
        for (ShipHullSpecAPI allShipHullSpec : Global.getSettings().getAllShipHullSpecs()) {
            populateShip(allShipHullSpec);
        }
        for (FighterWingSpecAPI allShipHullSpec : Global.getSettings().getAllFighterWingSpecs()) {
            try {
                ShipRenderInfo info = new ShipRenderInfo( allShipHullSpec.getVariant().getHullSpec().getHullId(),true);
                renderInfoRepo.put(allShipHullSpec.getVariant().getHullSpec().getHullId(),info);
            }  catch (Exception e) {
                log.info("Fighter wing id : "+allShipHullSpec.getVariant().getHullSpec().getHullId()+" not loaded!");
            }

        }
    }

    public static void populateShip(ShipHullSpecAPI allShipHullSpec) {
        if(allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.STATION)) return;
        try {
            if(allShipHullSpec.getBaseHullId().contains("atlas2")){
                String test ="a";

            }
            ShipRenderInfo info = new ShipRenderInfo(allShipHullSpec.getBaseHullId(),false);
            if(allShipHullSpec.getHints().contains(ShipHullSpecAPI.ShipTypeHints.SHIP_WITH_MODULES)){
                String variantId = getVaraint(allShipHullSpec);
                if(variantId==null){
                    variantId = getVaraint(allShipHullSpec.getBaseHull());
                }

              String filepath = Global.getSettings().getVariant(variantId).getVariantFilePath();
                if(filepath==null){
                    filepath = Global.getSettings().getVariant( getVaraint(allShipHullSpec.getBaseHull())).getVariantFilePath();
                }
                filepath = cleanPath(filepath);
                filepath =  filepath.replace("\\", "/");
                info.getModuleSlotsFromVariantFile(filepath);
                info.populateSlotShipHullsMap();
                info.populateModuleList(allShipHullSpec.getHullId());
                ShipRenderInfo.Module mod = info.createModule(info.center, info.width, info.height, info.center, allShipHullSpec.getHullId(), -100);
                info.setCentralModule(mod);
            }else{
                ShipRenderInfo.Module mod = info.createModule(info.center, info.width, info.height, info.center, allShipHullSpec.getHullId(), -1);
                info.setCentralModule(mod);
            }
            renderInfoRepo.put(allShipHullSpec.getHullId(),info);
        } catch (Exception e) {
            if(allShipHullSpec.getHullId().contains("cerberus")){
                log.info("Hull id : "+ allShipHullSpec.getHullId()+" not loaded!");
            }
            log.info("Hull id : "+ allShipHullSpec.getHullId()+" not loaded!");
        }
    }


}
