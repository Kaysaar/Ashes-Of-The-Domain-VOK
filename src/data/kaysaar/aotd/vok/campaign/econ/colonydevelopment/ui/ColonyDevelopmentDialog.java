package data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.ui;

import ashlib.data.plugins.ui.models.BasePopUpDialog;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.BaseColonyDevelopment;
import data.kaysaar.aotd.vok.campaign.econ.colonydevelopment.models.ColonyDevelopmentCondition;
import data.kaysaar.aotd.vok.plugins.ReflectionUtilis;

public class ColonyDevelopmentDialog extends BasePopUpDialog {
    UIPanelAPI panelRemoved;
    MarketAPI market;
    int returnValue = 1;
    ColonyDevelopmentNameComponent nameComponent;
    ColonyDevelopmentMainContent content;
    public ColonyDevelopmentDialog(String headerTitle,UIPanelAPI surveyPanel,MarketAPI market) {
        super(headerTitle);
        this.panelRemoved = surveyPanel;
        this.market = market;
    }

    @Override
    public void createContentForDialog(TooltipMakerAPI tooltip, float width) {
        float height = getPanelToInfluence().getPosition().getHeight()-50;
        content = new ColonyDevelopmentMainContent(width, height,market);
        tooltip.addCustom(content.getMainPanel(),0f);
        tooltip.setHeightSoFar(height);
        super.createContentForDialog(tooltip, width);
    }

    @Override
    public void createConfirmAndCancelSection(CustomPanelAPI mainPanel) {
        super.createConfirmAndCancelSection(mainPanel);
        float totalWidth =400f;
        TooltipMakerAPI tooltip = mainPanel.createUIElement(totalWidth, 25.0F, false);
        nameComponent =new ColonyDevelopmentNameComponent(550,30);
        tooltip.addCustom(nameComponent.getMainPanel(),0f);
        mainPanel.addUIElement(tooltip).inTL(10.0F, mainPanel.getPosition().getHeight() - 40.0F);
        addTooltip(tooltip);
    }

    @Override
    public void applyConfirmScript() {
        TextFieldAPI field = (TextFieldAPI) ReflectionUtilis.findFieldOfClass(panelRemoved, TextFieldAPI.class);
        field.setText(nameComponent.field.getText());
        market.addCondition(BaseColonyDevelopment.condIdApplier);
        ColonyDevelopmentCondition cond = (ColonyDevelopmentCondition) market.getCondition(BaseColonyDevelopment.condIdApplier).getPlugin();
        cond.setIdOfDevelopment((String) content.list.chosen.getCustomData());

        returnValue = 0;
    }

    @Override
    public void advance(float amount) {
        super.advance(amount);
        if(content!=null&&content.list!=null){
            confirmButton.setEnabled(content.list.chosen != null);
        }

    }

    @Override
    public void onExit() {
        super.onExit();
        ReflectionUtilis.invokeMethodWithAutoProjection("dismiss",panelRemoved,returnValue);
    }
}
