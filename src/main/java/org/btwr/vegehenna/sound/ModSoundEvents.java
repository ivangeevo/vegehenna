package org.btwr.vegehenna.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;

public class ModSoundEvents {
    public static final SoundEvent GOURD_EXPLODE = register("gourd_explode");

    private static SoundEvent register(String name) {
        Identifier id = IdUtils.ofVG(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {}
}
