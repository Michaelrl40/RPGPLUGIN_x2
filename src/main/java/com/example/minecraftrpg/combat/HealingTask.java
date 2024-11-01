package com.example.minecraftrpg.combat;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HealingTask extends BukkitRunnable {
    private final Player player;
    private final double totalHeal;
    private final int duration; // in ticks (20 ticks = 1 second)
    private final double healPerTick;
    private int ticksElapsed;

    public HealingTask(Player player, double totalHeal, int durationInSeconds) {
        this.player = player;
        this.totalHeal = totalHeal;
        this.duration = durationInSeconds * 20; // convert seconds to ticks
        this.healPerTick = totalHeal / duration;
        this.ticksElapsed = 0;
    }

    @Override
    public void run() {
        if (ticksElapsed >= duration || !player.isOnline()) {
            this.cancel();
            return;
        }

        if (player.isDead()) {
            this.cancel();
            return;
        }

        // Don't heal if at max health
        if (player.getHealth() >= player.getMaxHealth()) {
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
}
