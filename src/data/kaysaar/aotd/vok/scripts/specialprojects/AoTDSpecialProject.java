package data.kaysaar.aotd.vok.scripts.specialprojects;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.ProgressBarComponent;
import data.kaysaar.aotd.vok.ui.basecomps.holograms.HologramViewer;
import data.kaysaar.aotd.vok.ui.specialprojects.SpecialProjectStageWindow;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;

import static data.kaysaar.aotd.vok.campaign.econ.globalproduction.megastructures.ui.components.MegastructureUIMisc.createResourcePanelForSmallTooltipCondensed;

public class AoTDSpecialProject {
    public String specID;

    ArrayList<AoTDSpecialProjectStage> stages = new ArrayList<>();

    public AoTDSpecialProjectSpec getProjectSpec() {
        return SpecialProjectSpecManager.getSpec(specID);
    }

    public String currentStageId;


    public boolean wasCompleted = false;
    public boolean wasInitalized = false;

    public void setSpecID(String specID) {
        this.specID = specID;

    }


    public void init() {
        stages = new ArrayList<>();
        for (String s : getProjectSpec().getStageMap().keySet()) {
            stages.add(new AoTDSpecialProjectStage(s));
        }
    }

    public void update() {
        for (String s : getProjectSpec().getStageMap().keySet()) {
            if (stages.stream().noneMatch(x -> x.specId.equals(s))) {
                stages.add(new AoTDSpecialProjectStage(s));
            }
        }
        Iterator<AoTDSpecialProjectStage> it = stages.iterator();
        while (it.hasNext()) {
            AoTDSpecialProjectStage stage = it.next();
            if (!getProjectSpec().getStageMap().containsKey(stage.specId)) {
                it.remove();
            }
        }
        if (currentStageId != null) {
            if (stages.stream().noneMatch(x -> x.specId.equals(currentStageId))) {
                currentStageId = null;
            }
        }

    }


    public AoTDSpecialProjectStage getStage(String id) {
        for (AoTDSpecialProjectStage stage : stages) {
            if (stage.specId.equals(id)) {
                return stage;
            }
        }
        return null;
    }

    public float getCreditCostsComputed() {
        return 0f;
    }

    public ArrayList<OtherCostData> getOtherCostsOverrideForStage(String stageId) {
        return getStage(stageId).getSpec().getOtherCosts();
    }

    public HashMap<String, Integer> getGPCostOverrideForStage(String stageId) {
        return getStage(stageId).getSpec().getGpCost();
    }

    public ArrayList<SpecialProjectStageWindow> getStagesForUI(CustomPanelAPI mainPanel) {
        ArrayList<SpecialProjectStageWindow> windows = new ArrayList<>();
        for (AoTDSpecialProjectStage spec : stages) {
            AoTDSpecialProjectStageSpec specStage = SpecialProjectSpecManager.getStageSpec(spec.specId);
            SpecialProjectStageWindow window = new SpecialProjectStageWindow(this, spec.specId, mainPanel, specStage.getMode(), specStage.getOriginMode(), specStage.getUiCordsOfBox(), specStage.getUiCordsOnHologram());
            windows.add(window);

        }


        return windows;
    }

    public void createDetailedTooltipForButton(TooltipMakerAPI tooltip, float width) {
        createTooltipForButton(tooltip, width);
        for (AoTDSpecialProjectStageSpec stagesSpec : this.getProjectSpec().getStagesSpecs()) {
            tooltip.addCustom(createSubStageProgressMoved(width - 110, stagesSpec, 10), 2f);

        }
        tooltip.addPara(this.getProjectSpec().getDescription(), 5f);

        tooltip.addSectionHeading("Effects upon project completion", Misc.getDarkHighlightColor(), null, Alignment.MID, width, 3f);
    }
    public void createRewardSection(TooltipMakerAPI tooltip,float width){
        tooltip.addPara("Gain " + Global.getSettings().getHullSpec("uaf_supercap_slv_core").getHullNameWithDashClass(), Misc.getPositiveHighlightColor(), 5f);
    }

    public void createTooltipForButton(TooltipMakerAPI tooltip, float width) {
        tooltip.setTitleFont(Fonts.ORBITRON_16);

        tooltip.addTitle(this.getProjectSpec().getName());
        tooltip.addSectionHeading("Upkeep", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, width - 110, 5f);

        tooltip.addCustom(createResourcePanelForSmallTooltipCondensed(width - 110, 20, 20, new HashMap<>(), new HashMap<>()), 5f);

        LabelAPI labelAPI = tooltip.addSectionHeading("Project Progress", Misc.getBasePlayerColor(), null, Alignment.MID, width - 110, 3f);
        ProgressBarComponent component = new ProgressBarComponent(width - 110, 21, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 5f);
    }

    public CustomPanelAPI createSubStageProgressMoved(float width, AoTDSpecialProjectStageSpec spec, float opadX) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, 1, null);
        CustomPanelAPI t = createSubStageProgress(width - opadX, 1, spec);

        test.addComponent(t).inTL(opadX, 0);
        test.getPosition().setSize(width, t.getPosition().getHeight());
        return test;
    }

    public CustomPanelAPI createSubStageProgress(float width, float height, AoTDSpecialProjectStageSpec spec) {
        CustomPanelAPI test = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI tooltip = test.createUIElement(width, height, false);
        tooltip.addSectionHeading(spec.getName(), Misc.getBasePlayerColor(), null, Alignment.MID, width, 0f);
        ProgressBarComponent component = new ProgressBarComponent(width - 5, 15, 0f, Misc.getBasePlayerColor().darker().darker());
        tooltip.addCustom(component.getRenderingPanel(), 2f);
        test.getPosition().setSize(width, tooltip.getHeightSoFar());
        test.addUIElement(tooltip).inTL(0, 0);
        return test;
    }
    public boolean isStageButtonEnabled(String stageId) {
        return true;
    }

}
