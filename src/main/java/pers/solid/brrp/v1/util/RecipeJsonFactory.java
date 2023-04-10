package pers.solid.brrp.v1.util;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface RecipeJsonFactory {
  void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId);
}
