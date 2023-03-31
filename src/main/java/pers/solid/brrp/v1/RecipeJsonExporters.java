package pers.solid.brrp.v1;

import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@ApiStatus.Experimental
public final class RecipeJsonExporters {
  private RecipeJsonExporters() {
  }

  public Consumer<RecipeJsonProvider> writeRecipeOnly(@NotNull RuntimeResourcePack pack) {
    return recipeJsonProvider -> pack.addRecipe(recipeJsonProvider.getRecipeId(), recipeJsonProvider);
  }

  public Consumer<RecipeJsonProvider> writeRecipeAndAdvancement(@NotNull RuntimeResourcePack pack) {
    return pack::addRecipeAndAdvancement;
  }

  public RecipeJsonProvider getRecipeJsonProvider(@NotNull CraftingRecipeJsonBuilder builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(atom::set);
    return atom.get();
  }

  public RecipeJsonProvider getRecipeJsonProvider(@NotNull SingleItemRecipeJsonBuilder builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(atom::set);
    return atom.get();
  }
}
