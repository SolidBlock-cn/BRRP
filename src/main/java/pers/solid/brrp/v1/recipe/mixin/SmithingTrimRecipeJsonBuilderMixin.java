package pers.solid.brrp.v1.recipe.mixin;

import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.SmithingTrimRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.recipe.SmithingTrimRecipeJsonBuilderExtension;

@Mixin(SmithingTrimRecipeJsonBuilder.class)
public abstract class SmithingTrimRecipeJsonBuilderMixin implements SmithingTrimRecipeJsonBuilderExtension {

  @Shadow public abstract SmithingTrimRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion);

  @Unique
  private boolean bypassesValidation;
  @Unique
  private @Nullable String customRecipeCategory;

  @Unique
  @SuppressWarnings("DataFlowIssue")
  private SmithingTrimRecipeJsonBuilder self() {
    return (SmithingTrimRecipeJsonBuilder) (Object) this;
  }

  @Override
  public SmithingTrimRecipeJsonBuilder criterionMethodBridge(String criterionName, AdvancementCriterion<?> criterion) {
    return criterion(criterionName, criterion);
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

  @Redirect(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/book/RecipeCategory;getName()Ljava/lang/String;"))
  public String redirectGetName(RecipeCategory instance) {
    if (customRecipeCategory != null) {
      return customRecipeCategory;
    } else {
      return instance.getName();
    }
  }

  @ModifyArg(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;withPrefixedPath(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/book/RecipeCategory;getName()Ljava/lang/String;")))
  public String redundantSlash(String path) {
    if (customRecipeCategory != null && customRecipeCategory.isEmpty()) {
      return StringUtils.replaceOnce(path, "recipes//", "recipes/");
    } else {
      return path;
    }
  }
}
