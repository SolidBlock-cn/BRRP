package pers.solid.brrp.v1.recipe.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.component.ComponentChanges;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.book.RecipeCategory;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import pers.solid.brrp.v1.recipe.SmithingTransformRecipeJsonBuilderExtension;

@Mixin(SmithingTransformRecipeJsonBuilder.class)
public abstract class SmithingTransformRecipeJsonBuilderMixin implements SmithingTransformRecipeJsonBuilderExtension {

  @Shadow
  public abstract SmithingTransformRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion);

  @Unique
  private boolean bypassesValidation;
  @Unique
  private @Nullable String customRecipeCategory;
  @Unique
  private @Nullable ComponentChanges componentChanges;

  @Unique
  @SuppressWarnings("DataFlowIssue")
  private SmithingTransformRecipeJsonBuilder self() {
    return (SmithingTransformRecipeJsonBuilder) (Object) this;
  }

  @Override
  public SmithingTransformRecipeJsonBuilder criterionMethodBridge(String criterionName, AdvancementCriterion<?> criterion) {
    return criterion(criterionName, criterion);
  }

  @Override
  public SmithingTransformRecipeJsonBuilder setBypassesValidation(boolean bypassesValidation) {
    this.bypassesValidation = bypassesValidation;
    return self();
  }

  @ModifyExpressionValue(method = "validate", at = @At(value = "INVOKE", target = "Ljava/util/Map;isEmpty()Z"))
  private boolean bypassValidation(boolean original) {
    return !bypassesValidation && original;
  }

  @Override
  public SmithingTransformRecipeJsonBuilder setCustomRecipeCategory(@Nullable String recipeCategory) {
    this.customRecipeCategory = recipeCategory;
    return self();
  }

  @WrapOperation(method = "offerTo(Lnet/minecraft/data/server/recipe/RecipeExporter;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/book/RecipeCategory;getName()Ljava/lang/String;"))
  public String redirectGetName(RecipeCategory instance, Operation<String> original) {
    if (customRecipeCategory != null) {
      return customRecipeCategory;
    } else {
      return original.call(instance);
    }
  }

  @ModifyArg(method = "offerTo(Lnet/minecraft/data/server/recipe/RecipeExporter;Lnet/minecraft/util/Identifier;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Identifier;withPrefixedPath(Ljava/lang/String;)Lnet/minecraft/util/Identifier;"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/book/RecipeCategory;getName()Ljava/lang/String;")))
  public String redundantSlash(String path) {
    if (customRecipeCategory != null && customRecipeCategory.isEmpty()) {
      return StringUtils.replaceOnce(path, "recipes//", "recipes/");
    } else {
      return path;
    }
  }

  @Override
  public SmithingTransformRecipeJsonBuilder setComponentChanges(ComponentChanges componentChanges) {
    this.componentChanges = componentChanges;
    return self();
  }

  @ModifyExpressionValue(method = "offerTo(Lnet/minecraft/data/server/recipe/RecipeExporter;Lnet/minecraft/util/Identifier;)V", at = @At(value = "NEW", target = "(Lnet/minecraft/item/ItemConvertible;)Lnet/minecraft/item/ItemStack;"))
  public ItemStack applyComponents(ItemStack itemStack) {
    if (componentChanges != null) {
      itemStack.applyChanges(componentChanges);
    }
    return itemStack;
  }
}
