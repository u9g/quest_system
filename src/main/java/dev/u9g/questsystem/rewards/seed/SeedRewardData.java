package dev.u9g.questsystem.rewards.seed;

import com.amihaiemil.eoyaml.YamlMapping;
import dev.u9g.questsystem.rewards.RewardData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record SeedRewardData(String seedType, int amount) implements RewardData {
    private static final Map<String, Material> seedToMaterialType = Map.of(
            "wheat", Material.WHEAT_SEEDS
    );

    public static SeedRewardData from(YamlMapping context) {
        return new SeedRewardData(context.string("seedType"), context.integer("amount"));
    }

    @Override
    public void giveReward(Player player) {
        // todo: incorporate /collect if inventory is full

        player.getInventory().addItem(
                new ItemStack(seedToMaterialType.get(seedType), amount)
        );
    }

    @Override
    public String toMiniMessage(Player player) {
        if (amount == 1) {
            return "a " + seedType + " seed";
        } else {
            return amount + " " + seedType + " seeds";
        }
    }
}
