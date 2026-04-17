package org.btwr.vegehenna.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class VGModClientConfig {
    // Handles config screen creation with Cloth Config API
    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("title.vegehenna.config"));
        //builder.setSavingRunnable();

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.vegehenna.category.general"));

        // Client Settings
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.vegehenna.text.clientSettingsText"))
                .build()
        );
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.vegehenna.text.emptyClientConfigText"))
                .build()
        );

        // Server Settings
        general.addEntry(entryBuilder
                .startTextDescription(Text.translatable("config.vegehenna.text.serverSettingsNoAccessText"))
                //.setDisplayRequirement(displayWhenRemoteOrLAN())
                .build()
        );

        return builder.build();
    }

     /* -----------------------------------------------------------
       Detection helpers
       ----------------------------------------------------------- */

    private static Requirement displayWhenLocalServer() {
        return VGModClientConfig::isTrueSingleplayer;
    }

    private static Requirement displayWhenRemoteOrLAN() {
        return VGModClientConfig::isNotTrueSingleplayer;
    }

    private static Requirement hideWhenNotTrueSingleplayer() {
        return () -> !isTrueSingleplayer();
    }

    private static boolean isWorldLoaded() {
        return MinecraftClient.getInstance().world != null;
    }

    private static boolean isNotTrueSingleplayer() {
        return !isTrueSingleplayer();
    }

    private static boolean isTrueSingleplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return false;
        if (client.getCurrentServerEntry() != null) return false;

        // Integrated server exists in SP and LAN host
        if (client.getServer() == null) return false;

        // Only SP has isRemote == false
        return !client.getServer().isRemote();
    }
}