package data.kaysaar.aotd.vok.ui.customprod.common;

import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import data.kaysaar.aotd.vok.campaign.econ.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.vok.ui.customprod.buttons.FighterProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.ItemProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.ShipProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.WeaponProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionDynamicPanelForScroll;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionCustomButton;

import java.util.ArrayList;
import java.util.List;

public class ProductionOptionList implements ExtendedUIPanelPlugin {

    ArrayList<ProductionCustomButton> buttonsStorage = new ArrayList<>();
    ProductionMenuFilterAPI filter;
    ProductionDynamicPanelForScroll scrollBarV2;
    CustomPanelAPI mainPanel, contentPanel;

    float heightOfButtons = 40;

    public ArrayList<ProductionCustomButton> getButtonsStorage() {
        return buttonsStorage;
    }

    public ProductionOptionList(List<AoTDProductionSpec> specs,
                                float width,
                                float height,
                                AoTDProductionSpec.AoTDProductionSpecType prodType) {
        mainPanel = Global.getSettings().createCustom(width, height, this);

        for (AoTDProductionSpec spec : specs) {
            buttonsStorage.add(createButtonForType(prodType, width - 10, heightOfButtons, spec));
        }
    }
    private ProductionCustomButton createButtonForType(AoTDProductionSpec.AoTDProductionSpecType prodType,
                                                       float width,
                                                       float height,
                                                       AoTDProductionSpec spec) {
        switch (prodType) {
            case SHIP:
                return new ShipProductionCustomButton(width, height, spec);
            case WEAPON:
                return new WeaponProductionCustomButton(width, height, spec);
            case FIGHTER:
                return new FighterProductionCustomButton(width, height, spec);
            case SPECIAL_ITEM:
            case COMMODITY_ITEM:
                return new ItemProductionCustomButton(width, height, spec);
            default:
                return new ProductionCustomButton(width, height, spec);
        }
    }


    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if(scrollBarV2==null){
            scrollBarV2 = new ProductionDynamicPanelForScroll(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),mainPanel,35,heightOfButtons,2);
        }
        if(contentPanel!=null){
            mainPanel.removeComponent(contentPanel);
            scrollBarV2.clearItems();
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(),mainPanel.getPosition().getHeight(),null);
        TooltipMakerAPI tl = contentPanel.createUIElement(contentPanel.getPosition().getWidth(),contentPanel.getPosition().getHeight(),true);
        ArrayList<ProductionCustomButton>validButtons = new ArrayList<>(buttonsStorage);
        if(filter!=null){
            filter.pruneList(validButtons);
        }

        for (ProductionCustomButton validButton : validButtons) {
            scrollBarV2.addItem(validButton.getMainPanel());
        }
        scrollBarV2.createUI();

        tl.addCustom(scrollBarV2.getMainPanel(),0f).getPosition().inTL(0,0);
        contentPanel.addUIElement(tl).inTL(0,0);
        mainPanel.addComponent(contentPanel).inTL(0,0);
    }

    public void resetList(List<AoTDProductionSpec> specs,AoTDProductionSpec.AoTDProductionSpecType prodType) {
        for (ProductionCustomButton productionCustomButton : buttonsStorage) {
            productionCustomButton.clearUI();
        }
        buttonsStorage.clear();
        for (AoTDProductionSpec spec : specs) {
            buttonsStorage.add(createButtonForType(prodType, mainPanel.getPosition().getWidth() - 10, heightOfButtons, spec));
        }
    }
    @Override
    public void clearUI() {
        mainPanel.removeComponent(contentPanel);
        scrollBarV2.clearItems();
    }

    public void setFilter(ProductionMenuFilterAPI filter) {
        this.filter = filter;
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

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }
}
