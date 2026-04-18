package data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.megastructures.impl.sections.bifrost.BifrostSection;

import java.util.ArrayList;

public class BifrostMegastructureManager {
    //This is unique megastructure it can't be tied to any entity
    public BifrostMegastructure megastructure;
    public static String memKey = "$aotd_bifrost_manager";
    public static BifrostMegastructureManager getInstance(){
        if(Global.getSector().getPersistentData().get(memKey)==null){
            Global.getSector().getPersistentData().put(memKey, new BifrostMegastructureManager());
        }
        return (BifrostMegastructureManager) Global.getSector().getPersistentData().get(memKey);
    }
    public BifrostMegastructureManager(){
        this.megastructure = new BifrostMegastructure();
        megastructure.trueInit("aotd_bifrost",null,null);
    }
    public ArrayList<StarSystemAPI>getStarSystemsOfPlayer(){
        ArrayList<StarSystemAPI> systems = new ArrayList<>();

        for (MarketAPI playerMarket : Misc.getPlayerMarkets(true)) {
            if(!systems.contains(playerMarket.getStarSystem())){
                systems.add(playerMarket.getStarSystem());
            }
        }
        return systems;
    }
    public ArrayList<StarSystemAPI>getStarSystemsOfPlayerWithBifrost(){
        ArrayList<StarSystemAPI> systems = new ArrayList<>();

        megastructure.getSections().forEach(x->systems.add(x.getStarSystemAPI()));
        return systems;
    }
    public BifrostMegastructure getMegastructure() {
        return megastructure;
    }

    public void addNewBifrostSection(StarSystemAPI system){
        BifrostSection section = new BifrostSection();
        section.init(megastructure,false,"bifrost_section");
        section.setStarSystemAPI(system);
        section.startRestoration();
    }

}
