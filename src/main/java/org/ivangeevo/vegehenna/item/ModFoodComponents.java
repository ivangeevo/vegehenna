package org.ivangeevo.vegehenna.item;

import net.minecraft.item.FoodComponent;

/** Contains all the default food components used in Vegehenna food items. **/
public class ModFoodComponents
{
    public static final FoodComponent CARROT =
            new FoodComponent.Builder().hunger(1).saturationModifier(0.003f).build();

    public static final FoodComponent COOKED_CARROT =
            new FoodComponent.Builder().hunger(2).saturationModifier(0.005f).build();

}
