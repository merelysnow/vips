package com.github.merelysnow.vips.controller;

import com.github.merelysnow.vips.data.Reward;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import javax.swing.*;
import java.util.List;

@RequiredArgsConstructor
public class RewardController {

    public final List<Reward> cache = Lists.newArrayList();
    private final FileConfiguration fileConfiguration;

    public void loadRewards() {
        for (String path : fileConfiguration.getConfigurationSection("Rewards").getKeys(false)) {
            ConfigurationSection key = fileConfiguration.getConfigurationSection("Rewards." + path);

            List<String> commands = key.getStringList("comandos");
            cache.add(new Reward(path, commands));
        }
    }

    public Reward get(String name) {
        return cache.stream().filter(reward -> reward.getGroup().equals(name))
                .findFirst().orElse(null);
    }
}
