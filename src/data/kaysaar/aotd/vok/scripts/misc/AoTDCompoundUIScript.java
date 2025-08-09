package data.kaysaar.aotd.vok.scripts.misc;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts.ProductionUtil;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;
import data.kaysaar.aotd.vok.ui.customprod.components.UILinesRenderer;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;

public class AoTDCompoundUIScript implements EveryFrameScript {
    public transient boolean removed = false;
    public static
    float frames = 0f;

    public static int getYPad() {

        return 45;
    }

    @Override
    public boolean isDone() {
        return removed;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        frames++;
        if (frames < 4) return;
        if (!removed) {
            UIPanelAPI coreUI = ProductionUtil.getCoreUI();
            UIPanelAPI leChildren = null;
            UIPanelAPI leGrandChildren = null;
            UIPanelAPI testing = null;
            if (coreUI != null) {
                for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(coreUI)) {
                    if (ReflectionUtilis.hasMethodOfName("getDisplaySensorRange", componentAPI)) {
                        leChildren = (UIPanelAPI) componentAPI;
                        break;
                    }
                }
            }
            if (leChildren != null) { //14
                ArrayList<UIComponentAPI> componentAPIS = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leChildren);
                for (UIComponentAPI componentAPI : componentAPIS) {
                    if (componentAPI instanceof CustomPanelAPI) {
                        if (((CustomPanelAPI) componentAPI).getPlugin() instanceof UILinesRenderer) {
                            removed = true;
                            Global.getSector().removeTransientScriptsOfClass(this.getClass());
                            return;
                        }
                    }
                }
                for (UIComponentAPI componentAPI : componentAPIS) {
                    if (ReflectionUtilis.hasMethodOfName("getValueDelta", componentAPI)) {
                        Object delta = ReflectionUtilis.invokeMethodWithAutoProjection("getValueDelta", componentAPI);
                        String font = (String) ReflectionUtilis.invokeMethodWithAutoProjection("getFont", delta);
                        if (font.equals("graphics/fonts/orbitron20aabold.fnt")) {
                            testing = (UIPanelAPI) componentAPI;
                        }
                    }
                }

                for (UIComponentAPI componentAPI : componentAPIS) {
                    if (ReflectionUtilis.hasMethodOfName("getFuelPerDay", componentAPI)) {
                        leGrandChildren = (UIPanelAPI) componentAPI;
                        break;
                    }
                }

            }


            if (leGrandChildren != null) {
                ArrayList<UIComponentAPI> grandChildrenComponents = (ArrayList<UIComponentAPI>) ReflectionUtilis.getChildrenCopy((UIPanelAPI) leGrandChildren);
                if (grandChildrenComponents.size() == 4) {
                    UIComponentAPI grandGrandChild = grandChildrenComponents.get(2);
                    Vector2f xy = new Vector2f(grandGrandChild.getPosition().getX(), grandGrandChild.getPosition().getY());
                    UILinesRenderer renderer = new UILinesRenderer(0f);
                    renderer.setBoxColor(Color.MAGENTA);

                    CustomPanelAPI testings = Global.getSettings().createCustom(grandGrandChild.getPosition().getWidth(), grandGrandChild.getPosition().getHeight(), renderer);
                    CustomPanelAPI insider = testings.createCustomPanel(testing.getPosition().getWidth(), testing.getPosition().getHeight(), null);
                    testings.addComponent(insider).inTL(-5, 0);
                    insider.getPosition().setSuspendRecompute(false);
                    CustomPanelAPI main = new AoTDCompoundShowcase(insider.getPosition().getWidth() + 10, insider.getPosition().getHeight()).getMainPanel();
                    insider.addComponent(main);
                    TooltipMakerAPI tooltip = testings.createUIElement(1, 1, true);
                    tooltip.addTooltipTo(new AoTDFuelTooltip(), main, TooltipMakerAPI.TooltipLocation.ABOVE);
                    leChildren.addComponent(testings).aboveLeft(testing, getYPad());


                    return;
                }


            }


        }
    }
}
