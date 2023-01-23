package com.github.merelysnow.vips.commands.vip;

import com.github.merelysnow.vips.VipsPlugin;
import com.github.merelysnow.vips.hook.LuckPermsHook;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("all")
public class VipTimeCommand {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.UK)
            .withZone(ZoneId.systemDefault());

    @Command(
            name = "tempovip",
            target = CommandTarget.PLAYER
    )

    public void handleCommandTime(Context<Player> context) {
        Player player = context.getSender();

        LuckPermsHook hook = VipsPlugin.getInstance().getLuckPermsHook();

        User user = hook.getPlayerAdapter(player);

        List<InheritanceNode> groups = new ArrayList<>();
        for (InheritanceNode inheritanceNode : user.getNodes(NodeType.INHERITANCE)) {
            if (inheritanceNode.hasExpiry()) {
                groups.add(inheritanceNode);
            }
        }

        if (groups.isEmpty()) {
            player.sendMessage("§cVocê não possui nenhum VIP.");
        }

        StringBuilder builder = new StringBuilder("\n");

        for (InheritanceNode node : groups) {
            Group group = hook.getGroupManager().getGroup(node.getGroupName());
            if (group == null) {
                continue;
            }

            Instant expiry = node.getExpiry();
            if (expiry == null) {
                continue;
            }

            builder.append(group.getCachedData().getMetaData().getPrefix().replace("&", "§"))
                    .append(ChatColor.WHITE)
                    .append("Expira em: ")
                    .append(DATE_TIME_FORMATTER.format(expiry))
                    .append("\n");
        }

        builder.append("\n ");

        player.sendMessage(builder.toString());
    }
}
