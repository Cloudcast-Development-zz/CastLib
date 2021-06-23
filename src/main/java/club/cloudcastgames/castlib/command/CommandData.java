package club.cloudcastgames.castlib.command;

import club.cloudcastgames.castlib.command.param.ParameterData;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.CustomTimingsHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class CommandData {
    @Getter private String[] names;
    @Getter private String permission;
    @Getter private String description;
    @Getter private boolean async;
    @Getter private boolean hidden;
    @Getter private List<ParameterData> parameters;
    @Getter private Method method;
    @Getter private CustomTimingsHandler timingsHandler;
    @Getter private boolean consoleAllowed;

    public CommandData(Command commandAnn, List<ParameterData> parameters, Method method, boolean consoleAllowed) {
        this.names = commandAnn.names();
        this.permission = commandAnn.permission();
        this.description = commandAnn.description();
        this.async = commandAnn.async();
        this.hidden = commandAnn.hidden();
        this.parameters = parameters;
        this.method = method;
        this.consoleAllowed = consoleAllowed;
        this.timingsHandler = new CustomTimingsHandler("CommandHandler - " + getName());
    }

    public String getName() {
        return (names[0]);
    }

    public boolean canAccess(CommandSender sender) {
        if (!(sender instanceof Player)) return (true);

        switch (permission) {
            case "op":
                return (sender.isOp());
            case "console":
                return (sender instanceof ConsoleCommandSender);
            case "":
                return (true);
            default:
                return (sender.hasPermission(permission));
        }
    }

    public String getUsageString() {
        return (getUsageString(getName()));
    }

    public String getUsageString(String aliasUsed) {
        StringBuilder stringBuilder = new StringBuilder();

        for (ParameterData paramHelp: getParameters()) {
            boolean needed = paramHelp.getDefaultValue().isEmpty();
            stringBuilder.append(needed ? "<" : "[").append(paramHelp.getName());
            stringBuilder.append(paramHelp.isWildcard() ? "..." : "");
            stringBuilder.append(needed ? ">" : "]").append(" ");
        }
        return ("/" + aliasUsed.toLowerCase() + " " + stringBuilder.toString().trim().toLowerCase());
    }

    public void execute(CommandSender sender, String[] params) {
        // We start to build the parameters we call the method with here.
        List<Object> transformedParameters = new ArrayList<>();

        // Add the sender.
        // If the method is expecting a Player or a general CommandSender will be handled by Java.
        transformedParameters.add(sender);

        // Fill in / validate parameters
        for (int parameterIndex = 0; parameterIndex < getParameters().size(); parameterIndex++) {
            ParameterData parameter = getParameters().get(parameterIndex);
            String passedParameter = (parameterIndex < params.length ? params[parameterIndex] : parameter.getDefaultValue()).trim();

            if (parameterIndex >= params.length && (parameter.getDefaultValue() == null || parameter.getDefaultValue().isEmpty())) {
                TextComponent component = new TextComponent();

                component.setText(ChatColor.RED + "Usage: " + getUsageString());

                sender.sendMessage(component.getText());
                return;
            }

            // Wildcards "capture" all strings after them
            if (parameter.isWildcard() && !passedParameter.trim().equalsIgnoreCase(parameter.getDefaultValue().trim())) {
                passedParameter = toString(params, parameterIndex);
            }

            Object result = CastCommandHandler.transformParameter(sender, passedParameter, parameter.getParamterClass());

            if (result == null) return;

            transformedParameters.add(result);

            if (parameter.isWildcard()) {
                break;
            }
        }

        timingsHandler.startTiming();

        try {
            // null = static method.
            method.invoke(null, transformedParameters.toArray());
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "It appears there was some issues processing your command...");
            ex.printStackTrace();
        }

        timingsHandler.stopTiming();
    }

    public static String toString(String[] args, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int arg = start; arg < args.length; arg++) {
            stringBuilder.append(args[arg]).append(" ");
        }

        return (stringBuilder.toString().trim());
    }
}
