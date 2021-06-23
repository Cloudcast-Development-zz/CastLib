package club.cloudcastgames.castlib;

import club.cloudcastgames.castlib.command.CastCommandHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class CastLib extends JavaPlugin {

    @Getter private static CastLib instance;
    public static final Random RANDOM = new Random();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        CastCommandHandler.init();

        Bukkit.getConsoleSender().sendMessage("[CastLib] loaded in " + (System.currentTimeMillis() - start) + "ms. (By CloudCast Development)");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
