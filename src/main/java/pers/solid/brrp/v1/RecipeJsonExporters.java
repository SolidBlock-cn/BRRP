package pers.solid.brrp.v1;

import net.minecraft.data.server.recipe.CraftingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ApiStatus.Experimental
public final class RecipeJsonExporters {
  private RecipeJsonExporters() {
  }

  public static Consumer<RecipeJsonProvider> writeRecipeOnly(@NotNull RuntimeResourcePack pack) {
    return recipeJsonProvider -> pack.addRecipe(recipeJsonProvider.getRecipeId(), recipeJsonProvider);
  }

  public static Consumer<RecipeJsonProvider> writeRecipeAndAdvancement(@NotNull RuntimeResourcePack pack) {
    return pack::addRecipeAndAdvancement;
  }

  public static RecipeJsonProvider getRecipeJsonProvider(@NotNull CraftingRecipeJsonFactory builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(atom::set);
    return atom.get();
  }

  public static RecipeJsonProvider getRecipeJsonProvider(@NotNull SingleItemRecipeJsonFactory builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(atom::set);
    return atom.get();
  }
}
