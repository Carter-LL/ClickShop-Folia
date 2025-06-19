package me.clickism.clickshop.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class FoliaCompat {

    private static Boolean foliaDetected = null;

    public static Plugin Savedplugin = null;

    public static boolean isFolia() {
        if (foliaDetected != null) return foliaDetected;
        foliaDetected = Bukkit.getServer().getName().toLowerCase().contains("folia");
        System.out.println("Folia Status: " + foliaDetected);
        return foliaDetected;
    }

    public static void runGlobalRegion(Plugin plugin, Runnable task, long delayTicks) {
        if (isFolia()) {
            try {
                Object scheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                Method runDelayed = scheduler.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
                runDelayed.invoke(scheduler, plugin, (Consumer<Object>) st -> task.run(), Math.max(delayTicks, 1));
            } catch (Exception e) {
                //lugin.getLogger().warning("Failed to use Folia global scheduler. Falling back.");
                e.printStackTrace();
                //Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runRegionAtLocation(Plugin plugin, Location location, Runnable task, long delayTicks) {
        if (isFolia()) {
            try {
                Method getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");
                Object regionScheduler = getRegionScheduler.invoke(null);

                Method runDelayed = regionScheduler.getClass().getMethod(
                        "runDelayed", Plugin.class, Location.class, Consumer.class, long.class
                );

                runDelayed.invoke(regionScheduler, plugin, location, (Consumer<Object>) st -> task.run(), Math.max(delayTicks, 1));
            } catch (Exception e) {
                e.printStackTrace();
                // fallback to Bukkit scheduler if Folia call fails
                Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }


    public static void runPlayerRegion(Plugin plugin, Player player, Runnable task, long delayTicks) {
        if (isFolia()) {
            try {
                Method getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");
                Object regionScheduler = getRegionScheduler.invoke(null);
                Location location = player.getLocation();

                Method runDelayed = regionScheduler.getClass().getMethod(
                        "runDelayed", Plugin.class, Location.class, Consumer.class, long.class
                );

                Consumer<Object> consumer = scheduledTask -> task.run();
                runDelayed.invoke(regionScheduler, plugin, location, consumer, Math.max(delayTicks, 1));

            } catch (Exception e) {
                //plugin.getLogger().warning("Failed to run delayed region task with Folia. Falling back.");
                e.printStackTrace();
                //Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runRandomRegion(Plugin plugin, World world, Runnable task, long delayTicks) {
        if (isFolia()) {
            try {
                Method getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");
                Object regionScheduler = getRegionScheduler.invoke(null);

                // Generate a random isolated region (far from normal gameplay)
                int base = 100000; // Far away base coord
                int x = base + ThreadLocalRandom.current().nextInt(1000) * 16;
                int z = base + ThreadLocalRandom.current().nextInt(1000) * 16;
                Location location = new Location(world, x, 64, z);

                Bukkit.getLogger().warning("Fake Chunk Region at: " + location.toString());

                Method runDelayed = regionScheduler.getClass().getMethod(
                        "runDelayed", Plugin.class, Location.class, Consumer.class, long.class
                );
                runDelayed.invoke(regionScheduler, plugin, location, (Consumer<Object>) st -> task.run(), Math.max(delayTicks, 1));

            } catch (Exception e) {
                //plugin.getLogger().warning("Failed to run task in random region. Falling back.");
                e.printStackTrace();
               // Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runGlobalRegionRepeating(Plugin plugin, Runnable task, long initialDelay, long period) {
        if (isFolia()) {
            try {
                Object scheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                Method runAtFixedRate = scheduler.getClass().getMethod(
                        "runAtFixedRate",
                        Plugin.class,
                        Consumer.class,
                        long.class,
                        long.class
                );
                runAtFixedRate.invoke(scheduler, plugin, (Consumer<Object>) st -> task.run(),
                        Math.max(initialDelay, 1), Math.max(period, 1));
            } catch (Exception e) {
                //plugin.getLogger().warning("Failed to use Folia global repeating scheduler. Falling back.");
                e.printStackTrace();
                //Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
            }
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
        }
    }

    public static void runRandomRegionRepeating(Plugin plugin, World world, Runnable task, long initialDelay, long period) {
        if (isFolia()) {
            try {
                Method getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");
                Object regionScheduler = getRegionScheduler.invoke(null);

                // Generate a random isolated region (far from normal gameplay)
                int base = 100000; // Far away base coord
                int x = base + ThreadLocalRandom.current().nextInt(1000) * 16;
                int z = base + ThreadLocalRandom.current().nextInt(1000) * 16;
                Location location = new Location(world, x, 64, z);

                Bukkit.getLogger().warning("Fake Chunk Region (Repeating) at: " + location.toString());

                Method runAtFixedRate = regionScheduler.getClass().getMethod(
                        "runAtFixedRate",
                        Plugin.class,
                        Location.class,
                        Consumer.class,
                        long.class,
                        long.class
                );
                runAtFixedRate.invoke(regionScheduler, plugin, location, (Consumer<Object>) st -> task.run(),
                        Math.max(initialDelay, 1), Math.max(period, 1));

            } catch (Exception e) {
               // plugin.getLogger().warning("Failed to run repeating task in random region. Falling back.");
                e.printStackTrace();
               // Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
            }
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
        }
    }


    public static void runPlayerRegionRepeating(Plugin plugin, Player player, Runnable task, long initialDelay, long period) {
        if (isFolia()) {
            try {
                Method getRegionScheduler = Bukkit.class.getMethod("getRegionScheduler");
                Object regionScheduler = getRegionScheduler.invoke(null);
                Location location = player.getLocation();

                Method runAtFixedRate = regionScheduler.getClass().getMethod(
                        "runAtFixedRate", Plugin.class, Location.class, Consumer.class, long.class, long.class
                );

                Consumer<Object> consumer = scheduledTask -> task.run();
                runAtFixedRate.invoke(regionScheduler, plugin, location, consumer,
                        Math.max(initialDelay, 1), Math.max(period, 1));
            } catch (Exception e) {
               // plugin.getLogger().warning("Failed to use Folia player region repeating scheduler. Falling back.");
                e.printStackTrace();
               // Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
            }
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelay, period);
        }
    }



    public static void teleportSafely(Plugin plugin, LivingEntity player, Location location) {
        if (isFolia()) {
            try {
                Method teleportAsync = Player.class.getMethod("teleportAsync", Location.class);
                teleportAsync.invoke(player, location);
            } catch (Exception e) {
                //plugin.getLogger().warning("teleportAsync failed, attempting fallback teleport.");
                e.printStackTrace();
                //player.teleport(location); // May still fail in Folia
            }
        } else {
            player.teleport(location);
        }
    }

    public static void runOnMainThread(Plugin plugin, Runnable task, long delayTicks) {
        Savedplugin = plugin;
        runGlobalRegion(plugin, task, delayTicks);
    }

    public static void runAsync(Runnable task) {
        if (isFolia()) {
            try {
                Object asyncScheduler = Bukkit.class.getMethod("getAsyncScheduler").invoke(null);

                Method runNow = asyncScheduler.getClass().getMethod(
                        "runNow", Plugin.class, Consumer.class
                );

                runNow.invoke(asyncScheduler, Savedplugin, (Consumer<Object>) st -> task.run());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(Savedplugin, task);
        }
    }



}
