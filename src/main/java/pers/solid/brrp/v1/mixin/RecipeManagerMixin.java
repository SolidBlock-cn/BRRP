package pers.solid.brrp.v1.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.api.ImmediateResource;
import pers.solid.brrp.v1.impl.ImmediateResourceLoader;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements ImmediateResourceLoader {
  @Shadow
  private boolean errored;

  @Shadow
  @Final
  private RegistryWrapper.WrapperLookup registryLookup;

  @Shadow
  @Final
  private static Logger LOGGER;

  @Shadow
  private Multimap<RecipeType<?>, RecipeEntry<?>> recipesByType;

  @Shadow
  private Map<Identifier, RecipeEntry<?>> recipesById;

  @Override
  public void applyImmediate$brrp(Map<Identifier, ImmediateResource<?>> map, ResourceManager manager, Profiler profiler) {
    if (map.isEmpty()) {
      LOGGER.info("BRRP: No immediate recipe loaded.");
      return;
    }
    this.errored = false;
    ImmutableMultimap.Builder<RecipeType<?>, RecipeEntry<?>> imRecipesByTypeBuilder = ImmutableMultimap.builder();
    imRecipesByTypeBuilder.putAll(this.recipesByType);
    ImmutableMap.Builder<Identifier, RecipeEntry<?>> imRecipesByIdBuilder = ImmutableMap.builder();
    imRecipesByIdBuilder.putAll(this.recipesById);

    int count = 0;
    for (var entry : map.entrySet()) {
      Identifier identifier = entry.getKey();

      final Object apply = entry.getValue().apply(this.registryLookup);
      if (apply instanceof final Recipe<?> recipe) {
        RecipeEntry<?> recipeEntry = new RecipeEntry<>(identifier, recipe);
        imRecipesByTypeBuilder.put(recipe.getType(), recipeEntry);
        imRecipesByIdBuilder.put(identifier, recipeEntry);
        count++;
      } else {
        LOGGER.warn("BRRP: Immediate resource with id {} is not a recipe: {}, ignored", identifier, apply);
      }
    }

    this.recipesByType = imRecipesByTypeBuilder.build();
    this.recipesById = imRecipesByIdBuilder.build();
    LOGGER.info("BRRP: Loaded {} immediate recipes", count);
  }
}
