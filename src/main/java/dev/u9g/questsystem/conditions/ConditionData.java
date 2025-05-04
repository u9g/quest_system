package dev.u9g.questsystem.conditions;

import dev.u9g.questsystem.QuestCommandDescriptionDisplayable;
import org.bukkit.entity.Player;

public interface ConditionData extends QuestCommandDescriptionDisplayable {
    // - this method stores the next level up requirement in the player's persistent data
    // which allows for an efficient lookup of whether a condition has been completed
    // - if the player already has a serialized level up requirement for this condition,
    // and this condition has a smaller requirement, the previous requirement will be overwritten
    // - serialize as -1L if there is no requirement
    void serializeNextLevelUpRequirement(Player player);

    boolean isCompleted(Player player);
}
