package dev.u9g.questsystem.structures;

import dev.u9g.questsystem.conditions.ConditionData;
import dev.u9g.questsystem.rewards.RewardData;

import java.util.List;

public record Quest(String id, String title, List<ConditionData> conditions, List<RewardData> rewards) {
}
