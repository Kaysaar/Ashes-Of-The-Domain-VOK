package data.kaysaar.aotd.vok.campaign.econ.globalproduction.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUIAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProductionUtil {
    public static UIPanelAPI getCoreUI() {
        CampaignUIAPI campaignUI;
        campaignUI = Global.getSector().getCampaignUI();
        InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

        CoreUIAPI core;
        if (dialog == null) {
            core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCore",campaignUI);
        }
        else {
            core = (CoreUIAPI) ReflectionUtilis.invokeMethod( "getCoreUI",dialog);
        }
        return core == null ? null : (UIPanelAPI) core;
    }

    public static UIPanelAPI getCurrentTab() {
        UIPanelAPI coreUltimate = getCoreUI();
        UIPanelAPI core = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab",coreUltimate);
        return core == null ? null : (UIPanelAPI) core;
    }

    public static Object getField(Object o, String fieldName) {
        return getFieldExplicitClass(o.getClass(), o, fieldName);
    }


    public static Object getFieldExplicitClass(Class<?> cls, Object o, String fieldName) {
        if (o == null) return null;
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(o);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Object o, String fieldName, Object to) {
        setFieldExplicitClass(o.getClass(), o, fieldName, to);
    }

    public static void setFieldExplicitClass(Class<?> cls, Object o, String fieldName, Object to) {
        if (o == null) return;
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(o, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object invokeGetter(Object o, String methodName, Object... args) {
        if (o == null) return null;
        try {
            Class<?>[] argClasses = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                argClasses[i] = args[i].getClass();
                // unbox
                if (argClasses[i] == Integer.class) {
                    argClasses[i] = int.class;
                }
                else if (argClasses[i] == Boolean.class) {
                    argClasses[i] = boolean.class;
                }
                else if (argClasses[i] == Float.class) {
                    argClasses[i] = float.class;
                }
            }
            Method method = o.getClass().getMethod(methodName, argClasses);
            return method.invoke(o, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
