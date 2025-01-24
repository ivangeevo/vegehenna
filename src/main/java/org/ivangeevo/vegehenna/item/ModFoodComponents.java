package org.ivangeevo.vegehenna.item;


import net.minecraft.component.type.FoodComponent;

/** Contains all the default food components used in Vegehenna food items. **/
public class ModFoodComponents
{
    public static final FoodComponent CHOCOLATE =
            new FoodComponent.Builder().nutrition(3).saturationModifier(0.15f).build();

    public static final FoodComponent CHOCOLATE_MILK =
            new FoodComponent.Builder().nutrition(3).saturationModifier(0.20f).build();

    public static final FoodComponent BOILED_POTATO =
            new FoodComponent.Builder().nutrition(1).saturationModifier(0.25f).build();

    public static final FoodComponent COOKED_CARROT =
            new FoodComponent.Builder().nutrition(2).saturationModifier(0.25f).build();
}
