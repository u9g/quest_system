package dev.u9g.questsystem.rewards;

import dev.u9g.questsystem.QuestCommandDescriptionDisplayable;
import org.bukkit.entity.Player;

public interface RewardData extends QuestCommandDescriptionDisplayable {
    void giveReward(Player player);
}
