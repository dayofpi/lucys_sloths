package com.dayofpi.lucys_sloths;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {
    private static SoundEvent createSound(String string) {
        return new SoundEvent(new Identifier(Main.MODID, string));
    }

    private static void registerSound(SoundEvent type) {
        Registry.register(Registry.SOUND_EVENT, type.getId(), type);
    }

    public static final SoundEvent SLOTH_AMBIENT = createSound("entity.sloth.ambient");
    public static final SoundEvent SLOTH_EAT = createSound("entity.sloth.eat");
    public static final SoundEvent SLOTH_STEP = createSound("entity.sloth.step");
    public static final SoundEvent SLOTH_HURT = createSound("entity.sloth.hurt");
    public static final SoundEvent SLOTH_DEATH = createSound("entity.sloth.death");
    public static final SoundEvent SLOTH_GROW_SEAGRASS = createSound("entity.sloth.grow_seagrass");

    public static void initSounds() {
        registerSound(SLOTH_AMBIENT);
        registerSound(SLOTH_EAT);
        registerSound(SLOTH_STEP);
        registerSound(SLOTH_HURT);
        registerSound(SLOTH_DEATH);
        registerSound(SLOTH_GROW_SEAGRASS);
    }
}
