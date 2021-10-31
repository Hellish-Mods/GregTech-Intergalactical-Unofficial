package muramasa.gti.loader.machines;

import static muramasa.gti.data.RecipeMaps.CUTTING;

import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.gti.data.Materials;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.FluidStack;

public class CuttingLoader {
    public static void init() {
        for (Material mat : Data.PLATE.all()) {
            if (!mat.has(Data.BLOCK))
                return;
            CUTTING.RB().ii(RecipeIngredient.of(Data.BLOCK.getMaterialTag(mat), 1)).io(Data.PLATE.get(mat, 9))
                    .fi(new FluidStack(Fluids.WATER, 1000)).add(mat.getMass() * 2, 24);
            CUTTING.RB().ii(RecipeIngredient.of(Data.BLOCK.getMaterialTag(mat), 1)).io(Data.PLATE.get(mat, 9))
                    .fi(Materials.Lubricant.getLiquid(250)).add(mat.getMass(), 16);

        }
    }
}