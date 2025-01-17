package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pers.solid.brrp.v1.recipe.RecipeJsonBuilderExtension;

@Mixin(CraftingRecipeJsonBuilder.class)
public interface CraftingRecipeJsonBuilderMixin<Self extends CraftingRecipeJsonBuilder> extends RecipeJsonBuilderExtension<Self> {
  @Shadow
  CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion);

  @SuppressWarnings("unchecked")
  @Override
  default Self criterionMethodBridge(String criterionName, AdvancementCriterion<?> criterion) {
    return (Self) this.criterion(criterionName, criterion);
  }
}
