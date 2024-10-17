package data.kaysaar.aotd.vok.plugins.ai;

import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import com.fs.starfarer.api.campaign.econ.MarketAPI;

import java.util.List;

public class FactionState implements Encodable {
    private List<MarketAPI> markets;

    public FactionState(List<MarketAPI> markets) {
        this.markets = markets;
    }

    // Convert the market and industry data into an INDArray for the neural network
    public INDArray toINDArray() {
        double[] state = toArray();
        return Nd4j.create(state);
    }

    @Override
    public double[] toArray() {
        // Convert the market and industry data into a double array format
        double[] state = new double[markets.size() * 2]; // Example: income + number of industries per market
        for (int i = 0; i < markets.size(); i++) {
            MarketAPI market = markets.get(i);
            state[i * 2] = market.getNetIncome(); // Income
            state[i * 2 + 1] = market.getIndustries().size(); // Number of industries
        }
        return state;
    }

    @Override
    public boolean isSkipped() {
        // Define if this state should be skipped (used in multi-threaded environments)
        return false;
    }

    @Override
    public INDArray getData() {
        // Returns the same array in INDArray form
        return toINDArray();
    }

    @Override
    public Encodable dup() {
        // Creates a copy of the current state, useful for simulation purposes
        return new FactionState(this.markets); // Assuming markets are immutable; otherwise, deep copy may be needed
    }
}
