package dev.u9g.questsystem;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import dev.u9g.questsystem.conditions.ConditionData;
import dev.u9g.questsystem.conditions.break_crop.BreakCropCondition;
import dev.u9g.questsystem.conditions.Condition;
import dev.u9g.questsystem.conditions.kill_mob.KillMobCondition;
import dev.u9g.questsystem.rewards.Reward;
import dev.u9g.questsystem.rewards.RewardData;
import dev.u9g.questsystem.rewards.seed.SeedReward;
import dev.u9g.questsystem.rewards.xp.XPReward;
import dev.u9g.questsystem.structures.Quest;
import dev.u9g.questsystem.structures.QuestLine;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class QuestManager implements Listener {
    public static final QuestManager it = new QuestManager();

    private List<QuestLine> questLines = new ArrayList<>();
    private Map<String, Condition<? extends ConditionData>> conditions = new HashMap<>();
    private Map<String, Reward<? extends RewardData>> rewards = new HashMap<>();

    private final NamespacedKey completedQuestsNSK = new NamespacedKey("quest_system", "completed_quests");
    private final NamespacedKey claimedRewardQuestsNSK = new NamespacedKey("quest_system", "claimed_reward_quests");

    void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        conditions.put("break_crop", new BreakCropCondition());
        conditions.put("kill_mob", new KillMobCondition());

        rewards.put("seed", new SeedReward());
        rewards.put("xp", new XPReward());

        conditions.values().forEach(c -> plugin.getServer().getPluginManager().registerEvents(c, plugin));

        this.readFromFile();
    }

    void addManyThingsAsSentence(StringBuilder msg, String prefix, List<? extends QuestCommandDescriptionDisplayable> descriptables, Player player) {
        List<String> renderedDescriptables = new ArrayList<>();
        for (QuestCommandDescriptionDisplayable reward : descriptables) {
            renderedDescriptables.add(reward.toMiniMessage(player));
        }

        msg.append(prefix);

        if (renderedDescriptables.size() == 1) {
            msg.append(renderedDescriptables.get(0)).append(".");
        } else if (renderedDescriptables.size() == 2) {
            msg.append(renderedDescriptables.get(0)).append(" and ").append(renderedDescriptables.get(1)).append(".");
        } else {
            for (int i = 0; i < renderedDescriptables.size(); i++) {
                msg.append(renderedDescriptables.get(i));
                if (i == renderedDescriptables.size() - 2) {
                    msg.append(", and ");
                } else if (i < renderedDescriptables.size() - 3) {
                    msg.append(", ");
                } else {
                    msg.append(".");
                }
            }
        }
    }

    public String executeQuestClaimRewardsCommandAndMakeMessage(Player player, String questId) {
        String completedQuests = player.getPersistentDataContainer().getOrDefault(completedQuestsNSK, PersistentDataType.STRING, "");
        Set<String> completedQuestsSet = new HashSet<>(Arrays.asList(completedQuests.split(";")));

        String claimedQuests = player.getPersistentDataContainer().getOrDefault(claimedRewardQuestsNSK, PersistentDataType.STRING, "");
        Set<String> claimedRewardQuestsSet = new HashSet<>(Arrays.asList(claimedQuests.split(";")));

        Set<String> toClaimQuestRewardsSet = new HashSet<>(completedQuestsSet);
        toClaimQuestRewardsSet.removeAll(claimedRewardQuestsSet);

        if (!toClaimQuestRewardsSet.contains(questId)) {
            return "You do not have the quest rewards for quest <green>" + questId + "</green> available now.";
        }

        Quest claimedQuest = null;
        x: for (QuestLine questLine : QuestManager.it.questLines) {
            for (Quest quest : questLine.quests()) {
                if (quest.id().equals(questId)) {
                    claimedQuest = quest;
                    break x;
                }
            }
        }

        if (claimedQuest == null) {
            return "Quest couldn't be found, please report this to an admin!";
        }

        for (RewardData reward : claimedQuest.rewards()) {
            reward.giveReward(player);
        }

        claimedRewardQuestsSet.add(claimedQuest.id());
        player.getPersistentDataContainer().set(this.claimedRewardQuestsNSK, PersistentDataType.STRING, String.join(";", claimedRewardQuestsSet));

        StringBuilder msg = new StringBuilder();

        addManyThingsAsSentence(msg, "You have received ", claimedQuest.rewards(), player);

        msg.append(" for completion of <green>").append(claimedQuest.title()).append("</green>!\n");

        return msg.toString();
    }

    public String makeQuestMessage(Player player) {
        StringBuilder msg = new StringBuilder("\n");
        boolean addedClaimableQuestRewardHeader = false;

        String completedQuests = player.getPersistentDataContainer().getOrDefault(completedQuestsNSK, PersistentDataType.STRING, "");
        Set<String> completedQuestsSet = new HashSet<>(Arrays.asList(completedQuests.split(";")));

        String claimedQuests = player.getPersistentDataContainer().getOrDefault(claimedRewardQuestsNSK, PersistentDataType.STRING, "");
        Set<String> claimedRewardQuestsSet = new HashSet<>(Arrays.asList(claimedQuests.split(";")));

        Set<String> toClaimQuestRewardsSet = new HashSet<>(completedQuestsSet);
        toClaimQuestRewardsSet.removeAll(claimedRewardQuestsSet);

        for (QuestLine questLine : QuestManager.it.questLines) {
            for (Quest quest : questLine.quests()) {
                if (toClaimQuestRewardsSet.contains(quest.id())) {
                    if (!addedClaimableQuestRewardHeader) {
                        msg.append("<red><b>Claimable Quest Rewards</red>\n");
                        addedClaimableQuestRewardHeader = true;
                    }
                    msg     .append("<hover:show_text:'<b><green>")
                            .append(questLine.title())
                            .append("</b> > <b><aqua>")
                            .append(quest.title())
                            .append("</b>")
                            .append("'><b>*</b> <aqua>")
                            .append(quest.title())
                            .append("</hover> <gray>")
                            .append("<hover:show_text:'");

                    addManyThingsAsSentence(msg, "You will be given ", quest.rewards(), player);

                    msg     .append("'><click:run_command:'/claimquestrewards ")
                            .append(quest.id())
                            .append("'>")
                            .append("(Click to Claim Rewards)</gray>\n");
                }
            }
        }

        boolean addedQuestHeader = false;


        for (QuestLine questLine : this.questLines) {
            for (Quest quest : questLine.quests()) {
                if (completedQuestsSet.contains(quest.id())) {
                    continue;
                }

                boolean isCompleted = true;

                for (ConditionData condition : quest.conditions()) {
                    isCompleted = isCompleted && condition.isCompleted(player);
                }

                if (!isCompleted) {
                    if (!addedQuestHeader) {
                        if (addedClaimableQuestRewardHeader) {
                            msg.append("\n");
                        }
                        msg.append("<green><b>Quests</green>\n");
                        addedQuestHeader = true;
                    }

                    msg     .append("<hover:show_text:'<b><green>")
                            .append(questLine.title())
                            .append("</b> > <b><aqua>")
                            .append(quest.title())
                            .append("</b>\n\n<light_purple>");

                    addManyThingsAsSentence(msg, "To complete this quest, you need to ", quest.conditions().stream().filter(c -> !c.isCompleted(player)).toList(), player);

                    msg     .append("</light_purple>\n\n<yellow>");

                    addManyThingsAsSentence(msg, "When completed, you will be given ", quest.rewards(), player);

                    msg
                            .append("</yellow>'><b>*</b> <aqua>")
                            .append(quest.title())
                            .append("</hover>\n");
                }

                break;
            }
        }

        if (!addedQuestHeader && !addedClaimableQuestRewardHeader) {
            msg.append("<red>You have <u>no</u> quests left to complete!\n");
        }

        return msg.toString();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        // do on player join in case new quests were added
        // since the player's last play time
        resetCachedPlayerRequirements(playerJoinEvent.getPlayer());
    }


    void resetCachedPlayerRequirements(Player player) {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("resetCachedPlayerRequirements"));

        // clear all cached requirements
        List<NamespacedKey> keysToClear = new ArrayList<>();

        for (NamespacedKey key : player.getPersistentDataContainer().getKeys()) {
            if (key.value().endsWith("_requirement")) {
                keysToClear.add(key);
            }
        }

        for (NamespacedKey key : keysToClear) {
            player.getPersistentDataContainer().remove(key);
        }

        // remake all cached requirements
        String completedQuests = player.getPersistentDataContainer().getOrDefault(completedQuestsNSK, PersistentDataType.STRING, "");
        Set<String> completedQuestsSet = new HashSet<>(Arrays.asList(completedQuests.split(";")));
        for (QuestLine questLine : this.questLines) {
            for (Quest quest : questLine.quests()) {
                if (completedQuestsSet.contains(quest.id())) {
                    continue;
                }

                for (ConditionData condition : quest.conditions()) {
                    if (!condition.isCompleted(player)) {
                        condition.serializeNextLevelUpRequirement(player);
                    }
                }

                break;
            }
        }
    }

    public void evaluatePossibleLevelUp(Player player) {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("evaluatePossibleLevelUp"));
        String completedQuests = player.getPersistentDataContainer().getOrDefault(completedQuestsNSK, PersistentDataType.STRING, "");
        Set<String> completedQuestsSet = new HashSet<>(Arrays.asList(completedQuests.split(";")));
        for (QuestLine questLine : this.questLines) {
            for (Quest quest : questLine.quests()) {
                if (completedQuestsSet.contains(quest.id())) {
                    continue;
                }

                var isQuestDone = true;
                for (ConditionData condition : quest.conditions()) {
                    isQuestDone = isQuestDone && condition.isCompleted(player);
                }

                if (isQuestDone) {
                    completedQuestsSet.add(quest.id());
                    player.getPersistentDataContainer().set(this.completedQuestsNSK, PersistentDataType.STRING, String.join(";", completedQuestsSet));
                }
            }
        }

        resetCachedPlayerRequirements(player);
    }

    public void readFromFile() {
        File questsFile = new File(QuestSystem.dataFolder, "quests.yaml");
        try {
            if (!questsFile.exists()) {
                // todo: disable plugin
            }

            YamlMapping questsRoot = Yaml.createYamlInput(questsFile).readYamlMapping();

            // todo: should add validation to a schema
            for (YamlNode value : questsRoot.yamlSequence("questlines")) {
                YamlMapping questLine = value.asMapping();
                var questLineMeta = questLine.yamlMapping("meta");

                var questsSeq = questLine.yamlSequence("quests");
                List<Quest> quests = new ArrayList<>();
                for (YamlNode node : questsSeq) {
                    YamlMapping questMapping = node.asMapping();
                    var questMeta = questMapping.yamlMapping("meta");

                    var conditionsSeq = questMapping.yamlSequence("conditions");
                    List<ConditionData> conditions = new ArrayList<>();
                    for (YamlNode yamlNode : conditionsSeq) {
                        YamlMapping condition = yamlNode.asMapping();
                        conditions.add(this.conditions.get(condition.string("type")).toData(condition));
                    }

                    var rewardsSeq = questMapping.yamlSequence("rewards");
                    List<RewardData> rewards = new ArrayList<>();
                    for (YamlNode yamlNode : rewardsSeq) {
                        YamlMapping reward = yamlNode.asMapping();
                        rewards.add(this.rewards.get(reward.string("type")).toData(reward));
                    }

                    Quest quest = new Quest(questMeta.string("id"), questMeta.string("title"), conditions, rewards);
                    quests.add(quest);
                }

                questLines.add(new QuestLine(
                        questLineMeta.string("id"),
                        questLineMeta.string("title"),
                        questLineMeta.string("description"),
                        quests
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            // todo: disable plugin
        }
    }
}
