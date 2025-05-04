package dev.u9g.questsystem.structures;

import java.util.List;

public record QuestLine(String id, String title, String description, List<Quest> quests) {
}
