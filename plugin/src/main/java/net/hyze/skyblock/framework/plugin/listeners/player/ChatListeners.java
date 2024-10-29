package net.hyze.skyblock.framework.plugin.listeners.player;

import com.google.common.collect.Maps;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.hyze.core.shared.CoreProvider;
import net.hyze.core.shared.group.Group;
import net.hyze.core.shared.messages.MessageUtils;
import net.hyze.core.shared.misc.cooldowns.UserCooldowns;
import net.hyze.core.shared.misc.utils.StringSimilarity;
import net.hyze.core.shared.user.User;
import net.hyze.core.spigot.misc.message.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListeners implements Listener {

    private static final String COOLDOWN_KEY = "COOLDOWN_KEY";
    private static final ConcurrentMap<Integer, String> LAST_MESSAGE = Maps.newConcurrentMap();

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {

        Player player = (Player) event.getPlayer();
        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        event.setCancelled(true);

        if (user.hasGroup(Group.MODERATOR)) {
            if (!UserCooldowns.hasEnded(user, COOLDOWN_KEY)) {
                Message.ERROR.send(player, "Aguarde " + UserCooldowns.getSecondsLeft(user, COOLDOWN_KEY) + " segundos para utilizar o chat novamente.");
                return;
            }

            UserCooldowns.start(user, COOLDOWN_KEY, 2, TimeUnit.SECONDS);
        }

        String message = MessageUtils.stripColor(MessageUtils.translateColorCodes(event.getMessage()));

        if (message.length() < 0) {
            return;
        }

        if (!user.hasGroup(Group.GAME_MASTER) && isSimilar(user.getId(), message)) {
            Message.ERROR.send(Bukkit.getPlayerExact(user.getNick()), "Você não pode enviar uma mensagem tão parecida com a anterior.");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();

        Group group = user.getHighestGroup();
        String name = user.getNick();
        ChatColor color = ChatColor.WHITE;

        if (group.equals(Group.DEFAULT)) {
            name = ChatColor.GRAY + name;
            color = ChatColor.GRAY;
        } else {
            name = user.getHighestGroup().getDisplayTag(user.getNick());
        }

        messageBuilder.append(name)
                .append(": ")
                .append(color)
                .append(message);

        Bukkit.getOnlinePlayers().stream().filter(target -> target.getWorld().equals(player.getWorld())).forEach(target -> {
            target.sendMessage(messageBuilder.toString());
        });

    }

    private boolean isSimilar(int id, String message) {
        message = MessageUtils.stripColor(message);

        String last = LAST_MESSAGE.getOrDefault(id, null);
        boolean result = (last != null
                && (last.equalsIgnoreCase(message) || StringSimilarity.getSimilarity(last, message) >= 0.9D));

        LAST_MESSAGE.put(id, message);
        return result;
    }

}
