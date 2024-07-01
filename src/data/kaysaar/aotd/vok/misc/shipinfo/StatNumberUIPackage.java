package data.kaysaar.aotd.vok.misc.shipinfo;

import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;

public class StatNumberUIPackage {
    public LabelAPI parentLabel;
    public String text;
    public String text2;
    public Color colorOfText;
    public Color colorOfText2;
    public StatNumberUIPackage(LabelAPI parentLabel, String text, String text2, Color colorOfText, Color colorOfText2){
        this.parentLabel = parentLabel;
        this.text = text;
        this.text2 = text2;
        this.colorOfText2 = colorOfText2;
        this.colorOfText = colorOfText;

    }
    public void placeLabelToParent(TooltipMakerAPI tooltip){
        LabelAPI labelAPI = null;
        if(tooltip==null)return;
        if(text2==null&&text!=null){
             labelAPI = tooltip.addPara(text,colorOfText,0);
            labelAPI.getPosition().inTL(tooltip.getWidthSoFar()-labelAPI.computeTextWidth(labelAPI.getText()),-parentLabel.getPosition().getY()-parentLabel.getPosition().getHeight());

        }
        else if (text2!=null&&!text2.isEmpty()) {
            labelAPI = tooltip.addPara(text+" %s",10f,colorOfText,colorOfText2,""+text2);
            labelAPI.getPosition().inTL(tooltip.getWidthSoFar()-labelAPI.computeTextWidth(labelAPI.getText()),-parentLabel.getPosition().getY()-parentLabel.getPosition().getHeight());

        }

        this.parentLabel = null;
    }


}
