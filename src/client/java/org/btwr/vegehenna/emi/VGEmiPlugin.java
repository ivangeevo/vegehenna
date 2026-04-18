package org.btwr.vegehenna.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.btwr.vegehenna.VegehennaMod;
import org.btwr.vegehenna.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VGEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory GOURD_FALL_BREAKING = new EmiRecipeCategory(
            Identifier.of(VegehennaMod.MOD_ID, "gourd_fall_breaking"),
            EmiStack.of(Blocks.MELON),
            EmiStack.of(Blocks.MELON)
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(GOURD_FALL_BREAKING);
        registerGourdFallBreaking(registry);
    }

    private void registerGourdFallBreaking(EmiRegistry registry) {
        addGourdFallRecipe(registry,
                EmiIngredient.of(Ingredient.ofItems(Blocks.MELON.asItem())),
                EmiStack.of(ModItems.MASHED_MELON, 2),
                "melon"
        );
        addGourdFallRecipe(registry,
                EmiIngredient.of(Ingredient.ofItems(Blocks.PUMPKIN.asItem())),
                EmiStack.of(Items.PUMPKIN_SEEDS, 2),
                "pumpkin"
        );
    }

    private void addGourdFallRecipe(EmiRegistry registry, EmiIngredient input, EmiStack output, String id) {
        registry.addRecipe(new EmiRecipe() {

            @Override
            public EmiRecipeCategory getCategory() {
                return GOURD_FALL_BREAKING;
            }

            @Override
            public @Nullable Identifier getId() {
                return Identifier.of(VegehennaMod.MOD_ID, "gourd_fall_breaking/" + id);
            }

            @Override
            public List<EmiIngredient> getInputs() {
                return List.of(input);
            }

            @Override
            public List<EmiStack> getOutputs() {
                return List.of(output);
            }

            @Override
            public int getDisplayWidth() {
                return 26;
            }

            @Override
            public int getDisplayHeight() {
                return 62;
            }

            @Override
            public void addWidgets(WidgetHolder widgets) {
                widgets.addSlot(input, 4, 2)
                        .appendTooltip(Text.translatable("emi.vegehenna.tooltip.gourd_fall_breaking.chance"))
                        .appendTooltip(Text.translatable("emi.vegehenna.tooltip.gourd_fall_breaking.guaranteed"));

                widgets.addText(EmiPort.ordered(Text.literal("↓")), 11, 23, 0xFFAAAAAA, false);
                widgets.addText(EmiPort.ordered(Text.literal("↓")), 11, 27, 0xFFAAAAAA, false);
                widgets.addText(EmiPort.ordered(Text.literal("↓")), 11, 31, 0xFFAAAAAA, false);

                widgets.addSlot(output, 4, 40).recipeContext(this);
            }
        });
    }
}