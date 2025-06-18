package org.ivangeevo.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionConsumingBuilder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModLootTableGenerator extends FabricBlockLootTableProvider {


    public ModLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate()
    {

        this.addDrop(ModBlocks.CARROT_FLOWERING, (block -> onMaxAgeDrops(block, ModItems.CARROT_SEEDS,2, Properties.AGE_3, 3)));
        this.addDrop(ModBlocks.BREAD_DOUGH, drops(ModItems.BREAD_DOUGH));
        this.addDrop(ModBlocks.UNCOOKED_CAKE, drops(ModItems.PASTRY_UNCOOKED_CAKE));
        //this.addDrop(ModBlocks.FARMLAND_FERTILIZED, block -> LootTable.builder().pool(addSurvivesExplosionCondition(Blocks.DIRT, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))).build()  ));
        //this.addDrop(ModBlocks.FARMLAND_FERTILIZED, block -> LootTable.builder().pool(LootPool.builder().Blocks.DIRT, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))).build()  ));
        LootCondition.Builder builder = BlockStatePropertyLootCondition.builder(Blocks.BEETROOTS).properties(StatePredicate.Builder.create().exactMatch(BeetrootsBlock.AGE, 3));
        this.addDrop(Blocks.BEETROOTS, this.beetrootDrops(builder));

    }

    public LootTable.Builder beetrootDrops(LootCondition.Builder condition) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.applyExplosionDecay(Blocks.BEETROOTS, LootTable.builder()
                .pool(LootPool.builder()
                        .with(
                                ItemEntry.builder(Items.BEETROOT)
                                        .conditionally(condition)
                                        .alternatively(
                                                ItemEntry.builder(Items.BEETROOT_SEEDS)
                                                        .conditionally(RandomChanceLootCondition.builder(0.33f))
                                        )
                        )
                )
                .pool(LootPool.builder()
                        .conditionally(condition)
                        .with(ItemEntry.builder(Items.BEETROOT_SEEDS)
                                .apply(
                                        ApplyBonusLootFunction.binomialWithBonusCount(impl.getOrThrow(Enchantments.FORTUNE),
                                                0.5714286F, 3)
                                )
                        )
                )
        );
    }

    public static LootTable.Builder onMaxAgeDrops(Block minedBlock, Item drop, int amount, IntProperty property, int age) {
        return LootTable.builder()
                .pool(
                        LootPool.builder()
                                .with(ItemEntry.builder(drop).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(amount))))
                                .conditionally(BlockStatePropertyLootCondition.builder(minedBlock)
                                        .properties(StatePredicate.Builder.create().exactMatch(property, age))));
    }

    public static LootTable.Builder differentBlockDropSurvives(Block minedBlock, Item drop, int amount, IntProperty property, int age) {
        return LootTable.builder()
                .pool(
                        LootPool.builder()
                                .with(ItemEntry.builder(drop).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(amount))))
                                .conditionally(BlockStatePropertyLootCondition.builder(minedBlock)
                                        .properties(StatePredicate.Builder.create().exactMatch(property, age))));
    }


    @Override
    public String getName() {
        return "BTWR Block Loot Tables";
    }
}
