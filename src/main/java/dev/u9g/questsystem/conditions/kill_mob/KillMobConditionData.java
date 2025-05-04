package dev.u9g.questsystem.conditions.kill_mob;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.conditions.ConditionData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public record KillMobConditionData(String target, long amount) implements ConditionData {

    public static KillMobConditionData from(YamlMapping context) {
        return new KillMobConditionData(
                context.string("target"), context.longNumber("amount"));
    }

    public boolean isCompleted(Player player) {
        var key = KillMobCondition.keysByName.get(target);
        return player
                .getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.LONG, 0L) >= amount;
    }

    @Override
    public void serializeNextLevelUpRequirement(Player player) {
        long previousValue = player.getPersistentDataContainer().getOrDefault(
                KillMobCondition.requirementKeysByName.get(target), PersistentDataType.LONG,
                -1L);

        // if the player already has a requirement, and we have a smaller requirement, overwrite
        if (amount < previousValue || previousValue == -1L) {
            player.getPersistentDataContainer()
                    .set(
                            KillMobCondition.requirementKeysByName.get(target),
                            PersistentDataType.LONG, amount);
        }
    }

    @Override
    public String toMiniMessage(Player player) {
        var key = KillMobCondition.keysByName.get(target);
        var left = amount - player
                .getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.LONG, 0L);
        return "kill <u>" + left + "</u> more " + target + "s";
    }
}
