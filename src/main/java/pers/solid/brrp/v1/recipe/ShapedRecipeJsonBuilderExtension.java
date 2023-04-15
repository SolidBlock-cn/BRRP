package pers.solid.brrp.v1.recipe;

import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;

public interface ShapedRecipeJsonBuilderExtension extends RecipeJsonBuilderExtension<ShapedRecipeJsonFactory> {
  default ShapedRecipeJsonFactory patterns(String... patterns) {
    throw new UnsupportedOperationException();
  }
}
