package data.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.ScientistAICoreBarEvent;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.ui.P;
import data.Ids.AodResearcherSkills;
import data.plugins.AoDUtilis;
import data.scripts.research.ResearchAPI;
import data.scripts.research.ResearchOption;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static data.plugins.AoDCoreModPlugin.aodTech;

public class HeadOfResearchCenterUI  implements CustomDialogDelegate {
    public static final float WIDTH = 600;
    public ResearchAPI researchAPI = (ResearchAPI) Global.getSector().getPersistentData().get(aodTech);
    public static final float HEIGHT = Global.getSettings().getScreenHeight() - 600f;
    public static final float ENTRY_HEIGHT = 130; //MUST be even
    public static final float ENTRY_WIDTH = WIDTH - 5f; //MUST be even
    public static final float CONTENT_HEIGHT = 80;

    public PersonAPI selected = null;
    public List<ButtonAPI> buttons = new ArrayList<>();

    public PersonAPI currentResearcher;
    public HeadOfResearchCenterUI(PersonAPI currentHead) {
        this.currentResearcher = currentHead;

    }
    public String getDescrpForResearcher(PersonAPI personAPI){
        if(personAPI.hasTag(AodResearcherSkills.RESOURCEFUL)){
            return "That scientist is very cautious and wants to use as little resources to accomplish task as possible\nDecrease upkeep of research facilities by 50%.";
        }
        if(personAPI.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)){
            return "This scientist possesses knowledge that for many has been already deemed to be lost in sands of time\nNegate need for special type of databanks for research, but increase amount of days to research technologies that requieres special databank three times more";
        }
        if(personAPI.hasTag(AodResearcherSkills.EXPLORER)){
            return "Explorer";
        }
        return"";
    }
    public String getSkillName(PersonAPI personAPI){
        if(personAPI.hasTag(AodResearcherSkills.RESOURCEFUL)){
            return "Resourceful";
        }
        if(personAPI.hasTag(AodResearcherSkills.SEEKER_OF_KNOWLEDGE)){
            return "Seeker of Knowledge";
        }
        if(personAPI.hasTag(AodResearcherSkills.EXPLORER)){
            return "Explorer";
        }
        return "";
    }
    @Override
    public void createCustomDialog(CustomPanelAPI panel, CustomDialogCallback callback) {
        TooltipMakerAPI panelTooltip = panel.createUIElement(WIDTH, HEIGHT, true);
        panelTooltip.addSectionHeading("Change Researcher", Alignment.MID, 0f);
        float opad = 10f;
        float spad = 2f;
        float ySpacer = 20f;
        int index =0;
        buttons.clear();
        for (PersonAPI personAPI : AoDUtilis.getResearchAPI().getResearchersInPossetion()) {

            Color baseColor = Misc.getButtonTextColor();
            Color bgColour = Misc.getDarkPlayerColor();
            Color brightColor = Misc.getBrightPlayerColor();
            String spriteName = personAPI.getPortraitSprite();
            SpriteAPI sprite = Global.getSettings().getSprite(spriteName);
            float aspectRatio = sprite.getWidth() / sprite.getHeight();
            float adjustedWidth = CONTENT_HEIGHT * aspectRatio;
            CustomPanelAPI researcherInfo = panel.createCustomPanel(ENTRY_WIDTH, ENTRY_HEIGHT + 2f, new ButtonReportingCustomPanel(this));
            TooltipMakerAPI anchor =researcherInfo.createUIElement(ENTRY_WIDTH - adjustedWidth - (3 * opad), CONTENT_HEIGHT, false);
            if (AoDUtilis.getResearchAPI().getCurrentResearcher()!=null&&AoDUtilis.getResearchAPI().getCurrentResearcher().equals(personAPI)) {
                baseColor = Color.darkGray;
                bgColour = Color.lightGray;
                brightColor = Color.gray;
            }

            ButtonAPI areaCheckbox = anchor.addAreaCheckbox("",personAPI.getId(), baseColor, bgColour, brightColor, //new Color(255,255,255,0)
                    ENTRY_WIDTH,
                    ENTRY_HEIGHT + 20,
                    0f,
                    true);
            if(AoDUtilis.getResearchAPI().getCurrentResearcher()!=null&&AoDUtilis.getResearchAPI().getCurrentResearcher().equals(personAPI)){
                areaCheckbox.setEnabled(false);
            }
            researcherInfo.addUIElement(anchor).inTL(-10,5+ySpacer*index);
            anchor = researcherInfo.createUIElement(128,128,false);
            anchor.addImage(spriteName, 128, 128, 0f);
            researcherInfo.addUIElement(anchor).inTL(1,13+ySpacer*index);
            anchor = researcherInfo.createUIElement(ENTRY_WIDTH - adjustedWidth - (6 * opad), CONTENT_HEIGHT, false);
            anchor.addPara("Name: "+personAPI.getName().getFirst()+ " " +personAPI.getName().getLast(), Color.ORANGE,10f);
            anchor.addPara("Skill: "+getSkillName(personAPI), Color.CYAN,10f);
            anchor.addPara("Description : " +getDescrpForResearcher(personAPI)+"\n", Color.WHITE,10f);
            buttons.add(areaCheckbox);
            researcherInfo.addUIElement(anchor).inTL(135,5+ySpacer*index);
            panelTooltip.addCustom(researcherInfo,0f);
            index++;
        }


        panel.addUIElement(panelTooltip).inTL(0.0F, 5.0F);
    }

    @Override
    public boolean hasCancelButton() {
        return false;
    }

    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        if (selected == null) return;
        researchAPI.setCurrentResearcher(selected);
    }

    @Override
    public void customDialogCancel() {

    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }
    public void reportButtonPressed(Object id) {
        if (id instanceof String) {
            for (PersonAPI personAPI : researchAPI.getResearchersInPossetion()) {
                if(personAPI.getId().equals(id)){
                    selected = personAPI;
                    break;
                }
            }

        }

        for (ButtonAPI button : buttons) {
            if (button.isChecked() && button.getCustomData() != id) button.setChecked(false);
        }
    }
    public static class ButtonReportingCustomPanel extends BaseCustomUIPanelPlugin {
        public HeadOfResearchCenterUI delegate;

        public ButtonReportingCustomPanel(HeadOfResearchCenterUI delegate) {
            this.delegate = delegate;
        }

        @Override
        public void buttonPressed(Object buttonId) {
            super.buttonPressed(buttonId);
            delegate.reportButtonPressed(buttonId);
        }
    }
}
