package dev.u9g.questsystem.conditions.break_crop;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.conditions.ConditionData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public record BreakCropConditionData(String target, long amount) implements ConditionData {
    public static BreakCropConditionData from(YamlMapping context) {
        return new BreakCropConditionData(
                context.string("target"), context.longNumber("amount"));
    }

    public boolean isCompleted(Player player) {
        var key = BreakCropCondition.keysByName.get(target);
        return player
                .getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.LONG, 0L) >= amount;
    }

    @Override
    public void serializeNextLevelUpRequirement(Player player) {
        long previousValue = player.getPersistentDataContainer().getOrDefault(
                BreakCropCondition.requirementKeysByName.get(target), PersistentDataType.LONG,
                -1L);

        // if the player already has a requirement, and we have a smaller requirement, overwrite
        if (amount < previousValue || previousValue == -1L) {
            player.getPersistentDataContainer()
                    .set(
                            BreakCropCondition.requirementKeysByName.get(target),
                            PersistentDataType.LONG, amount);
        }
    }

    @Override
    public String toMiniMessage(Player player) {
        var key = BreakCropCondition.keysByName.get(target);
        var left = amount - player
                .getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.LONG, 0L);
        if (left == 1L) {
            return "harvest <u>" + left + "</u> more " + target;
        }
        return "harvest <u>" + left + "</u> more " + target + " crops";
    }
}
