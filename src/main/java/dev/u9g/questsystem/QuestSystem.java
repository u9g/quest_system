package dev.u9g.questsystem;

import dev.u9g.questsystem.commands.ClaimQuestRewardsCommand;
import dev.u9g.questsystem.commands.QuestsCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class QuestSystem extends JavaPlugin {
    public static File dataFolder;
    public static Logger logger;

    @Override
    public void onEnable() {
        logger = getLogger();
        logger.info("Plugin has been enabled!");
        dataFolder = getDataFolder();

        if (!dataFolder.exists()) {
            boolean created = dataFolder.mkdirs();
            if (created) {
                logger.info("Plugin data folder created successfully!");
            } else {
                logger.severe("Failed to create plugin data folder!");
                setEnabled(false);
                return;
            }
        }

        Bukkit.getCommandMap().register("quests", new QuestsCommand("quests"));
        Bukkit.getCommandMap().register("quests", new ClaimQuestRewardsCommand("claimquestrewards"));

        QuestManager.it.init(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin has been disabled!");
    }
}
