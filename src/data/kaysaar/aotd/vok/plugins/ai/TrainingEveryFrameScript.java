package data.kaysaar.aotd.vok.plugins.ai;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import data.kaysaar.aotd.vok.plugins.ai.FactionEnvironment;
import data.kaysaar.aotd.vok.plugins.ai.FactionState;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;

public class TrainingEveryFrameScript implements EveryFrameScript {
    private static final int ACTIONS_EVERY_DAYS = 365; // Action interval in days
    private final QLearningDiscreteDense<FactionState> dql;
    private float timeElapsed = 0.0f; // Time accumulator in seconds
    private final FactionEnvironment factionEnvironment;

    public TrainingEveryFrameScript(QLearningDiscreteDense<FactionState> dql, String factionId) {
        this.dql = dql;
        this.factionEnvironment = new FactionEnvironment(factionId);
    }

    @Override
    public boolean isDone() {
        return false; // Keep running until manually stopped
    }

    @Override
    public boolean runWhilePaused() {
        return false; // Don't run while paused
    }

    @Override
    public void advance(float amount) {
        timeElapsed += amount; // Accumulate elapsed time in seconds

        // Convert the elapsed time to days
        float elapsedDays = Global.getSector().getClock().convertToDays(timeElapsed);

        // Check if 365 days have passed
        if (elapsedDays >= ACTIONS_EVERY_DAYS) {
            timeElapsed = 0; // Reset the elapsed time

            // Perform training step
            performTrainingStep();
        }
    }

    private void performTrainingStep() {
        // Reset the environment and get the current state
        FactionState currentState = factionEnvironment.reset();

        // Calculate profits for each action
        double buildProfit = calculateProfit(Action.BUILD, currentState);
        double upgradeProfit = calculateProfit(Action.UPGRADE, currentState);
        double demolishProfit = calculateProfit(Action.DEMOLISH, currentState);

        // Determine the best action based on the calculated profits
        Action bestAction = selectBestAction(buildProfit, upgradeProfit, demolishProfit);

        // Execute the action in the environment
        factionEnvironment.step(bestAction.ordinal());
    }

    private double calculateProfit(Action action, FactionState state) {
        // Implement logic to calculate profit based on the action and the current state
        double profit = 0.0;

        // For example, you could analyze the market and industry states to determine profitability
        // This is a simplified example; actual implementation would require more details
//
//        switch (action) {
//            case BUILD:
//                // Calculate the profit of building a new industry
//                profit = /* logic to calculate profit from building */;
//                break;
//            case UPGRADE:
//                // Calculate the profit of upgrading an existing industry
//                profit = /* logic to calculate profit from upgrading */;
//                break;
//            case DEMOLISH:
//                // Calculate the profit of demolishing an existing industry
//                profit = /* logic to calculate profit from demolishing */;
//                break;
//        }

        return profit;
    }

    private Action selectBestAction(double buildProfit, double upgradeProfit, double demolishProfit) {
        if (buildProfit >= upgradeProfit && buildProfit >= demolishProfit) {
            return Action.BUILD;
        } else if (upgradeProfit >= buildProfit && upgradeProfit >= demolishProfit) {
            return Action.UPGRADE;
        } else {
            return Action.DEMOLISH;
        }
    }
}

