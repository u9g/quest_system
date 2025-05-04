package dev.u9g.questsystem.rewards.xp;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.rewards.RewardData;
import org.bukkit.entity.Player;

public record XPRewardData(int amount) implements RewardData {
    @Override
    public void giveReward(Player player) {
        player.giveExp(amount);
    }

    public static XPRewardData from(YamlMapping context) {
        return new XPRewardData(context.integer("amount"));
    }

    @Override
    public String toMiniMessage(Player player) {
        return amount + " experience points";
    }
}