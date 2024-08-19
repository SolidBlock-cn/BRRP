package pers.solid.brrp.v1.recipe.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pers.solid.brrp.v1.recipe.RecipeJsonBuilderExtension;
import pers.solid.brrp.v1.recipe.ShapedRecipeJsonBuilderExtension;

@Mixin(ShapedRecipeJsonBuilder.class)
public abstract class ShapedRecipeJsonBuilderMixin implements ShapedRecipeJsonBuilderExtension {
  @Shadow
  public abstract ShapedRecipeJsonBuilder criterion(String string, CriterionConditions arg);

  @Shadow
  public abstract ShapedRecipeJsonBuilder pattern(String patternStr);

  private boolean bypassesValidation;
  private @Nullable String customRecipeCategory;

  @SuppressWarnings("DataFlowIssue")
  private ShapedRecipeJsonBuilder self() {
    return (ShapedRecipeJsonBuilder) (Object) this;
  }

  @Override
  public ShapedRecipeJsonBuilder patterns(String... patterns) {
    for (String pattern : patterns) {
      this.pattern(pattern);
    }
    return self();
  }

  @Override
  public ShapedRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
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
  public ShapedRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
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

  private CraftingRecipeCategory customCraftingCategory;

  @Override
  public ShapedRecipeJsonBuilder setCustomCraftingCategory(CraftingRecipeCategory customCraftingCategory) {
    this.customCraftingCategory = customCraftingCategory;
    return self();
  }

  @ModifyArg(method = "offerTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/server/recipe/ShapedRecipeJsonBuilder;getCraftingCategory(Lnet/minecraft/recipe/book/RecipeCategory;)Lnet/minecraft/recipe/book/CraftingRecipeCategory;"))
  public RecipeCategory applyCustomCraftingCategory(RecipeCategory par1) {
    if (par1 == null && customCraftingCategory != null) {
      return RecipeJsonBuilderExtension.invertGetCraftingCategory(customCraftingCategory);
    } else {
      return par1;
    }
  }
}
