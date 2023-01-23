package com.github.merelysnow.vips.hook;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class LuckPermsHook {

    private LuckPerms luckPerms;

    public void init() {
        RegisteredServiceProvider<LuckPerms> registration = Bukkit.getServicesManager()
                .getRegistration(LuckPerms.class);

        if (registration != null) {
            luckPerms = registration.getProvider();
        }
    }

    public GroupManager getGroupManager() {
        return luckPerms.getGroupManager();
    }

    public User getPlayerAdapter(Player player) {
        return luckPerms.getPlayerAdapter(Player.class).getUser(player);
    }

    public UserManager getUserManager() {
        return luckPerms.getUserManager();
    }
}