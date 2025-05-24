package de.vinweh.nerferVinweh;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class NerferEventHandler implements Listener {

    private final RiptideListener riptide = new RiptideListener();
    private final InvisListener invis;

    private final double speed1;
    private final double speed2;
    private final double speed3;

    public NerferEventHandler(Plugin plugin) {
        this.invis = new InvisListener((NerferVinweh) plugin);

        FileConfiguration config = plugin.getConfig();
        this.speed1 = config.getDouble("riptide.level1Speed", 0.6);
        this.speed2 = config.getDouble("riptide.level2Speed", 0.7);
        this.speed3 = config.getDouble("riptide.level3Speed", 0.85);

        Bukkit.getScheduler().runTaskTimer(plugin, invis::applyTickEffects, 0L, 40L);
    }

    @EventHandler
    public void onRiptideUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.TRIDENT) return;
        if (!item.containsEnchantment(Enchantment.RIPTIDE)) return;

        if (riptide.hasCooldown(player)) {
            long remaining = riptide.getRemaining(player);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cRiptide Cooldown: §e" + String.format("%.1f", remaining / 1000.0) + "s"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        int level = event.getItem().getEnchantmentLevel(Enchantment.RIPTIDE);
        double speed = switch (level) {
            case 1 -> speed1;
            case 2 -> speed2;
            case 3 -> speed3;
            default -> 0;
        };

        riptide.setCooldown(player);

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("NerferVinweh"), () ->
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(speed)), 1L);
    }

    @EventHandler
    public void onInvisibilityDamaged(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && player.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY)) {
            invis.reduceInvisibility(player, event.getFinalDamage());
        }
    }
}
