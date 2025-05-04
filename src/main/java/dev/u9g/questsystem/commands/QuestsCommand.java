package dev.u9g.questsystem.commands;

import dev.u9g.questsystem.QuestManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestsCommand extends Command {
    public QuestsCommand(@NotNull String name) {
        super(name);

        this.setPermission("quests.questscommand.use");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player p) {
            String minimessageToSend = QuestManager.it.makeQuestMessage(p);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(minimessageToSend));
            return true;
        }
        return false;
    }
}
