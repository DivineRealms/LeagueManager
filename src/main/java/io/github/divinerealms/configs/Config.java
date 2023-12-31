package io.github.divinerealms.configs;

import io.github.divinerealms.managers.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Config extends ConfigManager {
    private final String name = "config.yml";
    @Setter private FileConfiguration settings;

    public Config(final JavaPlugin plugin) {
        super(plugin, "");
    }

    public void reload() {
        reloadConfig(getName());
        setSettings(getConfig(getName()));
    }

    public List<String> getStringList(final String path) {
        final List<String> list = getSettings().getStringList(path);
        return new ArrayList<>(list);
    }
}