package data.kaysaar.aotd.vok.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Misc;

public class MusicEnforcerScript implements EveryFrameScript {
    public  int timesRan = 0;
    public  int timesRan2 = 0;
    public void delete(){
        Global.getSector().removeTransientScriptsOfClass(this.getClass());
    }
    public static void addScript(String musicId, boolean loopable ){
        MusicEnforcerScript script = new MusicEnforcerScript();
        script.setMusicId(musicId);
        script.setLoopable(loopable);
        Global.getSector().addTransientScript(script);
    }
    String musicId;
    boolean loopable;
    boolean alreadyPlayed = false;
    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public void setLoopable(boolean loopable) {
        this.loopable = loopable;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if(timesRan<=15){
            timesRan++;
        }
        if (timesRan < 2) return;
        if(Global.getSector().getCampaignUI().getCurrentInteractionDialog()!=null){
            if(!alreadyPlayed){
                Global.getSoundPlayer().playCustomMusic(1, 1, musicId, loopable);
                alreadyPlayed = true;
            }


        }
        else{
            if(timesRan2<=15){
                timesRan2++;
                Global.getSoundPlayer().playCustomMusic(1, 1, musicId, loopable);
            }

            if(timesRan2 < 6) return;

            Global.getSoundPlayer().restartCurrentMusic();
            delete();
        }


    }
}
