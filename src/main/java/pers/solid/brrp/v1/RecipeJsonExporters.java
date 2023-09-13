package pers.solid.brrp.v1;

import net.minecraft.advancement.Advancement;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


public final class RecipeJsonExporters {
  private RecipeJsonExporters() {
  }

  public static Consumer<RecipeJsonProvider> writeRecipeOnly(@NotNull RuntimeResourcePack pack) {
    return recipeJsonProvider -> pack.addRecipe(recipeJsonProvider.id(), recipeJsonProvider);
  }

  public static Consumer<RecipeJsonProvider> writeRecipeAndAdvancement(@NotNull RuntimeResourcePack pack) {
    return pack::addRecipeAndAdvancement;
  }

  public static RecipeJsonProvider getRecipeJsonProvider(@NotNull CraftingRecipeJsonBuilder builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(getRecipeExporter(atom));
    return atom.get();
  }

  public static RecipeJsonProvider getRecipeJsonProvider(@NotNull SingleItemRecipeJsonBuilder builder) {
    final AtomicReference<RecipeJsonProvider> atom = new AtomicReference<>();
    builder.offerTo(getRecipeExporter(atom));
    return atom.get();
  }

  private static RecipeExporter getRecipeExporter(AtomicReference<RecipeJsonProvider> atom) {
    return new RecipeExporter() {
      @Override
      public void accept(RecipeJsonProvider recipeJsonProvider) {
        atom.set(recipeJsonProvider);
      }

      @Override
      public Advancement.Builder getAdvancementBuilder() {
        // we can simply ignore this
        return Advancement.Builder.createUntelemetered();
      }
    };
  }
}
