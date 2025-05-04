package dev.u9g.questsystem.rewards.seed;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.rewards.Reward;

public class SeedReward implements Reward<SeedRewardData> {
    @Override
    public SeedRewardData toData(YamlMapping context) {
        return SeedRewardData.from(context);
    }
}
