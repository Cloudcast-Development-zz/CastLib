package club.cloudcastgames.castlib.command.param.defaults;

import club.cloudcastgames.castlib.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BooleanParameterType implements ParameterType<Boolean> {
    private static final Map<String, Boolean> MAP = new HashMap<>();

    // We can add more if you find someone using something like yas or ye...
    static {
        MAP.put("true", true);
        MAP.put("on", true);
        MAP.put("yes", true);

        MAP.put("false", false);
        MAP.put("off", false);
        MAP.put("no", false);
    }

    @Override
    public Boolean transform(CommandSender sender, String source) {
        if (!MAP.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid boolean.");
            return (null);
        }
        return MAP.get(source.toLowerCase());
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return (MAP.keySet().stream().filter(string -> StringUtils.startsWithIgnoreCase(string, source)).collect(Collectors.toList()));
    }
}
