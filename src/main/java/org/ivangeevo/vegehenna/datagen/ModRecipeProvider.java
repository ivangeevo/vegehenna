package org.ivangeevo.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmokingRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import org.ivangeevo.vegehenna.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider
{

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }
    // kept as example on how to add lists lol

    //private static final List<ItemConvertible> NORMAL_LEATHERS = List.of(Items.LEATHER,BTWR_Items.LEATHER_CUT);




    @Override
    public void generate(RecipeExporter exporter)
    {

        generateShapelessRecipes(exporter);
        generateShapedRecipes(exporter);

        // Generation for all non separated into a category cooking recipes is generalized.
        // By default the cook time for all items is 1200 for smoker, 6000 for campfire.
        generateOnlySmokingCookingRecipes(exporter, 1200 );

    }

    public static void generateOnlySmokingCookingRecipes(RecipeExporter exporter, int cookingTime)
    {
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime , ModItems.BREAD_DOUGH, Items.BREAD, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime, ModItems.PASTRY_UNCOOKED_COOKIES, Items.COOKIE, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime, ModItems.PASTRY_UNCOOKED_CAKE, Items.CAKE, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE, Items.PUMPKIN_PIE, 0.15f);
        RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime, Items.CARROT, ModItems.CARROT_COOKED, 0.15f);
        //RecipeProvider.offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new, cookingTime, ModItems.PASTRY_UNCOOKED_COOKIES, Items.COOKIE, 0.15f);

    }

    public static void generateShapelessRecipes(RecipeExporter exporter)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_COOKIES).input(Items.COCOA_BEANS).input(ModItems.FLOUR).input(ModItems.FLOUR).input(ModItems.FLOUR).input(ModItems.FLOUR).criterion("has_cocoa_beans", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE).input(Items.EGG).input(ModItems.FLOUR).input(ModItems.FLOUR).input(ModItems.FLOUR).input(Items.SUGAR).input(Items.PUMPKIN).criterion("has_pumpkin", RecipeProvider.conditionsFromItem(Items.PUMPKIN)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COCOA_POWDER).input(Items.COCOA_BEANS).criterion("has_cocoa_beans", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
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