package data.kaysaar.aotd.vok.scripts.research.scientist.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import data.kaysaar.aotd.vok.scripts.research.scientist.scripts.ScientistScriptUnlock;

import java.util.ArrayList;

public class ScientistValidationListener implements EconomyTickListener {
    public  ArrayList<ScientistScriptUnlock>scripts = new ArrayList<>();

    public ArrayList<ScientistScriptUnlock> getScripts() {
        return scripts;
    }
    public static ScientistValidationListener getInstance(){
        if(!Global.getSector().getListenerManager().hasListenerOfClass(ScientistValidationListener.class)){
            Global.getSector().getListenerManager().addListener(new ScientistValidationListener());
        }
        return Global.getSector().getListenerManager().getListeners(ScientistValidationListener.class).iterator().next();
    }

    public void addScript(ScientistScriptUnlock script) {
        boolean exists = scripts.stream().anyMatch(s -> s.getClass() == script.getClass());
        if (!exists) {
            scripts.add(script);
        }
    }

    public void removeScript(Script script){
        scripts.remove(script);
    }

    @Override
    public void reportEconomyTick(int iterIndex) {
        scripts.stream().filter(ScientistScriptUnlock::shouldRun).forEach(ScientistScriptUnlock::run);
    }

    @Override
    public void reportEconomyMonthEnd() {

    }
}
