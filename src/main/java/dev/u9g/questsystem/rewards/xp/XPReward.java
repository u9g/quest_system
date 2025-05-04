package dev.u9g.questsystem.rewards.xp;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.rewards.Reward;

public class XPReward implements Reward<XPRewardData> {
    @Override
    public XPRewardData toData(YamlMapping context) {
        return XPRewardData.from(context);
    }
}