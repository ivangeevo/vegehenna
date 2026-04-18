package org.btwr.vegehenna.event;

import org.btwr.vegehenna.event.events.ModBreakEvents;
import org.btwr.vegehenna.event.events.ModUsageEvents;

public class ModEvents {

    public static void register() {
        ModBreakEvents.register();
        ModUsageEvents.register();
        //ModLootTableEvents.register();
    }
}
