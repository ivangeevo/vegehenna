package org.ivangeevo.vegehenna.datagen;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.ivangeevo.vegehenna.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.data.server.recipe.CookingRecipeJsonBuilder.*;
import static net.minecraft.data.server.recipe.CookingRecipeJsonBuilder.createSmoking;

public class ModRecipeProvider extends FabricRecipeProvider implements RecipeProviderUtils {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    // kept as example on how to add lists lol

    //private static final List<ItemConvertible> NORMAL_LEATHERS = List.of(Items.LEATHER,BTWR_Items.LEATHER_CUT);


    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    @Override
    public void generate(RecipeExporter exporter)
    {

        disableVanilla(exporter, "cake");

        generateShapelessRecipes(exporter);
        generateShapedRecipes(exporter);

        generateOnlySmokingCookingRecipes(exporter);

        RecipeProvider.offerFoodCookingRecipe(exporter, "smelting", RecipeSerializer.SMELTING, SmeltingRecipe::new,
                200, Items.CARROT, ModItems.COOKED_CARROT, 0.2f);

        RecipeProvider.offerFoodCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, CampfireCookingRecipe::new,
                600, Items.CARROT, ModItems.COOKED_CARROT, 0.2f);


    }

    public static void generateOnlySmokingCookingRecipes(RecipeExporter exporter)
    {
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, Items.POTATO, ModItems.BOILED_POTATO, 0.3f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, Items.CARROT, ModItems.COOKED_CARROT, 0.3f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100 , ModItems.BREAD_DOUGH, Items.BREAD, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_COOKIES, Items.COOKIE, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_CAKE, Items.CAKE, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE, Items.PUMPKIN_PIE, 0.15f);

    }

    public static void generateShapelessRecipes(RecipeExporter exporter)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_COOKIES).input(Items.COCOA_BEANS).input(ModItems.FLOUR).criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE).input(Items.EGG).input(ModItems.FLOUR).input(ModItems.FLOUR).input(ModItems.FLOUR).input(Items.SUGAR).input(Items.PUMPKIN).criterion("has_pumpkin", RecipeProvider.conditionsFromItem(Items.PUMPKIN)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COCOA_POWDER).input(Items.COCOA_BEANS).criterion("has_cocoa_beans", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CHOCOLATE,2).input(ModItems.COCOA_POWDER).input(Items.SUGAR).input(Items.MILK_BUCKET).criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CHOCOLATE_MILK).input(Items.MILK_BUCKET).input(ModItems.COCOA_POWDER).criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER)).offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.STRAW,9).input(Items.HAY_BLOCK).criterion("has_hay_block", RecipeProvider.conditionsFromItem(Items.HAY_BLOCK)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BEETROOT_SEEDS).input(Items.BEETROOT).criterion("has_beetroot", RecipeProvider.conditionsFromItem(Items.BEETROOT)).offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.PACKED_MUD).input(Items.MUD).input(ModItems.STRAW).criterion("has_straw", RecipeProvider.conditionsFromItem(ModItems.STRAW)).offerTo(exporter);

    }
    public static void generateShapedRecipes(RecipeExporter exporter)
    {
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.FLOUR,3).input('#', Items.WHEAT).pattern("###").criterion("has_wheat", RecipeProvider.conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.BREAD_DOUGH).input('#', ModItems.FLOUR).pattern("# ").pattern("##").criterion("has_flour", RecipeProvider.conditionsFromItem(ModItems.FLOUR)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_CAKE).input('E', Items.EGG).input('F', ModItems.FLOUR).input('M', Items.MILK_BUCKET).input('S', Items.SUGAR).pattern("SSS").pattern("MEM").pattern("FFF").criterion("has_egg", RecipeProvider.conditionsFromItem(Items.EGG)).offerTo(exporter);
    }


}