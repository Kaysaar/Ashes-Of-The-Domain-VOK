package data.kaysaar.aotd.vok.ui.customprod;

import ashlib.data.plugins.ui.models.CustomButton;
import ashlib.data.plugins.ui.models.ExtendedUIPanelPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import data.kaysaar.aotd.tot.produciton.specs.AoTDProductionSpec;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionBrowserSection;
import data.kaysaar.aotd.tot.ui.customprod.components.ProductionCustomButton;
import data.kaysaar.aotd.vok.scripts.specialprojects.BlackSiteProjectManager;
import data.kaysaar.aotd.vok.scripts.specialprojects.models.ProjectReward;
import data.kaysaar.aotd.vok.ui.SoundUIManager;
import data.kaysaar.aotd.vok.ui.customprod.buttons.FighterProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.ItemProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.ShipProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.buttons.WeaponProductionCustomButton;
import data.kaysaar.aotd.vok.ui.customprod.components.ProductionGatheringComponent;
import data.kaysaar.aotd.vok.ui.customprod.orders.CurrentOrderList;
import data.kaysaar.aotd.vok.ui.customprod.orders.IssuedOrdersList;

import java.util.List;

public class ProductionMainPanel implements ExtendedUIPanelPlugin, SoundUIManager {
    CustomPanelAPI mainPanel;
    CustomPanelAPI contentPanel;
    ProductionTypesSection list;
    ProductionBrowserSection section;
    AoTDProductionSpec.AoTDProductionSpecType currType = AoTDProductionSpec.AoTDProductionSpecType.SHIP;
    ProductionGatheringComponent productionGatheringComponent;
    CurrentOrderList orderList;
    IssuedOrdersList issuedOrdersList;

    public IssuedOrdersList getIssuedOrdersList() {
        return issuedOrdersList;
    }

    public boolean isInOtherOrders = false;

    public ProductionMainPanel(float width, float height) {
        mainPanel = Global.getSettings().createCustom(width, height, this);
        createUI();
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void createUI() {
        if (contentPanel != null) {
            mainPanel.removeComponent(contentPanel);
        }
        contentPanel = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        float panelSize = contentPanel.getPosition().getWidth() * 0.35f - 20;
        productionGatheringComponent = new ProductionGatheringComponent(panelSize, 125, this);
        float remHeight = contentPanel.getPosition().getHeight() - 130;
        orderList = new CurrentOrderList(panelSize, remHeight);
        issuedOrdersList = new IssuedOrdersList(panelSize, remHeight,this);
        contentPanel.addComponent(orderList.getMainPanel()).inTL(0, contentPanel.getPosition().getHeight() - orderList.getMainPanel().getPosition().getHeight());
        contentPanel.addComponent(productionGatheringComponent.getMainPanel()).inTL(0, 5);
        list = new ProductionTypesSection();
        final ProductionMainPanel parent = this;
        section = new ProductionBrowserSection(contentPanel.getPosition().getWidth() - panelSize - 15, contentPanel.getPosition().getHeight(), currType){
            @Override
            protected ProductionCustomButton createProductionButtonForType(
                    AoTDProductionSpec.AoTDProductionSpecType type,
                    float width,
                    float height,
                    AoTDProductionSpec spec
            )
            {
                switch (prodType) {
                    case SHIP:
                        if (!BlackSiteProjectManager.getProjectMatchingRewardThroughSpec(ProjectReward.ProjectRewardType.SHIP, spec.getId()).isEmpty()) {
                            return null;
                        }
                        return new ShipProductionCustomButton(width, height, spec, getColumnLayout());

                    case WEAPON:
                        if (!BlackSiteProjectManager.getProjectMatchingRewardThroughSpec(ProjectReward.ProjectRewardType.WEAPON, spec.getId()).isEmpty()) {
                            return null;
                        }
                        return new WeaponProductionCustomButton(width, height, spec, getColumnLayout());

                    case FIGHTER:
                        if (!BlackSiteProjectManager.getProjectMatchingRewardThroughSpec(ProjectReward.ProjectRewardType.FIGHTER, spec.getId()).isEmpty()) {
                            return null;
                        }
                        return new FighterProductionCustomButton(width, height, spec, getColumnLayout());

                    case SPECIAL_ITEM:
                        if (!BlackSiteProjectManager.getProjectMatchingRewardThroughSpec(ProjectReward.ProjectRewardType.ITEM, spec.getId()).isEmpty()) {
                            return null;
                        }
                        return new ItemProductionCustomButton(width, height, spec, getColumnLayout());

                    case COMMODITY_ITEM:
                        if (!BlackSiteProjectManager.getProjectMatchingRewardThroughSpec(ProjectReward.ProjectRewardType.AICORE, spec.getId()).isEmpty()) {
                            return null;
                        }
                        return new ItemProductionCustomButton(width, height, spec, getColumnLayout());

                    default:
                        return new ProductionCustomButton(width, height, spec, getColumnLayout());
                }
            }

            @Override
            public CustomButton.ButtonEventListener createListenerForButton(ProductionCustomButton bt) {
                return  new CustomButton.ButtonEventListener() {

                    @Override
                    public void onButtonClicked() {
                        int am = 1;
                        if(isPressingShift){
                            am = 5;
                        }
                        if(isPressingCtrl){
                            am = 10;
                        }
                        if(isPressingShift&&isPressingCtrl){
                            am = 50;
                        }

                        parent.orderList.addOrder(bt.getSpec().getId(),bt.getSpec(),am);

                        parent.swapPanels(false);
                    }
                };

            }
        };
        section.createUI();
        contentPanel.addComponent(section.getMainPanel()).inTL(contentPanel.getPosition().getWidth() - section.getMainPanel().getPosition().getWidth() - 5, 0);
        contentPanel.addComponent(list.getMainPanel()).inTL(contentPanel.getPosition().getWidth() - section.getMainPanel().getPosition().getWidth() - 5, 0);
        mainPanel.addComponent(contentPanel).inTL(0, 0);

    }

    public void swapPanels(boolean isInOtherOrders) {
        if (isInOtherOrders == this.isInOtherOrders) {
            return;
        }

        this.isInOtherOrders = isInOtherOrders;

        if (isInOtherOrders) {
            contentPanel.removeComponent(orderList.getMainPanel());
            issuedOrdersList.createUI();
            contentPanel.addComponent(issuedOrdersList.getMainPanel())
                    .inTL(0, contentPanel.getPosition().getHeight() - issuedOrdersList.getMainPanel().getPosition().getHeight());
        } else {
            contentPanel.removeComponent(issuedOrdersList.getMainPanel());
            orderList.createUI();
            contentPanel.addComponent(orderList.getMainPanel())
                    .inTL(0, contentPanel.getPosition().getHeight() - orderList.getMainPanel().getPosition().getHeight());
        }
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
        if (list != null) {
            if (list.isNeedsUpdate()) {
                list.setNeedsUpdate(false);
                AoTDProductionSpec.AoTDProductionSpecType prev = currType;
                currType = (AoTDProductionSpec.AoTDProductionSpecType) list.curr.getCustomData();
                if (prev != currType) {
                    section.setProdType(currType);
                }
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }

    @Override
    public void buttonPressed(Object buttonId) {

    }

    @Override
    public void playSound() {
        Global.getSoundPlayer().playCustomMusic(1, 1, "aotd_shipyard", true);
    }

    @Override
    public void pauseSound() {
        Global.getSoundPlayer().pauseCustomMusic();
        Global.getSoundPlayer().restartCurrentMusic();
    }
}
