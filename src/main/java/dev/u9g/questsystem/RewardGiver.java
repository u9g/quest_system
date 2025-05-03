package dev.u9g.questsystem;

import org.bukkit.entity.Player;

import java.util.Map;

public interface RewardGiver {
    void giveReward(Player player, Map<String, String> rewardContext);
}
