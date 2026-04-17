package org.btwr.vegehenna.config;

import org.btwr.shared_library.api.config.ConfigBuilder;
import org.btwr.shared_library.api.config.ConfigGroup;
import org.btwr.shared_library.api.config.ConfigSetting;
import org.btwr.shared_library.api.config.TomlConfigManager;
import org.btwr.vegehenna.VegehennaMod;

public class VGModConfig {
    /** Replace with your MOD_ID for easy adaptation **/
    private static final String MOD_ID = VegehennaMod.MOD_ID;

    public static final ConfigGroup CONFIG;

    /** Call this method in your mod initializer so the class can initialize **/
    public static void register() {}

    public static final ConfigSetting<Boolean> canWeedsGrow =
            ConfigBuilder.booleanSetting("canWeedsGrow")
                    .defaultValue(false)
                    .comment("Toggles whether weeds can grow on crops/farmland")
                    .build();

    static {
        CONFIG = new ConfigGroup(String.format("%s/%s_common.toml", MOD_ID, MOD_ID));
        CONFIG.add(canWeedsGrow);
        TomlConfigManager.registerGroup(CONFIG); // auto init/load/save
    }
}