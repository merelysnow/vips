package com.github.merelysnow.vips.commands.key;

import com.github.merelysnow.vips.VipsPlugin;
import com.github.merelysnow.vips.hook.LuckPermsHook;
import com.github.merelysnow.vips.data.Key;
import com.google.common.collect.Lists;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.luckperms.api.model.group.Group;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreateKeyCommand {

    private final List<String> blocked = Lists.newArrayList("master", "gerente", "manager", "admin", "moderador", "ajudante");

    @Command(
            name = "gerarkey",
            usage = "gerarkey <grupo> <dias>",
            permission = "vips.admin",
            target = CommandTarget.PLAYER
    )

    public void handleCommandAdd(Context<Player> context, String groupName, int days) {

        Player player = context.getSender();
        LuckPermsHook hook = VipsPlugin.getInstance().getLuckPermsHook();
        Group group = hook.getGroupManager().getGroup(groupName);

        if (group == null) {
            player.sendMessage("§cGrupo não encontrado.");
            return;
        }

        if (days <= 0) {
            player.sendMessage("§cTempo inválido.");
            return;
        }

        if (blocked.contains(groupName)) {
            context.sendMessage("§cEsse grupo está bloqueado.");
            return;
        }

        Key key = new Key(RandomStringUtils.randomAlphabetic(8), groupName, Instant.now().plusMillis(TimeUnit.DAYS.toMillis(days)));

        player.sendMessage(String.format(
                "§fKey: §7%s §8(%s) §7- §f%sD.",
                key.getId(),
                key.getGroup().toUpperCase(),
                days));
        VipsPlugin.getInstance().getKeysController().registerKey(key);
    }
}
