package data.kaysaar.aotd.vok.misc.shipinfo;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.ui.P;
import com.fs.state.AppDriver;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.UILinesRenderer;
import data.kaysaar.aotd.vok.misc.AoTDMisc;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ShipRenderer implements CustomUIPanelPlugin {
    LinkedHashMap<CustomPanelAPI, ShipRenderInfo.Module> partsOfShip = new LinkedHashMap<>();
    transient SpriteAPI spriteToRender = Global.getSettings().getSprite("rendering", "GlitchSquare");
    CustomPanelAPI absoultePanel = null;

    public void setAbsoultePanel(CustomPanelAPI absoultePanel) {
        this.absoultePanel = absoultePanel;
    }

    public void setPartsOfShip(HashMap<CustomPanelAPI, ShipRenderInfo.Module> partsOfShip, CustomPanelAPI test) {
        this.partsOfShip = sortPartsOfShipByRenderingOrder(partsOfShip);
        this.test = test;

    }

    public boolean canRender() {
        if (absoultePanel == null) return true;
        for (CustomPanelAPI panelAPI : partsOfShip.keySet()) {
            if (panelAPI.getPosition().getY() < absoultePanel.getPosition().getY() - 60) {
                return false;
            }
            if (panelAPI.getPosition().getY() > absoultePanel.getPosition().getY() + absoultePanel.getPosition().getHeight() + 60) {
                return false;
            }
        }
        return true;
    }

    private LinkedHashMap<CustomPanelAPI, ShipRenderInfo.Module> sortPartsOfShipByRenderingOrder(HashMap<CustomPanelAPI, ShipRenderInfo.Module> parts) {
        // Convert the entries of the HashMap to a list
        List<Map.Entry<CustomPanelAPI, ShipRenderInfo.Module>> entries = new ArrayList<>(parts.entrySet());

        // Sort the list based on the renderingOrder
        Collections.sort(entries, new Comparator<Map.Entry<CustomPanelAPI, ShipRenderInfo.Module>>() {
            @Override
            public int compare(Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> e1, Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> e2) {
                return Integer.compare(e1.getValue().renderingOrder, e2.getValue().renderingOrder);
            }
        });

        // Create a LinkedHashMap to preserve the sorted order
        LinkedHashMap<CustomPanelAPI, ShipRenderInfo.Module> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
    CustomPanelAPI stencilMaskBorder;

    public void setStencilMaskBorder(CustomPanelAPI stencilMaskBorder) {
        this.stencilMaskBorder = stencilMaskBorder;
    }
    public float renderingPercentage = 1f;
    boolean isUsingStencil = false;

    public void setRenderingPercentage(float renderingPercentage) {
        this.renderingPercentage = renderingPercentage;
    }

    public void setUsingStencil(boolean usingStencil) {
        isUsingStencil = usingStencil;
    }


    CustomPanelAPI test;
    public float scale = 1f;

    public void setScale(float scale) {
        this.scale = scale;
    }
    public Color colorOverride;
    public void setCollorOverride(Color color){
        colorOverride = color;
    }
    @Override
    public void positionChanged(PositionAPI position) {
        return;
    }

    @Override
    public void renderBelow(float alphaMult) {
        return;
    }

    @Override
    public void render(float alphaMult) {
//        if(test!=null){
//            spriteToRender.setColor(Color.ORANGE);
//            spriteToRender.setSize(test.getPosition().getWidth(),test.getPosition().getHeight());
//            spriteToRender.renderAtCenter(test.getPosition().getCenterX(),test.getPosition().getCenterY());
//        }
        if (!canRender()) return;
        if(isUsingStencil){
            AoTDMisc.startStencil(stencilMaskBorder,renderingPercentage);
        }
        for (Map.Entry<CustomPanelAPI, ShipRenderInfo.Module> entry : partsOfShip.entrySet()) {
            SpriteAPI sprite = Global.getSettings().getSprite(Global.getSettings().getHullSpec(entry.getValue().slotOnOriginal.id).getSpriteName());
            sprite.setAngle(entry.getValue().slotOnOriginal.angle);
            if(colorOverride!=null){
                sprite.setColor(colorOverride);
            }

            sprite.setSize((float) entry.getValue().width * scale, (float) entry.getValue().height * scale);
            sprite.renderAtCenter(entry.getKey().getPosition().getCenterX(), entry.getKey().getPosition().getCenterY());
            for (ShipRenderInfo.Slot builtInSlot : entry.getValue().built_in_slots) {
                WeaponSpecAPI weapon = Global.getSettings().getWeaponSpec(builtInSlot.id);
                String base = weapon.getTurretSpriteName();
                SpriteAPI baseSprite = Global.getSettings().getSprite(base);
                if(colorOverride!=null){
                    baseSprite.setColor(colorOverride);
                }
                setSizeOfSpriteToScale(baseSprite);
                baseSprite.setAngle(builtInSlot.angle);
                float x = entry.getKey().getPosition().getX() + entry.getValue().center.x * scale;
                float y = entry.getKey().getPosition().getY() + entry.getValue().center.y * scale;
                renderSlotMoved(baseSprite, x + (-builtInSlot.locationOnShip.x * scale), y + builtInSlot.locationOnShip.y * scale);

            }
        }
        if(isUsingStencil){
            AoTDMisc.endStencil();
        }

    }


    private static void renderSlotMoved(SpriteAPI underSprite, float x, float y) {
        underSprite.renderAtCenter(x, y);
    }

    private void setSizeOfSpriteToScale(@NotNull SpriteAPI baseSprite) {
        baseSprite.setSize((float) baseSprite.getWidth() * scale, (float) baseSprite.getHeight() * scale);
    }

    @Override
    public void advance(float amount) {
        return;
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
        return;
    }

    @Override
    public void buttonPressed(Object buttonId) {
        return;
    }
}