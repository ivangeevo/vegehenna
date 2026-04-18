package org.btwr.vegehenna.event;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.event.GameEvent;
import org.btwr.shared_library.util.utils.IdUtils;
import org.btwr.vegehenna.event.events.ModBreakEvents;
import org.btwr.vegehenna.event.events.ModUsageEvents;

public class ModEvents {

    public static final RegistryEntry<GameEvent> GOURD_EXPLODE = Registry.registerReference(
            Registries.GAME_EVENT,
            IdUtils.ofVG("gourd_explode"),
            new GameEvent(16) // range, same as BLOCK_DESTROY
    );

    public static void register() {
        ModBreakEvents.register();
        ModUsageEvents.register();
        //ModLootTableEvents.register();
    }
}
