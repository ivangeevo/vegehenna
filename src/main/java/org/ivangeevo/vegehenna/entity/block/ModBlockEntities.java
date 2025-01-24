package org.ivangeevo.vegehenna.entity.block;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.ModBlocks;

public class ModBlockEntities {

    public static BlockEntityType<WeedsBlockEntity> WEEDS;

    public static void registerBlockEntities() {
        WEEDS = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(VegehennaMod.MOD_ID, "weeds"),
                BlockEntityType.Builder.create(WeedsBlockEntity::new, ModBlocks.WEEDS).build(null)
        );


    }


}
