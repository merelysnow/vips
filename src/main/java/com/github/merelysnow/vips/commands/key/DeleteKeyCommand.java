package com.github.merelysnow.vips.commands.key;

import com.github.merelysnow.vips.VipsPlugin;
import com.github.merelysnow.vips.data.Key;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import me.saiintbrisson.minecraft.command.target.CommandTarget;
import org.bukkit.entity.Player;

public class DeleteKeyCommand {

    @Command(
            name = "delkey",
            usage = "delkey <key>",
            permission = "vips.admin",
            target = CommandTarget.PLAYER
    )

    public void handleCommandDel(Context<Player> context, String id) {
        Player player = context.getSender();
        Key key = VipsPlugin.getInstance().getKeysController().get(id);

        if (key == null) {
            player.sendMessage("§cNenhuma key foi encontada com esse id.");
            return;
        }

        VipsPlugin.getInstance().getKeysController().unregisterKey(key);
        player.sendMessage("§eYeaah! key deletada com sucesso.");
    }
}
