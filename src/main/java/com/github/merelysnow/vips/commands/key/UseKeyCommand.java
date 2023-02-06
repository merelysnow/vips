package com.github.merelysnow.vips.commands.key;

import com.github.merelysnow.vips.VipsPlugin;
import com.github.merelysnow.vips.data.Reward;
import com.github.merelysnow.vips.hook.LuckPermsHook;
import com.github.merelysnow.vips.data.Key;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class UseKeyCommand {

    @Command(
            name = "usarkey",
            usage = "usarkey <key>",
            target = CommandTarget.PLAYER
    )

    public void handleCommandUse(Context<Player> context, String id) {
        Player player = context.getSender();
        Key key = VipsPlugin.getInstance().getKeysController().get(id);

        if (key == null) {
            player.sendMessage("§cA key é invalida ou já foi usada.");
            return;
        }

        LuckPermsHook hook = VipsPlugin.getInstance().getLuckPermsHook();
        UserManager userManager = hook.getUserManager();
        User user = hook.getPlayerAdapter(player);

        for (InheritanceNode inheritanceNode : user.getNodes(NodeType.INHERITANCE)) {
            if (inheritanceNode.getGroupName().equals(key.getGroup())) {
                player.sendMessage("§cVocê já possui esse vip.");
                return;
            }
        }

        user.data().add(InheritanceNode.builder().group(key.getGroup()).expiry(key.getInstant()).build());

        Group group = hook.getGroupManager().getGroup(key.getGroup());
        Group primary = hook.getGroupManager().getGroup(user.getPrimaryGroup());

        if (primary.getWeight().getAsInt() < group.getWeight().getAsInt()) {
            user.setPrimaryGroup(key.getGroup());
        }

        Reward reward = VipsPlugin.getInstance().getRewardController().get(key.getGroup());

        if(reward != null) {
            for(String cmd : reward.getCommands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{player}", player.getName()));
            }
        }

        String prefix = group.getCachedData().getMetaData().getPrefix().replace("&", "§");

        userManager.saveUser(user);
        userManager.cleanupUser(user);

        VipsPlugin.getInstance().getKeysController().unregisterKey(key);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(prefix + " §f" + player.getName(), "§fTornou-se " + prefix);
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.AMBIENCE_THUNDER, 3.0F, 6.0F);
        }

        player.sendMessage("§aVocê ativou o VIP " + prefix + " §a com sucesso.");
        Bukkit.getConsoleSender().sendMessage(String.format("[VIP-LOGS] O jogador %s ativou a key (%s)%n", player.getName(), key.getId()));
    }
}
