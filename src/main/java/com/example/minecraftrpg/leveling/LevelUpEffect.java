package com.example.minecraftrpg.leveling;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class LevelUpEffect {
    public static void play(Player player) {
        Firework fw = player.getWorld().spawn(player.getLocation(), Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.YELLOW, Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build();

        meta.addEffect(effect);
        meta.setPower(0); // Minimal power for quick explosion
        fw.setFireworkMeta(meta);
    }
}