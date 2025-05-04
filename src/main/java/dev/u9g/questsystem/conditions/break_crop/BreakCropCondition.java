package dev.u9g.questsystem.conditions.break_crop;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.QuestManager;
import dev.u9g.questsystem.conditions.Condition;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class BreakCropCondition implements Condition<BreakCropConditionData> {
    private static final NamespacedKey wheatNSK = new NamespacedKey("quest_system", "break_crop_wheat");
    private static final NamespacedKey wheatRequirementNSK = new NamespacedKey("quest_system", "break_crop_wheat_requirement");
    private static final NamespacedKey potatoesNSK = new NamespacedKey("quest_system", "break_crop_potato");
    private static final NamespacedKey potatoesRequirementNSK = new NamespacedKey("quest_system", "break_crop_potato_requirement");
    private static final NamespacedKey carrotsNSK = new NamespacedKey("quest_system", "break_crop_carrot");
    private static final NamespacedKey carrotsRequirementNSK = new NamespacedKey("quest_system", "break_crop_carrot_requirement");

    private static final Map<Material, NamespacedKey> keys = Map.of(
            Material.WHEAT, wheatNSK,
            Material.POTATOES, potatoesNSK,
            Material.CARROTS, carrotsNSK
    );

    final static Map<String, NamespacedKey> requirementKeysByName = Map.of(
            "wheat", wheatRequirementNSK,
            "potato", potatoesRequirementNSK,
            "carrot", carrotsRequirementNSK
    );

    final static Map<Material, NamespacedKey> requirementKeysByMaterial = Map.of(
            Material.WHEAT, wheatRequirementNSK,
            Material.POTATOES, potatoesRequirementNSK,
            Material.CARROTS, carrotsRequirementNSK
    );

    public static final Map<String, NamespacedKey> keysByName = Map.of(
            "wheat", wheatNSK,
            "potato", potatoesNSK,
            "carrot", carrotsNSK);

    @Override
    public BreakCropConditionData toData(YamlMapping context) {
        return BreakCropConditionData.from(context);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        Player player = blockBreakEvent.getPlayer();

        NamespacedKey key = keys.get(blockBreakEvent.getBlock().getType());

        if (key == null) return; // not a block break that we track

        // no race condition between get/set because this happens on the main thread.
        long value = player
                .getPersistentDataContainer()
                .getOrDefault(key, PersistentDataType.LONG, 0L);

        value += 1;

        player
                .getPersistentDataContainer()
                .set(key, PersistentDataType.LONG, value);

        NamespacedKey requirementKey = requirementKeysByMaterial.get(blockBreakEvent.getBlock().getType());

        long requirement = player.getPersistentDataContainer().getOrDefault(requirementKey, PersistentDataType.LONG, -1L);

        if (requirement == -1L) return; // no requirement

        if (value >= requirement) {
            // we don't know that they leveled up but they did fill at least one requirement
            QuestManager.it.evaluatePossibleLevelUp(player);
        }
    }
}
