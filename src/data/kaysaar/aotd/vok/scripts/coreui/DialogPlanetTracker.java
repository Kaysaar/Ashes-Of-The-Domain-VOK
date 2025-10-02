package data.kaysaar.aotd.vok.scripts.coreui;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.VisualPanelAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.campaign.CampaignEntity;
import com.fs.starfarer.campaign.CampaignPlanet;
import com.fs.starfarer.combat.entities.terrain.Planet;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.impl.nidavelir.NidavelirComplexMegastructure;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.models.megastructures.GPBaseMegastructure;
import data.kaysaar.aotd.vok.listeners.CoreUiInterceptor;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import static data.kaysaar.aotd.vok.listeners.CoreUiInterceptor.initalizeBackgroundPLanetForOtherDialog;

public class DialogPlanetTracker implements EveryFrameScript {
    public static boolean didIt = false;
    transient UIComponentAPI saved;
    transient UIComponentAPI ring;
    BackgroundInterlooper looper ;
    BackgroundInterlooper coreUILooper ;
    public boolean switchedToPanelDuringInteraction = false;
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
        if (Global.getSector().getCampaignUI().getCurrentInteractionDialog() == null) {
            saved = null;
            ring = null;
            didIt = false;
            return;

        }
        InteractionDialogAPI dialog = Global.getSector().getCampaignUI().getCurrentInteractionDialog();
        VisualPanelAPI panel = dialog.getVisualPanel();
        if(dialog.getInteractionTarget() instanceof PlanetAPI planeta){
            if(planeta.getMemory().get(GPBaseMegastructure.memKey) instanceof NidavelirComplexMegastructure megastructure){
                if (!didIt) {
                    if (AoTDMisc.getNidavelir() != null) {
                        for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy((UIPanelAPI) panel)) {
                            if(ReflectionUtilis.hasMethodOfName("getPlanet",componentAPI)&&ReflectionUtilis.hasMethodOfName("setForce2dMode",componentAPI)){
                                saved = componentAPI;
                                this.looper  =initalizeBackgroundPLanetForOtherDialog(megastructure, (UIPanelAPI) componentAPI, (UIPanelAPI) panel);
                                didIt = true;
                            }
                            else if  (ReflectionUtilis.hasMethodOfName("getPlanet", componentAPI)) {
                                saved = componentAPI;
                                ring = (UIComponentAPI) ReflectionUtilis.instantiate(saved.getClass(), new CampaignPlanet((String) null, (String) null, megastructure.shipyard.getType(), dialog.getInteractionTarget().getRadius() + 35, (CampaignEntity) dialog.getInteractionTarget().getLightSource()), 500f, 500f, true);
                                ((UIPanelAPI) panel).addComponent(ring).inTL(saved.getPosition().getX() - 55, saved.getPosition().getCenterY() - 400);
                                Planet planet = (Planet) ReflectionUtilis.invokeMethod("getGraphics", ring);
                                Planet curr = (Planet) ReflectionUtilis.invokeMethod("getGraphics", saved);
                                planet.setTilt(curr.getTilt());
                                planet.setAngle(megastructure.shipyard.getCurrAngle());
                                planet.setPitch(curr.getPitch());
                                didIt = true;
                                break;
                            }
                        }
                    }

                }
                if(CoreUITabId.OUTPOSTS.equals(Global.getSector().getCampaignUI().getCurrentCoreTab())&&!switchedToPanelDuringInteraction&&saved!=null){
                  CoreUiInterceptor.initalizeBackgroundPLanetWithPreSetOpacity(megastructure,0.1f);

                }
                if (!ReflectionUtilis.getChildrenCopy((UIPanelAPI) panel).contains(saved) || saved.getOpacity() == 0f) {
                    saved = null;
                    ((UIPanelAPI) panel).removeComponent(ring);
                    if(looper != null){
                        ((UIPanelAPI) panel).removeComponent(looper.getMainPanel());
                        looper = null;
                    }
                    ring = null;
                    didIt = false;
                }
                ;
                if (saved != null && ring != null) {
                    Planet planet = (Planet) ReflectionUtilis.findFieldOfClass(ring,Planet.class);
                    Planet curr = (Planet) ReflectionUtilis.findFieldOfClass(saved,Planet.class);
                    planet.setTilt(curr.getTilt());
                    planet.setPitch(curr.getPitch());
                    curr.setAngle(planet.getAngle());
                }

            }


        }

    }

}
