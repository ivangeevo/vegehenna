package org.ivangeevo.vegehenna.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.Builder.class)
public interface LootPoolBuilderAccessor {

    @Accessor ImmutableList.Builder<LootPoolEntry> getEntries();

    @Accessor @Mutable void setEntries(ImmutableList.Builder<LootPoolEntry> entries);
}