package com.github.merelysnow.vips;

import com.github.merelysnow.vips.commands.key.UseKeyCommand;
import com.github.merelysnow.vips.commands.vip.GiveVipCommand;
import com.github.merelysnow.vips.commands.vip.VipTimeCommand;
import com.github.merelysnow.vips.controller.KeysController;
import com.github.merelysnow.vips.commands.key.CreateKeyCommand;
import com.github.merelysnow.vips.commands.key.DeleteKeyCommand;
import com.github.merelysnow.vips.controller.RewardController;
import com.github.merelysnow.vips.database.KeyDatabase;
import com.github.merelysnow.vips.hook.LuckPermsHook;
import lombok.Getter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class VipsPlugin extends JavaPlugin {

    private LuckPermsHook luckPermsHook;
    private KeyDatabase keyDatabase;
    private KeysController keysController;
    private RewardController rewardController;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        keyDatabase = new KeyDatabase(this);
        keysController = new KeysController(getKeyDatabase());
        rewardController = new RewardController(getConfig());

        keyDatabase.selectMany()
                .forEach(key -> {
                    keysController.registerKey(key);
                });
        rewardController.loadRewards();
        registerObjects();

        getLuckPermsHook().init();
        registerCommands();
    }

    private void registerCommands() {
        BukkitFrame bukkitFrame = new BukkitFrame(this);

        bukkitFrame.registerCommands(
                new CreateKeyCommand(),
                new DeleteKeyCommand(),
                new UseKeyCommand(),
                new GiveVipCommand(),
                new VipTimeCommand()
        );

        MessageHolder messageHolder = bukkitFrame.getMessageHolder();

        messageHolder.setMessage(MessageType.NO_PERMISSION, "??cVoc?? n??o tem permiss??o para executar este comando.");
        messageHolder.setMessage(MessageType.ERROR, "??cUm erro ocorreu! {error}");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "??cUtilize: /{usage}");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "??cVoc?? n??o pode utilizar este comando pois ele ?? direcioado apenas para {target}.");
    }

    private void registerObjects() {
        luckPermsHook = new LuckPermsHook();
    }

    public static VipsPlugin getInstance() {
        return getPlugin(VipsPlugin.class);
    }

}
