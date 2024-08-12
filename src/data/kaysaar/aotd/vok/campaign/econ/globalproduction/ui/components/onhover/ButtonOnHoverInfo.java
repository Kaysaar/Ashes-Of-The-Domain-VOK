package data.kaysaar.aotd.vok.campaign.econ.globalproduction.ui.components.onhover;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class ButtonOnHoverInfo implements TooltipMakerAPI.TooltipCreator {
    String desc1;
    String desc2;
    String desc3;
    String desc4;
    String reasonForDisable;
    String title;
    boolean isDisabled;
    float width;
    public ButtonOnHoverInfo(float width, boolean isDisabled, String reasonForDisable,String desc1,String desc2,String desc3,String desc4,String title){
        this.width = width;
        this.isDisabled = isDisabled;
        this.reasonForDisable = reasonForDisable;
        this.desc1 = desc1;
        this.desc2 = desc2;
        this.desc3 = desc3;
        this.desc4= desc4;
        this.title= title;
    }

    @Override
    public boolean isTooltipExpandable(Object tooltipParam) {
        return true;
    }

    @Override
    public float getTooltipWidth(Object tooltipParam) {
        return width;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
        tooltip.addTitle(title);
        if(desc1!=null){
            tooltip.addPara(desc1,10);
        }
        if(desc2!=null){
            tooltip.addPara(desc2,10);
        }
        if(desc3!=null){
            tooltip.addPara(desc3,10);
        }
        if(desc4!=null){
            tooltip.addPara(desc4,10);
        }
        if(isDisabled&&reasonForDisable!=null){
        tooltip.addPara(reasonForDisable, Misc.getNegativeHighlightColor(),10f);

        }
    }
}
