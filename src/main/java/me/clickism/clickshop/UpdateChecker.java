package me.clickism.clickshop;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private static final String API_URL = "https://api.spigotmc.org/legacy/update.php?resource=";
    private final String resourceId;
    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin, String resourceId) {
        this.resourceId = resourceId;
        this.plugin = plugin;
    }

    public void checkVersion(final Consumer<String> consumer) {
    }
}
