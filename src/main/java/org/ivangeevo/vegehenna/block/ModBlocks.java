package org.ivangeevo.vegehenna.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.blocks.FertilizedFarmlandBlock;
import org.ivangeevo.vegehenna.block.blocks.FloweringCarrotBlock;
import org.ivangeevo.vegehenna.block.blocks.SugarcaneRootsBlock;
import org.ivangeevo.vegehenna.block.blocks.WeedsBlock;

public class ModBlocks
{

    public static final Block FARMLAND_FERTILIZED = registerBlock("farmland_fertilized",
            new FertilizedFarmlandBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .ticksRandomly()
                    .strength(0.6f)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .blockVision(Blocks::always)
                    .suffocates(Blocks::always)));

    public static final Block CARROT_FLOWERING = registerBlock("carrot_flowering",
            new FloweringCarrotBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.CROP)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final Block SUGARCANE_ROOTS = registerBlock("sugar_cane_roots",
            new SugarcaneRootsBlock(FabricBlockSettings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .pistonBehavior(PistonBehavior.DESTROY)));


    public static final Block WEEDS = registerBlock("weeds",
            new WeedsBlock(FabricBlockSettings.create()
                    .strength(0f)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.GRASS)));



    private static Block registerBlock(String name, Block block)
    {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(VegehennaMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block)
    {
        return Registry.register(Registries.ITEM, new Identifier(VegehennaMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks()
    {
        VegehennaMod.LOGGER.debug("Registering ModBlocks for " + VegehennaMod.MOD_ID);
    }


}
