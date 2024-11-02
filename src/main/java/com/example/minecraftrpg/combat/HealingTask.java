package com.example.minecraftrpg.combat;

import com.example.minecraftrpg.MinecraftRPG;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class HealingTask extends BukkitRunnable {
    private final Player player;
    private final double totalHeal;
    private final int duration;
    private final double healPerTick;
    private int ticksElapsed;
    private final MinecraftRPG plugin;

    public HealingTask(MinecraftRPG plugin, Player player, double totalHeal, int durationInSeconds) {
        this.plugin = plugin;
        this.player = player;
        this.totalHeal = totalHeal;
        this.duration = durationInSeconds * 20;
        this.healPerTick = totalHeal / duration;
        this.ticksElapsed = 0;
    }

    @Override
    public void run() {
        if (ticksElapsed >= duration || !player.isOnline() || player.isDead()) {
            onComplete();
            this.cancel();
            return;
        }

        // Don't heal if at max health
        if (player.getHealth() >= player.getMaxHealth()) {
            onComplete();
            this.cancel();
            return;
        }

        // Apply healing
        double newHealth = Math.min(player.getMaxHealth(),
                player.getHealth() + healPerTick);
        player.setHealth(newHealth);

        // Show healing progress every second (every 20 ticks)
        if (ticksElapsed % 20 == 0) {
            double percentComplete = (ticksElapsed * 100.0) / duration;
            player.sendMessage(ChatColor.GREEN + "Healing: " +
                    String.format("%.1f", percentComplete) + "%");
        }

        ticksElapsed++;
    }

    public abstract void onComplete();
}
