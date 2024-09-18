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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.BRRPMixins;
import pers.solid.brrp.v1.api.RegistryResourceFunction;
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
  private Multimap<RecipeType<?>, RecipeEntry<?>> recipesByType;

  @Shadow
  private Map<Identifier, RecipeEntry<?>> recipesById;

  @Override
  public void applyImmediate$brrp(Map<Identifier, Object> map, ResourceManager manager, Profiler profiler) {
    if (map.isEmpty()) {
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

      Object apply = entry.getValue();
      if (apply instanceof RegistryResourceFunction<?> rf) {
        apply = rf.apply(registryLookup);
      }
      if (apply instanceof final Recipe<?> recipe) {
        RecipeEntry<?> recipeEntry = new RecipeEntry<>(identifier, recipe);
        imRecipesByTypeBuilder.put(recipe.getType(), recipeEntry);
        imRecipesByIdBuilder.put(identifier, recipeEntry);
        count++;
      } else {
        BRRPMixins.LOGGER.warn("BRRP: Immediate resource with id {} is not a recipe: {}, ignored", identifier, apply);
      }
    }

    this.recipesByType = imRecipesByTypeBuilder.build();
    this.recipesById = imRecipesByIdBuilder.build();
    BRRPMixins.LOGGER.info("BRRP: Applied {} immediate recipes", count);
  }
}
