package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import pers.solid.brrp.v1.recipe.SmithingTrimRecipeJsonBuilderExtension;

@Mixin(SmithingTrimRecipeJsonBuilder.class)
public abstract class SmithingTrimRecipeJsonBuilderMixin implements SmithingTrimRecipeJsonBuilderExtension {

  @Shadow public abstract SmithingTrimRecipeJsonBuilder criterion(String name, CriterionConditions conditions);

  private boolean bypassesValidation;
  private @Nullable String customRecipeCategory;

  @SuppressWarnings("DataFlowIssue")
  private SmithingTrimRecipeJsonBuilder self() {
    return (SmithingTrimRecipeJsonBuilder) (Object) this;
  }

  @Override
  public SmithingTrimRecipeJsonBuilder criterionMethodBridge(String criterionName, CriterionConditions criterionConditions) {
    return criterion(criterionName, criterionConditions);
  }

  @Override
  public SmithingTrimRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
    this.bypassesValidation = bypassesValidation;
    return self();
  }

  @Inject(method = "validate", at = @At("HEAD"), cancellable = true)
  private void bypassValidation(Identifier recipeId, CallbackInfo ci) {
    if (bypassesValidation) {
      ci.cancel();
    }
  }

  @Override
  public SmithingTrimRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
    this.customRecipeCategory = recipeCategory;
    return self();
  }

  @ModifyArgs(method = "offerTo(Ljava/util/function/Consumer;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE",target = "Lnet/minecraft/data/server/recipe/SmithingTrimRecipeJsonBuilder$SmithingTrimRecipeJsonProvider;<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/recipe/RecipeSerializer;Lnet/minecraft/recipe/Ingredient;Lnet/minecraft/recipe/Ingredient;Lnet/minecraft/recipe/Ingredient;Lnet/minecraft/advancement/Advancement$Builder;Lnet/minecraft/util/Identifier;)V"))
  public void modifyOfferTo(Args args) {
    if (customRecipeCategory != null) {
      args.set(6, args.<Identifier>get(0).withPrefixedPath("recipes/" + this.customRecipeCategory + (this.customRecipeCategory.isEmpty() ? "" : "/")));
    }
  }
}
