package de.vinweh.nerferVinweh;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class RiptideListener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public boolean hasCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return false;
        long now = System.currentTimeMillis();
        return now - cooldowns.get(uuid) < getCooldown(player);
    }

    public long getRemaining(Player player) {
        UUID uuid = player.getUniqueId();
        if (!cooldowns.containsKey(uuid)) return 0;
        long now = System.currentTimeMillis();
        return Math.max(0, getCooldown(player) - (now - cooldowns.get(uuid)));
    }

    public void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public long getCooldown(Player player) {
        Block block = player.getLocation().getBlock();
        return (block.getType() == Material.WATER || block.getType() == Material.BUBBLE_COLUMN) ? 3000 : 5000;
    }
}
