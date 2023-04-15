package pers.solid.brrp.v1.recipe;

import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;

public interface ShapedRecipeJsonBuilderExtension extends RecipeJsonBuilderExtension<ShapedRecipeJsonBuilder> {
  default ShapedRecipeJsonBuilder patterns(String... patterns) {
    throw new UnsupportedOperationException();
  }
}
