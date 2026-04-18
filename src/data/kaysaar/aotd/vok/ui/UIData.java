package data.kaysaar.aotd.vok.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.campaign.CharacterStats;
import com.fs.starfarer.campaign.fleet.FleetMember;
import com.fs.starfarer.loading.specs.FighterWingSpec;
import com.fs.starfarer.ui.impl.CargoTooltipFactory;
import com.fs.starfarer.ui.impl.StandardTooltipV2;
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable;

import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIData {
    public static float WIDTH = Global.getSettings().getScreenWidth();
    public static float HEIGHT = Global.getSettings().getScreenHeight()-10;



    public static void createFighterTooltip(final FleetMemberAPI fleetMember, final FighterWingSpecAPI spec,final UIComponentAPI componentAPI) {
        final Object standardTooltipV2 = ReflectionUtilis.invokeMethodDirectly(ReflectionUtilis.findStaticMethodByParameterTypes(CargoTooltipFactory.class, FleetMember.class,FighterWingSpec.class,int.class, CharacterStats.class,boolean.class),null ,fleetMember,  spec, 0, null, false);
        ReflectionUtilis.invokeStaticMethod(StandardTooltipV2Expandable.class,"addTooltipBelow", componentAPI,standardTooltipV2);
    }

    public static void createWeaponTooltip(final WeaponSpecAPI spec,  final  UIComponentAPI componentAPI) {
        final Object standardTooltipV2 =ReflectionUtilis.invokeStaticMethodWithAutoProjection(StandardTooltipV2.class,"createWeaponTooltip", spec,null,null);
        ReflectionUtilis.invokeStaticMethod(StandardTooltipV2Expandable.class,"addTooltipBelow", componentAPI,standardTooltipV2);
    }

    public static void createTooltipForShip(final FleetMemberAPI fleetMemberAPI, final UIComponentAPI componentAPI) {
       final Object standardTooltipV2 =ReflectionUtilis.invokeStaticMethodWithAutoProjection(StandardTooltipV2.class,"createFleetMemberExpandedTooltip", fleetMemberAPI,null);
       ReflectionUtilis.invokeStaticMethod(StandardTooltipV2Expandable.class,"addTooltipBelow", componentAPI,standardTooltipV2);
    }

    private static float getCenter(float beginX, float width) {
        ;
        float endX = beginX + width;
        float widthOfSection = endX - beginX;
        float center = beginX + widthOfSection / 2;
        return center;
    }
    public static ArrayList<RowData> calculateAmountOfRows(float widthOfRow, LinkedHashMap<String, Integer> designs, float xPadding) {
        ArrayList<RowData> data = new ArrayList<>();
        float currentX = 0;
        float rows = 0;
        RowData daten = new RowData(rows, new LinkedHashMap<String, Integer>());
        LabelAPI dummy = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);
        for (Map.Entry<String, Integer> entry : designs.entrySet()) {
            if (entry.getValue() == 0) continue;
            String txt = entry.getKey() + "(" + entry.getValue() + ")";
            float widthOfButton = dummy.computeTextWidth(txt) + 30;
            currentX += widthOfButton;
            if (currentX > widthOfRow) {
                currentX = widthOfButton;
                rows++;
                data.add(daten);
                daten = new RowData(rows, new LinkedHashMap<String, Integer>());
            }
            daten.stringsInRow.put(txt, (int) widthOfButton);
            currentX += xPadding;
        }
        data.add(daten);
        return data;
    }

    private static float getxPad(LabelAPI buildTime, float center) {
        return center - (buildTime.computeTextWidth(buildTime.getText()) / 2);
    }




}
