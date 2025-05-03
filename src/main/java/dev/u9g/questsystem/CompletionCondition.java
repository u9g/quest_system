package dev.u9g.questsystem;

import org.bukkit.entity.Player;

import java.util.Map;

public interface CompletionCondition {
    boolean isCompleted(Player player, Map<String, String> objectiveContext);
}
