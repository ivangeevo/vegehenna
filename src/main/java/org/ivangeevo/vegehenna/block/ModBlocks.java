package org.ivangeevo.vegehenna.block;

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
import org.ivangeevo.vegehenna.block.blocks.SugarCaneBlock;
import org.ivangeevo.vegehenna.block.blocks.SugarCaneRootsBlock;
import org.ivangeevo.vegehenna.block.blocks.WeedsBlock;

public class ModBlocks
{

    public static final Block FARMLAND_FERTILIZED = registerBlock("farmland_fertilized",
            new FertilizedFarmlandBlock(Block.Settings.create()
                    .mapColor(MapColor.DIRT_BROWN)
                    .ticksRandomly()
                    .strength(0.6f)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .blockVision(Blocks::always)
                    .suffocates(Blocks::always)));

    public static final Block CARROT_FLOWERING = registerBlock("carrot_flowering",
            new FloweringCarrotBlock(Block.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.CROP)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final Block SUGAR_CANE_ROOTS = registerBlockWithoutItem("sugar_cane_roots",
            new SugarCaneRootsBlock(Block.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .pistonBehavior(PistonBehavior.DESTROY)));

    public static final Block SUGAR_CANE = registerBlockWithoutItem("sugar_cane",
            new SugarCaneBlock(Block.Settings.create()
                    .mapColor(MapColor.DARK_GREEN)
                    .noCollision()
                    .ticksRandomly()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .pistonBehavior(PistonBehavior.DESTROY)));



    public static final Block WEEDS = registerBlockWithoutItem("weeds",
            new WeedsBlock(Block.Settings.create()
                    .strength(0f)
                    .ticksRandomly()
                    .sounds(BlockSoundGroup.GRASS)));



    private static Block registerBlock(String name, Block block)
    {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(VegehennaMod.MOD_ID, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block)
    {
        return Registry.register(Registries.BLOCK, Identifier.of(VegehennaMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block)
    {
        return Registry.register(Registries.ITEM, Identifier.of(VegehennaMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks()
    {
        VegehennaMod.LOGGER.debug("Registering ModBlocks for " + VegehennaMod.MOD_ID);
    }


}
