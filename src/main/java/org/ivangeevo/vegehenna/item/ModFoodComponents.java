package org.ivangeevo.vegehenna.item;


import net.minecraft.component.type.FoodComponent;

/** Contains all the default food components used in Vegehenna food items. **/
public class ModFoodComponents
{
    public static final FoodComponent COOKED_CARROT =
            new FoodComponent.Builder().nutrition(2).saturationModifier(0.005f).build();

}
