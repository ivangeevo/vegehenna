package org.btwr.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.vegehenna.VegehennaMod;
import org.btwr.vegehenna.block.ModBlocks;
import org.btwr.vegehenna.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {

    public ModLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    private String configBasePath() {
        return "config." + VegehennaMod.MOD_ID + ".";
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder tb) {
        addItemGroup("group_vegehenna", "Vegehenna", tb);
        this.addBlockTranslations(tb);
        this.addItemTranslations(tb);
        this.addConfigTranslations(tb);
        this.addEmiTranslations(tb);
        tb.add("subtitles.vegehenna.gourd_explode", "Gourd explodes");
    }

    private void addBlockTranslations(TranslationBuilder tb) {
        tb.add(ModBlocks.FARMLAND_FERTILIZED, "Fertilized Farmland");
    }

    private void addItemTranslations(TranslationBuilder tb) {
        tb.add(ModItems.BOILED_POTATO, "Boiled Potato");
        tb.add(ModItems.COOKED_CARROT, "Cooked Carrot");

        tb.add(ModItems.CARROT_SEEDS, "Carrot Seeds");
        tb.add(ModItems.SUGAR_CANE_ROOTS, "Sugar Cane Roots");
        tb.add(ModItems.MASHED_MELON, "Mashed Melon");

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

    protected void addEmiTranslations(TranslationBuilder tb) {
        addEmiCategory("gourd_fall_breaking", "Fall breaking", tb);
        addEmiTooltip("gourd_fall_breaking.chance", "Must fall 5+ blocks, chance increases with height", tb);
        addEmiTooltip("gourd_fall_breaking.guaranteed", "Guaranteed to break at 15+ blocks", tb);
    }

    private void addConfigTranslations(TranslationBuilder tb) {
        this.addConfigMenuDefaults(tb);
        this.addConfigMenuTitle("Vegehenna Configuration Menu", tb);
        this.addConfigCategory("general", "General", tb);
        //this.addConfig("canWeedsGrow", "Enable weeds growing", tb);
        //this.addConfigTooltip("canWeedsGrow", "Toggles whether weeds can grow on crops/farmland", tb);
    }

    private void addConfigMenuDefaults(TranslationBuilder tb) {
        this.addSimpleText("clientSettingsText", "Client Settings:", tb);
        this.addSimpleText("emptyClientConfigText", "§eNote:§r There are currently no client config settings.", tb);
        this.addSimpleText("serverSettingsText", "Server Settings:", tb);
        this.addSimpleText("serverSettingsNoAccessText", "§eNote:§r Server settings are not accessible in menus." +
                "\nThey can only be changed by editing the config file manually and require a world reload to take effect.", tb
        );
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

    protected void addEmiCategory(String key, String name, TranslationBuilder tb) {
        tb.add("emi.category.vegehenna." + key, name);
    }

    protected void addEmiTooltip(String key, String name, TranslationBuilder tb) {
        tb.add("emi.vegehenna.tooltip." + key, name);
    }

    private void addSimpleText(String path, String translation, TranslationBuilder tb) {
        tb.add(configBasePath() + "text." + path, translation);
    }
}