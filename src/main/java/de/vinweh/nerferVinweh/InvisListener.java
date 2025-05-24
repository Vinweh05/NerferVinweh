package de.vinweh.nerferVinweh;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.*;

import java.util.HashMap;
import java.util.UUID;

import static java.lang.Math.exp;

public class InvisListener {

    private final HashMap<UUID, Long> messageCooldown = new HashMap<>();

    private final double minTicks;
    private final double maxTicks;
    private final double growthRate;
    private final double base;
    private final long messageDelay;

    public InvisListener(NerferVinweh plugin) {

        FileConfiguration config = plugin.getConfig();
        this.minTicks = config.getDouble("invisibility.minTicks", 20);
        this.maxTicks = config.getDouble("invisibility.maxTicks", 60);
        this.growthRate = config.getDouble("invisibility.growthRate", 0.122);
        this.base = config.getDouble("invisibility.base", 17.7);
        this.messageDelay = config.getLong("invisibility.messageCooldownMs", 30000);
    }

    public void applyTickEffects() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 0, true, false));
            }
        }
    }

    public void reduceInvisibility(Player player, double damage) {
        PotionEffect effect = player.getPotionEffect(PotionEffectType.INVISIBILITY);
        if (effect == null) return;

        int currentDuration = effect.getDuration();
        double minutes = currentDuration / 1200.0;
        double rawReduction = base * exp(growthRate * minutes);

        double clamped = Math.max(minTicks, Math.min(maxTicks, rawReduction));
        int reductionPerDamage = (int) (clamped);
        int reductionTicks = (int) (damage * reductionPerDamage);

        int newDuration = Math.max(20, currentDuration - reductionTicks);

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, newDuration, effect.getAmplifier(), false, false));

        long now = System.currentTimeMillis();
        long last = messageCooldown.getOrDefault(player.getUniqueId(), 0L);

        if (now - last > messageDelay) {
            player.sendMessage("§cDeine Unsichtbarkeit wurde durch Schaden geschwächt.");
            messageCooldown.put(player.getUniqueId(), now);
        }
    }
}
