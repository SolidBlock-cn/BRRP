package pers.solid.brrp.v1.util;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

/**
 * <p>This class exists only for versions 1.16.5, because various types of recipe providers do not have a common class. Therefore, this interface is used.</p>
 * <p>Please use this interface as a lambda by adding {@code ::offerTo}, for example:</p>
 * <pre>{@code
 * RecipeProvider recipeProviderShapedRecipeJsonFactory.create(this, 4).input('#', baseBlock).pattern(...).criterion(...)::offerTo
 * }</pre>
 */
@FunctionalInterface
public interface RecipeJsonFactory {
  void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId);
}
