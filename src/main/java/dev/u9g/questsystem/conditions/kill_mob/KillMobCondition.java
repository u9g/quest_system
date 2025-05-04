package dev.u9g.questsystem.conditions.kill_mob;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.QuestManager;
import dev.u9g.questsystem.conditions.Condition;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class KillMobCondition implements Condition<KillMobConditionData> {
    private static final NamespacedKey pigNSK = new NamespacedKey("quest_system", "kill_mob_pig");
    private static final NamespacedKey pigRequirementNSK = new NamespacedKey("quest_system", "kill_mob_pig_requirement");
    private static final NamespacedKey cowNSK = new NamespacedKey("quest_system", "kill_mob_cow");
    private static final NamespacedKey cowRequirementNSK = new NamespacedKey("quest_system", "kill_mob_cow_requirement");
    private static final NamespacedKey rabbitNSK = new NamespacedKey("quest_system", "kill_mob_rabbit");
    private static final NamespacedKey rabbitRequirementNSK = new NamespacedKey("quest_system", "kill_mob_rabbit_requirement");
    private static final NamespacedKey chickenNSK = new NamespacedKey("quest_system", "kill_mob_chicken");
    private static final NamespacedKey chickenRequirementNSK = new NamespacedKey("quest_system", "kill_mob_chicken_requirement");

    private static final Map<EntityType, NamespacedKey> keys = Map.of(
            EntityType.PIG, pigNSK,
            EntityType.COW, cowNSK,
            EntityType.RABBIT, rabbitNSK,
            EntityType.CHICKEN, chickenNSK
    );

    final static Map<String, NamespacedKey> keysByName = Map.of(
            "pig", pigNSK,
            "cow", cowNSK,
            "rabbit", rabbitNSK,
            "chicken", chickenNSK
    );

    final static Map<EntityType, NamespacedKey> requirementKeysByEntityType = Map.of(
            EntityType.PIG, pigRequirementNSK,
            EntityType.COW, cowRequirementNSK,
            EntityType.RABBIT, rabbitRequirementNSK,
            EntityType.CHICKEN, chickenRequirementNSK
    );

    final static Map<String, NamespacedKey> requirementKeysByName = Map.of(
            "pig", pigRequirementNSK,
            "cow", cowRequirementNSK,
            "rabbit", rabbitRequirementNSK,
            "chicken", chickenRequirementNSK
    );

    @Override
    public KillMobConditionData toData(YamlMapping context) {
        return KillMobConditionData.from(context);
    }

    @EventHandler
    public void onKillMob(EntityDeathEvent entityDeathEvent) {
        NamespacedKey key = keys.get(entityDeathEvent.getEntityType());

        if (key == null) return; // not a mob that we track

        var lastDamageCause = entityDeathEvent.getEntity().getLastDamageCause();
        if (lastDamageCause == null) return;
        var damageSource = lastDamageCause.getDamageSource();
        var causingEntity = damageSource.getCausingEntity();
        if (causingEntity == null) return;

        if (causingEntity instanceof Player player) {
            long value = player
                    .getPersistentDataContainer()
                    .getOrDefault(key, PersistentDataType.LONG, 0L);

            value += 1;

            player
                    .getPersistentDataContainer()
                    .set(key, PersistentDataType.LONG, value);

            NamespacedKey requirementKey = requirementKeysByEntityType.get(entityDeathEvent.getEntityType());

            long requirement = player.getPersistentDataContainer().getOrDefault(requirementKey, PersistentDataType.LONG, -1L);

            if (requirement == -1L) return; // no requirement

            if (value >= requirement) {
                // we don't know that they leveled up but they did fill at least one requirement
                QuestManager.it.evaluatePossibleLevelUp(player);
            }
        }
    }
}
