package data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components;

import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;

public class ButtonData {
    public String textButton;
    public Object customData;
    public boolean isButtonEnabled;
    public Color textColor;
    public String customCommand;
    public Object getCustomData() {
        return customData;
    }
    public TooltipMakerAPI.TooltipCreator creator;
    public String buttonId;
    public String getTextButton() {
        return textButton;
    }
    public String sectionID;
    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }
    public ButtonData(String textButton, Object customData,boolean isButtonEnabled,Color textColor,String customCommand,TooltipMakerAPI.TooltipCreator creator,String buttonId,String sectionID) {
        this.textButton = textButton;
        this.customData = customData;
        this.isButtonEnabled = isButtonEnabled;
        this.textColor = textColor;
        this.customCommand = customCommand;
        this.creator = creator;
        this.buttonId = buttonId;
        this.sectionID = sectionID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public TooltipMakerAPI.TooltipCreator getCreator() {
        return creator;
    }

    public Color getTextColor() {
        return textColor;
    }

    public String getCustomCommand() {
        return customCommand;
    }
}
