package data.kaysaar.aotd.vok.ui.customprod;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.Ids.AoTDTechIds;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpecManager;
import data.kaysaar.aotd.vok.scripts.research.AoTDMainResearchManager;
import data.kaysaar.aotd.vok.ui.onhover.ButtonOnHoverInfo;

import java.awt.*;
import java.util.List;

public class ProductionTypesSection implements ExtendedUIPanelPlugin {
    public ButtonAPI ship,weapon,fighter,item;
    public ButtonAPI curr = null;
    public CustomPanelAPI mainPanel;
    public boolean needsUpdate = false;

    public boolean isNeedsUpdate() {
        return needsUpdate;
    }

    public void setNeedsUpdate(boolean needsUpdate) {
        this.needsUpdate = needsUpdate;
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    public ProductionTypesSection(){
        LabelAPI label = Global.getSettings().createLabel("", Fonts.DEFAULT_SMALL);

        mainPanel = Global.getSettings().createCustom(500,20,this);
        TooltipMakerAPI tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),false);
        ship = tooltip.addButton("Ships", AoTDProductionSpec.AoTDProductionSpecType.SHIP, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,label.computeTextWidth("ships")+30,20,0f);
        ship.getPosition().inTL(0,0);
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order ships to be build in dockyards, and they will be delivered to gathering point when completed.", null, null, null, "Ship building section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        weapon = tooltip.addButton("Weapons", AoTDProductionSpec.AoTDProductionSpecType.WEAPON, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,label.computeTextWidth("weapons")+30,20,0f);
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order weapons to be crafted in factories, and they will be delivered to gathering point when completed.", null, null, null, "Weapon crafting section"), TooltipMakerAPI.TooltipLocation.BELOW, false);

        fighter = tooltip.addButton("Fighters", AoTDProductionSpec.AoTDProductionSpecType.FIGHTER, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,label.computeTextWidth("fighters")+30,20,0f);
        tooltip.addTooltipToPrevious(new ButtonOnHoverInfo(400, false, null, "Here you can order fighters to be assembled in shipyards, and they will be delivered to gathering point when completed.", null, null, null, "Fighter assembly section"), TooltipMakerAPI.TooltipLocation.BELOW, false);
        item = tooltip.addButton("Items", AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM, Misc.getBasePlayerColor(),Misc.getDarkPlayerColor(),Alignment.MID,CutStyle.TOP,label.computeTextWidth("items")+30,20,0f);
        final boolean has = AoTDProductionSpecManager.getLearnedSpecsForFaction(AoTDProductionSpec.AoTDProductionSpecType.SPECIAL_ITEM,Global.getSector().getPlayerFaction()).isEmpty();
        tooltip.addTooltipToPrevious(new TooltipMakerAPI.TooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 500;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addTitle("Colony item forge section");
                if (!has) {
                    tooltip.addPara("We have no schematics of any colony item!", Misc.getNegativeHighlightColor(), 10f);
                    tooltip.addPara("To gain access we need to either find blueprints located in %s", 5f, Color.ORANGE, "Pre Collapse Facilities");
                    tooltip.addPara("Or research %s", 5f, Color.ORANGE, AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getName());

                } else {
                    tooltip.addPara("This section leads to colony item production", 10f);
                    tooltip.addPara("With knowledge we have found and might of our industries we rise from Ashes of The Domain", 10f);
                    tooltip.addPara("To expand items we can craft we need either find blueprints located in %s", 5f, Color.ORANGE, "Pre Collapse Facilities");
                    tooltip.addPara("Or research %s", 5f, Color.ORANGE, AoTDMainResearchManager.getInstance().getSpecForSpecificResearch(AoTDTechIds.DOMAIN_TYPE_MODEL_STANDARDIZATION).getName());
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW, false);
        item.setEnabled(!has);
        curr = ship;
        curr.highlight();
        weapon.getPosition().rightOfMid(ship,5);
        fighter.getPosition().rightOfMid(weapon,5);
        item.getPosition().rightOfMid(fighter,5);
        mainPanel.addUIElement(tooltip).inTL(0,5);

    }

    @Override
    public void createUI() {

    }

    @Override
    public void clearUI() {

    }

    @Override
    public void positionChanged(PositionAPI position) {

    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {
        if(ship!=null){
            if(ship.isChecked()){
                ship.setChecked(false);
                curr.unhighlight();
                curr = ship;
                curr.highlight();
                setNeedsUpdate(true);
            }
            if(weapon.isChecked()){
                weapon.setChecked(false);
                curr.unhighlight();
                curr = weapon;
                curr.highlight();
                setNeedsUpdate(true);
            }
            if(fighter.isChecked()){
                fighter.setChecked(false);
                curr.unhighlight();
                curr = fighter;
                curr.highlight();
                setNeedsUpdate(true);
            }
            if(item.isChecked()){
                item.setChecked(false);
                curr.unhighlight();
                curr = item;
                curr.highlight();
                setNeedsUpdate(true);
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
