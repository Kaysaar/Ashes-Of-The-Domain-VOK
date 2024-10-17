package data.kaysaar.aotd.vok.plugins.ai;

import com.fs.starfarer.api.util.Misc;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import java.util.List;

public class FactionEnvironment implements MDP<FactionState, Integer, DiscreteSpace> {
    private final DiscreteSpace actionSpace = new DiscreteSpace(3); // Example: Build, Upgrade, Demolish
    private List<MarketAPI> factionMarkets;
    private double factionIncome;
    private String factionId;

    public FactionEnvironment(String factionId) {
        this.factionId = factionId;
        reset(); // Initialize the environment when created
    }

    @Override
    public ObservationSpace<FactionState> getObservationSpace() {
        return new FactionObservationSpace(factionMarkets.size()); // Pass the size of markets
    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public FactionState reset() {
        this.factionMarkets = Misc.getFactionMarkets(factionId);
        this.factionIncome = calculateFactionIncome(factionMarkets);
        return new FactionState(factionMarkets);
    }

    @Override
    public void close() {
        // Implement any cleanup logic if necessary
    }

    @Override
    public StepReply<FactionState> step(Integer actionIndex) {
        // Define possible actions: Build, Upgrade, or Demolish industries
        Action actionToTake = Action.getActionByIndex(actionIndex);

        // Execute the action: build, upgrade, or demolish an industry
        executeAction(actionToTake);

        // Recalculate income
        double newFactionIncome = calculateFactionIncome(factionMarkets);
        double reward = (newFactionIncome - this.factionIncome) / 100000; // Reward based on income growth

        // Update state and return the result
        FactionState newState = new FactionState(factionMarkets);
        this.factionIncome = newFactionIncome; // Update the income for the next step
        return new StepReply<>(newState, reward, isDone(), null);
    }

    @Override
    public boolean isDone() {
        // Define the condition for episode completion
        return false; // For simplicity, we can run indefinitely until manually stopped
    }

    @Override
    public MDP<FactionState, Integer, DiscreteSpace> newInstance() {
        return new FactionEnvironment(factionId);
    }

    private double calculateFactionIncome(List<MarketAPI> markets) {
        double totalIncome = 0.0;
        for (MarketAPI market : markets) {
            totalIncome += market.getNetIncome(); // Assuming getNetIncome returns the profit of the market
        }
        return totalIncome;
    }

    private void executeAction(Action action) {
        // Implement logic to build, upgrade, or demolish industries in the markets
        switch (action) {
            case BUILD:
                // Logic to build an industry
                break;
            case UPGRADE:
                // Logic to upgrade an existing industry
                break;
            case DEMOLISH:
                // Logic to demolish an industry
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    public enum Action {
        BUILD(0), UPGRADE(1), DEMOLISH(2);

        private final int index;

        Action(int index) {
            this.index = index;
        }

        public static Action getActionByIndex(int index) {
            for (Action action : values()) {
                if (action.index == index) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Invalid action index: " + index);
        }
    }
}
