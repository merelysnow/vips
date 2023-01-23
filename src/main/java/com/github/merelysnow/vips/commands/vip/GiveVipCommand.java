package com.github.merelysnow.vips.commands.vip;

import com.google.common.collect.Lists;
import com.github.merelysnow.vips.VipsPlugin;
import com.github.merelysnow.vips.hook.LuckPermsHook;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("all")
public class GiveVipCommand {

    private final List<String> blocked = Lists.newArrayList("master", "gerente", "manager", "admin", "moderador", "ajudante");

    @Command(
            name = "darvip",
            usage = "darvip <jogador> <grupo> <dias>",
            permission = "vips.admin",
            target = CommandTarget.ALL
    )

    public void executeSubscribeCommand(Context<Player> context, Player target, String groupName, int groupDays) {
        LuckPermsHook hook = VipsPlugin.getInstance().getLuckPermsHook();

        Group group = hook.getGroupManager().getGroup(groupName);
        if (group == null) {
            context.sendMessage("§cGrupo inválido.");
            return;
        }

        if (groupDays <= 0) {
            context.sendMessage("§cTempo inválido.");
            return;
        }

        if (blocked.contains(groupName)) {
            context.sendMessage("§cVocê não pode definir esse grupo.");
            return;
        }

        User user = hook.getPlayerAdapter(target);

        DataMutateResult result = user.data().add(InheritanceNode.builder().group(group).expiry(groupDays, TimeUnit.DAYS).build());
        if (!result.wasSuccessful()) {
            context.sendMessage("§cNão foi possivel adicionar esse grupo pra este jogador.");
            return;
        }


        Group primary = hook.getGroupManager().getGroup(user.getPrimaryGroup());

        if (primary.getWeight().getAsInt() < group.getWeight().getAsInt()) {
            DataMutateResult dataMutateResult = user.setPrimaryGroup(groupName);
            if (!dataMutateResult.wasSuccessful()) {
                context.sendMessage("§cNão foi possivel adicionar esse grupo pra este jogador.");
                return;
            }
        }

        hook.getUserManager().saveUser(user);
        hook.getUserManager().cleanupUser(user);

        String prefix = Objects.requireNonNull(group.getCachedData().getMetaData().getPrefix()).replace("&", "§");

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendTitle(prefix.substring(0, 2) + target.getName(),
                    "§ftornou-se " + prefix);
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.AMBIENCE_THUNDER, 3.0F, 6.0F);
        }

        context.sendMessage("§aGrupo adicionado com sucesso!");
    }
}
