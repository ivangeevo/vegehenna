package org.ivangeevo.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.vegehenna.VegehennaMod;
import org.ivangeevo.vegehenna.block.ModBlocks;
import org.ivangeevo.vegehenna.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {


    public ModLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        addItemGroup("group_vegehenna", "Vegehenna", tb);
        this.addBlockTranslations(tb);
        this.addItemTranslations(tb);
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.FARMLAND_FERTILIZED, "Fertilized Farmland");
    }

    private void addItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.BOILED_POTATO, "Boiled Potato");
        tb.add(ModItems.COOKED_CARROT, "Cooked Carrot");

        tb.add(ModItems.CARROT_SEEDS, "Carrot Seeds");
        tb.add(ModItems.SUGAR_CANE_ROOTS, "Sugar Cane Roots");

        tb.add(ModItems.STRAW, "Straw");
        tb.add(ModItems.FLOUR, "Flour");
        tb.add(ModItems.BREAD_DOUGH, "Bread Dough");
        tb.add(ModItems.PASTRY_UNCOOKED_COOKIES, "Cookie Dough");
        tb.add(ModItems.PASTRY_UNCOOKED_CAKE, "Cake Batter");
        tb.add(ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE, "Oven Ready Pumpkin Pie");

        tb.add(ModItems.COCOA_POWDER, "Cocoa Powder");
        tb.add(ModItems.CHOCOLATE, "Chocolate");
        tb.add(ModItems.CHOCOLATE_MILK, "Chocolate Milk");
    }


    private void addItemGroup(String entryPath, String translation, TranslationBuilder tb) {
        tb.add("itemgroup." + entryPath, translation);
    }

    private void addConfigMenuTitle(String translation, TranslationBuilder tb) {
        tb.add("title." + VegehennaMod.MOD_ID + ".config", translation);
    }

    private void addConfigCategory(String categoryPath, String translation, TranslationBuilder tb) {
        tb.add("config." + VegehennaMod.MOD_ID + ".category." + categoryPath, translation);
    }

    private void addConfig(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + VegehennaMod.MOD_ID + "." + configPath, translation);
    }

    private void addConfigTooltip(String configPath, String translation, TranslationBuilder tb) {
        tb.add("config." + VegehennaMod.MOD_ID + ".tooltip." + configPath, translation);
    }
}
