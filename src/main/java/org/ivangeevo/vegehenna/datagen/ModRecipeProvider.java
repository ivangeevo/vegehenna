package org.ivangeevo.vegehenna.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.recipe.ExtendedShapelessRecipe;
import org.btwr.shared_library.util.utils.RecipeUtils;
import org.ivangeevo.vegehenna.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider implements RecipeUtils {

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
    public void generate(RecipeExporter exporter) {
        this.generateForVanilla(exporter);
        this.generateForMod(exporter);
    }

    private void generateForVanilla(RecipeExporter exporter) {

        disableVanilla(exporter, "cake");
        disableVanilla(exporter, "baked_potato_from_smoking");

        // Override wheat from hay block recipe to give straw instead
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.STRAW, 9)
                .input(Items.HAY_BLOCK)
                .criterion("has_hay_block", conditionsFromItem(Items.HAY_BLOCK))
                .offerTo(exporter, Identifier.ofVanilla("wheat"));

        // Override hay block recipe to require straw instead of wheat
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.HAY_BLOCK)
                .input('X', ModItems.STRAW)
                .pattern("XXX")
                .pattern("XXX")
                .pattern("XXX")
                .criterion("has_straw", conditionsFromItem(ModItems.STRAW))
                .offerTo(exporter, Identifier.ofVanilla("hay_block"));
    }

    private void generateForMod(RecipeExporter exporter) {

        ExtendedShapelessRecipe.JsonBuilder.create(RecipeCategory.MISC, Items.WHEAT_SEEDS, 2)
                .additionalDrop(ModItems.STRAW)
                .input(Items.WHEAT)
                .criterion("has_wheat", conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_COOKIES, 4)
                .input(Items.COCOA_BEANS)
                .input(ModItems.FLOUR)
                .criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE)
                .input(Items.EGG)
                .input(ModItems.FLOUR)
                .input(ModItems.FLOUR)
                .input(ModItems.FLOUR)
                .input(Items.SUGAR)
                .input(Items.PUMPKIN)
                .criterion("has_pumpkin", RecipeProvider.conditionsFromItem(Items.PUMPKIN))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.COCOA_POWDER)
                .input(Items.COCOA_BEANS)
                .criterion("has_cocoa_beans", RecipeProvider.conditionsFromItem(Items.COCOA_BEANS))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CHOCOLATE,2)
                .input(ModItems.COCOA_POWDER)
                .input(Items.SUGAR)
                .input(Items.MILK_BUCKET)
                .criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CHOCOLATE_MILK)
                .input(Items.MILK_BUCKET)
                .input(ModItems.COCOA_POWDER)
                .criterion("has_cocoa_powder", RecipeProvider.conditionsFromItem(ModItems.COCOA_POWDER))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.STRAW,9)
                .input(Items.HAY_BLOCK)
                .criterion("has_hay_block", RecipeProvider.conditionsFromItem(Items.HAY_BLOCK))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.PACKED_MUD)
                .input(Items.MUD)
                .input(ModItems.STRAW)
                .criterion("has_straw", RecipeProvider.conditionsFromItem(ModItems.STRAW))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.MELON_SEEDS)
                .input(ModItems.MASHED_MELON)
                .criterion("has_mashed_melon", RecipeProvider.conditionsFromItem(ModItems.MASHED_MELON))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, Items.MELON_SLICE, 5)
                .input(Items.MELON)
                .criterion("has_melon", RecipeProvider.conditionsFromItem(Items.MELON))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.FLOUR,3)
                .input('#', Items.WHEAT)
                .pattern("###")
                .criterion("has_wheat", RecipeProvider.conditionsFromItem(Items.WHEAT))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.BREAD_DOUGH)
                .input('#', ModItems.FLOUR).pattern("# ")
                .pattern("##")
                .criterion("has_flour", RecipeProvider.conditionsFromItem(ModItems.FLOUR))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, ModItems.PASTRY_UNCOOKED_CAKE)
                .input('E', Items.EGG)
                .input('F', ModItems.FLOUR)
                .input('M', Items.MILK_BUCKET)
                .input('S', Items.SUGAR)
                .pattern("SSS")
                .pattern("MEM")
                .pattern("FFF")
                .criterion("has_egg", RecipeProvider.conditionsFromItem(Items.EGG))
                .offerTo(exporter);

        offerFoodCookingRecipe(exporter, "smelting", RecipeSerializer.SMELTING, SmeltingRecipe::new,
                200, Items.CARROT, ModItems.COOKED_CARROT, 0.2f);
        offerFoodCookingRecipe(exporter, "campfire_cooking", RecipeSerializer.CAMPFIRE_COOKING, CampfireCookingRecipe::new,
                600, Items.CARROT, ModItems.COOKED_CARROT, 0.2f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, Items.POTATO, ModItems.BOILED_POTATO, 0.3f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, Items.CARROT, ModItems.COOKED_CARROT, 0.3f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100 , ModItems.BREAD_DOUGH, Items.BREAD, 0.15f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_COOKIES, Items.COOKIE, 0.15f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_CAKE, Items.CAKE, 0.15f);
        offerFoodCookingRecipe(exporter, "smoking", RecipeSerializer.SMOKING, SmokingRecipe::new,
                100, ModItems.PASTRY_UNCOOKED_PUMPKIN_PIE, Items.PUMPKIN_PIE, 0.15f);
    }

    public static <T extends AbstractCookingRecipe> void offerFoodCookingRecipe(RecipeExporter exporter, String cooker, RecipeSerializer<T> serializer, AbstractCookingRecipe.RecipeFactory<T> recipeFactory, int cookingTime, ItemConvertible items, ItemConvertible output, float experience) {
        CookingRecipeJsonBuilder.create(Ingredient.ofItems(items), RecipeCategory.FOOD, output, experience, cookingTime, serializer, recipeFactory)
                .criterion(RecipeProvider.hasItem(items), RecipeProvider.conditionsFromItem(items))
                .offerTo(exporter, output + "_from_" + cooker);
    }

}