package pers.solid.brrp.v1.recipe.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonBuilder;
import net.minecraft.recipe.book.RecipeCategory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import pers.solid.brrp.v1.recipe.SingleItemRecipeJsonBuilderExtension;

@Mixin(SingleItemRecipeJsonBuilder.class)
public abstract class SingleItemRecipeJsonBuilderMixin implements SingleItemRecipeJsonBuilderExtension {

  @Shadow
  public abstract SingleItemRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion);

  @Unique
  private boolean bypassesValidation;
  @Unique
  private @Nullable String customRecipeCategory;

  @Unique
  @SuppressWarnings("DataFlowIssue")
  private SingleItemRecipeJsonBuilder self() {
    return (SingleItemRecipeJsonBuilder) (Object) this;
  }

  @Override
  public SingleItemRecipeJsonBuilder criterionMethodBridge(String criterionName, AdvancementCriterion<?> criterion) {
    return criterion(criterionName, criterion);
  }

  @Override
  public SingleItemRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
    this.bypassesValidation = bypassesValidation;
    return self();
  }

  @ModifyExpressionValue(method = "validate", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"))
  private boolean bypassValidation(boolean original) {
    return !bypassesValidation && original;
  }

  @Override
  public SingleItemRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
    this.customRecipeCategory = recipeCategory;
    return self();
  }

  @WrapOperation(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/book/RecipeCategory;getName()Ljava/lang/String;"))
  public String redirectGetName(RecipeCategory instance, Operation<String> original) {
    if (customRecipeCategory != null) {
      return customRecipeCategory;
    } else {
      return original.call(instance);
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
