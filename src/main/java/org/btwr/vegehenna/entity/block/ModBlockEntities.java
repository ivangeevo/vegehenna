package org.btwr.vegehenna.entity.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.vegehenna.block.ModBlocks;

public class ModBlockEntities {

    public static BlockEntityType<WeedsBlockEntity> WEEDED_FARMLAND;

    public static void registerBlockEntities() {
        WEEDED_FARMLAND = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.ofVanilla("farmland"),
                BlockEntityType.Builder.create(
                        WeedsBlockEntity::new,
                        Blocks.FARMLAND,
                        ModBlocks.FARMLAND_FERTILIZED
                ).build(null)
        );
    }

}