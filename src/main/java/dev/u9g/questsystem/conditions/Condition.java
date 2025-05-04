package dev.u9g.questsystem.conditions;

import com.amihaiemil.eoyaml.YamlMapping;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface Condition<T extends ConditionData> extends Listener {
    T toData(YamlMapping context);
}
