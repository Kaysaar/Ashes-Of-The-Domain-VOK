package data.kaysaar.aotd.vok.plugins.ai;

import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class FactionObservationSpace implements ObservationSpace<FactionState> {
    private final int numMarkets;
    private final int numFeaturesPerMarket = 2; // Example: income and number of industries
    private final int totalFeatures; // Total features in the observation

    public FactionObservationSpace(int numMarkets) {
        this.numMarkets = numMarkets;
        this.totalFeatures = numMarkets * numFeaturesPerMarket; // Total features = markets * features per market
    }

    @Override
    public String getName() {
        return "FactionObservationSpace"; // Return a name for the observation space
    }

    @Override
    public int[] getShape() {
        return new int[]{1, totalFeatures}; // Shape is [1, totalFeatures]
    }

    @Override
    public INDArray getLow() {
        // Define the lower bounds for the observation space
        double[] lowValues = new double[totalFeatures];
        return Nd4j.create(lowValues); // All lower bounds set to 0
    }

    @Override
    public INDArray getHigh() {
        // Define the upper bounds for the observation space
        double[] highValues = new double[totalFeatures];
        // Assuming a high value for income and industries; you can adjust this based on your game's mechanics
        for (int i = 0; i < totalFeatures; i++) {
            highValues[i] = Double.MAX_VALUE; // Set a maximum value
        }
        return Nd4j.create(highValues);
    }

}
