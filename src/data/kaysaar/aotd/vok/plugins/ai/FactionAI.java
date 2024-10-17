package data.kaysaar.aotd.vok.plugins.ai;

import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdConv;

public class FactionAI {
    public static void main(String[] args) {
        FactionEnvironment env = new FactionEnvironment("myFaction");
        QLearningDiscreteDense<FactionState> dql = new QLearningDiscreteDense<>(
                env,
                NetworkUtil.buildDQNFactory(), // Neural network configuration
                NetworkUtil.buildConfig()      // RL configuration
        );

        // Start training the model
        dql.train();
        env.close();
    }
}
