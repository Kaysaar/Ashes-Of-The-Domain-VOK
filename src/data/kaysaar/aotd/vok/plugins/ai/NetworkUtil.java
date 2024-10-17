package data.kaysaar.aotd.vok.plugins.ai;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.configuration.QLearningConfiguration;
import org.deeplearning4j.rl4j.network.configuration.DQNDenseNetworkConfiguration;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.nd4j.linalg.learning.config.RmsProp;

import java.io.File;
import java.io.IOException;

/**
 * Util class containing methods to build the neural network and its configuration for the specific actions in the game.
 */
public final class NetworkUtil {
    /**
     * Number of neural network inputs corresponding to the available actions.
     */
    public static final int NUMBER_OF_INPUTS = Action.values().length; // Corresponding to BUILD, UPGRADE, DEMOLISH

    /**
     * Lowest value of the observation (negative rewards).
     */
    public static final double LOW_VALUE = -1;

    /**
     * Highest value of the observation (positive rewards).
     */
    public static final double HIGH_VALUE = 1;

    private NetworkUtil() {}

    public static QLearningConfiguration buildConfig() {
        return QLearningConfiguration.builder()
                .seed(123L)
                .maxEpochStep(200)
                .maxStep(15000)
                .expRepMaxSize(150000)
                .batchSize(128)
                .targetDqnUpdateFreq(500)
                .updateStart(10)
                .rewardFactor(0.01)
                .gamma(0.99)
                .errorClamp(1.0)
                .minEpsilon(0.1f)
                .epsilonNbStep(1000)
                .doubleDQN(true)
                .build();
    }

    public static DQNFactoryStdDense buildDQNFactory() {
        final DQNDenseNetworkConfiguration configuration = DQNDenseNetworkConfiguration.builder()
                .l2(0.001)
                .updater(new RmsProp(0.000025))
                .numHiddenNodes(300)
                .numLayers(2)
                .build();

        return new DQNFactoryStdDense(configuration);
    }

    public static MultiLayerNetwork loadNetwork(final String networkName) {
        try {
            return MultiLayerNetwork.load(new File(networkName), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
