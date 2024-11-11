package org.ivangeevo.vegehenna.mixin;

import net.minecraft.item.Item;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntry.class)
public interface ItemEntryAccessor
{
    @Accessor RegistryEntry<Item> getItem();
    @Accessor @Mutable void setItem(RegistryEntry<Item> item);
}