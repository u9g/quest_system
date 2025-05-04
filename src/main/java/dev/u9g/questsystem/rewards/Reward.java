package dev.u9g.questsystem.rewards;

import com.amihaiemil.eoyaml.YamlMapping;
import org.bukkit.entity.Player;

public interface Reward<T extends RewardData> {
    T toData(YamlMapping context);
}
